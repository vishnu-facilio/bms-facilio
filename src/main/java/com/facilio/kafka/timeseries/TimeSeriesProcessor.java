package com.facilio.kafka.timeseries;

import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.kafka.FacilioProcessor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Properties;

public class TimeSeriesProcessor extends FacilioProcessor {


    private long orgId;
    private String orgDomainName;
    private String topic;
    private String consumerGroup;
    private String errorStream;

    private KafkaConsumer consumer;
    private KafkaProducer producer;

    private static final String EVENT_TYPE = "timeseries";
    private static final Logger LOGGER = LogManager.getLogger(TimeSeriesProcessor.class.getName());

    public TimeSeriesProcessor(long orgId, String orgDomainName) {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        topic = AwsUtil.getIotKinesisTopic(orgDomainName);
        errorStream = topic +"-error";
        String clientName = orgDomainName +"-" + EVENT_TYPE + "-";
        String environment = AwsUtil.getConfig("environment");
        consumerGroup = clientName + environment;

        consumer = new KafkaConsumer(getConsumerProperties(consumerGroup));
        producer = new KafkaProducer(getProducerProperties());
    }

    private void sendToKafka(Record record, String data) {
        JSONObject dataMap = new JSONObject();
        try {
            dataMap.put("timestamp", ""+record.getApproximateArrivalTimestamp().getTime());
            dataMap.put("key", record.getPartitionKey());
            dataMap.put("data", data);
            dataMap.put("sequenceNumber", record.getSequenceNumber());
            producer.send(new ProducerRecord<>(errorStream, record.getPartitionKey(), dataMap.toString()));
        } catch (Exception e) {
            LOGGER.info(errorStream + " : " + dataMap);
            LOGGER.info("Exception while producing to kafka ", e);
        }
    }

    public void run() {
        try {
            AccountUtil.setCurrentAccount(orgId);
        } catch (Exception e) {

        }
    }
}
