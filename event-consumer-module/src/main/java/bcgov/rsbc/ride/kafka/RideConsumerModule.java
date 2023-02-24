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

import bcgov.rsbc.ride.kafka.service.ConsumerService;


@Path("/consume")
public class RideConsumerModule {

    private final static Logger logger = LoggerFactory.getLogger(RideConsumerModule.class);

    @Inject
    ConsumerService consumerService;



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "Hello kafka consumer";

    }


//    @GET
//    @Path("/ping2")
    @Incoming("incoming-testevent")
    @Blocking
    public void receive(payloadrecord event) {
        logger.info("Payload: {}", event);
//        payloadrecord payloaddata=event;
        try {
            //Change sendAndAwait to wait at most 5 seconds.
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
            consumerService.publishEventtoDecodedTopic(event.toString(),"sample");
//            emitterTestEvt.send(Record.of(uid, payloaddata));
//            return Response.ok().entity("success").build();
        } catch (Exception e) {
            logger.error("Exception occurred while sending issuance event, exception details: {}", e.toString() + "; " + e.getMessage());
//            return Response.serverError().entity("Failed sending test event to kafka").build();
        }
//        String et= ((String) event);
//        logger.info(et);
    }
}