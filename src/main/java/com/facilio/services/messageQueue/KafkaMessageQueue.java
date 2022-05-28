package com.facilio.services.messageQueue;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.log4j.LogManager;

import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSource;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.kafka.KafkaProcessor;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;

class KafkaMessageQueue extends MessageQueue {
	
	private static org.apache.log4j.Logger LOGGER = LogManager.getLogger(KafkaMessageQueue.class.getName());
    private AdminClient kafkaClient = null;
    private FacilioKafkaProducer producer = null;

    private AdminClient getKafkaClient() {
        if (kafkaClient==null){
            kafkaClient =KafkaAdminClient.create(getKafkaProperties());
        }
        return kafkaClient;
    }
    
    KafkaMessageQueue(MessageSource messageQueueSource) {
		super(messageQueueSource);
	}
    
    @SuppressWarnings("unchecked")
	protected KafkaMessageSource getSource() {
    	return (KafkaMessageSource) messageQueueSource;
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


    private Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", getSource().getBroker());
        properties.put("connections.max.idle.ms", 300000);
        properties.put("receive.buffer.bytes", 65536);
        properties.put("request.timeout.ms", 120000);
        KafkaUtil.setKafkaAuthProps(properties, getSource());
        return properties;
    }

    @Override
    public List<RecordMetadata> put(String streamName, List<FacilioRecord> records) throws Exception {
        List<RecordMetadata> listOfRecordMetaData = new ArrayList<>();
        if (producer == null) {
        	producer = new FacilioKafkaProducer(getSource());
        }
        try {
            for (FacilioRecord record : records) {
                listOfRecordMetaData.add(producer.putRecord(streamName, record));
            }
        } catch (Exception ex) {
            producer.close();
            throw ex;
        }
        return listOfRecordMetaData;
    }

    @Override
    public RecordMetadata put(String streamName, FacilioRecord record) throws Exception {
        List<FacilioRecord> records = new ArrayList<>();
        records.add(record);
        return put(streamName, records).get(0);
    }

    @Override
    public void createQueue(String queueName) {
        LOGGER.info(" creating kafka stream "+queueName);
        try {
            CreateTopicsResult result = getKafkaClient().createTopics(Collections.singletonList(new NewTopic(queueName, 1, (short) 3)));
            result.values().get(queueName).get(10, TimeUnit.SECONDS);
            LOGGER.info("Stream created : " + queueName);

            if (!FacilioProperties.isMessageProcessor()) {
                // Topic would be created in user server and client is not needed anymore
                getKafkaClient().close();
                kafkaClient = null;
            }

        } catch (Exception ex){
            LOGGER.info(" Exception occurred while adding " + queueName +" - "+ ex.getMessage());
        }
    }


    private FacilioProcessor getProcessor(long orgId, String topic, String consumerGroup, int processorId, KafkaMessageSource messageSource) {
        return new KafkaProcessor(orgId, topic, consumerGroup, processorId, messageSource);
    }
    
    @Override
    protected int getConsumersOnlineCount(String consumerGroup) throws Exception {
    	
    	 AdminClient kafkaClient = getKafkaClient();
         DescribeConsumerGroupsResult describeConsumerGroupsResult = kafkaClient.describeConsumerGroups(Collections.singleton(consumerGroup));
         int numberOfConsumersOnline = 0;
         List<String> consumerGroupList = kafkaClient.listConsumerGroups().all().get(5, TimeUnit.SECONDS).stream().map(ConsumerGroupListing::groupId).collect(Collectors.toList());
         LOGGER.info("ConsumerGroups : "+ consumerGroupList);
         try {
             if (consumerGroupList.contains(consumerGroup)) {
                 Collection<MemberDescription> members = describeConsumerGroupsResult.describedGroups().get(consumerGroup).get(5, TimeUnit.SECONDS).members();
                 //zombies are entries of consumers in the kafka members list for this consumer group
                 //which are dead but the entry expires only after session.timeout, so exclude them from counting as active members
                 int zombies = 0;
                 for (MemberDescription m : members) {
                     if (m.clientId().startsWith(ServerInfo.getHostname())) {
                         zombies++;
                     }
                 }
                 LOGGER.info("members = "+members.size()+" , zombies = "+ zombies);
                 numberOfConsumersOnline = members.size()-zombies;
                 LOGGER.info("number of consumers online " + numberOfConsumersOnline +" for consumer group - "+consumerGroup);
             }
         } catch (Exception kex) {
             LOGGER.error("Exception while getting consumer group details ", kex);
         }
         return numberOfConsumersOnline;
    }

    @Override
    protected void initiateProcessFactory(long orgId, String topic, String consumerGroup, int processorId) throws Exception {
    	try {
            new Thread(getProcessor(orgId, topic, consumerGroup, processorId, getSource())).start();
            LOGGER.info("Started processor - " + topic + ":" + processorId);
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }

}
