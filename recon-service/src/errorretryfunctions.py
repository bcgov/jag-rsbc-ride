import json
import os
from producerapifuncs import producerAPITasks

def error_retry_task(dbclient,err_staging_collection,err_table_collection,err_threshold,logger):

    #DONE: Query error staging
    results = err_staging_collection.find()
    errretrystatus = True
    # logger.info(f'found {len(list(results))} rows in error staging table')
    #DONE: For each do below
    for row in results:
        logger.debug('processing row')
        logger.debug(row)
        try:
            # if row['event_type'] and row['event_type'] == 'event_1':
            if row['eventType'] :
                # DONE: Retry sending to producer api
                payload_json=row['payloaddata']
                producer_api_obj=producerAPITasks(os.getenv('PRODUCER_API_HOST'),logger)
                headers={'ride-api-key':os.getenv('RIDE_API_KEY'),'Content-Type':'application/json'}
                logger.debug(payload_json)
                # print(payload_json)
                success=producer_api_obj.sendAPIReq(row['apipath'],headers,'POST',payload_json)
                # apiendpoint=f"{os.getenv('PRODUCER_API_HOST')}{row['apipath']}"
                # print(apiendpoint)
                # headers = {'Authorization': 'Bearer <access_token>'}
                # success=False
                # DONE: if success delete from staging table
                if success:
                    err_staging_collection.delete_one(row)
                # DONE: if error update error count
                else:
                    errretrystatus = False
                    retry_count_val = (lambda x: 1 if not ('retry_count' in x.keys()) else x['retry_count'] + 1)(row)
                    # DONE: if error count more than 10 then delete from staging and move to main error table
                    if retry_count_val>int(err_threshold):
                        err_staging_collection.delete_one(row)
                        # DONE: Dedup before saving to error table
                        query_err_table = err_table_collection.find(row)
                        if len(list(query_err_table)) > 0:
                            return True
                        else:
                            result = err_table_collection.insert_one(row)
                            return True
                    else:
                        new_column = {"$set": {"retry_count": retry_count_val}}
                        result = err_staging_collection.update_one(row, new_column)
        except Exception as e:
            errretrystatus=False
            logger.error('error in retrying for this row')
            logger.error(row)
            logger.error(e)

    return errretrystatus