

import requests
import json

class EmailFunctions:

    def __init__(self,api_key,base_url):
        self.api_key=api_key
        self.base_url=base_url

    def sendbasicemail(self,templateid,receiveremail):
        url = f'{self.base_url}/v2/notifications/email'
        data = { "email_address": receiveremail, "template_id": templateid }
        headers = {'Content-Type': 'application/json', 'Authorization': f'ApiKey-v1 {self.api_key}'}
        response = requests.post(url, json=data, headers=headers)
        print(response.text)
