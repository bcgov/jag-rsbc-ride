package bcgov.rsbc.ride.kafka.mockproducer;

import java.time.Duration;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;

@Path("/")
public class MockProducer {

    private final static Logger logger = LoggerFactory.getLogger(MockProducer.class);

   
    @Inject 
    @Channel("outgoing-channel")
    //Emitter<Event> emitter;
    //Emitter<Record<Integer, String>> emitter;
    MutinyEmitter<Record<Integer, String>> emitter;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "Hello kafka producer";
        
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/apps/etk/businessEvent/{eventId}")
    public Response publishEtkBusinessEvents(@PathParam("eventId") Integer eventId, String etkEvent) {
    	logger.info("Publish etkBusinessEvent [eventId: {}, payload: {} to kafka.", eventId, etkEvent);

    	try {
    		logger.info("Call emitter.sendAndAwait to send event.");
    		
    		//Change sendAndAwait to wait at most 5 seconds.
    		//emitter.sendAndAwait(Record.of(eventId, etkEvent));
    		emitter.send(Record.of(eventId, etkEvent)).await().atMost(Duration.ofSeconds(5));
    		logger.info("Call emitter.sendAndAwait end.");
    		
    		return Response.ok().entity("Event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending event to kafka").build();
        }

//    	try {
//            Uni.createFrom().completionStage(emitter.send(Record.of(eventId, etkEvent)))
//                    .await().indefinitely();
//
//            return Response.ok().entity("foo").build();
//        } catch (Exception e) {
//            return Response.serverError().build();
//        }
    	
//    	return Uni.createFrom().completionStage(emitter.send(Record.of(eventId, etkEvent)))
//                .onItem().transform(ignored -> Response.ok().entity("Event sent successfully").build())
//                .onFailure().recoverWithItem(Response.serverError().build());
    	
//        emitter.send(Message.of(Record.of(eventId, etkEvent))
//        	.withAck(() -> { 
//        		logger.info("Event published, ack returned from kafka broker");
//        		
//        		// Called when the message is acked 
//        		return CompletableFuture.completedFuture(null);
//        		//return Response.ok("evtEvent successfully sent to Kafka").build();
//        	})
//        	.withNack(throwable -> { 
//        		logger.error("Failed sending evtEvent [eventID: {}] to kafka. nack returned from kafka broker", eventId);
//				  // Called when the message is nacked return
//				  return CompletableFuture.completedFuture(null); 
//				  
//        	}));
//        return Response.status(Status.NOT_FOUND.getStatusCode(), "Failed publish evtEvent [eventID: " + eventId + "] to kafka").build();

    	//    	CompletionStage<Void> result;
//   	   	try {
//   	   		result = emitter.send(Record.of(eventId, etkEvent));
//   	   	}catch (Exception e) {
//   	   		logger.error("Failed sending evtEvent [eventID: {}] to kafka. Exception details: {}", eventId, e.toString() + "; " + e.getMessage());
//   	   		return Response.status(Status.NOT_FOUND.getStatusCode(), "Failed publish evtEvent [eventID: " + eventId + "] to kafka").build();
//   	   	}
//   	   	
//    	logger.info("Event published, returns from kafka broker: {}", result.toString());
//    	return Response.ok("evtEvent successfully sent to Kafka").build();
    }
    
    
//    @Incoming("incoming-channel")
//    public void receive(Record<Integer, Event> record) {
//        logger.info("Got a etkEvent: %d - %s", record.key(), record.value());
//    }
    
}