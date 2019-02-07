package com.facilio.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.facilio.aws.util.AwsUtil;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.producer.FacilioProducer;

public class FacilioKafkaProducer implements FacilioProducer {

    private String topic;

    private KafkaProducer<String, org.json.simple.JSONObject> producer;

    public FacilioKafkaProducer (String topic) {
        this.topic = topic;
        producer = new KafkaProducer<>(getProducerProperties());
    }

    public Object putRecord(FacilioRecord record) {
        return producer.send(new ProducerRecord<>(topic, record.getPartitionKey(), record.getData()));
    }

    private Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", AwsUtil.getConfig("kafka.brokers"));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return  props;
    }

    public void close() {
        producer.close();
    }
}
