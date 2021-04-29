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
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaBroadcaster extends AbstractBroadcaster {

    private static final Logger LOGGER = LogManager.getLogger(KafkaBroadcaster.class.getName());

    private static KafkaBroadcaster broadcaster = new KafkaBroadcaster();
    public static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    private KafkaProcessor allConsumerProcessor;
    private KafkaProcessor signleConsumerProcessor;

    // executors
    private ScheduledExecutorService executor = null;

    private KafkaBroadcaster() { // Making it singleton
        String kinesisNotificationTopic;
        if(FacilioProperties.isProduction()) {
            kinesisNotificationTopic = "production-notifications-new";
        } else {
            kinesisNotificationTopic = FacilioProperties.getEnvironment() + "-notifications-new";
        }

        LOGGER.debug("Notification topic: " + kinesisNotificationTopic);

        // consumer initialization
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        allConsumerProcessor = new KafkaProcessor(kinesisNotificationTopic, applicationName);
        String singleConsumerTopic = kinesisNotificationTopic + "-singleConsumer";
        signleConsumerProcessor = new KafkaProcessor(singleConsumerTopic, singleConsumerTopic);
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
    protected void outgoingMessage(Message message, boolean sendToAllWorkers) throws Exception {
        if (sendToAllWorkers) {
            allConsumerProcessor.sendMessage(message);
        } else {
            signleConsumerProcessor.sendMessage(message);
        }
    }

    @Override
    protected void incomingMessage(Message message) {
        // read incoming message from kafka servers
        pushToLiveSession(message);
    }

    private class KafkaProcessor {
        private FacilioProducer producer;
        private String topic;
        private KafkaConsumer<String, String> consumer;
        private TopicPartition topicPartition;

        private ScheduledExecutorService executor = null;

        public KafkaProcessor(String topic, String groupId) {
            this.topic = topic;

            producer = new FacilioKafkaProducer();

            consumer = new KafkaConsumer<>(getProperties(groupId));
            topicPartition = new TopicPartition(topic, 0);
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
                            LOGGER.error("Exception while parsing data to JSON ", ex);
                        }
                        finally {
                            consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(record.offset() + 1)));
                        }
                    }
                }
            }, 0, 3, TimeUnit.SECONDS);
        }

        public void sendMessage(Message message) throws Exception {
            JSONObject data = message.toJson();

            String partitionKey = this.topic;
            LOGGER.debug("Outgoing message: " + message);
            RecordMetadata future = (RecordMetadata) producer.putRecord(this.topic, new FacilioRecord(partitionKey, data));
        }
    }
}
