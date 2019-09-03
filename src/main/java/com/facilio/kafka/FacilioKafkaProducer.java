package com.facilio.kafka;

import java.util.Properties;

import com.facilio.aws.util.FacilioProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import com.facilio.kinesis.ErrorDataProducer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.producer.FacilioProducer;

public class FacilioKafkaProducer implements FacilioProducer {

    private String topic;

    // private KafkaProducer<String, String> producer;

    public FacilioKafkaProducer (String topic) {
        this.topic = topic;
        // producer = new KafkaProducer<>(getProducerProperties());
    }

    public RecordMetadata putRecord(FacilioRecord record) {
        return ErrorDataProducer.send(new ProducerRecord<>(topic, record.getPartitionKey(), record.getData().toJSONString()));
    }

    private Properties getProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", FacilioProperties.getKafkaProducer());
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
        //producer.close();
    }
}
