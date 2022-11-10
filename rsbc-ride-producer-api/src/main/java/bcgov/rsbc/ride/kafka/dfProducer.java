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

import bcgov.rsbc.ride.kafka.models.df_appacceptedevent;
import bcgov.rsbc.ride.kafka.models.df_appacceptedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.df_disclosuresentevent;
import bcgov.rsbc.ride.kafka.models.df_disclosuresentpayloadrecord;
import bcgov.rsbc.ride.kafka.models.df_evidencesubmittedevent;
import bcgov.rsbc.ride.kafka.models.df_evidencesubmittedpayloadrecord;
import bcgov.rsbc.ride.kafka.models.df_payrecvdevent;
import bcgov.rsbc.ride.kafka.models.df_payrecvdpayloadrecord;
import bcgov.rsbc.ride.kafka.models.df_reviewscheduleddevent;
import bcgov.rsbc.ride.kafka.models.df_reviewscheduledpayloadrecord;

@Path("/dfevents")
public class dfProducer {



    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public String dfping() {
        return "df";
    }
}
