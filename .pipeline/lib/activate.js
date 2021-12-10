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
  var stsExists
  var backupPodExists
  var backupPod
  const mocksvc_component = 'jh-etk-mocksvc';
  const rideweb_component = 'jh-cmn-rideweb';
  const ridesvc_component = 'jh-cmn-ridesvc';
  const components = ['jh-etk-primeadapter',
                      'jh-etk-disputesvc',
                      'jh-etk-eventsvc',
                      'jh-etk-icbcadapter',
                      'jh-etk-issuancesvc',
                      'jh-etk-jiadapter',
                      mocksvc_component,
                      'jh-etk-paymentsvc',
                      'jh-etk-rcmpadapter',
                      'jh-etk-scweb',
                      rideweb_component,
                      ridesvc_component,
                    ];
  const dbComponent = 'jh-etk-db';


  console.log("Activating standby instance")
  console.log(`Standby: ${standby}`)
  //Set active var
  if (standby === '-green'){
    var active = '-blue'
  } else if (standby === '-blue') {
    var active = '-green'
  }
  console.log(`Active: ${active}`)
  

  // Delete active routes
  console.log('Delete routes...')
  oc.raw('delete', ['route'], {selector:`app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  oc.raw('delete', ['route'], {selector:`app=jh-cmn${phases[phase].suffix},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})

  // Delete db, mocksvc, testsvc "active" services
  console.log('Delete services...')
  oc.raw('delete', ['service'], {selector:`name=jh-etk-db-${phases[phase].tag}-active,app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  oc.raw('delete', ['service'], {selector:`name=jh-etk-mocksvc-${phases[phase].tag}-active,app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  oc.raw('delete', ['service'], {selector:`name=jh-etk-testsvc-${phases[phase].tag}-active,app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-all-routes-deploy.json`, {
    'param':{
      'NAME': phases[phase].name,
      'SUFFIX': phases[phase].suffix,
      'HOSTNAME_SUFFIX': phases[phase].hostname_suffix_active,
      'SSG_WHITELIST': phases[phase].ssg_whitelist,
      'HAPROXY_WHITELIST': phases[phase].haproxy_whitelist,
      'HAPROXY_TIMEOUT': phases[phase].haproxy_timeout,
      'CONFIGURED_SCWEB_HOSTNAME': phases[phase].configured_scweb_hostname,
      'CONFIGURED_RIDEWEB_HOSTNAME': phases[phase].configured_rideweb_hostname,
      'RCMPADAPTER_ROUTE_HOST': phases[phase].rcmpadapter_route_host,
      'PAYMENTSVC_ROUTE_HOST': phases[phase].paymentsvc_route_host,
      'CONFIGURED_RIDESVC_HOSTNAME': phases[phase].configured_ridesvc_hostname
    }
  }))
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-bg-active-services.json`, {
    'param':{
      'NAME': phases[phase].name,
      'PHASE': phases[phase].phase,
      'SUFFIX': phases[phase].suffix
    }
  }))
  oc.applyRecommendedLabels(objects, phases[phase].name, phase, `${changeId}`, phases[phase].instance)
  //oc.importImageStreams(objects, phases[phase].tag, phases.build.namespace, phases.build.tag)

  oc.applyAndDeploy(objects, phases[phase].instance)

  if(phase === 'prod') {
    oc.raw('delete', ['route'], {selector:`name=jh-etk-icbcadapter-prod`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-mocksvc-prod`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-issuancesvc-prod`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-jiadapter-prod`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-primeadapter-prod`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-eventsvc-prod`, wait:'true', namespace:phases[phase].namespace})
  } else if(phase === 'stage') {
    oc.raw('delete', ['route'], {selector:`name=jh-etk-icbcadapter-stage`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-mocksvc-stage`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-issuancesvc-stage`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-jiadapter-stage`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-primeadapter-stage`, wait:'true', namespace:phases[phase].namespace})
    oc.raw('delete', ['route'], {selector:`name=jh-etk-eventsvc-stage`, wait:'true', namespace:phases[phase].namespace})
  } 
  
  // Final step: idle all "new" standby (old active) components (including standby DB)
  console.log("Idle all new standby (old active) application components")
  for (const component of components) {
    dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${active}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    console.log("Component DC: " + dcExists.stdout.toString())
    if( dcExists.stdout.toString().includes(`${component}-${phases[phase].tag}${active}`) ) {
      console.log(`Scale ${component} to zero...`)
      oc.raw('scale', ['dc'], {selector:`name=${component}-${phases[phase].tag}${active}`, replicas:'0', namespace:phases[phase].namespace})
    } else {
      console.log(`Could not scale down the old active ${component}`)
    }
  }  

  // Final step: idle "new" standby (old active) DB component
  console.log("Idle the new standby (old active) database component");
  const dbComponentInstance = `${dbComponent}-${phases[phase].tag}${active}`;
  stsExists=oc.raw('get', ['sts'], {selector:`cluster-name=${dbComponentInstance}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  if( stsExists.stdout.toString().includes(dbComponentInstance) ) {
    console.log(`Scale ${dbComponentInstance} to zero...`)
    oc.raw('scale', ['sts'], {selector:`cluster-name=${dbComponentInstance}`, replicas:'0', namespace:phases[phase].namespace})
  } else {
    console.log(`Could not scale down ${dbComponentInstance}`)
  }

  // Final step: scale up new active (old standby) replicas from 1 to 3 (including standby DB)
  // The following application components shouldn't be scaled up: rideweb, ridesvc, mocksvc
  components.splice(components.indexOf(mocksvc_component), 1);
  components.splice(components.indexOf(rideweb_component), 1);
  components.splice(components.indexOf(ridesvc_component), 1);

  console.log("Change all new active (old standby) application components replicas from 1 to 3")
  for (const component of components) {
    dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    console.log("Component DC: " + dcExists.stdout.toString())
    if( dcExists.stdout.toString().includes(`${component}-${phases[phase].tag}${standby}`) ) {
      console.log(`Scale ${component} to 3...`)
      oc.raw('scale', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, replicas:'3', namespace:phases[phase].namespace})
    } else {
      console.log(`Could not scale up the old standby ${component}`)
    }
  }  

  // Final step: scale up new active (old standby) DB component from 1 to 3
  console.log("Scale up new active (old standby) database component");
  const dbComponentInstanceNewActive = `${dbComponent}-${phases[phase].tag}${standby}`;
  stsExists=oc.raw('get', ['sts'], {selector:`cluster-name=${dbComponentInstanceNewActive}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  if( stsExists.stdout.toString().includes(dbComponentInstanceNewActive) ) {
    console.log(`Scale up ${dbComponentInstanceNewActive} to 3...`)
    oc.raw('scale', ['sts'], {selector:`cluster-name=${dbComponentInstanceNewActive}`, replicas:'3', namespace:phases[phase].namespace})
  } else {
    console.log(`Could not scale up ${dbComponentInstanceNewActive}`)
  }
}
