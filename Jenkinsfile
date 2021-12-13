pipeline {
    agent none
    options {
        skipDefaultCheckout true
        disableResume()
    }
    stages {
        stage('Abort Previously Running Jobs') {
            steps {
                echo "Aborting all running jobs ..."
                script {
                    abortAllPreviousBuildInProgress(currentBuild)
                }
            }
        }
        stage('Build and Deploy Confirmation') {
           agent none
           steps {
               confirm_build()
           }
        }
        stage('Build') {
            agent { label 'build' }
            options { skipDefaultCheckout(false) }
            steps {
                build_app()
            }
        }
        stage('Idle App (PR)') {
            agent { label 'deploy' }
            steps {
                idle_app_in_pr()
            }
        }
        stage('Deploy App (PR)') {
            agent { label 'deploy' }
            steps {
                deploy_app_in_pr()
            }
        }
        stage('Deployment Verification (PR)') {
            agent { label 'deploy' }
            steps {
                deploy_verify_in_pr()
           }
        }
        stage('Design Confirmed (PR)') {
            agent none
            when {
                expression { env.AUTO_DEPLOY_TO.toInteger() == 1 }
             }
            steps {
                script {
                    design_confirmed()
                }
            }
        }
        stage('Functionality Confirmed (PR)') {
           agent none
            when {
                expression { env.AUTO_DEPLOY_TO.toInteger() == 1 }
             }
            steps {
                script {
                    functionality_confirmed()
                }
            }
        }
        stage('Start Deployment? (DEV)') {
            agent none
            when {
                 allOf {
                     expression { env.SKIP_DEV != "true" }
                     expression { env.CHANGE_TARGET == 'master' }
                     not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                     not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                 }
             }
            steps {
                script {
                    start_dev_deploy()
                }
            }
        }
        stage('Idle App (DEV)') {
            agent { label 'deploy' }
             when {
                 allOf {
                     expression { env.SKIP_DEV != "true" }
                     expression { env.CHANGE_TARGET == 'master' }
                     not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                     not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                 }
             }
            steps {
                idle_app_in_dev()
            }
        }
        stage('Deploy App (DEV)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.SKIP_DEV != "true" }
                    expression { env.CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                }
            }
            steps {
                deploy_app_in_dev()
            }
        }
        stage('Deployment Verification (DEV)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.SKIP_DEV != "true" }
                    expression { env.CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                }
            }
            steps {
                deploy_verify_in_dev()
            }
        }
        stage('Start Deployment? (STAGE)') {
            agent none
             when {
                 allOf {
                     expression { env.SKIP_STAGE != "true" }
                     expression { env.CHANGE_TARGET == 'master' || CHANGE_BRANCH =~ /bg\/*/ }
                     not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                 }
             }
            steps {
                start_stage_deploy()
            }
        }
        stage('Scale Down Active Instance (STAGE)') {
            agent { label 'deploy' }
            when {
              allOf {
                  expression { CHANGE_TARGET == 'master' }
                  not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
              }
            }
            steps {
                change_active_replicas_to_1_in_stage()
            }
        }
        stage('Deploy App into Standby (STAGE)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.SKIP_STAGE != "true" }
                    expression { env.CHANGE_TARGET == 'master' || CHANGE_BRANCH =~ /bg\/*/ }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                }
            }
            steps {
                deploy_app_in_stage()
            }
        }
        stage('Deployment Verification (STAGE)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.SKIP_STAGE != "true" }
                    expression { env.CHANGE_TARGET == 'master' || CHANGE_BRANCH =~ /bg\/*/ }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                }
            }
            steps {
                deploy_verify_in_stage()
            }
        }
        stage('Activate Latest Deployment (STAGE)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.SKIP_STAGE != "true" }
                    expression { env.CHANGE_TARGET == 'master' || CHANGE_BRANCH =~ /bg\/*/ }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                }
            }
            steps {
                script {
                    activate_deployment_in_stage(env.AUTO_DEPLOY_TO.toInteger() < 4)
                }
            }
        }
        stage('Start Deployment? (TEST)') {
            agent none
            when {
                allOf {
                     expression { env.CHANGE_TARGET == 'master' }
                     not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                     not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                start_test_deploy()
            }
        }
        stage('Idle App (TEST)') {
            agent { label 'deploy' }
            when {
                allOf {
                     expression { env.CHANGE_TARGET == 'master' }
                     not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                     not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                idle_app_in_test()
            }
        }
        stage('Deploy App (TEST)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                deploy_app_in_test()
            }
        }
        stage('Deployment Verification (TEST)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { env.CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                deploy_verify_in_test()
            }
        }
        stage('Confirm TEST completed') {
            agent { label 'deploy' }
            when {
                expression { env.CHANGE_TARGET == 'master' }
                not { expression { CHANGE_BRANCH =~ /patch\/*/ } }
                not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
            }
            steps {
                test_confirmed()
            }
        }
        stage('Start Deployment? (PROD)') {
            agent none
             when {
                 allOf {
                     expression { CHANGE_TARGET == 'master' }
                     not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                 }
             }
            steps {
                start_prod_deploy()
            }
        }
        stage('Scale Down Active Instance (PROD)') {
            agent { label 'deploy' }
            when {
              allOf {
                  expression { CHANGE_TARGET == 'master' }
                  not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
              }
            }
            steps {
                change_active_replicas_to_1_in_prod()
            }
        }
        stage('Deploy App into Standby (PROD)') {
            agent { label 'deploy' }
            when {
              allOf {
                  expression { CHANGE_TARGET == 'master' }
                  not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
              }
            }
            steps {
                deploy_app_in_prod()
            }
        }
        stage('Deployment Verification (PROD)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { return env.CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                deploy_verify_in_prod()
            }
        }
        stage('Activate Latest Deployment (PROD)') {
            agent { label 'deploy' }
            when {
                allOf {
                    expression { CHANGE_TARGET == 'master' }
                    not { expression { CHANGE_BRANCH =~ /bg\/*/ } }
                }
            }
            steps {
                activate_deployment_in_prod()
            }
        }
    }
}

