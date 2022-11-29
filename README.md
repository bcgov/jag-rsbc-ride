

[![Lifecycle:Experimental](https://img.shields.io/badge/Lifecycle-Experimental-339999)](<Redirect-URL>)

# RIDE
RIDE (RoadSafetyBC Integrated Digital Ecosystem) is the technical name for a suite of microservices that implement RoadSafetyBC's RIDE application.  
This repository contains the code, the how-to guides, and the build and deployment scripts needed to develop and run RIDE.

## 1. RIDE Application Context Diagram

Source: JPSS / ISB Wiki - [RoadSafetyBC RIDE-based Integration (Current and Possible Future State)](https://justice.gov.bc.ca/wiki/pages/viewpage.action?pageId=301400122)

## 2. RIDE Key URLs


## 3. RIDE Component Types

* **Business Logic Service Component**: encapsulates the business logic of a given functionality (i.e.
defines the what).
* **Integration Adaptor Component**: contains the necessary integration logic required to interface
with a given partner system (i.e. the how)
* **Web Interface Component**: dashboarding, error mgmt., reports, admin tools & public facing portals (future).

## 4. RIDE Components Defined

## 5. Branch Structure

## 6. Development flow


## 7. Component Build and Deploy Status    

### <b>RIDE Producer API</b>

<u>Build Status</u>

[![Build and Push To Dev](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_devdeploy.yml/badge.svg)](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_devdeploy.yml)

[![Build and Push To Test](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_testdeploy.yml/badge.svg)](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_testdeploy.yml)

[![Build and Push To Prod](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_proddeploy.yml/badge.svg)](https://github.com/bcgov/jag-rsbc-ride/actions/workflows/build_push_pr_onopen_proddeploy.yml)

<u>Dev Deployment</u>   
![Deploy to Dev](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-producer-api-dev&revision=true)

<u>Test Deployment</u>  
![Deploy to Test](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-producer-api-test&revision=true)


<u>PROD Deployment</u>    
![Deploy to Prod](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-producer-api-prod&revision=true)


### <b>Kafka Cluster Components</b>  

<u>Dev Deployment</u>   
![Deploy to Dev](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-infrastructure-dev&revision=true)

<u>Test Deployment</u>  
![Deploy to Test](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-infrastructure-test&revision=true)


<u>PROD Deployment</u>    
![Deploy to Prod](https://argocd-shared.apps.silver.devops.gov.bc.ca/api/badge?name=be5301-ride-infrastructure-test&revision=true)

