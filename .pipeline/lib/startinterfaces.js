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

  // Functions
  //
  // Function to do a hard sleep
  function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }

  // Function to see if SCWeb Pod responds to Ping
  function isSCWebReady(targetScwebPod) {
      console.log("Pinging the target SCWeb: " + targetScwebPod)
      var loopCount = 0
      var pingResponse = oc.raw('exec', [targetScwebPod, '--', 'curl', '-ks', '-o', '/dev/null', '-w', '%{http_code}', 'http://localhost:9090/management/ping'])
      while(pingResponse.stdout.toString().trim() != '200'){
          if (loopCount === 3) {break}
          if (loopCount === 0){
              console.log("SCWeb Pod Ping failed.  Waiting... ")
          } else {
              process.stdout.write(".");
          }
          loopCount++
          sleep(10000)
          pingResponse = oc.raw('exec', [targetScwebPod, '--', 'curl', '-ks', '-o', '/dev/null', '-w', '%{http_code}', 'http://localhost:9090/management/ping'])
      }
      console.log("Ping successful " + pingResponse.stdout.toString().trim())
  }

  // Main
  console.log("Starting App Interfaces on Target")
  // Get jh-etk-scweb dc and pod selectors and names
  if (standby){
    var targetScwebDC=oc.raw('get', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name,VERSION:.status.latestVersion,DESIRED:.spec.replicas,CURRENT:.status.replicas', namespace:phases[phase].namespace})
    var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
    var latestVersion=latestVersionObject.stdout.toString().trim()
    var targetScwebPodNameObject=oc.raw('get', ['pod'], {selector:`deployment=jh-etk-scweb-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    var isTargetScwebPodReady=oc.raw('get', ['pod'], {selector:`deployment=jh-etk-scweb-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
  } else {
    var targetScwebDC=oc.raw('get', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name,VERSION:.status.latestVersion,DESIRED:.spec.replicas,CURRENT:.status.replicas', namespace:phases[phase].namespace})
    var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
    var latestVersion=latestVersionObject.stdout.toString().trim()
    var targetScwebPodNameObject=oc.raw('get', ['pod'], {selector:`deployment=jh-etk-scweb-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    var isTargetScwebPodReady=oc.raw('get', ['pod'], {selector:`deployment=jh-etk-scweb-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
  }
  var targetScwebPodName=targetScwebPodNameObject.stdout.toString().trim()
  // console.log("targetScwebPodName: " + targetScwebPodName)
  // Start all interfaces on target scweb pod
  if (targetScwebPodName) {
      isSCWebReady(targetScwebPodName);
      try {
        var interfaceStartOutput = oc.raw('exec', [targetScwebPodName, '--', 'curl', '-ks', '-o', '/dev/null', '-w', '%{http_code}', 'http://localhost:9090/management/interface/start/all'])
        console.log(interfaceStartOutput.stdout.toString().trim())
        if (interfaceStartOutput.stdout.toString().trim() != '200') {
          throw new Error("Could not start interfaces. HTTP response code not 200: " + interfaceStartOutput.stdout.toString().trim())
        }
      }
      catch(err){
        console.log("SCWeb curl start/all failed")
        console.log(err)
        throw new Error("Could not start interfaces. oc exec error occurred: " + err)
      }
  } else {
      console.log("Could not get the target SCWeb Pod")
      throw new Error("Could not start interfaces.  Pod unavailable, aborting run!")
  }
}
