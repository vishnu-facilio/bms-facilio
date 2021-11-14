package com.facilio.services.kafka;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;

public class FacilioKafkaProducer implements FacilioProducer {
    private static final Logger LOGGER = LogManager.getLogger(FacilioKafkaProducer.class.getName());

    private static final int MAX_KAFKA_MESSAGE_SIZE = 110 * 1024; //110KB


    private KafkaProducer<String, String> producer;

    public FacilioKafkaProducer() {
        this(getProducerProperties(null));
    }

    public FacilioKafkaProducer(Properties props) {
        this(props, null);
    }
    
    public FacilioKafkaProducer(KafkaMessageSource source) {
        this(getProducerProperties(source), source);
    }
    
    public FacilioKafkaProducer(Properties props, KafkaMessageSource source) {
        producer = new KafkaProducer<String, String>(props);
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

    private static Properties getProducerProperties(KafkaMessageSource source) {
    	if (source == null) {
    		// Getting default source from props. If not, source should be passed as a param from some config
    		source = MessageSourceUtil.getDefaultSource();
    	}
    	
        Properties props = new Properties();
        props.put("bootstrap.servers", source.getBroker());
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaUtil.setKafkaAuthProps(props, source);
        
        return  props;
    }

    public void close() {
        producer.close();
    }
}
