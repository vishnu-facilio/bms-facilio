package com.facilio.services.messageQueue;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.MessageSource;
import com.facilio.services.procon.message.FacilioRecord;

import lombok.NonNull;

public abstract class MessageQueue {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> STREAMS = new HashSet<>();
    
    protected MessageSource messageQueueSource;
    
    MessageQueue(@NonNull MessageSource messageQueueSource) {
    	this.messageQueueSource = messageQueueSource;
    }
    
    protected abstract <T extends MessageSource> T getSource();
    
    /**
     * Entry point for starting processors
     */
    public void start(Map<String, Object> messageTopic, boolean fetchStream) {
    	if (fetchStream) {
    		updateStream();
    	}
        /*
        //In case of server restart,
        //this wait time ensures that the broker knows the consumer has been killed.
        //i.e active members list entry for all the consumers in the current machine expires
        Thread.sleep(MIN_SESSION_TIMEOUT);
        */
        startProcessor(messageTopic);
    }


    /**
     * Gets list of topics/streams
     *
     * @return list of topics/streams
     */

    public abstract List<String> getTopics();

    /**
     * Creates a new message queue
     *
     * @param name name of the queue
     */

    public abstract void createQueue(String name);

    /**
     * Pushes record to the stream
     *
     * @param streamName Name of the stream
     * @param record     record to be pushed to the stream
     * @return
     */
    public abstract Object put(String streamName, FacilioRecord record) throws Exception;

    /**
     * Pushes record to the stream
     *
     * @param streamName Name of the stream
     * @param records    List of records to be pushed to the stream
     * @return
     */
    public abstract Object put(String streamName, List<FacilioRecord> records) throws Exception;

    /**
     * Loops over all orgs and starts the processor
     */
    private void startProcessor(Map<String, Object> topicDetails) {
        int currentThreadCount = 0;
        try {
        	Long orgId = (Long) topicDetails.get(AgentConstants.ORGID);
            String topic = (String) topicDetails.get(AgentConstants.MESSAGE_TOPIC);
            
            if (currentThreadCount < FacilioProperties.getMaxProcessorThreads()) {
            	currentThreadCount = currentThreadCount + startProcessor(orgId, topic, topicDetails, currentThreadCount);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }
    
    protected abstract int getConsumersOnlineCount(String topic) throws Exception;


    private int startProcessor(long orgId, String topic, Map<String, Object> topicDetails, int currentThreadCount) {
        int noOfProcessorsStarted = 0;
        try {
            if (topic != null && STREAMS.contains(topic)) {
            	
            	 int maxConsumers = (Integer) topicDetails.get(AgentConstants.MAX_CONSUMERS);
                 int maxConsumersPerInstance = (Integer) topicDetails.get(AgentConstants.MAX_CONSUMERS_PER_INSTANCE);
                 
                 String consumerGroup = getConsumerGroup(topic);
                 int numberOfConsumersOnline = getConsumersOnlineCount(consumerGroup);
                 int consumersLeftToStart = maxConsumers - numberOfConsumersOnline;
                 
                 while (consumersLeftToStart > 0 && noOfProcessorsStarted < maxConsumersPerInstance && currentThreadCount < FacilioProperties.getMaxProcessorThreads()) {
                	 initiateProcessFactory(orgId, topic, consumerGroup, noOfProcessorsStarted);
                     
                     currentThreadCount++;
                     consumersLeftToStart--;
                     noOfProcessorsStarted++;
                 }
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return noOfProcessorsStarted;
    }

    protected abstract void initiateProcessFactory(long orgId, String topic, String consumerGroup, int processorId) throws Exception;

    private void updateStream() {
        try {
            List<String> streamNames = getTopics();
            if (streamNames != null && STREAMS.isEmpty()) {
                STREAMS.addAll(streamNames);
            } else {
                if (streamNames != null) {
                    for (String stream : streamNames) {
                        if (!STREAMS.contains(stream)) {
                            STREAMS.add(stream);
                        } else {
                            LOGGER.info("Stream already exists");
                        }
                    }
                } else {
                    LOGGER.error("getTopics() returned null");
                }
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }
    
    private String getConsumerGroup(String topic) {
		String environment = FacilioProperties.getConfig("environment");
        return topic + "-processor-" + environment;
	}

    private static Properties getLoggingProps() {
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }

}
