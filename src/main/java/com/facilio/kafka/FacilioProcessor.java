package com.facilio.kafka;

import com.facilio.aws.util.AwsUtil;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

public abstract class FacilioProcessor implements Runnable {

    protected Properties getConsumerProperties(String consumerGroup) {
        Properties props = new Properties();
        props.put("bootstrap.servers", AwsUtil.getConfig("kafka.brokers"));
        props.put("group.id", consumerGroup);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        return props;
    }

    protected Properties getProducerProperties() {
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
}
