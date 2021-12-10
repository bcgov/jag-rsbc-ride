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

  const fs = require('fs');
  var db_version = fs.readFileSync(path.resolve(__dirname, '../../jh-etk-db/migrations/current/DBVersion.txt'), {encoding:'utf-8'});
  // console.log("DB version from file: " + db_version)

  //Set active var based on supplied standby parameter, if it exists
  if (standby){
      if (standby === '-green'){
          var active = '-blue'
      } else if (standby === '-blue') {
          var active = '-green'
      }
      // console.log(`Standby: ${standby}`)
      // console.log(`Active: ${active}`)
  }
  function getDBPodName() {
    // Get jh-etk-db patroni leader from configmap
    if (standby){
      var patroniLeaderPod=oc.raw('get', ['configmap'], {'field-selector':`metadata.name=jh-etk-db-${phases[phase].tag}${standby}-leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.annotations.leader', namespace:phases[phase].namespace})
      // Expect to find a patroni confimap for the target in the future state.
      if (patroniLeaderPod.stdout.toString().trim().length != 0){
        console.log("patroniLeaderPod:" + patroniLeaderPod.stdout.toString().trim())
        var targetDBPod=patroniLeaderPod.stdout.toString().trim()
      } else {
        // If there is no patroni confimap for the target then it is a non-patroni database
        // Will get here during the transition from non-patroni to patroni deployments.
        console.log("NON-Patroni A/B deployment")
        var targetDBPodExists=oc.raw('get', ['pod'], {selector:`name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
        var targetDBPod=targetDBPodExists.stdout.toString().trim()
      }
    } else {
      // Target is not an A/B environment
      var patroniLeaderPod=oc.raw('get', ['configmap'], {'field-selector':`metadata.name=jh-etk-db-${phases[phase].tag}-leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.annotations.leader', namespace:phases[phase].namespace})
      // Expect to find a patroni confimap for the target in the future state.
      if (patroniLeaderPod.stdout.toString().trim().length != 0){
        console.log("patroniLeaderPod:" + patroniLeaderPod.stdout.toString().trim())
        var targetDBPod=patroniLeaderPod.stdout.toString().trim()
      } else {
        console.log("NON-Patroni deployment")
        var targetDBPodExists=oc.raw('get', ['pod'], {selector:`name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
        var targetDBPod=targetDBPodExists.stdout.toString().trim()
      }
    }
    console.log("##########################################")
    console.log("Target DB Pod (Leader) Name: " + targetDBPod)
    console.log("##########################################")
    return targetDBPod
} 

  // Function to see if DB Pod is Available
  function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }

  function isDBStatefulSetAvailable() {
    console.log("Checking if DB StatefulSet is fully Available.")
    var loopCount = 0
    if (standby){
        var isTargetDBAvailable=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    } else {
        var isTargetDBAvailable=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    }
    // This block is here for the transition to patroni.
    // It will be needed when encountering non-patroni DBs.
    if (isTargetDBAvailable.stdout.toString() == ""){
      console.log("Not a Patroni DB")
      sleep(10000)
      return
    }
    console.log("isTargetDBAvailable:::::  " + isTargetDBAvailable.stdout.toString().trim())
    while(isTargetDBAvailable.stdout.toString().trim() != '3'){
      if (loopCount === 30) {break}
      if (standby){
          isTargetDBAvailable=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
      } else {
          isTargetDBAvailable=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
      }
      if (loopCount === 0){
          console.log("DB StatefulSet is not yet Available.  Waiting... ")
      } else {
          process.stdout.write(".");
      }

      loopCount++
      sleep(10000)
    }
    console.log("DB StatefulSet is NOW Available")
  }
  
  // Function to see if DB Pod responds to Ping
  // Not currently being used.
  function isDBReady(targetDBPod) {
      console.log("Pinging the target DB: " + targetDBPod)
      var loopCount = 0
      var pingResponse = oc.raw('exec', [targetDBPod, '--', 'curl', '-ks', '-o', '/dev/null', '-w', '%{http_code}', 'http://localhost:9090/management/ping'])
      while(pingResponse.stdout.toString().trim() != '200'){
          if (loopCount === 3) {break}
          if (loopCount === 0){
              console.log("DB Pod Ping failed.  Waiting... ")
          } else {
              process.stdout.write(".");
          }
          loopCount++
          sleep(10000)
          pingResponse = oc.raw('exec', [targetDBPod, '--', 'curl', '-ks', '-o', '/dev/null', '-w', '%{http_code}', 'http://localhost:9090/management/ping'])
      }
      console.log("Ping successful " + pingResponse.stdout.toString().trim())
  }

  // Function to perform DB upgrade
  function upgradeDB(targetDBPod) {
      console.log("Running upgradeDB on: " + targetDBPod)
      var migrationOutput = oc.raw('exec', [targetDBPod, '--', 'bash', '/tmp/migrations/upgradeDB'])
      console.log(migrationOutput.stdout.toString().trim())
  }

  // Main
  console.log("Starting Database Migration...")
  // Perform migration on target db pod
  if (getDBPodName()) {
      upgradeDB(getDBPodName())
  } else {
      console.log("No target DB Pod exists, waiting...")
      // Wait for the new pod to be available
      isDBStatefulSetAvailable()
      upgradeDB(getDBPodName())
  }
}
