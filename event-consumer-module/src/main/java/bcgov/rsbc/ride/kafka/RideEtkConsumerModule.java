package bcgov.rsbc.ride.kafka;


//import bcgov.rsbc.ride.kafka.models.*;
import bcgov.rsbc.ride.kafka.service.etkConsumerService;
import io.smallrye.reactive.messaging.annotations.Blocking;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Path("/consumeetk")
public class RideEtkConsumerModule {

    private final static Logger logger = LoggerFactory.getLogger(RideEtkConsumerModule.class);

    @Inject
    etkConsumerService consumerService;



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "pong from df consumer";

    }


    @Incoming("incoming-issuance")
    @Blocking
    public void receive(String event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded issuance event UID: {}", uid);
            consumerService.publishEventtoIssuanceDecodedTopic(event);
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-payment")
    @Blocking
    public void receive1(String event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded payment event UID: {}", uid);
            consumerService.publishEventtoPaymentDecodedTopic(event);
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-dispute")
    @Blocking
    public void receive2(String event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded dispute event UID: {}", uid);
            consumerService.publishEventtoDisputeDecodedTopic(event);
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }

    @Incoming("incoming-disputeupdate")
    @Blocking
    public void receive3(String event) {
        logger.info("Payload: {}", event);
        try {
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka decoded dispute update event UID: {}", uid);
            consumerService.publishEventtoDisputeUpteDecodedTopic(event);
        } catch (Exception e) {
            logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }
    }


}