package bcgov.rsbc.ride.kafka;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/ping")
public class RideProducerAPI {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "pong";
    }
}


