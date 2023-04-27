
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


metricsobj=reconmetrics(db)

err_metric = Gauge("msgs_err_count", "Count of errored messages",['count_type'])
err_metric.labels("err_staging").set_function(metricsobj.genStageErrMetric)


@app.route('/ping')
def pingroute():
    resp="working"
    return resp


@app.route('/metrics')
def metrics():
    return generate_latest()



if __name__ == '__main__':
    # Start Flask app
    app.run(debug=True)