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
  const dbComponent = 'jh-etk-db';
  const components = ['jh-etk-primeadapter',
                      'jh-etk-disputesvc',
                      'jh-etk-eventsvc',
                      'jh-etk-icbcadapter',
                      'jh-etk-issuancesvc',
                      'jh-etk-jiadapter',
                      'jh-etk-paymentsvc',
                      'jh-etk-rcmpadapter',
                      'jh-etk-scweb'];


  console.log("Scale down active instance replicas from 3 to 1 ...")
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

  // Change current active replicas (except standby DB) from 3 to 1 ...
  for (var component of components) {
    dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${active}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    
    // console.log("Component name: " + dcExists.stdout.toString().trim())
    if( dcExists.stdout.toString().includes(`${component}-${phases[phase].tag}`) ) {
      console.log(`\nScale ${component} (${dcExists.stdout.toString().trim()}) to 1...`)
      oc.raw('scale', ['dc'], {selector:`name=${dcExists.stdout.toString()}`, replicas:'1', namespace:phases[phase].namespace})
    } else {
      console.log(`Could not scale down the application component: ${component}`)
      //throw new Error("Idle application has failed, aborting run!")
    }
  }

  // Change current active DB replicas from 3 to 1 ...
  const dbComponentInstance = `${dbComponent}-${phases[phase].tag}${active}`;
  var stsExists=oc.raw('get', ['sts'], {selector:`cluster-name=${dbComponentInstance}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  if( stsExists.stdout.toString().includes(dbComponentInstance) ) {
    console.log(`Scale ${dbComponentInstance} to 1...`)
    oc.raw('scale', ['sts'], {selector:`cluster-name=${dbComponentInstance}`, replicas:'1', namespace:phases[phase].namespace})
  } else {
    console.log(`Could not scale down ${dbComponentInstance}`)
  }
}
