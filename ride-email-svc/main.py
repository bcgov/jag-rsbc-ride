

from fastapi import FastAPI,Response
import fastapi
from fastapi.responses import PlainTextResponse,JSONResponse

import os
import logging
import uvicorn

from src import app

numeric_level = getattr(logging, os.getenv('LOG_LEVEL').upper(), 10)
# Set up logging
logging.basicConfig(
    # level=logging[os.getenv('LOG_LEVEL')],
    level=numeric_level,
    format='%(asctime)s %(levelname)s %(module)s:%(lineno)d [RIDE_EMAIL]: %(message)s'
)

api = fastapi.FastAPI()

def configure():
    configure_routing()

def configure_routing():
    api.include_router(app.router)

if __name__ == '__main__':
    configure()
    uvicorn.run("main:api", host="0.0.0.0", port=5001, reload=True)
else:
    configure()