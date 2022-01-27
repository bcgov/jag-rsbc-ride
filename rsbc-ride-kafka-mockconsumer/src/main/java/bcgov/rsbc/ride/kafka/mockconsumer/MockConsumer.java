package bcgov.rsbc.ride.kafka.mockconsumer;

import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bcgov.rsbc.ride.kafka.mockconsumer.service.ConsumerService;
import io.smallrye.reactive.messaging.kafka.Record;

@Path("/")
public class MockConsumer {

    private final static Logger logger = LoggerFactory.getLogger(MockConsumer.class);
    
    @Inject
	ConsumerService consumerService;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "Hello kafka consumer";
        
    }

    @Incoming("incoming-channel")
    public CompletionStage<Void> receive(Message<Record<Integer, String>> record) {
        logger.info("Got a etkEvent [eventID: {}; eventPayload: {}]", record.getPayload().key(), record.getPayload().value());
        boolean sent = consumerService.publishEvent(record.getPayload().value());
        if (sent) {
        	logger.info("Event successfully published to consumer application");
        	return record.ack();
        } else {
        	logger.error("Event failed published to consumer application");
        	return record.nack(new Exception("Event failed published to consumer application"));
        }
    }
    
}