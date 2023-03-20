
import pymssql
import os
import json

class SqlDBFunctions():

    def __init__(self,dbname,dbserver,dbusername,dbpassword):
        self.dbcon=pymssql.connect(server=dbserver, database=dbname, user=dbusername, password=dbpassword)
        self.cursor = self.dbcon.cursor()

    @staticmethod
    def prepQuerystr(payload,datasource):
        # print('in prep str')
        # print(payload)
        tmpstr=''
        if datasource=='df':
            tmpPayload=json.loads(payload)
            keys=tmpPayload.keys()
            payloadKey=""
            for v in keys:
                if "payload" in v:
                    payloadKey=v
            payloadForstr=tmpPayload[payloadKey][0]
            for k,v in payloadForstr.items():
                if not(v==None):
                    tmpstr=tmpstr+f"{k}='{v}'"+ " AND "
            # print(tmpstr[:-5])
        else:
            tmpPayload = json.loads(payload)
            for k, v in tmpPayload.items():
                if not (v == None):
                    tmpstr = tmpstr + f"{k}='{v}'" + " AND "
        return tmpstr[:-5]
       # else:
       #      tmpstr = ''
       #      tmpPayload = json.loads(payload)
       #      keys = tmpPayload.keys()
       #      payloadKey = ""
       #      for v in keys:
       #         if "payload" in v:
       #              payloadKey = v
       #      payloadForstr = tmpPayload[payloadKey][0]
       #      for k, v in payloadForstr.items():
       #          if not (v == None):
       #              tmpstr = tmpstr + f"{k}='{v}'" + " AND "
       #      # print(tmpstr[:-5])
       #      return tmpstr[:-5]



    def reconQuery(self,queryStr):
        # print(queryStr)
        recordfound=False
        result = self.cursor.execute(queryStr)
        rows = self.cursor.fetchall()
        if len(rows)>0:
            recordfound=True

        return recordfound
        # print(rows)

