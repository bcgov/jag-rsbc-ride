package bcgov.rsbc.ride.kafka.service;

import java.net.URI;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.Duration;


//import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
//import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;


import bcgov.rsbc.ride.kafka.models.payloadrecord;


import bcgov.rsbc.ride.kafka.models.appacceptedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.disclosuresentpayloadrecord;
import bcgov.rsbc.ride.kafka.models.evidencesubmittedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.payrecvdpayloadrecord;
import bcgov.rsbc.ride.kafka.models.reviewscheduledpayloadrecord;

@ApplicationScoped
public class ConsumerService {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Inject
    @Channel("outgoing-testevent")
    MutinyEmitter<Record<String, String>> emitterTestEvt;

    @Inject
    @Channel("outgoing-app_accepted_decoded")
    MutinyEmitter<Record<String, String>> emitterAppAccptdEvent;

    @Inject
    @Channel("outgoing-disclosure_sent_decoded")
    MutinyEmitter<Record<String, String>> emitterDisclosureSentEvent;

    @Inject
    @Channel("outgoing-evidence_submitted_decoded")
    MutinyEmitter<Record<String, String>> emitterEvidenceSubmitEvent;

    @Inject
    @Channel("outgoing-payment_received_decoded")
    MutinyEmitter<Record<String, String>> emitterPayRecvdEvent;

    @Inject
    @Channel("outgoing-review_scheduled_decoded")
    MutinyEmitter<Record<String, String>> emitterRevSchedEvent;





    public boolean publishEventtoDecodedTopic(String eventPayload,String eventType) {



        switch(eventType) {
            case "sample":
                // code block
                logger.info(eventPayload);
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                emitterTestEvt.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                break;
            case "app_accepted":
                // code block
                try {
                    logger.info(eventPayload);
                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                    emitterAppAccptdEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                } catch (Exception e) {
                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
                }
                break;
            case "disclosure_sent":
                // code block
                try {
                    logger.info(eventPayload);
                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                    emitterDisclosureSentEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                } catch (Exception e) {
                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
                }
                break;
            case "evidence_submitted":
                // code block
                try {
                    logger.info(eventPayload);
                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                    emitterEvidenceSubmitEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                } catch (Exception e) {
                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
                }
                break;
            case "payment_received":
                // code block
                try {
                    logger.info(eventPayload);
                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                    emitterPayRecvdEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                } catch (Exception e) {
                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
                }
                break;
            case "review_scheduled":
                // code block
                try {
                    logger.info(eventPayload);
                    uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                    emitterRevSchedEvent.send(Record.of(uid.toString(), eventPayload)).await().atMost(Duration.ofSeconds(5));
                } catch (Exception e) {
                    logger.error("Exception occurred while sending decoded event, exception details: {}", e.toString() + "; " + e.getMessage());
                }
                break;
            default:
                // code block
                logger.info(eventPayload);
                logger.info("no matches for the event type");
        }


        return false;
    }






}