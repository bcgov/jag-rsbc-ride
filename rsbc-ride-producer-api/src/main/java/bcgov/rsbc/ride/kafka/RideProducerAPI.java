package bcgov.rsbc.ride.kafka;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

//import org.eclipse.microprofile.config.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.eclipse.microprofile.reactive.messaging.Channel;
import io.smallrye.reactive.messaging.MutinyEmitter;
import io.smallrye.reactive.messaging.kafka.Record;


import bcgov.rsbc.ride.kafka.models.testevent;
import bcgov.rsbc.ride.kafka.models.payloadrecord;
import bcgov.rsbc.ride.kafka.apiKeys;

//import org.eclipse.microprofile.config.ConfigProvider.*;



@Path("/")
public class RideProducerAPI {

    private final static Logger logger = LoggerFactory.getLogger(RideProducerAPI.class);

    @Inject
    @Channel("outgoing-testevent")
    MutinyEmitter<Record<Long, payloadrecord>> emitterTestEvt;


    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "pong";
    }


    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/sendtestevent")
    public Response publishTestEvent(@HeaderParam("ride-api-key") String apiKey, testevent testeventobj) {
        logger.info(apiKey);
        List<apiKeys> allkeys = apiKeys.listAll();
        logger.info(String.valueOf(allkeys.get(0).apikeyval));
//        apiKeys.
        logger.info("Publish testevent [payload: {}] to kafka.", testeventobj.getPayload());
//        logger.info("{}",issuanceEvent.getPayload().get(0));
//        logger.info("{}",issuanceEvent.getTypeofevent());
//        return Response.ok().entity("Issuance event sent successfully").build();
        payloadrecord payloaddata=(payloadrecord) testeventobj.getPayload().get(0);

        try {
            //Change sendAndAwait to wait at most 5 seconds.
            Long uid = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            logger.info("Kafka event UID: {}", uid);
//            emitterTestEvt.send(Record.of(uid, payloaddata)).await().atMost(Duration.ofSeconds(5));
            return Response.ok().entity("success").build();
        } catch (Exception e) {
            logger.error("Exception occurred while sending issuance event, exception details: {}", e.toString() + "; " + e.getMessage());
            return Response.serverError().entity("Failed sending test event to kafka").build();
        }
    }






}