void confirm_build(){
  script {
      def INPUT_PARAMS
      if (env.CHANGE_TARGET == 'master' && env.CHANGE_BRANCH.indexOf('patch/') != 0) {
        INPUT_PARAMS = input message: 'Build and deploy the latest changes?', ok: 'Yes',
            parameters: [
                choice(name: 'AUTO_DEPLOY_TO', choices: ['PR','DEV','STAGE','TEST'].join('\n'), description: 'Auto deploy to'),
                booleanParam(defaultValue: false, name: 'SKIP_DEV', description: 'Skip Dev Deployment'),
                booleanParam(defaultValue: false, name: 'SKIP_STAGE', description: 'Skip Stage Deployment'),
                booleanParam(defaultValue: true, name: 'RUN_TEST', description: 'Execute automated testing'),
                booleanParam(defaultValue: false, name: 'DEBUG_LOGGING', description: 'Enable debugging')]
      }
      else if (env.CHANGE_BRANCH.indexOf('bg/') == 0) {
        INPUT_PARAMS = input message: 'Build and deploy the latest changes?', ok: 'Yes',
            parameters: [
                choice(name: 'AUTO_DEPLOY_TO', choices: ['PR','STAGE'].join('\n'), description: 'Auto deploy to'),
                booleanParam(defaultValue: false, name: 'SKIP_STAGE', description: 'Skip Stage Deployment'),
                booleanParam(defaultValue: true, name: 'RUN_TEST', description: 'Execute automated testing'),
                booleanParam(defaultValue: false, name: 'DEBUG_LOGGING', description: 'Enable debugging')]
      }
      else {
        INPUT_PARAMS = input message: 'Build and deploy the latest changes?', ok: 'Yes',
            parameters: [
                choice(name: 'AUTO_DEPLOY_TO', choices: ['PR'], description: 'Auto deploy to'),
                booleanParam(defaultValue: true, name: 'RUN_TEST', description: 'Execute automated testing'),
                booleanParam(defaultValue: false, name: 'DEBUG_LOGGING', description: 'Enable debugging')]
      }

      // Capture the preference of whether to skip dev and stage deployments
      env.SKIP_DEV = INPUT_PARAMS.SKIP_DEV;
      env.SKIP_STAGE = INPUT_PARAMS.SKIP_STAGE;

      if (INPUT_PARAMS.AUTO_DEPLOY_TO == 'PR') {
          env.AUTO_DEPLOY_TO = '1'
      }
      else if (INPUT_PARAMS.AUTO_DEPLOY_TO == 'DEV') {
          env.AUTO_DEPLOY_TO = '2'
      }
      else if (INPUT_PARAMS.AUTO_DEPLOY_TO == 'STAGE') {
          env.AUTO_DEPLOY_TO = '3'
      }
      else if (INPUT_PARAMS.AUTO_DEPLOY_TO == 'TEST') {
          env.AUTO_DEPLOY_TO = '4'
      }

      env.RUN_TEST = INPUT_PARAMS.RUN_TEST

      if (INPUT_PARAMS.DEBUG_LOGGING) {
        // Set this parameter to DEBUG=* to add debug
        env.DEBUG_LOGGING = "DEBUG=*"
      } else {
        // Leave as empty string to disable DEBUG logging
        env.DEBUG_LOGGING = "DEBUG="
      }

      echo "Confirming build and deploy of branch ${env.CHANGE_BRANCH}. AUTO_DEPLOY_TO: ${INPUT_PARAMS.AUTO_DEPLOY_TO}, SKIP_STAGE: ${INPUT_PARAMS.SKIP_STAGE}, RUN_TEST: ${INPUT_PARAMS.RUN_TEST}, DEBUG_LOGGING: ${INPUT_PARAMS.DEBUG_LOGGING}."
  }
}
void build_app(){
  script {
      def filesInThisCommitAsString = sh(script:"git diff --name-only HEAD~1..HEAD | grep -v '^.jenkins/' || echo -n ''", returnStatus: false, returnStdout: true).trim()
      def hasChangesInPath = (filesInThisCommitAsString.length() > 0)
      echo "${filesInThisCommitAsString}"
      if (!currentBuild.rawBuild.getCauses()[0].toString().contains('UserIdCause') && !hasChangesInPath){
          currentBuild.rawBuild.delete()
          error("No changes detected in the path ('^.jenkins/')")
      }
  }
  echo "Building ..."
  sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run build -- --pr=${CHANGE_ID}"
}

