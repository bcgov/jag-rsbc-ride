package bcgov.rsbc.ride.kafka.models;

public class reconapiErrpayload {

    private String apipath;
    private String datasource;
    private String event_type;
    private String payloaddata;
    private String error_reason;
    private String error_type;





    // Getters and setters
    public String getapipath() {
        return apipath;
    }
    public void setapipath(String apipath) {
        this.apipath = apipath;
    }

    public String getdatasource() {
        return datasource;
    }
    public void setdatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getEventType() {
        return event_type;
    }
    public void setEventType(String event_type) {
        this.event_type = event_type;
    }

    public String getpayloaddata() {
        return payloaddata;
    }
    public void setpayloaddata(String payloaddata) {
        this.payloaddata = payloaddata;
    }

    public String getErrorReason() {
        return error_reason;
    }
    public void setErrorReason(String error_reason) {
        this.error_reason = error_reason;
    }


    public String getErrorType() {
        return error_type;
    }
    public void setErrorType(String error_type) {
        this.error_type = error_type;
    }
}
