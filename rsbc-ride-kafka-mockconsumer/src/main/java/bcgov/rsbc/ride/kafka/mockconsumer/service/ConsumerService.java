package bcgov.rsbc.ride.kafka.mockconsumer.service;

import java.net.URI;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bcgov.rsbc.ride.kafka.mockconsumer.model.EndpointProperties;


@ApplicationScoped
public class ConsumerService {
	/** The Constant logger. */
	private final static Logger logger = LoggerFactory.getLogger(ConsumerService.class);
	
	/** The endpoints property. */
	@Inject
	EndpointProperties endpointsProperty;
	
	/**
     * Gets the component statuses.
     *
     * @return the component statuses
     */
    public boolean publishEvent(String eventPayload) {
    	String errorDesc;
    	
    	RestClient restClient = null;
    	try {
	    	restClient = RestClientBuilder.newBuilder()
	    				.baseUri(URI.create(endpointsProperty.consumer_application_mock))
	    	            .build(RestClient.class);
    	} catch (Exception e) {
    		errorDesc = "Exception occurred while accessing " + endpointsProperty.consumer_application_mock + "; exception details: " + e.toString() + e.getMessage();
    		logger.error(errorDesc);
    	}  
    	
    	if (restClient != null) {
    		// call publishEvent api
	    	try {
	    		logger.debug("Event to be sent to {}: {}", endpointsProperty.consumer_application_mock, eventPayload);
	    		Response responseEntity = restClient.publishEvent(eventPayload);
	    		if (org.apache.http.HttpStatus.SC_OK == responseEntity.getStatus()) {
	    			return true;
	    		} else {
	    			errorDesc = "HTTP error code " + responseEntity.getStatus() + "\n" + responseEntity.toString();
					logger.error("Error occurred while sending event to consumer application: {}",  errorDesc);
	    		}
	    	} catch (Exception e) {
	    		logger.error("Exception occurred while sending event to consumer application: {}", e.toString());
	    	}
    	}
    	
    	try {
    		if (restClient != null) {
    			restClient.close();
    		}
		} catch (Exception e) {
			logger.error("Failed closing restClient, details: {}", e.toString());
		}
    	return false;
    }
}
