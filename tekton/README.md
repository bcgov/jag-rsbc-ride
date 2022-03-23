## Directory Structure

| Folder | Content |
| --- | --- |
| pipeline/ | Contains the RIDE specific Openshift pipeline definitions, they are <ul><li>**tekton-pipeline-ride.yaml**: builds new images for the RIDE components and updates the ArgoCD deployment metadata in [tenant-gitops-be5301 repository](https://github.com/bcgov-c/tenant-gitops-be5301/tree/deployment/jag-rsbc-ride)</li><li>**tekton-pipeline-cleanup.yaml**: cleanups the assets (including pods, statefulsets, pvc, etc) that build up the associated PR environment.</li></ul> |
| secrets/ | Contains the definition that creates the secrets required by RIDE CI/CD tools |
| tasks/ | Contains RIDE specific tasks used in RIDE Openshift pipelines. They are: <ul><li>**buildah-with-resource-setting.yaml**: similar to [t-buildah](https://console.apps.silver.devops.gov.bc.ca/k8s/ns/be5301-tools/tekton.dev~v1beta1~Task/t-buildah) task, but with overwritten default pod resource (i.e., memory and cpu) allocation.</li><li>**t-github-open-pr.yaml**: it's used to create a pull request in [tenant-gitops-be5301 repository](https://github.com/bcgov-c/tenant-gitops-be5301/tree/deployment/jag-rsbc-ride). Upon merging PR, it will trigger a RIDE PROD deployment.</li><li>**t-ride-git-cli.yaml**: custom task that updates dev, test, and prod deployment manifest in [tenant-gitops-be5301 repository](https://github.com/bcgov-c/tenant-gitops-be5301/tree/deployment/jag-rsbc-ride)</li><li>**t-ride-yp-03.yaml**: custom task that updates dev, test, and prod deployment manifest within the pipeline workspace. </li></ul> |
| triggers/ | Contains the RIDE specific triggers definition that integrates the Github events and the RIDE openshift pipelines, they are: <ul><li>**route.yaml**: route definition that creates the route to allow github integration with ride-build-and-deploy pipeline. </li><li>**route-cleanup.yaml**: route definition that creates the route to allow github integration with ride-pipeline-run-cleanup pipeline.</li><li>**trigger-ride.yaml**:  definition that creates the TriggerBinding, EventListener and PipelineRun for ride-build-and-deploy pipeline. </li><li>**trigger-ride-cleanup.yaml**: definition that creates the TriggerBinding, EventListener and PipelineRun for ride-pipeline-run-cleanup pipeline.</li></ul> |


## Related tickets 

 * [JHI-2927 - Decide on Jenkins alternatives, e.g., Openshift pipeline, Github Actions, + ArgoCD](https://justice.gov.bc.ca/jirarsi/browse/JHI-2927) 
 * [JHI-2896 - Setup Openshift pipeline (Tekton) and ArgoCD on be5301-tools namespace](https://justice.gov.bc.ca/jirarsi/browse/JHI-2896) 


## Relevant reading material
### [BCDevOps Openshift Pipeline (Tekton) documentation](https://github.com/bcgov/pipeline-templates/tree/main/tekton)
### [BCDevOps ArgoCD documentation](https://github.com/BCDevOps/openshift-wiki/tree/b1a4e6db91932fd3f29705a5c8ee44983abf8763/docs/ArgoCD)
