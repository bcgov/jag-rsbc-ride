# Connect to Recon Service API from local system  

The Recon service endpoints are only available within cluster. To work with the API locally, port forward needs to be done so the APi can be invoked locally.    

### Pre-Requisites  
To be able to do the port forward oc cli need to be installed in the system. Install oc cli before performing the steps.  

### Login to Openshift  

Login to Open shift console to get the login command. Run the command to login to Openshift from local system.  

### Port forward  

Change project to the specific namespace for which recon service need to be connected  

```  
oc project <project_name>  
```  

Run this command to complete the port forward  

```  
oc -n <project-name> port-forward service/ride-recon-svc-clusterip-dev 5000  
```  

The API will now be available at: http://localhost:5000