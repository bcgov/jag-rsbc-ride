
import fastapi
from fastapi.responses import PlainTextResponse,JSONResponse
import os


router = fastapi.APIRouter()

@router.get('/ping')
async def pingmethod():
    return JSONResponse(status_code=200, content={"status":"working"})