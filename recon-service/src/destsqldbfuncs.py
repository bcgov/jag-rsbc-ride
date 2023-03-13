
import pymssql
import os

class SqlDBFunctions():

    def __init__(self,dbname,dbserver,dbusername,dbpassword):
        self.dbcon=pymssql.connect(server=dbserver, database=dbname, user=dbusername, password=dbpassword)
        self.cursor = self.dbcon.cursor()

    @staticmethod
    def prepQuerystr(payload):
        # print(payload)
        tmpstr=''
        for k,v in payload.items():
            tmpstr=tmpstr+f"{k}='{v}'"+ " AND "
        # print(tmpstr[:-5])
        return tmpstr[:-5]

    def reconQuery(self,queryStr):
        # print(queryStr)
        recordfound=False
        result = self.cursor.execute(queryStr)
        rows = self.cursor.fetchall()
        if len(rows)>0:
            recordfound=True

        return recordfound
        # print(rows)

