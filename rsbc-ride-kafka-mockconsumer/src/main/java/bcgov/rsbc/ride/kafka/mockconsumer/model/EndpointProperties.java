package bcgov.rsbc.ride.kafka.mockconsumer.model;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The Class ApplicationProperties.
 */
@ConfigurationProperties("endpointURL")
public class EndpointProperties {
	
	/** The mock app. */
	public String consumer_application_mock;
}
