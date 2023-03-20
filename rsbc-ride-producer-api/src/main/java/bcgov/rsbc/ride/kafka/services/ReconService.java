package bcgov.rsbc.ride.kafka.services;

import bcgov.rsbc.ride.kafka.dfProducer;
import bcgov.rsbc.ride.kafka.models.reconapiMainpayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ReconService {

    private final static Logger logger = LoggerFactory.getLogger(ReconService.class);



    private OkHttpService okHttpService = new OkHttpService();

    public boolean saveTomainStaging(String apiPath,String payloadDate,String dataSrc,String eventType,String reconapihost) {


        try{
            reconapiMainpayload apiObj=new reconapiMainpayload();
            apiObj.setapipath(apiPath);
            apiObj.setpayloaddata(payloadDate);
            apiObj.setdatasource(dataSrc);
            apiObj.setEventType(eventType);
            String jsonPayload = new ObjectMapper().writeValueAsString(apiObj);
            logger.info(jsonPayload);
            String reconapiurl=reconapihost+"/savemainstaging";
            logger.info(reconapiurl);
            String response = okHttpService.postJson(reconapiurl, jsonPayload);
            logger.info(response);
        }catch(Exception e){
            logger.error("[RIDE]: Exception occurred while saving event to main staging able, exception details: {}", e.toString() + "; " + e.getMessage());
            return false;
        }

        return true;



    }
}
