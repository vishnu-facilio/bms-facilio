package com.facilio.services.messageQueue;

import com.facilio.accounts.dto.Organization;
import com.facilio.agentv2.AgentConstants;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public abstract class MessageQueue {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> STREAMS = new HashSet<>();
    private static final HashSet<String> EXISTING_ORGS = new HashSet<>();

    private static final List<Long> EMPTY_LIST = Collections.emptyList();

    static HashSet<String> getSTREAMS() {
        return STREAMS;
    }

    static HashSet<String> getExistingOrgs() {
        return EXISTING_ORGS;
    }

    /**
     * Entry point for starting processors
     */
    public void start() {
        updateStream();
        startProcessor();
    }

    /**
     * Starts processor for specific org
     *
     * @param orgId         Org ID
     * @param orgDomainName Org Domain Name
     */
    abstract void startProcessor(long orgId, String orgDomainName);

    /**
     * Pushes record to the message queue
     *
     * @param orgId         Org ID
     * @param orgDomainName Org Domain Name
     * @param type          Type of the processor event,processor,alarm etc.,
     * @param record        record containing the message to put to the queue
     * @throws Exception Throws exception if stream not found
     */

    public abstract void put(long orgId, String orgDomainName, String type, FacilioRecord record) throws Exception;

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
