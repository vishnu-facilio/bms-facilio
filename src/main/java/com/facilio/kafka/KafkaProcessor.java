package com.facilio.kafka;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.procon.processor.FacilioProcessor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.log4j.LogManager;

import java.sql.SQLException;
import java.util.*;

public class KafkaProcessor {

    private static final HashSet<String> STREAMS = new HashSet<>();
    private static org.apache.log4j.Logger log = LogManager.getLogger(KafkaProcessor.class.getName());
    private static final HashSet<String> EXISTING_ORGS = new HashSet<>();


    private static Properties getLoggingProps(){
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender" );
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }
    private static void updateStream() {
        try {
            AdminClient adminClient = KafkaAdminClient.create(getKafkaProperties());
            ListTopicsResult topicsResult = adminClient.listTopics();
            KafkaFuture<Set<String>> topicNames = topicsResult.names();
            Set<String> streamNames = topicNames.get();
            if (streamNames != null) {
                STREAMS.addAll(streamNames);
            }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    private static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", AwsUtil.getKafkaConsumer());
        properties.put("connections.max.idle.ms", 300000);
        properties.put("receive.buffer.bytes", 65536);
        properties.put("request.timeout.ms", 120000);

        return properties;
    }

    private static void startProcessor() {

//        PropertyConfigurator.configure(getLoggingProps());

        List<FacilioField> columnList = AccountConstants.getOrgFields();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table("Organizations")
                .select(columnList);

        try {
            List<Map<String, Object>> props = builder.get();
            for (Map<String, Object> prop : props) {
                Long orgId = (Long) prop.get("orgId");
                String orgDomainName = (String) prop.get("domain");
                if( ! EXISTING_ORGS.contains(orgDomainName)) {
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
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    private static void startProcessor(long orgId, String orgDomainName) {
        try {
            if(orgDomainName != null && STREAMS.contains(orgDomainName)) {
                System.out.println("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
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


    private static void initiateProcessFactory(long orgId, String orgDomainName, String type) {
        try {
            new Thread(getProcessor(orgId, orgDomainName, type)).start();
        }
        catch (Exception e){
            log.info("Exception occurred ", e);
        }

    }

    private static FacilioProcessor getProcessor(long orgId, String orgDomainName, String type) throws SQLException {

        /*switch(type){
            case "event" :{
                return new EventProcessor(orgId, orgDomainName, type);
            }
            case "timeSeries" :{
                return new TimeSeriesProcessor(orgId, orgDomainName, type);
            }
            case "agent":{
                return new AgentProcessor(orgId,orgDomainName,type);
            }
        }*/
        return new Processor(orgId,orgDomainName);
    }

    public static void start() {
        updateStream();
        startProcessor();
    }
}
