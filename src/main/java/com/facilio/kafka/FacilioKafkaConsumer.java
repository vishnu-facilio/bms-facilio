package com.facilio.kafka;

import com.facilio.aws.util.AwsUtil;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.server.ServerInfo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FacilioKafkaConsumer implements FacilioConsumer {

    private KafkaConsumer<String, org.json.simple.JSONObject> consumer;

    public FacilioKafkaConsumer(String client, String consumerGroup) {
        consumer = new KafkaConsumer<>(getConsumerProperties(client, consumerGroup));
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
        props.put("clientid", client);

        return props;
    }

    public List<FacilioRecord> getRecords() {
        return getRecords(5000L);
    }

    public List<FacilioRecord> getRecords(long timeout) {
        ConsumerRecords<String, JSONObject> records = consumer.poll(timeout);
        List<FacilioRecord> facilioRecords = new ArrayList<>();
        for (ConsumerRecord<String, JSONObject> record : records) {
            FacilioRecord facilioRecord = new FacilioRecord(record.key(), record.value());
            facilioRecord.setId(String.valueOf(record.offset()));
            facilioRecord.setTimeStamp(record.timestamp());
            facilioRecords.add(facilioRecord);
        }
        return facilioRecords;
    }

    public void commit(FacilioRecord record) {
        consumer.commitSync();
    }

    public void subscribe(String topic) {
        TopicPartition topicPartition = new TopicPartition(topic, 0);
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(topicPartition);
        consumer.assign(topicPartitionList);
    }
}
