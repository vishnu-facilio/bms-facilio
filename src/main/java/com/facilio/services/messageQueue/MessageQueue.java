package com.facilio.services.messageQueue;

import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

public abstract class MessageQueue {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> STREAMS = new HashSet<>();
    private static final Map<String, Integer> EXISTING_ORG_PARTITION = new HashMap<>();

    private static final List<Long> EMPTY_LIST = Collections.emptyList();


    /**
     * Entry point for starting processors
     */
    public void start() {
        updateStream();
        startProcessor();
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
    private void startProcessor() {
        //PropertyConfigurator.configure(getLoggingProps());
        int currentThreadCount = 0;
        try {
            List<Map<String, Object>> orgMessageTopics = MessageQueueTopic.getTopics(EMPTY_LIST);
            if (CollectionUtils.isNotEmpty(orgMessageTopics)) {
                for (Map<String, Object> topicDetails : orgMessageTopics) {
                    Long orgId = (Long) topicDetails.get(AgentConstants.ORGID);
                    String orgDomainName = (String) topicDetails.get(AgentConstants.MESSAGE_TOPIC);
                    if (currentThreadCount < FacilioProperties.getMaxProcessorThreads()) {
                        try {
                            currentThreadCount = currentThreadCount + startProcessor(orgId, orgDomainName, topicDetails, currentThreadCount);
                        } catch (Exception e) {
                            try {
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception while starting stream " + orgDomainName, new Exception("Exception while starting stream will retry after 10 sec"));
                                Thread.sleep(10000L);
                                currentThreadCount = currentThreadCount + startProcessor(orgId, orgDomainName, topicDetails, currentThreadCount);
                            } catch (InterruptedException interrupted) {
                                LOGGER.info("Exception occurred ", interrupted);
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception again while starting stream " + orgDomainName, interrupted);
                            }
                        }
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    private int startProcessor(long orgId, String orgDomainName, Map<String, Object> topicDetails, int currentThreadCount) {
        int noOfProcessorsStarted = 0;
        try {
            if (orgDomainName != null && STREAMS.contains(orgDomainName)) {
                LOGGER.info("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
                noOfProcessorsStarted = initiateProcessFactory(orgId, orgDomainName, "processor", topicDetails, currentThreadCount);
                /*initiateProcessFactory(orgId, orgDomainName, "event");
                initiateProcessFactory(orgId, orgDomainName, "timeSeries");
                initiateProcessFactory(orgId,orgDomainName,"agent");*/

                //EXISTING_ORG_PARTITION.put(orgDomainName, processorId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return noOfProcessorsStarted;
    }

    public abstract int initiateProcessFactory(long orgId, String orgDomainName, String type, Map<String, Object> topicDetails, int currentThreadCount) throws Exception;

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

    private static Properties getLoggingProps() {
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender");
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }

}