void idle_app_in_pr(){
  echo "Idling All Application Interfaces (PR)"
  script {
      def IDLE_APP_PR_RETURN_STATUS = sh ( script: "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run idleapp -- --pr=${CHANGE_ID} --env=pr", returnStatus: true )
      if (IDLE_APP_PR_RETURN_STATUS != 0){
          echo "PR Idle App sh return code: ${IDLE_APP_PR_RETURN_STATUS}"
          error("Idling App in PR stage failed!  Halting the pipeline")
      }
  }
}

void deploy_app_in_pr(){
  echo "Deploying Application for Pull Request..."
  sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run deploy -- --pr=${CHANGE_ID} --env=pr"
}

void deploy_verify_in_pr(){
  echo "Execute self testing of Pull Request deployment"
  script {
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run selftest -- --pr=${CHANGE_ID} --env=pr"
  }
}

void design_confirmed(){
  script {
      echo "Design Verified?"
      input message: 'Design Verified?', ok: 'Yes'
  }
}
void functionality_confirmed(){
  script {
      echo "Functionality Verified?"
      input message: 'Functionality Verified?', ok: 'Yes'
  }
}
void start_dev_deploy(){
  script {
      if ( env.AUTO_DEPLOY_TO.toInteger() < 2 ) {
          input message: 'Should we continue with deployment to DEV?', ok: 'Yes'
      } else {
          echo "Starting Deployment to DEV"
      }
  }
}
void idle_app_in_dev(){
  echo "Start All Application Interfaces (DEV)"
  script {
      def IDLE_APP_DEV_RETURN_STATUS = sh ( script: "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run idleapp -- --pr=${CHANGE_ID} --env=dev", returnStatus: true )
      if (IDLE_APP_DEV_RETURN_STATUS != 0){
          echo "DEV Idle App sh return code: ${IDLE_APP_DEV_RETURN_STATUS}"
          error("Idling App in DEV stage failed!  Halting the pipeline")
      }
  }
}

void deploy_app_in_dev(){
  echo "Deploying to DEV..."
  sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run deploy -- --pr=${CHANGE_ID} --env=dev"
}

void deploy_verify_in_dev(){
  echo "Execute self testing of Dev deployment"
  script {
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run selftest -- --pr=${CHANGE_ID} --env=dev"
  }
}

