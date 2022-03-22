# RIDE
RIDE (RoadSafetyBC Integrated Digital Ecosystem) is the technical name for a suite of microservices that implement RoadSafetyBC's RIDE application.  
This repository contains the code, the how-to guides, and the build and deployment scripts needed to develop and run RIDE.

## 1. RIDE Application Context Diagram

Source: JPSS / ISB Wiki - [RoadSafetyBC RIDE-based Integration (Current and Possible Future State)](https://justice.gov.bc.ca/wiki/pages/viewpage.action?pageId=301400122)

## 2. RIDE Key URLs
* RIDE Common Console URL (Prod): https://ride.apps.silver.devops.gov.bc.ca/
* RIDE Build Pipeline (Prod): https://console.apps.silver.devops.gov.bc.ca/k8s/ns/be5301-tools/tekton.dev~v1beta1~Pipeline/ride-build-and-deploy
* RIDE Cleanup Pipeline (Prod): https://console.apps.silver.devops.gov.bc.ca/k8s/ns/be5301-tools/tekton.dev~v1beta1~Pipeline/ride-pipeline-run-cleanup
* RIDE Deployment (ArgoCD) URL: https://argocd-shared.apps.silver.devops.gov.bc.ca/

## 3. RIDE Component Types

* **Business Logic Service Component**: encapsulates the business logic of a given functionality (i.e.
defines the what).
* **Integration Adaptor Component**: contains the necessary integration logic required to interface
with a given partner system (i.e. the how)
* **Web Interface Component**: dashboarding, error mgmt., reports, admin tools & public facing portals (future).

## 4. RIDE Components Defined

This table summarizes the components that comprise the RIDE application.

| Component | Type | Description |
| --- | --- | --- |
| [rsbc-ride-kafka-evteventproducer](rsbc-ride-kafka-evteventproducer/README.md) | Business Logic Service | Accept business events sent from jh-etk system, and publish them to Kafka |
| [rsbc-ride-kafka-mockproducer](rsbc-ride-kafka-mockproducer/README.md) | Business Logic Service | Simulate kafka producer |
| [rsbc-ride-kafka-mockconsumer](rsbc-ride-kafka-mockconsumer/README.md) | Business Logic Service | Simulate kafka consumer |
| [Apache Kafka]() | --- | --- |
| [Apicurio Service Registry]() | --- | --- |

## 5. Directory Structure

    policy/                      		- Network policy file that defines how to route traffic between namespaces and pods in be5301-*
    rsbc-ride-kafka-evteventproducer/   - RIDE eVT business events producer (events published to Kafka) component folder
    rsbc-ride-kafka-mockproducer/       - RIDE mock kafka events producer component folder
    rsbc-ride-kafka-mockconsumer/       - RIDE mock kafka events consumer component folder
    tekton/            					- Folder contains RIDE specific tasks, triggers, and pipelines definition; which can be used to reproduce the pipeline.
    README.md                  			- This file


## 6. Build the app and execute unit test cases

```
mvn clean package
```