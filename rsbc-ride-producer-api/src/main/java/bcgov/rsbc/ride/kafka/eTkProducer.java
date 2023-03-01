package bcgov.rsbc.ride.kafka;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;

import javax.ws.rs.core.Response;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;

import bcgov.rsbc.ride.kafka.model.testevent;

import io.vertx.core.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/etkevents")
public class eTkProducer {

    private final static Logger logger = LoggerFactory.getLogger(dfProducer.class);

    @Inject
    @Channel("outgoing-etktestevent")
    MutinyEmitter<Record<Long, testEvent>> emitterTestEvt;


    @GET
    @Path("/ping")
    @Produces(MediaType.APPLICATION_JSON)
    public Response etkping() {
        return Response.ok().entity("{\"status\":\"working\"}").build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etktestevent")
    public Response etktestevent(String eventobj) {
        logger.info(eventobj);
        Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
//        emitterTestEvt.send(Record.of(uid.toString(), eventobj)).await().atMost(Duration.ofSeconds(5));

        return Response.ok().entity("{\"status\":\"working\"}").build();
    }



    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/etktestevent1")
    public Response etktestevent1(testEvent message) {
        logger.info(message.toString());
//        testevent newEvent=(testevent) message.toString();
        Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        try{
            emitterTestEvt.send(Record.of(uid, message)).await().atMost(Duration.ofSeconds(5));

            return Response.ok().entity("{\"status\":\"working\"}").build();

        }catch (Exception e) {
            logger.error("[RIDE]: Exception occurred while sending app_accepted event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending  event to kafka").build();
        }

    }



}