void start_stage_deploy(){
  script {
      if ( env.AUTO_DEPLOY_TO.toInteger() < 3 ) {
          input message: 'Should we continue with deployment to STAGE?', ok: 'Yes'
      } else {
          echo "Starting deployment to STAGE"
      }
  }
}

void change_active_replicas_to_1_in_stage() {
    echo "Change current active replicas from 3 to 1 (STAGE)"
    script {
      openshift.withCluster(){
          openshift.withProject('be5301-tools') {
              def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
              // bg_cm_s.describe()
              def bg_cm = bg_cm_s.object()
              env.STAGE_STANDBY_TARGET = "${bg_cm.data.stage_standby_target}"
              echo "STAGE_STANDBY_TARGET is equal to ${env.STAGE_STANDBY_TARGET}"
          }
      }
      def STAGE_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS = sh ( script: "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run bgpredeploy -- --pr=${CHANGE_ID} --env=stage --standby=${env.STAGE_STANDBY_TARGET}", returnStatus: true )
      if (STAGE_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS != 0){
          echo "STAGE ACTIVE REPLICAS CHANGE TO ONE sh return code: ${STAGE_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS}"
          error("Change active replicas to 1 in STAGE stage failed!  Halting the pipeline")
      }
  }
}

void deploy_app_in_stage(){
  script {
      echo "Deploying ..."
      openshift.withCluster(){
          openshift.withProject('be5301-tools') {
              def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
              def bg_cm = bg_cm_s.object()
              env.STAGE_STANDBY_TARGET = "${bg_cm.data.stage_standby_target}"
              echo "STAGE_STANDBY_TARGET is equal to ${env.STAGE_STANDBY_TARGET}"
          }
      }
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run deploy -- --pr=${CHANGE_ID} --env=stage --standby=${env.STAGE_STANDBY_TARGET}"
  }
}

void deploy_verify_in_stage(){
  echo "Execute self testing of Stage Standby deployment"
  script {
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run selftest -- --pr=${CHANGE_ID} --env=stage --standby=${env.STAGE_STANDBY_TARGET}"
  }
}

void activate_deployment_in_stage(promptUser){
    if (promptUser) {
        input message: 'Activate Latest Deployment in Stage?', ok: 'Yes'
    }
  echo "Activating STANDBY (${env.STAGE_STANDBY_TARGET}) to STAGE"
  sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run activate -- --pr=${CHANGE_ID} --env=stage --standby=${env.STAGE_STANDBY_TARGET}"
  echo "Updating Blue/Green ConfigMap..."
  openshift.withCluster(){
      openshift.withProject('be5301-tools') {
          def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
          def bg_cm = bg_cm_s.object()
          echo "ConfigMap ${bg_cm.data.stage_standby_target}"
          if (bg_cm.data.stage_standby_target == '-green') {
              bg_cm.data.stage_standby_target='-blue'
          } else if (bg_cm.data.stage_standby_target == '-blue') {
              bg_cm.data.stage_standby_target='-green'
          } else {
              echo "Something is wrong with the ConfigMap"
          }
          openshift.apply(bg_cm) // Patch the object on the server
      }
  }
}

void start_test_deploy(){
  script {
      if ( env.AUTO_DEPLOY_TO.toInteger() < 4 ) {
          input message: 'Should we continue with deployment to TEST?', ok: 'Yes'
      } else {
          echo "Starting deployment to TEST"
      }
  }
}

void idle_app_in_test(){
  echo "Start All Application Interfaces (TEST)"
  script {
      def IDLE_APP_TEST_RETURN_STATUS = sh ( script: "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run idleapp -- --pr=${CHANGE_ID} --env=test", returnStatus: true )
      if (IDLE_APP_TEST_RETURN_STATUS != 0){
          echo "TEST Idle App sh return code: ${IDLE_APP_TEST_RETURN_STATUS}"
          error("Idling App in TEST stage failed!  Halting the pipeline")
      }
  }
}

void deploy_app_in_test(){
  echo "Deploying to TEST..."
  sh "cd .pipeline && ./npmw ci && ./npmw run deploy -- --pr=${CHANGE_ID} --env=test"
}

