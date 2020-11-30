package com.facilio.services.messageQueue;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.kafka.KafkaProcessor;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.log4j.LogManager;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaMessageQueue extends MessageQueue {
    private static org.apache.log4j.Logger LOGGER = LogManager.getLogger(KafkaMessageQueue.class.getName());
    private static AdminClient kafkaClient = null;
    private static final KafkaMessageQueue INSTANCE =new KafkaMessageQueue();
    private static Map<String, FacilioProcessor> KAFKA_PROCESSOR_MAP = new HashMap<>();

    private KafkaMessageQueue(){}
    static KafkaMessageQueue getClient(){
        return INSTANCE;
    }
    private static AdminClient getKafkaClient(){
        if (kafkaClient==null){
            kafkaClient =KafkaAdminClient.create(getKafkaProperties());
        }
        return kafkaClient;
    }

    @Override
    void startProcessor(long orgId, String orgDomainName) {
        try {
            if (orgDomainName != null && getSTREAMS().contains(orgDomainName)) {
                LOGGER.info("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
                FacilioProcessor kafkaProcessor = getProcessor(orgId, orgDomainName, "processor");
                new Thread(kafkaProcessor).start();
                KAFKA_PROCESSOR_MAP.put(orgId + "-" + orgDomainName + "-" + "processor", kafkaProcessor);
                getExistingOrgs().add(orgDomainName);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

    @Override
    public List<String> getTopics(){
        AdminClient adminClient = getKafkaClient();
        ListTopicsResult topicsResult = adminClient.listTopics();
        KafkaFuture<Set<String>> topicNames = topicsResult.names();
        try {
            Set<String> streamNames = topicNames.get(10,TimeUnit.SECONDS);
            return new ArrayList<>(streamNames);
        } catch (InterruptedException e) {
            LOGGER.error("Interrupted Exception occured while getting topics in kafka");
        } catch (ExecutionException e) {
            LOGGER.error("Execution Exception occured while getting topics in kafka");
        } catch (TimeoutException e) {
            LOGGER.error("Timed out while getting topics in kafka");
        }
        return null;
    }


    private static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", FacilioProperties.getKafkaConsumer());
        properties.put("connections.max.idle.ms", 300000);
        properties.put("receive.buffer.bytes", 65536);
        properties.put("request.timeout.ms", 120000);

        return properties;
    }

    public void put(long orgId, String orgDomainName, String type, FacilioRecord record) throws Exception {
        FacilioProcessor processor = KAFKA_PROCESSOR_MAP.get(orgId + "-" + orgDomainName + "-" + type);
        if (processor != null) {
            processor.put(record);
        } else {
            throw new Exception("Kafka-Processor not found in map");
        }
    }

    @Override
    public void createQueue(String queueName) {
        LOGGER.info(" creating kafka stream "+queueName);
        try {
            CreateTopicsResult result =getKafkaClient().createTopics(Collections.singletonList(new NewTopic(queueName, 1, (short)3)));
            result.values().get(queueName).get(10, TimeUnit.SECONDS);
            LOGGER.info("Stream created : " + queueName );
        } catch (Exception ex){
            LOGGER.info(" Exception Stream exists for name : " + queueName +" "+ ex.getMessage());
        }
    }


    private static FacilioProcessor getProcessor(long orgId, String orgDomainName, String type) {
        return new KafkaProcessor(orgId,orgDomainName);
    }
}
