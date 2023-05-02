
import os
import pandas as pd

class reconmetrics():

    def __init__(self,dbclient,logging):
        self.dbclient=dbclient
        self.main_staging_collection = dbclient[os.getenv('MAIN_STG_COLLECTION')]
        self.err_staging_collection = dbclient[os.getenv('ERR_STG_COLLECTION')]
        self.main_table_collection = dbclient[os.getenv('MAIN_TABLE_COLLECTION')]
        self.err_table_collection = dbclient[os.getenv('ERR_TABLE_COLLECTION')]
        self.logging=logging

    def genErrMetrics(self):
        total_err_staging_count = self.err_staging_collection.count_documents({})
        total_err_count = self.err_table_collection.count_documents({})
        print(total_err_staging_count)
        print(total_err_count)

        # err_metric.set({'err_type': "staging"}, total_err_staging_count)
        # err_metric.set({'err_type': "main"}, total_err_count)
        metric_str='requests_total_counter{err_type="staging"} '+str(total_err_staging_count)
        metric_str1 = 'requests_total_counter{err_type="main"} ' + str(total_err_count)
        return(f'{metric_str}\n{metric_str1}')
    
    def genStageErrMetric(self):
        self.logging.info("generating metrics for error staging")
        total_err_staging_count=0
        try:
            total_err_staging_count = self.err_staging_collection.count_documents({})
        except Exception as e:
            self.logging.error("error in generating metrics for error staging")
            self.logging.info(e)
        return total_err_staging_count

    def genMainErrMetric(self):
        self.logging.info("generating metrics for main error")
        total_err_count=0
        try:
            total_err_count = self.err_table_collection.count_documents({})
        except Exception as e:
            self.logging.error("error in generating metrics for main error")
            self.logging.info(e)
        return total_err_count

    def genStageMetric(self):
        self.logging.info("generating metrics for main staging")
        total_stg_count=0
        try:
            total_stg_count = self.main_staging_collection.count_documents({})
        except Exception as e:
            self.logging.error("error in generating metrics for main staging")
            self.logging.info(e)
        return total_stg_count

    def genMainMetric(self):
        self.logging.info("generating metrics for main table")
        total_main_count=0
        try:
            total_main_count = self.main_table_collection.count_documents({})
        except Exception as e:
            self.logging.error("error in generating metrics for main table")
            self.logging.info(e)
        return total_main_count

    def genReconExcpCount(self):
        self.logging.info("generating metrics for recon exceptions")
        total_recon_count=0
        try:
            total_recon_count = self.main_staging_collection.count_documents({'recon_count': {'$gt': int(os.getenv('ERR_THRESHOLD_COUNT'))}})
        except Exception as e:
            self.logging.error("error in generating metrics for recon exceptions")
            self.logging.info(e)
        return total_recon_count

    def genDetailedCounts(self,event_type_count_metric,source_type_count_metric):
        self.logging.info("generating detailed metrics")
        genSuccess = 0
        try:
            alldocs=self.main_table_collection.find({})
            df=pd.DataFrame(list(alldocs))
            # print(df['datasource','eventType'].value_counts())
            # print(df['datasource'].value_counts().keys())
            for k,v in df['datasource'].value_counts().items():
                source_type_count_metric.labels(source_type=k).set(v)
            for k, v in df['eventType'].value_counts().items():
                event_type_count_metric.labels(event_type=k).set(v)
                # print(k)
                # print(v)
            genSuccess=1
        except Exception as e:
            self.logging.error("error in generating detailed metrics")
            self.logging.info(e)
        return genSuccess