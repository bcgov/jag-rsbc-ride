
import os
from destsqldbfuncs import SqlDBFunctions
from commonutils import map_event_type_destination,map_source_db


def recondestination(dbclient,main_staging_collection,main_table_collection,logger):

    # DONE: Query records in staging table
    results = main_staging_collection.find()
    reconstatus=True

    # DONE: Query for each record in destination db(based on row type)
    for row in results:
        # DONE: If found delete from staging if not update recon count column
        logger.debug('processing row')
        logger.debug(row)
        try:
            if row['eventType']:
                bi_table_name=map_event_type_destination(row['eventType'])
                bi_db_name=map_source_db(row['datasource'])
                # print(bi_db_name)
                # DONE: Query SQL DB
                bi_sql_db_obj = SqlDBFunctions(bi_db_name, os.getenv('BI_SQL_DB_SERVER'),os.getenv('BI_SQL_DB_USERNAME'), os.getenv('BI_SQL_DB_PASSWORD'))
                qrystr = bi_sql_db_obj.prepQuerystr(row['payloaddata'],row['datasource'])
                table_name=bi_table_name
                reconqrystr = f'SELECT * FROM {table_name} WHERE {qrystr}'
                # print(reconqrystr)
                found = bi_sql_db_obj.reconQuery(reconqrystr,logger)
                if found:
                    main_staging_collection.delete_one(row)
                    # DONE: If found save to master table
                    # DONE: Dedup before saving to master
                    query_main_table = main_table_collection.find(row)
                    if len(list(query_main_table)) > 0:
                        return True
                    else:
                        result = main_table_collection.insert_one(row)
                else:
                    recon_count_val = (lambda x: 1 if not ('recon_count' in x.keys()) else x['recon_count'] + 1)(row)
                    new_column = {"$set": {"recon_count": recon_count_val}}
                    result = main_staging_collection.update_one(row, new_column)
        except Exception as e:
            reconstatus=False
            logger.error('error in recon for this row')
            logger.error(row)
            logger.error(e)
    return reconstatus





 # try:
 #            if row['event_type'] and row['event_type']== 'event_1':
 #
 #                table_name=os.getenv('BI_SQL_APP1_TABLE1')
 #                # reconqrystr=f'SELECT * FROM {os.getenv("BI_SQL_APP1_DB")}.{os.getenv("BI_SQL_APP1_SCHEMA")}.{table_name} WHERE {qrystr}'
 #                # found=bi_sql_db_obj.reconQuery(reconqrystr)
 #                # print(qrystr)
 #                # found=False