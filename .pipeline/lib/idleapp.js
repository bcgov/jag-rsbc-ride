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


  console.log("Idling Application components...")
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

  // Idle standby components (except standby DB) before active database backup
  for (var component of components) {
    if (standby){
        dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}${standby}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    } else {
        dcExists=oc.raw('get', ['dc'], {selector:`name=${component}-${phases[phase].tag}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
    }
    // console.log("Component name: " + dcExists.stdout.toString().trim())
    if( dcExists.stdout.toString().includes(`${component}-${phases[phase].tag}`) ) {
      console.log(`\nScale ${component} (${dcExists.stdout.toString().trim()}) to zero...`)
      oc.raw('scale', ['dc'], {selector:`name=${dcExists.stdout.toString()}`, replicas:'0', namespace:phases[phase].namespace})
    } else {
      console.log(`Could not idle the application component: ${component}`)
      //throw new Error("Idle application has failed, aborting run!")
    }
  }

}
