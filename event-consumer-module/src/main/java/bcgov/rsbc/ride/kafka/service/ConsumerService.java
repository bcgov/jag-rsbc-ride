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

@ApplicationScoped
public class ConsumerService {

    private final static Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Inject
    @Channel("outgoing-testevent")
    MutinyEmitter<Record<Long, String>> emitterTestEvt;





    public boolean publishEventtoDecodedTopic(String eventPayload,String eventType) {

        switch(eventType) {
            case "sample":
                // code block
                logger.info(eventPayload);
                Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
                emitterTestEvt.send(Record.of(uid, eventPayload)).await().atMost(Duration.ofSeconds(5));
                break;
            case "y":
                // code block
                break;
            default:
                // code block
        }


        return false;
    }






}