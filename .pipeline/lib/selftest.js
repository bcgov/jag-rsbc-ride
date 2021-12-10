'use strict';
const {OpenShiftClientX} = require('pipeline-cli')
const path = require('path');

module.exports = (settings)=>{
  const phases = settings.phases
  const options= settings.options
  const phase=options.env
  const standby=options.standby
  const oc=new OpenShiftClientX(Object.assign({'namespace':phases[phase].namespace}, options));
  var dcExists
  var readyTestResults = []
  var isSuccess = true
  var result = ''
  var failureSummary = ''

  const testsvc_component = 'jh-etk-testsvc';
  const components = ['jh-etk-primeadapter',
                      'jh-etk-disputesvc',
                      'jh-etk-eventsvc',
                      'jh-etk-issuancesvc',
                      'jh-etk-jiadapter',
                      'jh-etk-paymentsvc',
                      'jh-etk-rcmpadapter',
                      //'jh-cmn-rideweb',
                      'jh-etk-scweb',
                      testsvc_component];

  const componentUrls = {"jh-etk-primeadapter": "http://localhost:8080/primeadapter/v3/ready" ,
                         "jh-etk-disputesvc": "http://localhost:8080/dispute/v3/ready",
                         "jh-etk-eventsvc": "http://localhost:8080/events/v3/ready",
                         "jh-etk-icbcadapter": "http://localhost:8080/icbcadapter/v3/ready",
                         "jh-etk-issuancesvc": "http://localhost:8080/issuancesvc/v3/ready",
                         "jh-etk-jiadapter": "http://localhost:8080/dispute/v3/ready",
                         "jh-etk-paymentsvc": "http://localhost:8080/payment/v3/ready",
                         "jh-etk-rcmpadapter": "http://localhost:8080/etk/rcmp/v1/ready",
                         //"jh-cmn-rideweb": "http://localhost:8080",
                         "jh-etk-scweb": "http://localhost:8080/ready",
                         "jh-etk-testsvc": "http://localhost:5000/ready"};


  console.log("Running Application component self tests...")
  // Set active var
  if (standby){
      if (standby === '-green'){
          var active = '-blue'
      } else if (standby === '-blue') {
          var active = '-green'
      }
      console.log(`Standby: ${standby}`)
      console.log(`Active: ${active}`)
  }

  // skip testsvc selftest in stage and prod
  if (phase == "stage" || phase == "prod"){
    console.log(`Remove ${testsvc_component} from selftest list during ${phase} deployment`)
    components.splice(components.indexOf(testsvc_component), 1);
  }

  for (var component of components) {
    if (standby){
        dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
        var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
        var latestVersion=latestVersionObject.stdout.toString().trim()
        var targetPodNameObject=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    } else {
        dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
        var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
        var latestVersion=latestVersionObject.stdout.toString().trim()
        var targetPodNameObject=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    }
    var targetPodName=targetPodNameObject.stdout.toString().trim()
    console.log("targetPodName: " + targetPodName)
    if (targetPodName) {
        var componentUrl = componentUrls[component]
        //console.log(componentUrl)
        try {
          var componentReadyOutput = oc.raw('exec', [targetPodName, '--', 'curl', '-ksw', '%{http_code}' , `${componentUrl}`])
          //console.log(componentReadyOutput.stdout.toString().trim())
          if (componentReadyOutput.stdout.toString().trim() === '200'){
            result = `${component} ready test passed`
            console.log(`${result}`)
          } else {
            result = `${component} ready test failed`

            // capture error details
            var errorDetails = `Cannot connect to pod ${targetPodName} - ` + oc.raw('exec', [targetPodName, '--', 'curl', `${componentUrl}`]).stdout.toString()
            console.log(`${result}`)
            console.log(`Test failure details:\n${errorDetails}\n`)

            isSuccess = false
          }
          readyTestResults.push(result)
        }
        catch(err){
          result = `${component} ready test failed`
          console.log(`${result}`)
          console.log("Test failure details:\nPod Ready curl call failed\n")
          console.log(err)
          isSuccess = false

          readyTestResults.push(result)
        }
    } else {
      result = `${component} ready test failed`
      console.log(`${result}`)
      console.log("Test failure details:\nCould not get the target Pod\n")
      isSuccess = false

      readyTestResults.push(result)
    }
  }

  if (!isSuccess) {
    console.log("\nTest Failure Summary\n")
    while (readyTestResults.length){
      console.log(readyTestResults.pop())
    }
  
    throw new Error('Deployment Verification Test failed, aborting deployment...')
  }
}
