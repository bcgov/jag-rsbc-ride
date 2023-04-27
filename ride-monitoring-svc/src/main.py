
from flask import Flask, jsonify,make_response

from prometheus_client import Gauge, generate_latest

app = Flask(__name__)



@app.route('/ping')
def pingroute():
    resp="working"
    return resp


@app.route('/metrics')
def metrics():
    return generate_latest()



if __name__ == '__main__':
    # Start Flask app
    app.run(debug=True)