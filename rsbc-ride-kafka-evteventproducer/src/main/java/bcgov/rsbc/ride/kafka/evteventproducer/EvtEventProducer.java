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
import bcgov.rsbc.ride.kafka.evteventproducer.model.payment.EVT_Payment_Event;
import bcgov.rsbc.ride.kafka.evteventproducer.model.dispute.EVT_Dispute_Event;
import bcgov.rsbc.ride.kafka.evteventproducer.model.dispute_statusupdate.EVT_DisputeStatusUpdate_Event;

@Path("/")
public class EvtEventProducer {

    private final static Logger logger = LoggerFactory.getLogger(EvtEventProducer.class);

   
    @Inject 
    @Channel("outgoing-evtIssuanceEvent")
    MutinyEmitter<Record<Long, EVT_Issuance_Event>> emitterIssuanceEvt;

    @Inject 
    @Channel("outgoing-vtPaymentEvent")
    MutinyEmitter<Record<Long, EVT_Payment_Event>> emitterPaymentEvt;

    @Inject 
    @Channel("outgoing-vtDisputeEvent")
    MutinyEmitter<Record<Long, EVT_Dispute_Event>> emitterDisputeEvt;

    @Inject 
    @Channel("outgoing-vtDisputeSUEvent")
    MutinyEmitter<Record<Long, EVT_DisputeStatusUpdate_Event>> emitterDisputeSUEvt;
    
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
    		
    		//Change sendAndAwait to wait at most 5 seconds.
    		//emitterIssuanceEvt.sendAndAwait(Record.of(eventId, issuanceEvent));
    		Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
    		emitterIssuanceEvt.send(Record.of(uid, issuanceEvent)).await().atMost(Duration.ofSeconds(5));
    		
    		return Response.ok().entity("Issuance event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending issuance event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending issuance event to kafka").build();
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etk/paymentEvent")
    public Response publishVTPaymentEvent(EVT_Payment_Event paymentEvent) {
    	logger.info("Publish etkPaymentEvent [payload: {}] to kafka.", paymentEvent);

    	try {
    		Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
    		emitterPaymentEvt.send(Record.of(uid, paymentEvent)).await().atMost(Duration.ofSeconds(5));
    		
    		return Response.ok().entity("Payment event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending payment event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending paymentevent to kafka").build();
        }
    }
    

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etk/disputeEvent")
    public Response publishVTDispuateEvent(EVT_Dispute_Event disputeEvent) {
    	logger.info("Publish etkDisputeEvent [payload: {}] to kafka.", disputeEvent);

    	try {
    		Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
    		emitterDisputeEvt.send(Record.of(uid, disputeEvent)).await().atMost(Duration.ofSeconds(5));
    		
    		return Response.ok().entity("Dispute event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending dispute event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending dispute event to kafka").build();
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etk/disputeStatusUpdateEvent")
    public Response publishVTDispuateSUEvent(EVT_DisputeStatusUpdate_Event disputeSUEvent) {
    	logger.info("Publish etkDisputeStatusUpdateEvent [payload: {}] to kafka.", disputeSUEvent);

    	try {
    		Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
    		emitterDisputeSUEvt.send(Record.of(uid, disputeSUEvent)).await().atMost(Duration.ofSeconds(5));
    		
    		return Response.ok().entity("Dispute status update event sent successfully").build();
    	} catch (Exception e) {
    		logger.error("Exception occurred while sending dispute status update event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending dispute status update event to kafka").build();
        }
    }
}