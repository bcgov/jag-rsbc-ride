import json
from typing import List
from fastapi import FastAPI,Response,Header, Request
from fastapi.responses import PlainTextResponse,JSONResponse
import os
from pymongo import MongoClient
import uvicorn
import logging

from aioprometheus import Gauge,MetricsMiddleware, render,REGISTRY,Counter
from aioprometheus.asgi.starlette import metrics

from reconfunctions import recondestination
from errorretryfunctions import error_retry_task
from metricsfuncs import reconmetrics


numeric_level = getattr(logging, os.getenv('LOG_LEVEL').upper(), 10)
# Set up logging
logging.basicConfig(
    # level=logging[os.getenv('LOG_LEVEL')],
    level=numeric_level,
    format='%(asctime)s %(levelname)s %(module)s:%(lineno)d [RIDE_RECON]: %(message)s'
)



# print(REGISTRY.get_all())

app = FastAPI()
# err_metric=None
# for collector in REGISTRY.get_all():
#     print(collector.name)
#     if collector.name=='msgs_err_count':
#         app.state.err_metric=collector
#         err_metric = collector
#         print(err_metric)
#         pass
#
#     else:
#         app.state.err_metric = Gauge("msgs_err_count", "Count of errored messages")
#         err_metric = Gauge("msgs_err_count", "Count of errored messages")
#         print(err_metric)
# app.state.err_metric = Gauge("msgs_err_count", "Count of errored messages")
# app.state.users_events_counter = Counter("events", "Number of events.")
app.add_middleware(MetricsMiddleware)
app.add_route("/metrics", metrics)
# print(app.state.err_metric)




client=MongoClient(os.environ.get('MONGO_URL'))
# db = client['kafkaevents']
db = client[os.environ.get('RECON_DB_NAME')]
main_staging_collection = db[os.getenv('MAIN_STG_COLLECTION')]
err_staging_collection = db[os.getenv('ERR_STG_COLLECTION')]
main_table_collection = db[os.getenv('MAIN_TABLE_COLLECTION')]
err_table_collection=db[os.getenv('ERR_TABLE_COLLECTION')]
err_threshold=os.getenv('ERR_THRESHOLD_COUNT')

@app.get('/ping', response_class=JSONResponse)
async def main_ping():
    return JSONResponse(status_code=200, content={"status":"working"})


# @app.get("/metrics")
# async def handle_metrics(
#     request: Request,  # pylint: disable=unused-argument
#     accept: List[str] = Header(None),
# ) -> Response:
#
#     content, http_headers = render(REGISTRY, accept)
#     print('in metrics')
#     logging.debug('here is the payload to be saved to main staging table')
#     return Response(content=content, media_type=http_headers["Content-Type"])



@app.post('/savemainstaging', response_class=JSONResponse)
async def save_main_staging_data(payload: dict):
    logging.info('triggering call to save payload in main staging table')
    respstatus={"status":"failure"}
    status_code=500
    try:
        payloadinput=payload.copy()
        payloadinput['payloadstr']=json.dumps(payload["payloaddata"])
        logging.debug('here is the payload to be saved to main staging table')
        logging.debug(payloadinput)
        # DONE: Dedup before saving to error staging
        query_main_staging = main_staging_collection.find(payloadinput)
        if len(list(query_main_staging)) > 0:
            logging.info('skipping saving to main staging.found duplicate.')
        else:
            result = main_staging_collection.insert_one(payloadinput)
        respstatus = {"status": "success"}
        status_code=200
    except Exception as e:
        logging.info('error in saving to staging main table')
        logging.error('error in saving to staging main table')
        logging.error(e)

    return JSONResponse(status_code=status_code, content=respstatus)

@app.post('/saveerrorstaging', response_class=JSONResponse)
async def save_err_staging_data(payload: dict):
    logging.info('triggering call to save payload in error staging table')
    respstatus={"status":"failure"}
    status_code = 500
    try:
        payloadinput=payload.copy()
        payloadinput['payloadstr']=json.dumps(payload["payloaddata"])
        logging.debug('here is the payload to be saved to error staging table')
        logging.debug(payloadinput)
        # DONE: Dedup before saving to error staging
        query_err_staging=err_staging_collection.find(payloadinput)
        if len(list(query_err_staging))>0:
            logging.info('skipping saving to error staging.found duplicate.')
        else:
            result = err_staging_collection.insert_one(payloadinput)
        respstatus = {"status": "success"}
        status_code = 200
    except Exception as e:
        logging.info('error in saving to error main table')
        logging.error('error in saving to error main table')
        logging.error(e)

    return JSONResponse(status_code=status_code, content=respstatus)

@app.get('/riderecon', response_class=JSONResponse)
async def recon_destination():
    logging.info('trigering recon job')
    respstatus = {"status": "failure"}
    status_code = 500
    try:
        recon_out=recondestination(client,main_staging_collection,main_table_collection,logging)
        if not(recon_out):
            raise Exception('error in one of the rows during recon')
        respstatus = {"status": "success"}
        status_code = 200
    except Exception as e:
        logging.info('error in recon job')
        logging.error('error in recon job')
        logging.error(e)

    return JSONResponse(status_code=status_code, content=respstatus)

@app.get('/rideerrorretry', response_class=JSONResponse)
async def error_retry():
    logging.info('trigering error retry job')
    respstatus = {"status": "failure"}
    status_code = 500
    try:
        error_retry_out=error_retry_task(client,err_staging_collection,err_table_collection,err_threshold,logging)
        if not(error_retry_out):
            raise Exception('error in one of the rows during retry')
        respstatus = {"status": "success"}
        status_code = 200
    except Exception as e:
        logging.info('error in error retry job')
        logging.error('error in eror retry job')
        logging.error(e)

    return JSONResponse(status_code=status_code, content=respstatus)


@app.get('/genmetrics',response_class=PlainTextResponse)
async def genmetrics(request: Request):
    logging.info('triggering metrics generation')
    # respstatus = {"status": "failure"}
    respstatus='error'
    status_code = 500
    try:
        metricObj=reconmetrics(db)
        # print(request.app.state.err_metric)
        # err_metric.set({'err_type': "staging"}, 1)
        metric_resp=metricObj.genErrMetrics()
        # respstatus = {"status": "success"}
        respstatus=metric_resp
        status_code = 200
    except Exception as e:
        logging.info('error in getting metrics')
        logging.error('error in getting metrics')
        logging.error(e)
    return respstatus

# @app.get("/users")
# async def get_user( request: Request):
#     for collector in REGISTRY.get_all():
#         print(collector.name)
#         if collector.name=='users_events_counter':
#             # app.state.users_events_counter=collector
#             # err_metric = collector
#             # print(err_metric)
#             pass
#
#         else:
#             app.state.users_events_counter = Counter("events", "Number of events.")
#             # err_metric = Gauge("msgs_err_count", "Count of errored messages")
#             # print(err_metric)
#     # app.state.users_events_counter = Counter("events", "Number of events.")
#
#     # request.app.state.users_events_counter.inc({"path": request.scope["path"]})
#     return Response(f"a")

if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=5001, reload=True)
