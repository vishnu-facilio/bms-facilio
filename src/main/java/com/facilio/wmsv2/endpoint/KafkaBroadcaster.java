package com.facilio.wmsv2.endpoint;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.modules.FieldUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import com.facilio.wmsv2.message.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaBroadcaster extends AbstractBroadcaster {

    private static FacilioProducer producer;
    private static String kinesisNotificationTopic = "notifications-new";

    private KafkaConsumer<String, String> consumer;
    private TopicPartition topicPartition;

    // executors
    private ScheduledExecutorService executor = null;

    public KafkaBroadcaster() {
        if(FacilioProperties.isProduction()) {
            kinesisNotificationTopic = "production-"+ kinesisNotificationTopic;
        } else {
            kinesisNotificationTopic = FacilioProperties.getEnvironment() + "-" + kinesisNotificationTopic;
        }

        producer = new FacilioKafkaProducer(kinesisNotificationTopic);


        // consumer initialization
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        consumer = new KafkaConsumer<>(getProperties(applicationName));
        topicPartition = new TopicPartition(kinesisNotificationTopic, 0);
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(topicPartition);
        consumer.assign(topicPartitionList);

        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                final JSONParser parser = new JSONParser();
                ConsumerRecords<String, String> records = consumer.poll(1000);
                for (ConsumerRecord<String, String> record : records) {
                    String value = record.value();
                    try {
                        Message message = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(value), Message.class);
                        incomingMessage(message);
                    } catch (Exception ex) {

                    }
                }
            }
        }, 0, 60 * 1000, TimeUnit.MILLISECONDS);
    }

    private Properties getProperties(String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", FacilioProperties.getKafkaConsumer());
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "latest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        return props;
    }

    @Override
    protected void outgoingMessage(Message message) {
        JSONObject dataMap = new JSONObject();
        JSONObject data = message.toJson();

        dataMap.put("timestamp", System.currentTimeMillis());
        dataMap.put("data", data);

        String partitionKey = kinesisNotificationTopic;
        RecordMetadata future = (RecordMetadata)producer.putRecord(new FacilioRecord(partitionKey, dataMap));
    }

    @Override
    protected void incomingMessage(Message message) {
        // read incoming message
    }
}
