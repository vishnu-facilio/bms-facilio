package com.facilio.services.kinesis;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.procon.message.FacilioRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ErrorDataProducer {

    private static final Logger LOGGER = LogManager.getLogger(ErrorDataProducer.class.getName());
    private static FacilioKafkaProducer facilioKafkaProducer = new FacilioKafkaProducer();
    ;

    public static RecordMetadata send(ProducerRecord<String, String> record) throws Exception {

        RecordMetadata recordMetadata = null;
        try {
            JSONObject recordData = (JSONObject) new JSONParser().parse(record.value());
            FacilioRecord facilioRecord = new FacilioRecord(record.key(), recordData);
            recordMetadata = facilioKafkaProducer.putRecord(record.topic(), facilioRecord);
        } catch (ParseException e) {
            LOGGER.info("Exception while putting record ", e);
        }
        return recordMetadata;
    }
}
