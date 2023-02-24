package bcgov.rsbc.ride.kafka;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
//import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import io.smallrye.reactive.messaging.annotations.Blocking;

import bcgov.rsbc.ride.kafka.models.testevent;
import bcgov.rsbc.ride.kafka.models.payloadrecord;

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

import bcgov.rsbc.ride.kafka.service.ConsumerService;


@Path("/consumedf")
public class RideDFConsumerModule {

    private final static Logger logger = LoggerFactory.getLogger(RideConsumerModule.class);

    @Inject
    ConsumerService consumerService;



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "pong from df consumer";

    }


    @Incoming("incoming-appaccepted")
    @Blocking
    public void receive(appacceptedpayloadrecord event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),event.getEventType().toString());
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-disclosuresent")
    @Blocking
    public void receive(disclosuresentpayloadrecord event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),event.getEventType().toString());
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-evidencesubmit")
    @Blocking
    public void receive(evidencesubmittedpayloadrecord event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),event.getEventType().toString());
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-payreceived")
    @Blocking
    public void receive(payrecvdpayloadrecord event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),event.getEventType().toString());
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-reviewscheduled")
    @Blocking
    public void receive(reviewscheduledpayloadrecord event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),event.getEventType().toString());
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }


}