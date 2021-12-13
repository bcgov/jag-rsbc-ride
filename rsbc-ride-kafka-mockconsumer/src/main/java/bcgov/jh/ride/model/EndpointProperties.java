package bcgov.jh.ride.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Class ApplicationProperties.
 */
@ConfigurationProperties("endpointURL")
public class EndpointProperties {
	
	/** The mock app. */
	public String consumer_application_mock;
}
