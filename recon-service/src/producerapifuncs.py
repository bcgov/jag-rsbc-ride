
import requests

class producerAPITasks:

    def __init__(self,apihost,logger):
        self.apihost=apihost
        self.logger=logger


    def sendAPIReq(self,apipath,headers,apimethod,payload):
        apiurl=f'{self.apihost}{apipath}'
        apistatus=False
        try:
            if apimethod=='POST':
                response = requests.post(apiurl, headers=headers, data=payload,timeout=10)
            print(response.status_code)
            if response.status_code==200:
                apistatus=True
        except Exception as e:
            self.logger.error('error from the api')
            self.logger.error(payload)
            self.logger.error(e)

        return apistatus