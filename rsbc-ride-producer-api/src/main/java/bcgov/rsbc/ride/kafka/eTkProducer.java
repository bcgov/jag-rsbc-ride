package bcgov.rsbc.ride.kafka;

import bcgov.rsbc.ride.kafka.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.mongodb.panache.PanacheQuery;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.time.Duration;

import bcgov.rsbc.ride.kafka.models.evtdisputeupdateevent;
import bcgov.rsbc.ride.kafka.models.evtissuanceeevent;
import bcgov.rsbc.ride.kafka.models.evtpaymenteevent;
import bcgov.rsbc.ride.kafka.models.evtdisputeevent;
import bcgov.rsbc.ride.kafka.models.evtcontraventionseevent;
import bcgov.rsbc.ride.kafka.models.evtpaymentqueryeevent;

import bcgov.rsbc.ride.kafka.services.ReconService;


@Path("/etkevents")
public class eTkProducer {

    private final static Logger logger = LoggerFactory.getLogger(eTkProducer.class);



    @Inject
    @Channel("outgoing-issuance")
    MutinyEmitter<Record<Long, evtissuanceeevent>> emitterIssuanceEvent;

    @Inject
    @Channel("outgoing-payment")
    MutinyEmitter<Record<Long, evtpaymenteevent>> emitterPaymentEvent;
//
    @Inject
    @Channel("outgoing-disputeupdate")
    MutinyEmitter<Record<Long, evtdisputeupdateevent>> emitterDisputeUpdateEvent;
//
    @Inject
    @Channel("outgoing-dispute")
    MutinyEmitter<Record<Long, evtdisputeevent>> emitterDisputeEvent;

    @Inject
    @Channel("outgoing-violations")
    MutinyEmitter<Record<Long, evtcontraventionseevent>> emitterContraventionsEvent;


    @Inject
    @Channel("outgoing-payquery")
    MutinyEmitter<Record<Long, evtpaymentqueryeevent>> emitterPayQueryEvent;

    @ConfigProperty(name = "recon.api.host")
    String reconapihost;


    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response etkping() {
        return Response.ok().entity("{\"status\":\"working\"}").build();
    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/issuance")
    public Response publishIssuanceEvent(@HeaderParam("ride-api-key") String apiKey, evtissuanceeevent eventobj) {

        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_issuance [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
//                logger.info(jsonPayload);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/issuance",jsonPayload,"etk","etk_issuance",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterIssuanceEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_issuance event, exception details: {}", e.toString() + "; " + e.getMessage());
                String jsonPayload = null;
                try {
                    jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/issuance",jsonPayload,"etk","etk_issuance",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }




    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/disputeupdate")
    public Response publishDisputeUpdateEvent(@HeaderParam("ride-api-key") String apiKey, evtdisputeupdateevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_disputeupdate [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/disputeupdate",jsonPayload,"etk","etk_disputeupdate",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterDisputeUpdateEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_disputeupdate event, exception details: {}", e.toString() + "; " + e.getMessage());
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/disputeupdate",jsonPayload,"etk","etk_disputeupdate",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/dispute")
    public Response publishDisputeEvent(@HeaderParam("ride-api-key") String apiKey, evtdisputeevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_dispute [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/dispute",jsonPayload,"etk","etk_dispute",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterDisputeEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_dispute event, exception details: {}", e.toString() + "; " + e.getMessage());
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/dispute",jsonPayload,"etk","etk_dispute",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/payment")
    public Response publishPaymentEvent(@HeaderParam("ride-api-key") String apiKey, evtpaymenteevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_payment [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/payment",jsonPayload,"etk","etk_payment",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterPaymentEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_payment event, exception details: {}", e.toString() + "; " + e.getMessage());
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/payment",jsonPayload,"etk","etk_payment",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/violations")
    public Response publishContraventionsEvent(@HeaderParam("ride-api-key") String apiKey, evtcontraventionseevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_violations [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/violations",jsonPayload,"etk","etk_violations",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterContraventionsEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_violations event, exception details: {}", e.toString() + "; " + e.getMessage());
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/violations",jsonPayload,"etk","etk_violations",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/payquery")
    public Response publishPaymentQueryEvent(@HeaderParam("ride-api-key") String apiKey, evtpaymentqueryeevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish payment_query [payload: {}] to kafka.", eventobj.toString());
//            logger.info("{}",eventobj.getTypeofevent());
//            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                //DONE: Prep payload for recon api save master
                ReconService reconObj=new ReconService();
                Boolean reconResp= reconObj.saveTomainStaging("/etkevents/payquery",jsonPayload,"etk","payment_query",reconapihost);
                if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                    logger.error("[RIDE]: Exception occurred while saving to main staging table");
                }
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
                emitterPayQueryEvent.send(Record.of(uid, eventobj)).await().atMost(Duration.ofSeconds(5));
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending payment_query event, exception details: {}", e.toString() + "; " + e.getMessage());
                try {
                    String jsonPayload = new ObjectMapper().writeValueAsString(eventobj);
                    ReconService reconObj=new ReconService();
                    Boolean reconResp= reconObj.saveToErrStaging("/etkevents/payquery",jsonPayload,"etk","payment_query",reconapihost,"producer_api",e.toString());
                    if(!reconResp){
//                    throw new Exception("error in saving to main staging table");
                        logger.error("[RIDE]: Exception occurred while saving to err staging table");
                    }
                } catch (JsonProcessingException ex) {
                    logger.error("[RIDE]: Exception occurred while saving to err staging table");
                }

                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }


//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/etktestevent")
//    public Response etktestevent(String eventobj) {
//        logger.info(eventobj);
//        Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//        emitterTestEvt.send(Record.of(uid.toString(), eventobj)).await().atMost(Duration.ofSeconds(5));
//
//        return Response.ok().entity("{\"status\":\"working\"}").build();
//    }



}