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

import javax.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;

import bcgov.rsbc.ride.kafka.models.evtdisputeevent;
import bcgov.rsbc.ride.kafka.models.evtdisputeeventpayloadrecord;

import bcgov.rsbc.ride.kafka.models.evtissuanceevent;
import bcgov.rsbc.ride.kafka.models.evtissuanceeventpayloadrecord;

import bcgov.rsbc.ride.kafka.models.evtpaymentevent;
import bcgov.rsbc.ride.kafka.models.evtpaymenteventpayloadrecord;

import bcgov.rsbc.ride.kafka.models.evtdisputeupdateevent;
import bcgov.rsbc.ride.kafka.models.evtdisputeupdateeventpayloadrecord;

import bcgov.rsbc.ride.kafka.model.testevent;

import io.quarkus.mongodb.panache.PanacheQuery;
import javax.ws.rs.*;
import bcgov.rsbc.ride.kafka.apiKeys;


@Path("/etkevents")
public class eTkProducer {


    private final static Logger logger = LoggerFactory.getLogger(dfProducer.class);


    @Inject
    @Channel("outgoing-issuance")
    MutinyEmitter<Record<Long, evtissuanceeventpayloadrecord>> emitterIssuanceEvent;

    @Inject
    @Channel("outgoing-payment")
    MutinyEmitter<Record<Long, evtpaymenteventpayloadrecord>> emitterPaymentEvent;

    @Inject
    @Channel("outgoing-disputeupdate")
    MutinyEmitter<Record<Long, evtdisputeupdateeventpayloadrecord>> emitterDisputeUpdateEvent;

    @Inject
    @Channel("outgoing-dispute")
    MutinyEmitter<Record<Long, evtdisputeeventpayloadrecord>> emitterDisputeEvent;


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
    public Response publishIssuanceEvent(@HeaderParam("ride-api-key") String apiKey, evtissuanceevent eventobj) {
        if(apiKey== null){
            return Response.serverError().status(401).entity("Auth Error").build();
        }
        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
        List<apiKeys> foundKeys = queryKeys.list();
        long foundKeyCount=queryKeys.count();

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_issuance [payload: {}] to kafka.", eventobj.getEvtissuanceeventpayload());
            logger.info("{}",eventobj.getTypeofevent());
            evtissuanceeventpayloadrecord payloaddata=(evtissuanceeventpayloadrecord) eventobj.getEvtissuanceeventpayload().get(0);
            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
//                emitterIssuanceEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_issuance event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }





    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/disputeupdate")
    public Response publishDisputeUpdateEvent(@HeaderParam("ride-api-key") String apiKey, evtdisputeupdateevent eventobj) {
//        if(apiKey== null){
//            return Response.serverError().status(401).entity("Auth Error").build();
//        }
//        PanacheQuery<apiKeys> queryKeys = apiKeys.find("apikeyval", apiKey);
//        List<apiKeys> foundKeys = queryKeys.list();
//        long foundKeyCount=queryKeys.count();
        long foundKeyCount=1;

        if(foundKeyCount==0){
            return Response.serverError().status(401).entity("Auth Error").build();
        }else{
            logger.info("[RIDE]: Publish etk_disputeupdate [payload: {}] to kafka.", eventobj.getEvtdisputeupdateeventpayload());
            logger.info("{}",eventobj.getTypeOfevent());
            evtdisputeupdateeventpayloadrecord payloaddata=(evtdisputeupdateeventpayloadrecord) eventobj.getEvtdisputeupdateeventpayload().get(0);
            try {
                //Change sendAndAwait to wait at most 5 seconds.
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                logger.info("[RIDE]: Kafka event UID: {}", uid);
//                emitterDisputeUpdateEvent.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
                return Response.ok().entity("{\"status\":\"sent to kafka\",\"event_id\":\""+uid+"\"}").build();
            } catch (Exception e) {
                logger.error("[RIDE]: Exception occurred while sending etk_disputeupdate event, exception details: {}", e.toString() + "; " + e.getMessage());
                return Response.serverError().entity("Failed sending  event to kafka").build();
            }

        }

    }



}
