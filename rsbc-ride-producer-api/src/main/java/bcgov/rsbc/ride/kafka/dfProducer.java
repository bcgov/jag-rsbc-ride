package bcgov.rsbc.ride.kafka;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

//import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;
//import org.jboss.resteasy.reactive.RestResponse;

import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;

import bcgov.rsbc.ride.kafka.models.appacceptedevent;
import bcgov.rsbc.ride.kafka.models.appacceptedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.disclosuresentevent;
import bcgov.rsbc.ride.kafka.models.disclosuresentpayloadrecord;
import bcgov.rsbc.ride.kafka.models.evidencesubmittedevent;
import bcgov.rsbc.ride.kafka.models.evidencesubmittedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.payrecvdevent;
import bcgov.rsbc.ride.kafka.models.payrecvdpayloadrecord;
import bcgov.rsbc.ride.kafka.models.reviewscheduleddevent;
import bcgov.rsbc.ride.kafka.models.reviewscheduledpayloadrecord;
import bcgov.rsbc.ride.kafka.models.reconapiMainpayload;

import io.quarkus.mongodb.panache.PanacheQuery;
import bcgov.rsbc.ride.kafka.apiKeys;
import javax.ws.rs.*;


//import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



@Path("/dfevents")
public class dfProducer {

    private final static Logger logger = LoggerFactory.getLogger(dfProducer.class);



    @Inject
    @Channel("outgoing-appaccepted")
    MutinyEmitter<Record<Long, appacceptedpayloadrecord>> emitterAppAccptdEvent;

    @Inject
    @Channel("outgoing-disclosuresent")
    MutinyEmitter<Record<Long, disclosuresentpayloadrecord>> emitterDisclosureSentEvent;

    @Inject
    @Channel("outgoing-evidencesubmit")
    MutinyEmitter<Record<Long, evidencesubmittedpayloadrecord>> emitterEvidenceSubmitEvent;

    @Inject
    @Channel("outgoing-payreceived")
    MutinyEmitter<Record<Long, payrecvdpayloadrecord>> emitterPayRecvdEvent;

    @Inject
    @Channel("outgoing-reviewscheduled")
    MutinyEmitter<Record<Long, reviewscheduledpayloadrecord>> emitterRevSchedEvent;



    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dfping() {
        return Response.ok().entity("{\"status\":\"working\"}").build();
    }


    private OkHttpService okHttpService = new OkHttpService();

    @ConfigProperty(name = "recon.api.host")
    String reconapihost;


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/appaccepted")
    public Response publishappAcceptedEvent(@HeaderParam("ride-api-key") String apiKey, appacceptedevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish app accepted [payload: {}] to kafka.", eventobj.getAppacceptedpayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
            logger.info("{}",eventobj.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
            appacceptedpayloadrecord payloaddata=(appacceptedpayloadrecord) eventobj.getAppacceptedpayload().get(0);

            try {
//                TODO: Prep payload for recon api save master
//                logger.info(eventobj.toString());
                reconapiMainpayload apiObj=new reconapiMainpayload();
                apiObj.setapipath("/dfevents/appaccepted");
                apiObj.setpayloaddata(eventobj.toString());
                apiObj.setdatasource("df");
                apiObj.setEventType("app_accepted");
                String jsonPayload = new ObjectMapper().writeValueAsString(apiObj);
//                logger.info(jsonPayload);
                String reconapiurl=reconapihost+"/savemainstaging";
                String response = okHttpService.postJson(reconapiurl, jsonPayload);
//                logger.info(reconapihost);
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterAppAccptdEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
//            return Response.ok().entity("success").build();
//            return Response.ok().entity("success").build();
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending app_accepted event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }



    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/disclosuresent")
    public Response publishDisclosureEvent(@HeaderParam("ride-api-key") String apiKey, disclosuresentevent eventobj) {

        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish disclosure sent [payload: {}] to kafka.", eventobj.getDisclosuresentpayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
            logger.info("{}",eventobj.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
            disclosuresentpayloadrecord payloaddata=(disclosuresentpayloadrecord) eventobj.getDisclosuresentpayload().get(0);

            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterDisclosureSentEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending disclosure event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }
        }

    }



    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/evidencesubmitted")
    public Response publishEvidenceSubmitEvent(@HeaderParam("ride-api-key") String apiKey, evidencesubmittedevent eventobj) {

        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish evidence submitted [payload: {}] to kafka.", eventobj.getEvidencesubmittedpayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
            logger.info("{}",eventobj.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
            evidencesubmittedpayloadrecord payloaddata=(evidencesubmittedpayloadrecord) eventobj.getEvidencesubmittedpayload().get(0);

            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterEvidenceSubmitEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending evidence submitted event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending event to kafka").build();
            }

        }

    }



    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/paymentreceived")
    public Response publishPaymentRecvdEvent(@HeaderParam("ride-api-key") String apiKey, payrecvdevent eventobj) {

        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish payment received [payload: {}] to kafka.", eventobj.getPayrecvdpayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
            logger.info("{}",eventobj.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
            payrecvdpayloadrecord payloaddata=(payrecvdpayloadrecord) eventobj.getPayrecvdpayload().get(0);

            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterPayRecvdEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending payment_received event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending event to kafka").build();
            }
        }

    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/reviewscheduled")
    public Response publishReviewSchedEvent(@HeaderParam("ride-api-key") String apiKey, reviewscheduleddevent eventobj) {

        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish review scheduled [payload: {}] to kafka.", eventobj.getReviewscheduledpayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
            logger.info("{}",eventobj.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
            reviewscheduledpayloadrecord payloaddata=(reviewscheduledpayloadrecord) eventobj.getReviewscheduledpayload().get(0);

            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("Kafka event UID: {}", uid);
                emitterRevSchedEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending review_scheduled event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending event to kafka").build();
            }
        }

    }







}
