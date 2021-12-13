package bcgov.jh.ride.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


/**
 * The Interface JhEtkService.
 */
public interface RestClient extends AutoCloseable{

	/**
	 * Ready.
	 *
	 * @return the string
	 */
	@GET
    @Path("/ready")
	String ready();
	
	/**
	 * Ping.
	 *
	 * @return the string
	 */
	@GET
    @Path("/ping")
    String ping();
	
	/**
	 * Publish event.
	 */
	@POST
	@Path("/publish/event")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	Response publishEvent(@org.eclipse.microprofile.openapi.annotations.parameters.RequestBody String eventPayload);
}
