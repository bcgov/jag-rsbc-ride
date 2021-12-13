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

  const components = ['rsbc-ride-kafka-mockproducer',
                      'rsbc-ride-kafka-mockconsumer'
                      ];

  // The deployment of your cool app goes here ▼▼▼

  if (phase == "stage"){
    console.log("NOT deleting routes for blue/green deploy to " + phase)
  } else if ( phase == "prod"){
    console.log("NOT deleting routes for blue/green deploy to " + phase)
  } else {
    console.log('Delete routes...')
    oc.raw('delete', ['route'], {selector:`app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  }

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-cmn-kafka-zookeeper-deploy.json`, {
    'param':{
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'PHASE': phases[phase].phase,
      'CPU_REQUEST': phases[phase].cpu_request,
      'CPU_LIMIT': phases[phase].cpu_limit,
      'MEMORY_REQUEST': phases[phase].memory_request,
      'MEMORY_LIMIT': '1G'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-cmn-kafka-kafka-deploy.json`, {
    'param':{
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'PHASE': phases[phase].phase,
      'CPU_REQUEST': phases[phase].cpu_request,
      'CPU_LIMIT': phases[phase].cpu_limit,
      'MEMORY_REQUEST': phases[phase].memory_request,
      'MEMORY_LIMIT': '1G'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-ride-kafka-mockproducer-deploy.json`, {
    'param':{
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'PHASE': phases[phase].phase,
      'CPU_REQUEST': phases[phase].cpu_request,
      'CPU_LIMIT': phases[phase].cpu_limit,
      'MEMORY_REQUEST': phases[phase].memory_request,
      'MEMORY_LIMIT': phases[phase].memory_limit
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-ride-kafka-mockconsumer-deploy.json`, {
    'param':{
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'PHASE': phases[phase].phase,
      'CPU_REQUEST': phases[phase].cpu_request,
      'CPU_LIMIT': phases[phase].cpu_limit,
      'MEMORY_REQUEST': phases[phase].memory_request,
      'MEMORY_LIMIT': phases[phase].memory_limit
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-ride-all-routes-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'HOSTNAME_SUFFIX': phases[phase].hostname_suffix,
       'HAPROXY_TIMEOUT': phases[phase].haproxy_timeout,
       'SSG_WHITELIST': phases[phase].ssg_whitelist
     }
   }))
 
  oc.applyRecommendedLabels(objects, phases[phase].name, phase, `${changeId}`, phases[phase].instance)
  oc.importImageStreams(objects, phases[phase].suffix, phases.build.namespace, phases.build.tag)

  oc.applyAndDeploy(objects, phases[phase].instance)

  //Function to do hard sleep
  function sleep(milliseconds) {
    const date = Date.now();
    let currentDate = null;
    do {
      currentDate = Date.now();
    } while (currentDate - date < milliseconds);
  }

  // Poll each component for pod status true
  // Idle standby components (except standby DB) before active database backup
  for (var component of components) {
    console.log(`Checking ${component} pod status`)
    var loopCount = 0
    if (standby){
      var componentDC=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name,VERSION:.status.latestVersion,DESIRED:.spec.replicas,CURRENT:.status.replicas', namespace:phases[phase].namespace})
      var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
      var latestVersion=latestVersionObject.stdout.toString().trim()
      var componentPodName=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
      var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
    } else {
      var componentDC=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name,VERSION:.status.latestVersion,DESIRED:.spec.replicas,CURRENT:.status.replicas', namespace:phases[phase].namespace})
      var latestVersionObject=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=VERSION:.status.latestVersion', namespace:phases[phase].namespace})
      var latestVersion=latestVersionObject.stdout.toString().trim()
      var componentPodName=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
      var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
    }
    console.log(`  ${component} DC:` + componentDC.stdout.toString().trim())
    console.log(`  ${component} Pod Name:` + componentPodName.stdout.toString().trim())
    console.log(`  ${component} latest Version:` + latestVersion)
    console.log(`  ${component} pod status: >>>` + isComponentPodReady.stdout.toString().trim() + "<<<")
    if (isComponentPodReady.stdout.toString().trim() === 'true'){
      console.log(`  ${component} pod is already: ` + isComponentPodReady.stdout.toString().trim() + "\n")
    }
    else {
      console.log(`${component} pod is NOT READY, waiting...`)
      while(isComponentPodReady.stdout.toString().trim().includes('false') || isComponentPodReady.stdout.toString().trim() === '' || isComponentPodReady.stdout.toString().trim() === '<none>'){
        if (loopCount === 120) { throw new Error("Loop Timeout has been reached.  Aborting run!") }
        if (standby){
          var componentPodName=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
          var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}${standby}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
        } else {
          var componentPodName=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
          var isComponentPodReady=oc.raw('get', ['pod'], {selector:`deployment=${component}-${phases[phase].tag}-${latestVersion},role=leader`, 'no-headers':'true', output:'custom-columns=CONTAINER:.status.containerStatuses[0].ready', namespace:phases[phase].namespace})
        }
        console.log(`  ${loopCount}: ${component} pod ` + componentPodName.stdout.toString().trim() + ` status: ` + isComponentPodReady.stdout.toString().trim())
        loopCount++
        sleep(5000)
      }
      console.log(`\n${component} pod is now READY\n`)
    }
  }
}
