import groovy.json.*

class OnGhEvent extends Script {

static Map exec(List args, File workingDirectory=null, Appendable stdout=null, Appendable stderr=null, Closure stdin=null){
    ProcessBuilder builder = new ProcessBuilder(args as String[])
    if (stderr ==null){
        builder.redirectErrorStream(true)
    }
    if (workingDirectory!=null){
        builder.directory(workingDirectory)
    }
    def proc = builder.start()

    if (stdin!=null) {
        OutputStream out = proc.getOutputStream();
        stdin(out)
        out.flush();
        out.close();
    }

    if (stdout == null ){
        stdout = new StringBuffer()
    }

    proc.waitForProcessOutput(stdout, stderr)
    int exitValue= proc.exitValue()

    Map ret = ['out': stdout, 'err': stderr, 'status':exitValue, 'cmd':args]

    return ret
}

    def run() {
        String ghPayload = build.buildVariableResolver.resolve("payload")
        String ghEventType = build.buildVariableResolver.resolve("x_github_event")
        String buildNumber = build.getNumber()
        String fullName = build.getProject().getFullName()

        println "GitHub Event: ${ghEventType}"

        //println "ghPayload:"
        //println "${ghPayload}"

        //binding.variables.each{ 
        //  println "${it.key}:${it.value}"
        //}
        File workDir = new File("/tmp/jenkins/on-gh-event/${fullName}/${buildNumber}")
        try{
            if ("pull_request" == ghEventType){
                def payload = new JsonSlurper().parseText(ghPayload)
                println "GitHub Action: ${payload.action}"
                if ("closed" == payload.action){
                    //File gitWorkDir = workDir
                    def ghRepo=com.cloudbees.jenkins.GitHubRepositoryName.create(payload.repository.clone_url).resolveOne()
                    //boolean isFromCollaborator=ghRepo.root.retrieve().asHttpStatusCode(ghRepo.getApiTailUrl("collaborators/${payload.pull_request.user.login}")) == 204
                    String cloneUrl = payload.repository.clone_url
                    //String sourceBranch = isFromCollaborator?"refs/pull/${payload.number}/head":"refs/heads/${payload.pull_request.base.ref}"
                    String repoName = payload.repository.name
                    String repoOwner = payload.repository.owner.login

                    //println "Is Collaborator:${isFromCollaborator} (${payload.pull_request.user.login})"
                    println "Login user: (${payload.pull_request.user.login})"
                    println "Clone Url:${cloneUrl}"
                    //println "Checkout Branch:${sourceBranch}"

                    String selector = "env-id=${payload.number},env-name!=prod,env-name!=test,env-name!=stage,env-name!=dev,github-owner=${repoOwner},github-repo=${repoName},!shared"

                    def ocGetProjects = exec(['oc','get','projects','-l','environment!=prod,environment!=test', '-o', 'custom-columns=name:.metadata.name', '--no-headers'])

                    ocGetProjects.out.toString().trim().split('\n').each({ namespace ->
                        //BuildConfig Output Images
                        def ocGetBcRet = exec(['oc',"--namespace=${namespace}",'get','bc','-l',selector, '-o', 'jsonpath={range .items[*]}{.spec.output.to.namespace}/{.spec.output.to.name}{"\\n"}{end}'])
                        println ocGetBcRet
                        if (ocGetBcRet.status == 0 ){
                            ocGetBcRet.out.toString().trim().split('\n').each({ item ->
                                if (item.length() > 3){
                                    def reference=item.split('/')
                                    if (reference[0].length()==0){
                                        reference[0]=namespace
                                    }
                                    println exec(['oc', "--namespace=${reference[0]}", 'tag', "${reference[1]}", '--delete=true'])
                                }
                            })
                        }

                        //DeploymentConfig Images
                        def ocGetDcRet = exec(['oc',"--namespace=${namespace}",'get','dc','-l',selector, '-o', 'jsonpath={range .items[*]}{range .spec.triggers[*]}{.imageChangeParams.from.namespace}/{.imageChangeParams.from.name}{"\\n"}{end}{end}'])
                        println ocGetDcRet
                        if (ocGetDcRet.status == 0 ){
                            ocGetDcRet.out.toString().trim().split('\n').each({ item ->
                                if (item.length() > 3){
                                    def reference=item.split('/')
                                    if (reference[0].length()==0){
                                        reference[0]=namespace
                                    }
                                    println exec(['oc', "--namespace=${reference[0]}", 'tag', "${reference[1]}", '--delete=true'])
                                }
                            })
                        }

                        //oc get dc -l 'env-id=pr-19,env-name!=prod' -o 'jsonpath={range .items[*]}{range .spec.triggers[*]}{.imageChangeParams.from.namespace}/{.imageChangeParams.from.name}{"\n"}{end}{end}'
                        println exec(['oc', "--namespace=${namespace}", 'delete', 'all', '-l', selector])
                        println exec(['oc', "--namespace=${namespace}", 'delete', 'pvc,Secret,configmap,Endpoints,RoleBinding,role,ServiceAccount', '-l', selector])
                    
                        // Delete db ConfigMaps, e.g., jh-etk-db-pr-646-config and jh-etk-db-pr-646-leader. 
                        // Their labels only contain app.kubernetes.io/name=patroni and cluster-name=jh-etk-db-pr-646, 
                        // so the selector above failed to include them
                        String selectorDBCM = "app.kubernetes.io/name=patroni,cluster-name=jh-etk-db-pr-${payload.number}"
                        println exec(['oc', "--namespace=${namespace}", 'delete', 'configmap', '-l', selectorDBCM])
                    })
                }
            }else if ("issue_comment" == ghEventType){
                def payload = new JsonSlurper().parseText(ghPayload)
                println "GitHub Action: ${payload.action}"
                if ("created" == payload.action && payload.issue.pull_request !=null ){
                    String comment = payload.comment.body.trim()

                    //OWNER or COLLABORATOR
                    //https://developer.github.com/v4/enum/commentauthorassociation/
                    String commentAuthorAssociation = payload.comment.author_association
                    if (comment.charAt(0) == '/'){
                        println "command: ${comment}"
                        String jobName= payload.repository.name
                        String jobPRName =  payload.repository.full_name

                        List projects = jenkins.model.Jenkins.instance.getAllItems(org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject.class).findAll {
                            def scmSource=it.getSCMSources()[0]
                            return payload.repository.owner.login.equalsIgnoreCase(scmSource.getRepoOwner()) && payload.repository.name.equalsIgnoreCase(scmSource.getRepository())
                        }
                        List branchProjects = []
                        projects.each {
                            def branchProject = it.getItem("PR-${payload.issue.number}")
                            if (branchProject!=null){
                                branchProjects.add(branchProject)
                            }
                        }

                        if (comment == '/restart' && (commentAuthorAssociation == 'OWNER' || commentAuthorAssociation == 'COLLABORATOR')){
                            //
                            branchProjects.each {
                                def targetProject=it
                                def cause = new hudson.model.Cause.RemoteCause('github.com', "Pull Request Command By '${payload.comment.user.login}'")
                                targetProject.scheduleBuild(0, cause)
                            }
                        }else if (comment == '/approve' && (commentAuthorAssociation == 'OWNER' || commentAuthorAssociation == 'COLLABORATOR')){
                            if (branchProjects.size() > 0){
                                branchProjects.each { targetJob ->
                                    if (targetJob.getLastBuild()){
                                        hudson.security.ACL.impersonate(hudson.security.ACL.SYSTEM, {
                                            for (org.jenkinsci.plugins.workflow.support.steps.input.InputAction inputAction : targetJob.getLastBuild().getActions(org.jenkinsci.plugins.workflow.support.steps.input.InputAction.class)){
                                                for (org.jenkinsci.plugins.workflow.support.steps.input.InputStepExecution inputStep:inputAction.getExecutions()){
                                                    if (!inputStep.isSettled()){
                                                        println inputStep.proceed(null)
                                                    }
                                                }
                                            }
                                        } as Runnable )
                                    }
                                }
                            }else{
                                println "There is no project or build associated with ${payload.issue.pull_request.html_url}"
                            }
                        }
                    }
                }
            }
        }finally{
            exec(['rm', '-rf', workDir.getAbsolutePath()])
        }


        return null;
    } //end run
    
    static void main(String[] args) {
        org.codehaus.groovy.runtime.InvokerHelper.runScript(OnGhEvent, args)     
    }
}
