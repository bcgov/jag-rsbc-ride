
from flask import Flask, jsonify,make_response
from pymongo import MongoClient
import os


from prometheus_client import Gauge, generate_latest
import logging
from metricsfuncs import reconmetrics

numeric_level = getattr(logging, os.getenv('LOG_LEVEL').upper(), 10)
# Set up logging
logging.basicConfig(
    # level=logging[os.getenv('LOG_LEVEL')],
    level=numeric_level,
    format='%(asctime)s %(levelname)s %(module)s:%(lineno)d [RIDE_MONITOR]: %(message)s'
)

app = Flask(__name__)



client=MongoClient(os.environ.get('MONGO_URL'))
db = client[os.environ.get('RECON_DB_NAME')]
main_staging_collection = db[os.getenv('MAIN_STG_COLLECTION')]
err_staging_collection = db[os.getenv('ERR_STG_COLLECTION')]
main_table_collection = db[os.getenv('MAIN_TABLE_COLLECTION')]
err_table_collection=db[os.getenv('ERR_TABLE_COLLECTION')]


metricsobj=reconmetrics(db,logging)

err_metric = Gauge("msgs_err_count", "Count of errored messages",['count_type'])
err_metric.labels("err_staging").set_function(metricsobj.genStageErrMetric)
err_metric.labels("err_main").set_function(metricsobj.genMainErrMetric)

total_count_metric = Gauge("msgs_total_count", "Count of total messages",['count_type'])
total_count_metric.labels("total_staging").set_function(metricsobj.genStageMetric)
total_count_metric.labels("total_main").set_function(metricsobj.genMainMetric)

recon_exception_metric = Gauge("msgs_recon_exceptions", "Count of messages exceeding recon threshold",['count_type'])
recon_exception_metric.labels("count_staging").set_function(metricsobj.genReconExcpCount)

# detailed_count_metric = Gauge("ride_msgs_detailed_counts", "Count of messages detailed by types",['source_type','event_type'])
event_type_count_metric = Gauge("ride_msgs_by_eventtype_counts", "Count of messages by event type",['event_type'])
source_type_count_metric = Gauge("ride_msgs_by_source_counts", "Count of messages by data source",['source_type'])


@app.route('/ping')
def pingroute():
    resp="working"
    return resp


@app.route('/metrics')
def metrics():
    return generate_latest()

@app.route('/gendetailedmetrics')
def genDetailedmetrics():
    metricsgenerated=0
    respstatus="error"
    statuscode=500
    try:
        detmetricsobj = reconmetrics(db, logging)
        metricresult=detmetricsobj.genDetailedCounts(event_type_count_metric,source_type_count_metric)
        if metricresult==1:
            respstatus="success"
            statuscode=200
    except Exception as e:
        logging.error("error in generating detailed metrics")
        logging.info(e)
    return make_response(jsonify(status=respstatus),statuscode)



if __name__ == '__main__':
    # Start Flask app
    app.run(debug=True)