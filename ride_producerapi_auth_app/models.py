from django.db import models
import uuid

# Create your models here.


# onboarded application names
class AppName(models.Model):
    sttschoices = (
        ('E', 'Enabled'),
        ('D', 'Disabled')
    )
    appname=models.CharField(max_length=210)
    appcode=models.CharField(max_length=20,primary_key=True)
    id = models.UUIDField(default=uuid.uuid4, editable=False)
    appstatus = models.CharField(max_length=64, choices=sttschoices)
    created=models.DateTimeField(auto_now_add=True,auto_now=False)
    updated = models.DateTimeField(auto_now=True)

    def __str__(self):
        return self.appcode

    def getstatus(self):
        for v in self.sttschoices:
            # print(v)
            if v[0] == self.appstatus:
                return v[1]

    def getstatusval(self):
        outpt = []
        for v in self.sttschoices:
            # print('working loop')
            # print(v)
            outpt.append(v[1])
        return outpt

    def getstatusdict(self):
        outpt = []
        for v in self.sttschoices:
            tmp = {}
            # print('working loop')
            # print(v)
            tmp[v[1]] = v[0]
            outpt.append(tmp)
        return outpt

# onboarded application api keys
class ApiKey(models.Model):
    appid=models.ForeignKey(AppName,on_delete=models.CASCADE)
    apikey = models.UUIDField(default=uuid.uuid4, editable=True,primary_key=True)
    created=models.DateTimeField(auto_now_add=True,auto_now=False)
    updated = models.DateTimeField(auto_now=True)

    def __str__(self):
        return str(self.appid)
