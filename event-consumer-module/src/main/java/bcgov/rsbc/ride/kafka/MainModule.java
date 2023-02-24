package bcgov.rsbc.ride.kafka;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;






@Path("/ping")
public class MainModule {

    private final static Logger logger = LoggerFactory.getLogger(RideConsumerModule.class);



    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String pingservice() {
        return "pong from consumer service";

    }


}