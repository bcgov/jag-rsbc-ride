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
  var db_version = fs.readFileSync(path.resolve(__dirname, '../../jh-etk-db/migrations/current/DBVersion.txt'), {encoding:'utf-8'});
  console.log("DB version from file: " + db_version)

  function getDBPodName() {
      // Get jh-etk-db patroni leader from configmap
      if (standby){
        var patroniLeaderPod=oc.raw('get', ['configmap'], {'field-selector':`metadata.name=jh-etk-db-${phases[phase].tag}${standby}-leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.annotations.leader', namespace:phases[phase].namespace})
        // Expect to find a patroni confimap for the target in the future state.
        if (patroniLeaderPod.stdout.toString().trim().length != 0){
          console.log("patroniLeaderPod:" + patroniLeaderPod.stdout.toString().trim())
          var targetDBPod=patroniLeaderPod.stdout.toString().trim()
          // Just b/c the ConfigMap shows a patroni pod, does that pod actually exist...
          var targetDBPodExists=oc.raw('get', ['pod'], {selector:`statefulset.kubernetes.io/pod-name=${targetDBPod},role=master`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
          console.log("TargetDBPodExists: " + targetDBPodExists)
          if (targetDBPodExists.stdout.toString().trim()){
            targetDBPod=targetDBPodExists.stdout.toString().trim()
          } else {
            targetDBPod=false
          }
        } else {
          // If there is no patroni confimap for the target then it is a new deployment.
          console.log("NON-Patroni A/B deployment")
          //var targetDBPodExists=oc.raw('get', ['pod'], {selector:`name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
          //var targetDBPod=targetDBPodExists.stdout.toString().trim()
          var targetDBPod=false
        }
      } else {
        // Target is not an A/B environment
        var patroniLeaderPod=oc.raw('get', ['configmap'], {'field-selector':`metadata.name=jh-etk-db-${phases[phase].tag}-leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.annotations.leader', namespace:phases[phase].namespace})
        // Expect to find a patroni confimap for the target in the future state.
        if (patroniLeaderPod.stdout.toString().trim().length != 0){
          console.log("patroniLeaderPod:" + patroniLeaderPod.stdout.toString().trim())
          var targetDBPod=patroniLeaderPod.stdout.toString().trim()
          // Just b/c the ConfigMap shows a patroni pod, does that pod actually exist...
          var targetDBPodExists=oc.raw('get', ['pod'], {selector:`statefulset.kubernetes.io/pod-name=${targetDBPod},role=master`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
          console.log("TargetDBPodExists: " + targetDBPodExists)
          console.log("TargetDBPodExistsNAME: " + targetDBPodExists.stdout.toString().trim())
          if (targetDBPodExists.stdout.toString().trim()){
            targetDBPod=targetDBPodExists.stdout.toString().trim()
          } else {
            targetDBPod=false
          }
        } else {
          console.log("Patroni ConfigMap NOT FOUND")
          var targetDBPodExists=oc.raw('get', ['pod'], {selector:`name=jh-etk-db-${phases[phase].tag},role=master`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
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
    console.log(`Waiting ${milliseconds} milliseconds`)
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }

  function isDBAvailable() { 
    if (standby){
      var stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    } else {
        var stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    }
    // This block is here for the transition to patroni.
    // It will be needed when encountering non-patroni DBs.
    if (stsCurrentReplicas.stdout.toString() == ""){
      return false
    } else {
      return true
    }
  }

  function assertDBStatefulSetAvailable() {
    console.log("Checking if DB StatefulSet is fully Available.")
    var loopCount = 0
    if (standby){
        var stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    } else {
        var stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
    }
    // This block is here for the transition to patroni.
    // It will be needed when encountering non-patroni DBs.
    if (stsCurrentReplicas.stdout.toString() == ""){
      console.log("Something is wrong with the StatefulSet")
      sleep(10000)
      return
    }
    console.log("stsCurrentReplicas:::::  " + stsCurrentReplicas.stdout.toString().trim())

    var activeReplicas = phases[phase].replicas
    
    while(stsCurrentReplicas.stdout.toString().trim() != activeReplicas){
      if (loopCount === 30) {
        console.log("The StatefulSet has failed to start correctly")
        throw new Error("Statefulset creation FAILED, aborting deployment!")
      }
      if (standby){
          stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
      } else {
          stsCurrentReplicas=oc.raw('get', ['sts'], {selector:`cluster-name=jh-etk-db-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=CURRENT:.status.currentReplicas', namespace:phases[phase].namespace})
      }
      if (loopCount === 0){
          console.log("DB StatefulSet is not yet Available.  Waiting... ")
      } else {
          console.log("stsCurrentReplicas:  " + stsCurrentReplicas.stdout.toString().trim())
      }

      loopCount++
      sleep(30000)
    }
    // This following is lame, need have a better test for actual db available than just currentReplicas.
    // Because currentReplicas goes to 1/1 (for PR and DEV) too quickly, the container is up but the db is not ready.  So we wait a bit.
    sleep(30000)
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

  // Function to perform DB downgrade
  function downgradeDB(targetDBPod, db_version) {
      console.log("Running downgradeDB on: " + targetDBPod)
      var migrationOutput = oc.raw('exec', [targetDBPod, '--', 'bash', '/tmp/migrations/downgradeDB', db_version])
      console.log(migrationOutput.stdout.toString().trim())
  }



  // Main
  console.log("Starting Database Deployment...")
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/patroni/jh-etk-db-patroni-secret.json`, {
   'param':{
     'PHASE': phases[phase].phase,
     'SUFFIX': phases[phase].suffix,
     'VERSION': phases[phase].tag
   }
  }))
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-db-deploy.json`, {
   'param':{
     'NAME': phases[phase].name,
     'SUFFIX': phases[phase].suffix,
     'VERSION': phases[phase].tag,
     'PHASE': phases[phase].phase,
     'IMAGE_STREAM_NAMESPACE': phases[phase].namespace,
     'CPU_REQUEST': phases[phase].cpu_request,
     'CPU_LIMIT': phases[phase].db_cpu_limit,
     'MEMORY_REQUEST': phases[phase].db_memory_request,
     'MEMORY_LIMIT': phases[phase].db_memory_limit,
     'REPLICAS': phases[phase].replicas,
     'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
     'CHANGE_ID': phases[phase].changeId,
     'GITHUB_REPO': `${oc.git.repository}`,
     'GITHUB_OWNER': `${oc.git.owner}`
   }
  }))
  // Perform image tag and labeling if STS exists OR do a full apply and deploy
  if (isDBAvailable()) {
      console.log("We seem to have a STS and so will not deploy")
      console.log("Idling STS pods...")
      if (standby){
        oc.raw('patch', ['statefulset', `jh-etk-db-${phases[phase].tag}${standby}`, '-p {\"spec\": {\"replicas\": 0 }}' ], {namespace:phases[phase].namespace})
      } else {
        oc.raw('patch', ['statefulset', `jh-etk-db-${phases[phase].tag}`, '-p {\"spec\": {\"replicas\": 0 }}' ], {namespace:phases[phase].namespace})
      }
      sleep(10000)
      oc.applyRecommendedLabels(objects, phases[phase].name, phase, `${changeId}`, phases[phase].instance)
      oc.importImageStreams(objects, phases[phase].tag, phases.build.namespace, phases.build.tag)
      console.log("Restarting STS pods...")
      if (standby){
        oc.raw('patch', ['statefulset', `jh-etk-db-${phases[phase].tag}${standby}`, `-p {\"spec\": {\"replicas\": ${phases[phase].replicas} }}` ], {namespace:phases[phase].namespace})
      } else {
        oc.raw('patch', ['statefulset', `jh-etk-db-${phases[phase].tag}`, `-p {\"spec\": {\"replicas\": ${phases[phase].replicas} }}` ], {namespace:phases[phase].namespace})
      }
      assertDBStatefulSetAvailable()
  } else {
      console.log("No StatefulSet target DB Pod exists")
      oc.applyRecommendedLabels(objects, phases[phase].name, phase, `${changeId}`, phases[phase].instance)
      oc.importImageStreams(objects, phases[phase].tag, phases.build.namespace, phases.build.tag)
      oc.applyAndDeploy(objects, phases[phase].instance)
      assertDBStatefulSetAvailable()
  }
}
