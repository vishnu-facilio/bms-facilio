package com.facilio.services.kafka;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.procon.consumer.FacilioConsumer;
import com.facilio.services.procon.message.FacilioRecord;

import org.apache.commons.lang3.StringUtils;
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

import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class FacilioKafkaConsumer implements FacilioConsumer {

    private KafkaConsumer<String, String> consumer;
    private TopicPartition topicPartition = null;
    private JSONParser parser = new JSONParser();
    private static final Logger LOGGER = LogManager.getLogger(FacilioKafkaConsumer.class.getName());

    public FacilioKafkaConsumer(String client, String consumerGroup, String topic) {
        consumer = new KafkaConsumer<>(getConsumerProperties(client, consumerGroup));
        subscribe(topic);
    }

    private Properties getConsumerProperties(String client, String consumerGroup) {
        Properties props = new Properties();
        props.put("bootstrap.servers", FacilioProperties.getKafkaConsumer());
        props.put("group.id", consumerGroup);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        props.put("max.partition.fetch.bytes", 31457280);
        props.put("auto.offset.reset", "latest");
        props.put("session.timeout.ms", 600000);
        props.put("group.instance.id", client);
        props.put("client.id", client);
        props.put("max.poll.interval.ms",3000000);
        return props;
    }

    public List<FacilioRecord> getRecords() {
        return getRecords(5000L);
    }

    public List<FacilioRecord> getRecords(long timeout) {
        List<FacilioRecord> facilioRecords = new ArrayList<>();
        try {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(timeout));
            for (ConsumerRecord<String, String> record : records) {
                StringReader reader = new StringReader(record.value());
                JSONObject object = (JSONObject) parser.parse(reader);
                JSONObject data = (JSONObject) parser.parse((String)object.get("data"));
                FacilioRecord facilioRecord = new FacilioRecord(record.key(), data);
                facilioRecord.setId(record.offset());
                try {
                    if(object.containsKey("timestamp")) {
                        Long timestamp = Long.parseLong(object.get("timestamp").toString());
                        facilioRecord.setTimeStamp(timestamp);
                    } else {
                        facilioRecord.setTimeStamp(record.timestamp());
                    }
                } catch (NumberFormatException e) {
                    LOGGER.info("Exception while getting timestamp " + e.getLocalizedMessage());
                }
                facilioRecords.add(facilioRecord);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting messages ", e);
        }
        return facilioRecords;
    }

    public void commit(FacilioRecord record) {
        try {
            long offset = record.getId();
            consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(offset+1)));
        } catch (NumberFormatException e) {
            LOGGER.info("Exception while parsing offset " + record.getId());
        }
    }

    public void subscribe(String topic) {
        if(topicPartition == null) {
            topicPartition = new TopicPartition(topic, 0);
            consumer.subscribe(Collections.singletonList(topic));
        }
    }
    
    public void seek(String topic,long offset) {
    	if(topicPartition == null) {
            topicPartition = new TopicPartition(topic, 0);
            consumer.seek(topicPartition, offset);
        }
    }
    
    public void close() {
        consumer.close();
        LOGGER.info("Closed kafka consumer");
    }
}
