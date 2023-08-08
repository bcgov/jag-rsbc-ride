# API Endpoints for Recon Service  

These are the endpoints provided by the service along with brief explanation. The Postman collection can be found [Here](https://bcgov.sharepoint.com/:f:/r/teams/01606/Shared%20Documents/DF%20Application%20Development/2-%20General%20Docs/RIDE_docs/postman_collections/recon_api?csf=1&web=1&e=bUDCdA)  


### Save to main staging table (/savemainstaging)  

This endpoint is used to save records in the main staging table.  

#### Payload Format  (POST)

```

{
    "apipath": "<producer-api-path-for-event>",
    "payloaddata": <actual payload for the event>,
    "datasource": "df/etk",
    "eventType": <event type name>,
    "eventid":""
}

```  

### Save to error staging table (/saveerrorstaging)  

This endpoint is used to save records in the error staging table.  

#### Payload Format  (POST)

```

{
    "apipath": "<producer-api-path-for-event>",
    "payloaddata": <actual payload for the event>,
    "datasource": "df/etk",
    "eventType": <event type name>,
    "eventid":"",
    "errorcategory":"data_issue or others",
    "errorType":"source of error service like producer_api",
    "errorReason":"error message",
    "messageStatus":"status of the event for e.g consumer_error"
}

```    

### Update event records (/updateevent/<event_id>)  

This endpoint is used to update event rows in the recon db collections. The event id to be updated is passed as URL param to the API call.   

#### Payload Format  (PATCH)

```

{
    "collectionName": "name of collection where the event record resides",
    "payloaddata": {
        "column_name":"value_to_update"
    }
}

```      

### Trigger recon process (/riderecon)  

This endpoint is used to trigger the recon process for the events. This is a GET request to the endpoint.  



### Trigger Error Retry process (/rideerrorretry)  

This endpoint triggers the retry process. This process retries all failed events. This is a GET request to the endpoint.  


### Generate Metrics (/genmetrics)  

This endpoint triggers the  generation of Prometheus compatible metrics for monitoring the message counts. This is a GET endpoint  

### Query Recon DB collections (/querytable)  

This endpoint is used to query records from the different collextions in Recon DB. This is the format for the endpoint:  

```
>api_host>/querytable?collection_name=<name_of_collection>&<column_name=<value>&<column_name=<value>  
```  

This is a GET request.  