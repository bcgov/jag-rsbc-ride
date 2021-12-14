package bcgov.rsbc.ride.kafka.mockproducer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;
import io.smallrye.common.annotation.Identifier;

// TODO: Auto-generated Javadoc
/**
 * The Class KafkaClients.
 */
//@ApplicationScoped
public class KafkaClients {

//	/** The Constant logger. */
//	private final static Logger logger = LoggerFactory.getLogger(KafkaClients.class);
//	
//    // "default-kafka-broker" is the default name of the configuration map.
//    // It can be overwritten using 'kafka-configuration' attribute.
//    /** The config. */
//    // mp.messaging.incoming.my-channel.kafka-configuration=my-configuration
//	@Inject
//    @Identifier("default-kafka-broker")
//    Map<String, Object> config;
//
//    /**
//     * On startup.
//     *
//     * @param startupEvent the startup event
//     */
//    void onStartup(@Observes StartupEvent startupEvent) {
//    	logger.info("MockProducer is starting up...");
//    	
//    	String topicName = "mock-etk-event";
//    	try {
//    		logger.info("Create an instance of adminClient");
//    		AdminClient adminClient = getAdmin();
//	        
//	        // Below code is to create a topic
//    		int partitions = 1;
//	        short replicationFactor = 1;
//	        String topRetentionPeriodInMS = "7776000000"; // topic retention period: 90 days
//	        String cleanupPolicy = TopicConfig.CLEANUP_POLICY_DELETE;
//		    logger.info("Attempting to create topic {}, partitions: {}, replicationFactor: {}, topRetentionPeriodInMS: {}, cleanupPolicy: {}", topicName, partitions, replicationFactor, topRetentionPeriodInMS, cleanupPolicy);
//        	
//	        Map<String, String> topicConfigs = new HashMap<>();
//	        topicConfigs.put(TopicConfig.RETENTION_MS_CONFIG, topRetentionPeriodInMS);
//	        topicConfigs.put(TopicConfig.CLEANUP_POLICY_CONFIG, cleanupPolicy);
//	        
//	        CreateTopicsResult result = adminClient.createTopics(
//	        		Collections.singleton(new NewTopic(topicName, partitions, replicationFactor).configs(topicConfigs)
//	               ));
//	
//	        // Call values() to get the result for a specific topic
//	        KafkaFuture<Void> future = result.values().get(topicName);
//	
//	        // Call get() to block until the topic creation is complete or has failed
//	        // if creation failed the ExecutionException wraps the underlying cause.
//	        future.get();
//	        logger.info("Topic {} is created.", topicName);
//	    } catch (Exception e) {
//	    	logger.error("Exception occurred while creating topic: {}, exception details: {}", topicName, e.toString() + "; " + e.getMessage());
//	    }
//    }
//    
//    /**
//     * Gets the admin.
//     *
//     * @return the admin
//     */
//    @Produces
//    AdminClient getAdmin() {
//        Map<String, Object> copy = new HashMap<>();
//        for (Map.Entry<String, Object> entry : config.entrySet()) {
//            if (AdminClientConfig.configNames().contains(entry.getKey())) {
//                copy.put(entry.getKey(), entry.getValue());
//            }
//        }
//        
//        AdminClient adminClient = null;
//        try {
//	        adminClient = KafkaAdminClient.create(copy);
//	    } catch (Exception e) {
//	    	logger.error("Exception occurred while creating KafkaAdminClient object, exception details: {}", e.toString() + "; " + e.getMessage());
//	    }
//        return adminClient;
//    }

}