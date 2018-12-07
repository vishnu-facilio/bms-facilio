package com.facilio.kafka.event;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.kafka.FacilioProcessor;
import com.facilio.server.ServerInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class EventProcessor extends FacilioProcessor {

    private List<EventRuleContext> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long orgId;
    private long lastEventTime = System.currentTimeMillis();
    private String orgDomainName;
    private String errorStream;
    private String topic;
    private String consumerGroup;

    private TopicPartition topicPartition;
    private KafkaConsumer consumer;
    private KafkaProducer producer;

    private static final String DATA_TYPE = "PUBLISH_TYPE";
    private static final Logger LOGGER = LogManager.getLogger(EventProcessor.class.getName());


    public EventProcessor(long orgId, String orgDomainName) {

        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        topic = AwsUtil.getIotKinesisTopic(orgDomainName);
        errorStream = topic +"-error";
        String clientName = orgDomainName +"-event-";
        String environment = AwsUtil.getConfig("environment");
        consumerGroup = clientName + environment;
        topicPartition = new TopicPartition(topic, 0);

        consumer = new KafkaConsumer(getConsumerProperties(ServerInfo.getHostname(), consumerGroup));
        producer = new KafkaProducer(getProducerProperties());

        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(topicPartition);
        consumer.assign(topicPartitionList);
    }

    private void sendToKafka(JSONObject data) {
        JSONObject dataMap = new JSONObject();
        try {
            if(data.containsKey("timestamp")) {
                dataMap.put("timestamp", data.get("timestamp"));
            } else {
                dataMap.put("timestamp", System.currentTimeMillis());
            }
            dataMap.put("key", data.get("key"));
            dataMap.put("data", data);
            producer.send(new ProducerRecord<>(errorStream, data.get("key"), dataMap.toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + dataMap);
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    private void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName +"-event-kafka";
        thread.setName(threadName);
    }

    private boolean processEvents(JSONObject object) throws Exception {

        long timeStamp = System.currentTimeMillis();
        if (object.containsKey("timestamp")) {
            timeStamp = Long.parseLong(String.valueOf(object.get("timestamp")));
        }
        String partitionKey = (String) object.get("key");

        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        long currentExecutionTime = bean.processEvents(timeStamp, object, eventRules, eventCountMap, lastEventTime, partitionKey);
        if(currentExecutionTime != -1) {
            lastEventTime = currentExecutionTime;
            return true;
        }
        return false;
    }

    private boolean processRecords(String data) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<EventRuleContext> ruleList = bean.getActiveEventRules();
            if(ruleList != null){
                eventRules = ruleList;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        JSONObject object = new JSONObject();
        boolean alarmCreated = false;
        try {
            JSONParser parser = new JSONParser();
            object = (JSONObject) parser.parse(data);
            if(object.containsKey(DATA_TYPE)){
                String dataType = (String)object.get(DATA_TYPE);
                if("event".equalsIgnoreCase(dataType)){
                    alarmCreated = processEvents(object);
                }
            } else {
                alarmCreated = processEvents(object);
            }
        } catch (Exception e) {
            try {
                if(AwsUtil.isProduction() && (object.size() > 0)) {
                    sendToKafka(object);
                }
            } catch (Exception e1) {
                LOGGER.info("Exception while sending data to " + errorStream, e1);
            }
            CommonCommandUtil.emailException("KEventProcessor", "Error in processing records in EventProcessor ", e,  data);
            LOGGER.info("Exception occurred ", e);
        }
        return alarmCreated;
    }

    public void run() {
        try {
            AccountUtil.setCurrentAccount(orgId);
            initialize();
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(5000);
                    for (ConsumerRecord<String, String> record : records) {
                        boolean commit = false;
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject data = (JSONObject) parser.parse(record.value());
                            String kinesisData = (String) data.get("data");
                            LOGGER.info(" timeseries data " + kinesisData);
                            // commit = processRecords(kinesisData);
                            commit = true;
                        } catch (ParseException e) {
                            LOGGER.log(Priority.INFO, "Exception while parsing data to JSON ", e);
                        } finally {
                            if(commit) {
                                consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(record.offset() + 1)));
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("exception while polling consumer ", e);
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException in) {
                        LOGGER.info("Interrupted exception ", in);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception while starting timeseries processor ", e);
        }
    }
}
