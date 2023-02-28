package bcgov.rsbc.ride.kafka.service;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@ApplicationScoped
public class etkConsumerService {

    private final static Logger logger = LoggerFactory.getLogger(etkConsumerService.class);

    @Inject
    @Channel("outgoing-issuance")
    MutinyEmitter<Record<String, String>> emitterIssuanceEvent;

    @Inject
    @Channel("outgoing-payment")
    MutinyEmitter<Record<String, String>> emitterPaymentEvent;
    //
    @Inject
    @Channel("outgoing-disputeupdate")
    MutinyEmitter<Record<String, String>> emitterDisputeUpdateEvent;
    //
    @Inject
    @Channel("outgoing-dispute")
    MutinyEmitter<Record<String, String>> emitterDisputeEvent;


    public boolean publishEventtoIssuanceDecodedTopic(String eventPayload) {
        try {
            logger.info(eventPayload);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            emitterIssuanceEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
        } catch (Exception e) {
            logger.error("Exception occurred while sending issuance decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }

        return false;

    }



    public boolean publishEventtoDisputeDecodedTopic(String eventPayload) {

        try {
            logger.info(eventPayload);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            emitterDisputeEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
        } catch (Exception e) {
            logger.error("Exception occurred while sending dispute decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }

        return false;


    }


    public boolean publishEventtoPaymentDecodedTopic(String eventPayload) {

        try {
            logger.info(eventPayload);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            emitterPaymentEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
        } catch (Exception e) {
            logger.error("Exception occurred while sending payment decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }

        return false;


    }


    public boolean publishEventtoDisputeUpteDecodedTopic(String eventPayload) {

        try {
            logger.info(eventPayload);
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            emitterDisputeUpdateEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
        } catch (Exception e) {
            logger.error("Exception occurred while sending dispute updated decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
        }

        return false;



    }




//
//    public boolean publishEventtoDecodedTopic(String eventPayload,String eventType) {
//
//
//
//        switch(eventType) {
//            case "sample":
//                // code block
//                logger.info(eventPayload);
//                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                emitterTestEvt.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                break;
//            case "app_accepted":
//                // code block
//                try {
//                    logger.info(eventPayload);
//                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                    emitterAppAccptdEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                } catch (Exception e) {
//                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
//                }
//                break;
//            case "disclosure_sent":
//                // code block
//                try {
//                    logger.info(eventPayload);
//                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                    emitterDisclosureSentEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                } catch (Exception e) {
//                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
//                }
//                break;
//            case "evidence_submitted":
//                // code block
//                try {
//                    logger.info(eventPayload);
//                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                    emitterEvidenceSubmitEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                } catch (Exception e) {
//                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
//                }
//                break;
//            case "payment_received":
//                // code block
//                try {
//                    logger.info(eventPayload);
//                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                    emitterPayRecvdEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                } catch (Exception e) {
//                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
//                }
//                break;
//            case "review_scheduled":
//                // code block
//                try {
//                    logger.info(eventPayload);
//                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//                    emitterRevSchedEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
//                } catch (Exception e) {
//                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
//                }
//                break;
//            default:
//                // code block
//                logger.info(eventPayload);
//                logger.info("no matches for the event type");
//        }
//
//
//        return false;
//    }






}