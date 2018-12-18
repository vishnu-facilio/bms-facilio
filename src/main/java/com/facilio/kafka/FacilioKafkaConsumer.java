package com.facilio.kafka;

import com.facilio.aws.util.AwsUtil;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.message.FacilioRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.*;

public class FacilioKafkaConsumer implements FacilioConsumer {

    private KafkaConsumer<String, String> consumer;
    private TopicPartition topicPartition = null;
    private static final Logger LOGGER = LogManager.getLogger(FacilioKafkaConsumer.class.getName());

    public FacilioKafkaConsumer(String client, String consumerGroup, String topic) {
        consumer = new KafkaConsumer<>(getConsumerProperties(client, consumerGroup));
        subscribe(topic);
    }

    private Properties getConsumerProperties(String client, String consumerGroup) {
        Properties props = new Properties();
        props.put("bootstrap.servers", AwsUtil.getConfig("kafka.brokers"));
        props.put("group.id", consumerGroup);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("max.partition.fetch.bytes", 3145728);
        props.put("auto.offset.reset", "latest");
        props.put("max.poll.interval.ms", 2000);
        props.put("client.id", client);

        return props;
    }

    public List<FacilioRecord> getRecords() {
        return getRecords(5000L);
    }

    public List<FacilioRecord> getRecords(long timeout) {
        List<FacilioRecord> facilioRecords = new ArrayList<>();
        try {
            ConsumerRecords<String, String> records = consumer.poll(timeout);
            for (ConsumerRecord<String, String> record : records) {
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(record.value());
                FacilioRecord facilioRecord = new FacilioRecord(record.key(), object);
                facilioRecord.setId(String.valueOf(record.offset()));
                facilioRecord.setTimeStamp(record.timestamp());
                facilioRecords.add(facilioRecord);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting messages ", e);
        }
        return facilioRecords;
    }

    public void commit(FacilioRecord record) {
        try {
            long offset = Long.parseLong(record.getId().trim());
            consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(offset+1)));
        } catch (NumberFormatException e) {
            LOGGER.info("Exception while parsing offset " + record.getId());
        }
    }

    public void subscribe(String topic) {
        if(topicPartition == null) {
            topicPartition = new TopicPartition(topic, 0);
            consumer.assign(Collections.singletonList(topicPartition));
        }
    }

    public void close() {
        consumer.close();
    }
}
