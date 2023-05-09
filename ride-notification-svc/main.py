

from fastapi import FastAPI,Response
import fastapi
from fastapi.responses import PlainTextResponse,JSONResponse

import os
import uvicorn

from src import app



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