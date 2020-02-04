package com.facilio.services.kinesis;

import com.facilio.aws.util.FacilioProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ErrorDataProducer {

    private static final Logger LOGGER = LogManager.getLogger(ErrorDataProducer.class.getName());

    private static Producer<String, String> producer;

    static {
        new ErrorDataProducer();
    }

    private ErrorDataProducer() {
        try {
            producer = new KafkaProducer<>(getKafkaProducerProperties());
            LOGGER.info("Initialized kafka producer for error messages");
        } catch (Exception e) {
            LOGGER.info("Exception while constructing kafka producer for error messages ", e);
        }
    }

    private static Properties getKafkaProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", FacilioProperties.getKafkaProducer());
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("acks", "all");
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return  props;
    }

    public static RecordMetadata send(ProducerRecord<String, String> record) {

        Future<RecordMetadata> future = producer.send(record);
        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.info(e);
        }
        return recordMetadata;

    }
}
