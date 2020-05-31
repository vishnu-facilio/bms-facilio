package com.facilio.services.messageQueue;

import com.facilio.accounts.dto.Organization;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.*;

public abstract class MessageQueue {
    private static final HashSet<String> STREAMS = new HashSet<>();
    private static org.apache.log4j.Logger log = LogManager.getLogger(MessageQueue.class.getName());
    private static final HashSet<String> EXISTING_ORGS = new HashSet<>();
    private static final HashSet<String> ORG_CHECK = new HashSet<>(Arrays.asList("rmzbangalore", "wtcb", "sutherland"));


    private static Properties getLoggingProps(){
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender" );
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }
    private void updateStream() {
        try {

            List<String> streamNames = getTopics();
            if (streamNames != null && STREAMS.isEmpty()) {
                STREAMS.addAll(streamNames);
            } else {
                if (streamNames != null) {
                    for (String stream : streamNames) {
                        if( ! STREAMS.contains(stream)) {
                            STREAMS.add(stream);
                        }
                    }
                }else{
                    log.error("getTopics() returned null");
                }
            }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    private void startProcessor() {

//        PropertyConfigurator.configure(getLoggingProps());

        try {
            List<Organization> orgs = getOrgs();
            if(CollectionUtils.isNotEmpty(orgs)) {

                for (Organization org : orgs) {
                    Long orgId = org.getOrgId();
                    String orgDomainName = org.getDomain();
                    if( (! EXISTING_ORGS.contains(orgDomainName)) && ORG_CHECK.contains(orgDomainName)) {
                        try {
                            startProcessor(orgId, orgDomainName);
                        } catch (Exception e) {
                            try {
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception while starting stream " + orgDomainName, new Exception("Exception while starting stream will retry after 10 sec"));
                                Thread.sleep(10000L);
                                startProcessor(orgId, orgDomainName);
                            } catch (InterruptedException interrupted) {
                                log.info("Exception occurred ", interrupted);
                                CommonCommandUtil.emailException("KafkaProcessor", "Exception while starting stream " + orgDomainName, interrupted);
                            }
                        }
                    }
                }
            }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    private void startProcessor(long orgId, String orgDomainName) {
        try {
            if(orgDomainName != null && STREAMS.contains(orgDomainName)) {
                log.info("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
                initiateProcessFactory(orgId,orgDomainName,"processor");
                /*initiateProcessFactory(orgId, orgDomainName, "event");
                initiateProcessFactory(orgId, orgDomainName, "timeSeries");
                initiateProcessFactory(orgId,orgDomainName,"agent");*/
                EXISTING_ORGS.add(orgDomainName);
            }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    public  void start() {
        updateStream();
        startProcessor();
    }


    public abstract List<String> getTopics()  throws Exception;

    abstract List<Organization> getOrgs() throws Exception;

    public abstract void initiateProcessFactory(long orgId, String orgDomainName, String type) ;


    public abstract void createQueue(String name);

}
