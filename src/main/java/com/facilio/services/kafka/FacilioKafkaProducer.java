package com.facilio.services.kafka;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.kinesis.ErrorDataProducer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FacilioKafkaProducer implements FacilioProducer {
    private static final Logger LOGGER = LogManager.getLogger(FacilioKafkaProducer.class.getName());

    private static final int MAX_KAFKA_MESSAGE_SIZE = 110 * 1024; //110KB


    private KafkaProducer<String, String> producer;

    public FacilioKafkaProducer() {
        producer = new KafkaProducer<>(getProducerProperties());
    }

    public RecordMetadata putRecord(String topic, FacilioRecord record) throws Exception {
        RecordMetadata recordMetadata = null;
        String data = record.getData().toJSONString();
        if (data.length() > MAX_KAFKA_MESSAGE_SIZE) {
            throw new Exception("MAX_KAFKA_MESSAGE_SIZE exceeded. Allowed size :" + MAX_KAFKA_MESSAGE_SIZE + " Received :" + data.length());
        }
        try {
            Future<RecordMetadata> future = producer.send(new ProducerRecord<>(topic, record.getPartitionKey(), record.getData().toJSONString()));
            try {
                recordMetadata = future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.info(e);
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while sending record to topic " + topic, ex);
        }
        return recordMetadata;
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
        producer.close();
    }
}
