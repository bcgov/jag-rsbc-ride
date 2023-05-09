
import fastapi
from fastapi.responses import PlainTextResponse,JSONResponse
import os
from src.emailfunctions import EmailFunctions


router = fastapi.APIRouter()

@router.get('/ping')
async def pingmethod():
    return JSONResponse(status_code=200, content={"status":"working"})

@router.post('/sendbasicemail',response_class=JSONResponse)
async def sendbasicemail(payload: dict):
    respstatus = {"status": "failure"}
    status_code = 500
    try:
        payloadinput = payload.copy()
        templateid=payloadinput['templateid']
        receiver=payloadinput['receiver']
        emailobj=EmailFunctions(os.getenv('GC_API_KEY'),os.getenv('GC_BASE_URL'))
        emailobj.sendbasicemail(templateid,receiver)
        respstatus = {"status": "success"}
        status_code = 200
    except Exception as e:
        print(e)

    return JSONResponse(status_code=status_code, content=respstatus)