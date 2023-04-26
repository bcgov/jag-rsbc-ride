
import os


class reconmetrics():

    def __init__(self,dbclient):
        self.dbclient=dbclient
        self.main_staging_collection = dbclient[os.getenv('MAIN_STG_COLLECTION')]
        self.err_staging_collection = dbclient[os.getenv('ERR_STG_COLLECTION')]
        self.main_table_collection = dbclient[os.getenv('MAIN_TABLE_COLLECTION')]
        self.err_table_collection = dbclient[os.getenv('ERR_TABLE_COLLECTION')]

    def genErrMetrics(self,):
        total_err_staging_count = self.err_staging_collection.count_documents({})
        total_err_count = self.err_table_collection.count_documents({})
        print(total_err_staging_count)
        print(total_err_count)

        # err_metric.set({'err_type': "staging"}, total_err_staging_count)
        # err_metric.set({'err_type': "main"}, total_err_count)
        metric_str='requests_total_counter{err_type="staging"} '+str(total_err_staging_count)
        metric_str1 = 'requests_total_counter{err_type="main"} ' + str(total_err_count)
        return(f'{metric_str}\n{metric_str1}')