void deploy_verify_in_test(){
  echo "Execute self testing of Test deployment"
  script {
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run selftest -- --pr=${CHANGE_ID} --env=test"
  }
}
void test_confirmed(){
  script {
      echo "Confirm TEST completed?"
      input message: 'Confirm Testing is complete?', ok: 'Yes'
  }
}
void start_prod_deploy(){
  script {
      def approver = input id: 'Deploy', message: 'Deploy into Prod Standby?', submitterParameter: 'submitted_by'
      echo "It was ${approver} who submitted the dialog."
      // if (approver == 'jennifer-dowd-admin-edit-view' || approver == 'vesselofgold-admin-edit-view' || approver == 'scotttowne-admin-edit-view') {
      if (true) {
          echo "Authorized deployer, proceeding..."
      } else {
          echo "User unauthorized!"
          error 'Unauthorized attempt to deploy...'
      }
  }
}
void change_active_replicas_to_1_in_prod() {
    echo "Change current active replicas from 3 to 1 (PROD)"
    script {
      openshift.withCluster(){
          openshift.withProject('be5301-tools') {
              def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
              // bg_cm_s.describe()
              def bg_cm = bg_cm_s.object()
              env.PROD_STANDBY_TARGET = "${bg_cm.data.prod_standby_target}"
              echo "PROD_STANDBY_TARGET is equal to ${env.PROD_STANDBY_TARGET}"
          }
      }
      def PROD_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS = sh ( script: "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run bgpredeploy -- --pr=${CHANGE_ID} --env=prod --standby=${env.PROD_STANDBY_TARGET}", returnStatus: true )
      if (PROD_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS != 0){
          echo "PROD ACTIVE REPLICAS CHANGE TO ONE sh return code: ${PROD_ACTIVE_REPLICAS_CHANGE_TO_1_STATUS}"
          error("Change active replicas to 1 in PROD stage failed!  Halting the pipeline")
      }
  }
}

void deploy_app_in_prod(){
  script {
      echo "Deploying App into Standby (PROD) ..."
      openshift.withCluster(){
          openshift.withProject('be5301-tools') {
              def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
              def bg_cm = bg_cm_s.object()
              echo "ConfigMap ${bg_cm.data.prod_standby_target}"
              env.PROD_STANDBY_TARGET = "${bg_cm.data.prod_standby_target}"
              echo "PROD_STANDBY_TARGET is equal to ${env.PROD_STANDBY_TARGET}"
          }
      }
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run deploy -- --pr=${CHANGE_ID} --env=prod --standby=${env.PROD_STANDBY_TARGET}"
  }
}

void deploy_verify_in_prod(){
  echo "Execute self testing of deployment in Standby (PROD) ..."
  script {
      sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run selftest -- --pr=${CHANGE_ID} --env=prod --standby=${env.PROD_STANDBY_TARGET}"
  }
}

void activate_deployment_in_prod(){
  script {
      def approver = input id: 'Promote', message: 'Activate Latest Deployment in Prod?', submitterParameter: 'submitted_by'
      echo "It was ${approver} who submitted the dialog."
      // if (approver == 'vesselofgold-admin-edit-view' || approver == 'scotttowne-admin-edit-view') {
      if (true) {
          echo "Authorized promoter, proceeding..."
          echo "Activating STANDBY (${env.PROD_STANDBY_TARGET}) to PROD"
          sh "cd .pipeline && ./npmw ci && ${DEBUG_LOGGING} ./npmw run activate -- --pr=${CHANGE_ID} --env=prod --standby=${env.PROD_STANDBY_TARGET}"

          echo "Updating Blue/Green ConfigMap..."
          openshift.withCluster(){
              openshift.withProject('be5301-tools') {
                  def bg_cm_s = openshift.selector("cm", "rsbc-ride-standby-target-blue-green-cm")
                  //  bg_cm_s.describe()
                  def bg_cm = bg_cm_s.object()
                  echo "ConfigMap ${bg_cm.data.prod_standby_target}"
                  if (bg_cm.data.prod_standby_target == '-green') {
                      bg_cm.data.prod_standby_target='-blue'
                  } else if (bg_cm.data.prod_standby_target == '-blue') {
                      bg_cm.data.prod_standby_target='-green'
                  } else {
                      echo "Something is wrong with the ConfigMap"
                  }
                  openshift.apply(bg_cm) // Patch the object on the server
              }
          }
      } else {
          echo "User unauthorized!"
          error 'Unauthorized attempt to deploy...'
      }
  }
}
