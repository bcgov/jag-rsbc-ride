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

  // Strange Metadata error is thrown if I don't "pre-delete" the tag.
  //oc.raw('delete', ['imagestreamtag'],{selector:'app.kubernetes.io/name=patroni,app.kubernetes.io/version=12', 'ignore-not-found':'true', 'wait':'true', 'namespace':phases[phase].namespace})

  // JHI-2644 removed
  //oc.raw('delete', ['imagestreamtag', 'postgres:12'], {'ignore-not-found':'true', 'namespace':phases[phase].namespace})

  // The building of your cool app goes here ▼▼▼
  /*
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appa-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'jh-cmn-kafka'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appb-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'jh-cmn-kafka'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appc-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'jh-cmn-kafka'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appd-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'jh-cmn-kafka'
    }
  }))
  */

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-ride-kafka-mockproducer-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'mockproducer'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-ride-kafka-mockconsumer-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_CONTEXT_DIR': 'mockConsumer'
    }
  }))
  
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-db-build.json`, {
    'param':{
      'NAME': phases[phase].dbname,
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'SOURCE_REPOSITORY_URL': oc.git.http_url,
      'SOURCE_REPOSITORY_REF': oc.git.ref,
      'SOURCE_BASE_CONTEXT_DIR': 'jh-etk-db',
      'SOURCE_CONTEXT_DIR': 'jh-etk-db'
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-build.json`, {
    'param':{
      'NAME': phases[phase].name,
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'SOURCE_REPOSITORY_URL': oc.git.http_url,
      'SOURCE_REPOSITORY_REF': oc.git.ref,
      'SOURCE_BASE_CONTEXT_DIR': '.',
      'SOURCE_CONTEXT_DIR': '.'
    }
  }))

   /*
   
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-testsvc-build.yaml`, {
     'param':{
      'NAME': phases[phase].name,
	   'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
	   'SOURCE_REPOSITORY_URL': oc.git.http_url,
      'SOURCE_REPOSITORY_REF': oc.git.ref,
      'SOURCE_CONTEXT_DIR': 'jh-etk-testsvc'
     }
   })) 
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-ridesvc-build.yaml`, {
    'param':{
      'VERSION': phases[phase].tag,
      'SUFFIX': phases[phase].suffix,
      'SOURCE_REPOSITORY_URL': oc.git.http_url,
      'SOURCE_REPOSITORY_REF': oc.git.ref,
      'SOURCE_CONTEXT_DIR': 'jh-cmn-ridesvc'
    }
  })) 
  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-rideweb-build.json`, {
   'param':{
    'VERSION': phases[phase].tag,
    'SOURCE_REPOSITORY_URL': oc.git.http_url,
    'SOURCE_REPOSITORY_REF': oc.git.ref
   }
 }))
 */
  oc.applyRecommendedLabels(objects, phases[phase].name, phase, phases[phase].changeId, phases[phase].instance)
  oc.applyAndBuild(objects)
}
