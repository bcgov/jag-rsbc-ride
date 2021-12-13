'use strict';
const {OpenShiftClientX} = require('pipeline-cli')
const path = require('path');

module.exports = (settings)=>{
  const phases = settings.phases
  const options = settings.options
  const oc=new OpenShiftClientX(Object.assign({'namespace':phases.build.namespace}, options));
  const phase='build'
  let objects = []
  const templatesLocalBaseUrl =oc.toFileUrl(path.resolve(__dirname, '../../openshift'))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-ride-kafka-mockproducer-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'rsbc-ride-kafka-mockproducer'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/rsbc-ride-kafka-mockconsumer-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'rsbc-ride-kafka-mockconsumer'
    }
  }))
  
  oc.applyRecommendedLabels(objects, phases[phase].name, phase, phases[phase].changeId, phases[phase].instance)
  oc.applyAndBuild(objects)
}
