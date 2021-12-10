'use strict';
const {OpenShiftClientX} = require('pipeline-cli')
const path = require('path');

module.exports = (settings)=>{
  const phases = settings.phases
  const options= settings.options
  const phase=options.env
  const standby=options.standby
  const changeId = phases[phase].changeId
  const oc=new OpenShiftClientX(Object.assign({'namespace':phases[phase].namespace}, options));
  const templatesLocalBaseUrl =oc.toFileUrl(path.resolve(__dirname, '../../openshift'))
  var objects = []
  var dcExists
  var backupPodExists
  var backupPod
  var dbAdminPasswordJson
  var dbAdminPassword
  var gzBackupFiles
  var dbcomponent = 'jh-etk-db'
  const components = ['jh-etk-primeadapter',
                      'jh-etk-disputesvc',
                      'jh-etk-eventsvc',
                      'jh-etk-icbcadapter',
                      'jh-etk-issuancesvc',
                      'jh-etk-jiadapter',
                      'jh-etk-mocksvc',
                      'jh-etk-paymentsvc',
                      'jh-etk-rcmpadapter',
                      //'jh-cmn-rideweb',
                      'jh-etk-scweb'];


  console.log("Cloning Active database")
  console.log(`Standby: ${standby}`)
  //Set active var
  if (standby === '-green'){
    var active = '-blue'
  } else if (standby === '-blue') {
    var active = '-green'
  }
  console.log(`Active: ${active}`)

  //Function to do hard sleep
  function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }

  // Because clonedb needs the db phase secret, we have to run the secret deployment here for the
  // 'first-run' edge case.
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/patroni/jh-etk-db-patroni-secret.json`, {
    'param':{
      'PHASE': phases[phase].phase,
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag
    }
  }))
  oc.applyRecommendedLabels(objects, phases[phase].name, phase, `${changeId}`, phases[phase].instance)
  oc.applyAndDeploy(objects, phases[phase].instance)

  // Idle existing standby components (except standby DB) before active database backup
  for (var component of components) {
    dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    // console.log(dcExists.stdout.toString())
    if( dcExists.stdout.toString().includes(`${component}-${phases[phase].tag}${standby}`) ) {
      console.log(`Scale ${component} to zero...`)
      oc.raw('scale', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, replicas:'0', namespace:phases[phase].namespace})
    }
  }

  // Spin up the idle standby db
  dcExists=oc.raw('get', ['sts'], {selector:`name=${dbcomponent}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  // console.log(dcExists.stdout.toString())
  if( dcExists.stdout.toString().includes(`${dbcomponent}-${phases[phase].tag}${standby}`) ) {
    console.log(`Scale ${dbcomponent}-${phases[phase].tag}${standby} to one...`)
    oc.raw('scale', ['sts'], {selector:`name=${dbcomponent}-${phases[phase].tag}${standby}`, replicas:'1', namespace:phases[phase].namespace})

    // wait until the standby db is fully running
    var latestVersionObject=oc.raw('get', ['sts'], {selector:`name=${dbcomponent}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
    var latestVersion=latestVersionObject.stdout.toString().trim()
    var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${dbcomponent}-${phases[phase].tag}${standby}-${latestVersion}`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
    var loopCount=0

    while(isComponentPodReady.stdout.toString().trim().includes('false') || isComponentPodReady.stdout.toString().trim() === '' || isComponentPodReady.stdout.toString().trim() === '<none>'){
      if (loopCount === 120) { 
        throw new Error("Loop Timeout has been reached.  Aborting run!")
      } else {
        var componentPodName=oc.raw('get', ['pod'], {selector:`deployment=${dbcomponent}-${phases[phase].tag}${standby}-${latestVersion}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
        var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${dbcomponent}-${phases[phase].tag}${standby}-${latestVersion}`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
      }
      console.log(`  ${loopCount}: ${dbcomponent} pod ` + componentPodName.stdout.toString().trim() + ` status: ` + isComponentPodReady.stdout.toString().trim())
      loopCount++
      sleep(5000)
    }
    console.log(`\n${dbcomponent} pod is now READY\n`)
  }

  // Get jh-etk-db-backup pod name
  backupPodExists=oc.raw('get', ['pod'], {selector:`name=jh-etk-db-backup`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  backupPod=backupPodExists.stdout.toString().trim()

  //Back up the active DB
  //Run database backup on jh-etk-db-backup pod
  var backupResults = oc.raw('exec', [backupPod, '--', 'bash', '/backup.sh', '-1'])
  console.log('################################################')
  //  console.log(backupResults.stdout.toString())
  if (backupResults.status.toString().includes("0")){
    console.log("Single backup run complete")
  } else {
    throw new Error("Backup failed, aborting run!")
  }
  console.log('################################################')
  // console.log(backupResults.toString().trim())
  // console.log(backupResults.stdout.toString().trim())
  // console.log('################################################')

  //Restore the backed-up active database data into the new standby DB

  //Get DB Admin password
  dbAdminPasswordJson=oc.raw('extract', ['--to=-', '--keys=database-admin-password', `secret/jh-etk-db-${phases[phase].tag}`], {namespace:phases[phase].namespace})
  dbAdminPassword = dbAdminPasswordJson.stdout.toString().trim()

  // Retrieve the backup file name to be used

  var latestActiveInstanceBackupFile = ""
  var activeDBBackupFileList = backupResults.stdout.toString().split(/\r\n|\n|\r/).filter(
    function(line) {return line.match(new RegExp("^Backup written to.*" + active))}
    ).map(line => line.replace(/^Backup written to /,"").replace(/\.$/,""))

  if (activeDBBackupFileList.length == 0) {
    console.log("Failed to identify the active DB backup file to use.  Backup log: \n" + backupResults.stdout.toString())
    throw new Error("Cannot find the latest active DB backup file; aborting run!")
  } else if (activeDBBackupFileList.length > 1) {
    console.log("Failed to identify one single active DB backup file to use.  Backup log: \n" + backupResults.stdout.toString())
    throw new Error("Cannot uniquely identify the latest active DB backup file; aborting run!")
  } else {
    latestActiveInstanceBackupFile = activeDBBackupFileList[0];
  }
  
  if (latestActiveInstanceBackupFile.length > 0 ){
    console.log('################################################')
    console.log('Active instance backup file to be used:')
    console.log(latestActiveInstanceBackupFile)
    console.log('################################################')
  } else {
    throw new Error("Could not find the latest active instance backup file; aborting run!")
  }

  // Restore the active data to the new standby database

  // console.log(backupPod)
  // console.log(dbAdminPassword)
  // console.log(`Active: ${active}`)
  // console.log(phases[phase].suffix)
  var restoreResults = oc.raw('exec', [backupPod, '--', 'bash', '/backup.sh', '-s', '-a', dbAdminPassword, '-r', `jh-etk-db${phases[phase].suffix}/jh_etk_db`, '-f', latestActiveInstanceBackupFile])
  console.log('################################################')
  if (restoreResults.status.toString().includes("0")){
    console.log(restoreResults.status.toString())
    console.log(restoreResults.stdout.toString())
    console.log("Restore completed successfully")
  } else {
    console.log(restoreResults.status.toString())
    console.log(restoreResults.stdout.toString())
    throw new Error("Restore of DB failed, aborting run!")
  }
  // console.log(restoreResults.toString().trim())
  // console.log(restoreResults.stdout.toString().trim())
  console.log('################################################')

}
