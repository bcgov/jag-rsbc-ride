package bcgov.rsbc.ride.kafka.evteventproducer;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.microprofile.reactive.messaging.Message;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;
import bcgov.rsbc.ride.kafka.evteventproducer.model.issuance.EVT_Issuance_Event;
import bcgov.rsbc.ride.kafka.evteventproducer.model.payment.EVT_Payment_Event;
import bcgov.rsbc.ride.kafka.evteventproducer.model.dispute.EVT_Dispute_Event;
import bcgov.rsbc.ride.kafka.evteventproducer.model.dispute_statusupdate.EVT_DisputeStatusUpdate_Event;

/**
 * The Class KafkaClients.
 */
@Path("/consume-evtIssuanceEvent")
public class EvtEventConsumer {

	private final static Logger logger = LoggerFactory.getLogger(EvtEventConsumer.class);
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/ping")
    public String hello() {
        return "Hello kafka consumer";
        
    }

//    @Channel("incoming-evtIssuanceEvent")
//    Multi<EVT_Issuance_Event> issuanceEvents;
//     
//    @GET
//    @Produces(MediaType.SERVER_SENT_EVENTS)
//    public Multi<String> stream() {
//        return issuanceEvents.map(issuanceEvent -> String.format("'%s' from %s", issuanceEvent.getTicketNo(), issuanceEvent.getSentTm()));
//    }

//    Multi<Movie> movies;
    
//    @Incoming("incoming-evtIssuanceEvent")
//    public CompletionStage<Void> receive(Message<Record<Integer, EVT_Issuance_Event>> record) {
//        logger.info("Got a etkIssuance Event [eventID: {}; eventPayload: {}]", record.getPayload().key(), record.getPayload().value());
//        return record.ack();
//    }

    @Incoming("incoming-evtIssuanceEvent")
    public void receive(EVT_Issuance_Event event) {
        logger.info("Received evt issuance event, ticketNO: {}; ticketSubmitDate: {}; ticketSentTime: {}. Payload: {}", event.getTicketNo(), event.getSubmitDt(), event.getSentTm(), event);
    }
    
    

    @Incoming("incoming-vtPaymentEvent")
    public void receive(EVT_Payment_Event event) {
        logger.info("Received payment event, ticketNO: {}; Payload: {}", event.getTicketNo(), event);
    }

    @Incoming("incoming-vtDisputeEvent")
    public void receive(EVT_Dispute_Event event) {
        logger.info("Received dispute event, contraventionNO: {}; Payload: {}", event.getContraventionNo(), event);
    }

    @Incoming("incoming-vtDisputeSUEvent")
    public void receive(EVT_DisputeStatusUpdate_Event event) {
        logger.info("Received dispute status update event, ticketNO: {}; Payload: {}", event.getTicketNo(), event);
    }
}