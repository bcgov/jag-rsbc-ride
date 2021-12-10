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

  const testsvc_component = 'jh-etk-testsvc';
  const components = [//'jh-cmn-kafka-zookeeper',
                      //'jh-cmn-kafka-kafka',
                      'jh-ride-kafka-mockproducer',
                      'jh-ride-kafka-mockconsumer',
                      
                      //'jh-cmn-kafka-appa',
                      //'jh-cmn-kafka-appb',
                      //'jh-cmn-kafka-appc',
                      //'jh-cmn-kafka-appd',
                      //'jh-etk-primeadapter',
                      'jh-etk-disputesvc',
                      'jh-etk-eventsvc',
                      'jh-etk-icbcadapter',
                      'jh-etk-issuancesvc',
                      'jh-etk-jiadapter',
                      'jh-etk-mocksvc',
                      'jh-etk-paymentsvc',
                      'jh-etk-rcmpadapter',
                      //'jh-cmn-ridesvc',
                      //'jh-cmn-rideweb',
                     // testsvc_component,
                      'jh-etk-scweb'];

  const fs = require('fs');
  var db_version = fs.readFileSync(path.resolve(__dirname, '../../jh-etk-db/migrations/current/DBVersion.txt'), {encoding:'utf-8'});

  // The deployment of your cool app goes here ▼▼▼

  //console.log("LOGGING phase...")
  //console.log(phase)

  // Quiesce jh-etk-primeadapter component to avoid race condition between old and new deployments
  dcExists=oc.raw('get', ['dc'], {selector:`name=jh-etk-primeadapter-${phases[phase].tag},env-id=${changeId}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  //console.log(dcExists.stdout.toString())
  if( dcExists.stdout.toString().includes(`jh-etk-primeadapter-${phases[phase].tag}`) ) {
    console.log('Scale to zero...')
    oc.raw('scale', ['dc'], {selector:`name=jh-etk-primeadapter-${phases[phase].tag},env-id=${changeId}`, replicas:'0', namespace:phases[phase].namespace})
  }

  dcExists=oc.raw('get', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag},env-id=${changeId}`, 'no-headers':'true', output:'custom-columns=NAME:.metadata.name', namespace:phases[phase].namespace})
  if( dcExists.stdout.toString().includes(`jh-etk-scweb-${phases[phase].tag}`) ) {
      console.log('Idling scweb before deployment to avoid racing condition...')
      oc.raw('scale', ['dc'], {selector:`name=jh-etk-scweb-${phases[phase].tag},env-id=${changeId}`, replicas:'0', namespace:phases[phase].namespace})
  }

  if (phase == "stage"){
    console.log("NOT deleting routes for blue/green deploy to " + phase)

    console.log(`Remove ${testsvc_component} from deployment list during ${phase} deployment`)
    components.splice(components.indexOf(testsvc_component), 1);
  } else if ( phase == "prod"){
    console.log("NOT deleting routes for blue/green deploy to " + phase)

    console.log(`Remove ${testsvc_component} from deployment list during ${phase} deployment`)
    components.splice(components.indexOf(testsvc_component), 1);
  } else {
    console.log('Delete routes...')
    oc.raw('delete', ['route'], {selector:`app=${phases[phase].instance},env-name=${phases[phase].phase},github-repo=${oc.git.repository},github-owner=${oc.git.owner}`, wait:'true', namespace:phases[phase].namespace})
  }

  //If it doesn't exist create a "environment" secret object
  oc.createIfMissing(oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-secrets.json`, {
   'param':{
     'NAME': `template.${phases[phase].name}-${phases[phase].phase}`,
     'SUFFIX': phases[phase].suffix
    }
  }))

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-zookeeper-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-kafka-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-ride-kafka-mockproducer-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-ride-kafka-mockconsumer-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-primeadapter-deploy.json`, {
    'param':{
      'NAME': phases[phase].name,
      'SUFFIX': phases[phase].suffix,
      'VERSION': phases[phase].tag,
      'PHASE': phases[phase].phase,
      'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
      'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
      'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
      'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
      'LOG_LEVEL': phases[phase].log_level,
      'SPLUNK_URL': phases[phase].splunk_url,
      'SPLUNK_TOKEN': phases[phase].splunk_token,
      'SPLUNK_CHANNEL': phases[phase].splunk_channel,
      'SFTP_SERVER_HOST': phases[phase].sftp_server_host,
      'FTP_INSTANCE_FOLDER_CLEANUP_YN': phases[phase].ftp_instance_folder_cleanup_yn,
      'CPU_REQUEST': phases[phase].scweb_cpu_request,
      'CPU_LIMIT': phases[phase].scweb_cpu_limit,
      'MEMORY_REQUEST': phases[phase].scweb_memory_request,
      'MEMORY_LIMIT': phases[phase].scweb_memory_limit,
      'DATABASE_VERSION': `${db_version}`,
      'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
      'REPLICAS': phases[phase].replicas
    }
   }))
 
   
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-mocksvc-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'LOG_LEVEL': phases[phase].log_level,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-disputesvc-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'HAPROXY_WHITELIST': phases[phase].haproxy_whitelist,
       'HAPROXY_TIMEOUT': phases[phase].haproxy_timeout,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'LOG_LEVEL': phases[phase].log_level,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-eventsvc-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'LOG_LEVEL': phases[phase].log_level,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-rcmpadapter-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'RCMPADAPTER_ROUTE_HOST': phases[phase].rcmpadapter_route_host,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'LOG_LEVEL': phases[phase].log_level,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-jiadapter-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'LOG_LEVEL': phases[phase].log_level,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'JUSTIN_INTERFACE_ACCESS_TOKEN_URI': phases[phase].justin_interface_access_token_uri,
       'JUSTIN_INTERFACE_TICKET_DISPUTE_URI': phases[phase].justin_interface_ticket_dispute_uri,
       'JUSTIN_INTERFACE_STATUS_UPDATE_URI': phases[phase].justin_interface_status_update_uri,
       'JUSTIN_INTERFACE_DISPUTE_COURT_RESULTS_URI': phases[phase].justin_interface_dispute_court_results_uri,
       'JUSTIN_INTERFACE_DISPUTE_COURT_RESULTS_NOTIFY_URI': phases[phase].justin_interface_uri_dispute_court_results_notify_process_status_uri,
       'JUSTIN_INTERFACE_MOCKYN': phases[phase].justin_interface_mockYN,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-issuancesvc-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'LOG_LEVEL': phases[phase].log_level,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-scweb-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'KEYCLOAK_URL': phases[phase].keycloak_url,
       'CONFIGURED_SCWEB_HOSTNAME': phases[phase].configured_scweb_hostname,
       'LOG_LEVEL': phases[phase].log_level,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'CPU_REQUEST': phases[phase].scweb_cpu_request,
       'CPU_LIMIT': phases[phase].scweb_cpu_limit,
       'MEMORY_REQUEST': phases[phase].scweb_memory_request,
       'MEMORY_LIMIT': phases[phase].scweb_memory_limit,
       'ICBC_SFTP_SERVER_HOST': phases[phase].icbc_sftp_server_host,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-paymentsvc-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'PAYBC_INDIVIDUAL_INVOICE_ENDPOINT_URL_PREFIX': phases[phase].paybc_individual_invoice_endpoint_url_prefix,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'PAYMENTSVC_ROUTE_HOST': phases[phase].paymentsvc_route_host,
       'LOG_LEVEL': phases[phase].log_level,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-icbcadapter-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'VERSION': phases[phase].tag,
       'PHASE': phases[phase].phase,
       'HOST': `${phases[phase].name}${phases[phase].suffix}-${phases[phase].namespace}.apps.silver.devops.gov.bc.ca`,
       'CUSTOM_ERROR_EMAILTO': phases[phase].custom_error_emailto,
       'CUSTOM_ERROR_EMAILFROM': phases[phase].custom_error_emailfrom,
       'CUSTOM_ERROR_URL_PREFIX': phases[phase].custom_error_url_prefix,
       'ICBC_PAYMENTSERVICE_ENDPOINT_URI': phases[phase].icbc_paymentservice_endpoint_uri,
       'ICBC_ISSUANCESERVICE_ENDPOINT_URI': phases[phase].icbc_issuanceservice_endpoint_uri,
       'ICBC_PINGSERVICE_ENDPOINT_URI': phases[phase].icbc_pingservice_endpoint_uri,
       'LOG_LEVEL': phases[phase].log_level,
       'SPLUNK_URL': phases[phase].splunk_url,
       'SPLUNK_TOKEN': phases[phase].splunk_token,
       'SPLUNK_CHANNEL': phases[phase].splunk_channel,
       'CPU_REQUEST': phases[phase].cpu_request,
       'CPU_LIMIT': phases[phase].cpu_limit,
       'MEMORY_REQUEST': phases[phase].memory_request,
       'MEMORY_LIMIT': phases[phase].memory_limit,
       'DATABASE_VERSION': `${db_version}`,
       'JAVA_MAX_MEM_RATIO': phases[phase].java_max_mem_ratio,
       'REPLICAS': phases[phase].replicas
     }
   }))
   objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-all-routes-deploy.json`, {
     'param':{
       'NAME': phases[phase].name,
       'SUFFIX': phases[phase].suffix,
       'HOSTNAME_SUFFIX': phases[phase].hostname_suffix,
       'HAPROXY_WHITELIST': phases[phase].haproxy_whitelist,
       'HAPROXY_TIMEOUT': phases[phase].haproxy_timeout,
       'SSG_WHITELIST': phases[phase].ssg_whitelist,
       'CONFIGURED_SCWEB_HOSTNAME': phases[phase].configured_scweb_hostname_standby,
       'CONFIGURED_RIDEWEB_HOSTNAME': phases[phase].configured_rideweb_hostname_standby,
       'RCMPADAPTER_ROUTE_HOST': phases[phase].rcmpadapter_route_host_standby,
       'PAYMENTSVC_ROUTE_HOST': phases[phase].paymentsvc_route_host_standby,
       'CONFIGURED_RIDESVC_HOSTNAME': phases[phase].configured_ridesvc_hostname_standby
     }
   }))
 
   

  /*
  
  
  // skip testsvc deployment in stage and prod environment
   if (!(phase == "stage" || phase == "prod")) {
     objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-etk-testsvc-deploy.yaml`, {
       'param':{
         'NAME': phases[phase].name,
         'SUFFIX': phases[phase].suffix,
         'VERSION': phases[phase].tag,
         'PHASE': phases[phase].phase,
         'HOSTNAME_SUFFIX': phases[phase].hostname_suffix,
         'LOG_LEVEL': phases[phase].log_level,
         'CPU_REQUEST': phases[phase].cpu_request,
         'CPU_LIMIT': phases[phase].cpu_limit,
         'MEMORY_REQUEST': phases[phase].memory_request,
         'MEMORY_LIMIT': phases[phase].memory_limit,
         'REPLICAS': phases[phase].replicas
       }
     }))
   }

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appa-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appb-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appc-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-kafka-appd-deploy.json`, {
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
  

  

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-ridesvc-deploy.json`, {
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

  objects.push(...oc.processDeploymentTemplate(`${templatesLocalBaseUrl}/jh-cmn-rideweb-deploy.json`, {
  'param':{
    'SUFFIX': phases[phase].suffix,
    'VERSION': phases[phase].tag,
    'PHASE': phases[phase].phase,
    'CPU_REQUEST': phases[phase].cpu_request,
    'CPU_LIMIT': phases[phase].cpu_limit,
    'MEMORY_REQUEST': phases[phase].memory_request,
    'MEMORY_LIMIT': phases[phase].memory_limit,
    'VUE_APP_ETK_COMPONENT_STATUS_ENDPOINT': `https://${phases[phase].configured_ridesvc_hostname}/apps/etk/componentStatus`,
    'VUE_APP_ETK_SCWEB_ENDPOINT': `https://${phases[phase].configured_scweb_hostname}`,
    'VUE_APP_ETK_SSO_ADMIN_ENDPOINT': phases[phase].sso_admin_endpoint,
    'VUE_APP_ETK_SPLUNK_DASHBOARD_URL': phases[phase].etk_splunk_dashboard_url,
    'VUE_APP_OPENSHIFT_URL': phases[phase].openshift_url
  }
  }))
	*/

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
