package com.facilio.services.messageQueue;

import java.util.*;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.MessageSource;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.procon.message.FacilioRecord;

import lombok.NonNull;

public abstract class MessageQueue {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> STREAMS = new HashSet<>();
    protected MessageSource messageQueueSource;
    protected boolean started = false;
    MessageQueue(@NonNull MessageSource messageQueueSource) {
    	this.messageQueueSource = messageQueueSource;
    }
    
    protected abstract <T extends MessageSource> T getSource();
    
    /**
     * Entry point for starting processors
     */
    public int startIfNotStarted(Map<String, Object> messageTopic, boolean fetchStream,int currentThreadCount) {
        if(!started){
            if (fetchStream) {
                updateStream();
            }
            currentThreadCount = startProcessor(messageTopic,currentThreadCount);
        }
        started = true;
        return currentThreadCount;
    }

    public void forceStart(Map<String, Object> messageTopic, boolean fetchStream,int currentThreadCount) {

        if (fetchStream) {
            updateStream();
        }
        startProcessor(messageTopic,currentThreadCount);
        started = true;
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
    
    public Object putRecord(String streamName, String key, JSONObject payload) throws Exception {
        JSONObject data;
        if (KafkaUtil.parseData(messageQueueSource)) {
        	// Temp handling. Will be removed
        	data = new JSONObject();
            data.put("data", payload.toJSONString());
        }
        else {
        	data = payload;
        }
        
        FacilioRecord record = new FacilioRecord(key, data);
        return put(streamName, record);
    }


    /**
     * Loops over all orgs and starts the processor
     */
    private int startProcessor(Map<String, Object> topicDetails,int currentThreadCount) {
        try {
            Long orgId = (Long) topicDetails.get(AgentConstants.ORGID);
            String topic = (String) topicDetails.get(AgentConstants.MESSAGE_TOPIC);
            
            if (currentThreadCount < FacilioProperties.getMaxProcessorThreads()) {
            	currentThreadCount = currentThreadCount + startProcessor(orgId, topic, topicDetails, currentThreadCount);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return currentThreadCount;
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

    /*@Override
    public boolean equals(Object o){
        if(!(o instanceof MessageQueue)){
            return false;
        }
        MessageQueue obj = (MessageQueue) o;
        return (Objects.equals(orgId, obj.getOrgId()) && Objects.equals(topic, obj.getTopic()));
    }
*/
}
