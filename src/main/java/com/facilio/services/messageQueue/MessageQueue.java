package com.facilio.services.messageQueue;

import com.facilio.accounts.dto.Organization;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.functions.T;
import sun.security.util.ObjectIdentifier;

import java.util.*;

public abstract class MessageQueue {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> STREAMS = new HashSet<>();
    private static final HashSet<String> EXISTING_ORGS = new HashSet<>();

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
    public abstract Object put(String streamName, FacilioRecord record);

    /**
     * Pushes record to the stream
     *
     * @param streamName Name of the stream
     * @param records    List of records to be pushed to the stream
     * @return
     */
    public abstract Object put(String streamName, List<FacilioRecord> records);

    /**
     * Loops over all orgs and starts the processor
     */
    private void startProcessor() {
        //PropertyConfigurator.configure(getLoggingProps());
        try {
            List<Map<String, Object>> orgMessageTopics = MessageQueueTopic.getTopics(EMPTY_LIST);
            if (CollectionUtils.isNotEmpty(orgMessageTopics)) {
                for (Map<String, Object> org : orgMessageTopics) {
                    Long orgId = (Long) org.get(AgentConstants.ORGID);
                    String orgDomainName = (String) org.get(AgentConstants.MESSAGE_TOPIC);
                    if ((!EXISTING_ORGS.contains(orgDomainName))) {
                        try {
                            startProcessor(orgId, orgDomainName);
                        } catch (Exception e) {
                            try {
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception while starting stream " + orgDomainName, new Exception("Exception while starting stream will retry after 10 sec"));
                                Thread.sleep(10000L);
                                startProcessor(orgId, orgDomainName);
                            } catch (InterruptedException interrupted) {
                                LOGGER.info("Exception occurred ", interrupted);
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception while starting stream " + orgDomainName, interrupted);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    private void startProcessor(long orgId, String orgDomainName) {
        try {
            if (orgDomainName != null && STREAMS.contains(orgDomainName)) {
                LOGGER.info("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
                initiateProcessFactory(orgId, orgDomainName, "processor");
                /*initiateProcessFactory(orgId, orgDomainName, "event");
                initiateProcessFactory(orgId, orgDomainName, "timeSeries");
                initiateProcessFactory(orgId,orgDomainName,"agent");*/
                EXISTING_ORGS.add(orgDomainName);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    public abstract void initiateProcessFactory(long orgId, String orgDomainName, String type) ;

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
