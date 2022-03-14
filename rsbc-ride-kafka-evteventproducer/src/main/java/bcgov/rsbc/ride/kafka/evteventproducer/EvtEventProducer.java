package bcgov.rsbc.ride.kafka.evteventproducer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import bcgov.rsbc.ride.kafka.evteventproducer.model.issuance.EVT_Issuance_Event;

@Path("/")
public class EvtEventProducer {

    private final static Logger logger = LoggerFactory.getLogger(EvtEventProducer.class);

   
    @Inject 
    @Channel("outgoing-evtIssuanceEvent")
    MutinyEmitter<Record<Long, EVT_Issuance_Event>> emitter;
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "Hello kafka producer";
        
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etk/issuanceEvent")
    public Response publishEvtIssuanceEvent(EVT_Issuance_Event issuanceEvent) {
    	logger.info("Publish etkIssuanceEvent [payload: {}] to kafka.", issuanceEvent);

    	try {
    		logger.info("Call emitter.sendAndAwait to send event.");
    		
    		//Change sendAndAwait to wait at most 5 seconds.
    		//emitter.sendAndAwait(Record.of(eventId, issuanceEvent));
    		Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    		emitter.send(Record.of(uid, issuanceEvent)).await().atMost(Duration.ofSeconds(5));
    		logger.info("Call emitter.sendAndAwait end.");
    		
    		return Response.ok().entity("Event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending event to kafka").build();
        }
    }
}