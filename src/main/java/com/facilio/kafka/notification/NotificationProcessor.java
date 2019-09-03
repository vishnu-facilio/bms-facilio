package com.facilio.kafka.notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.facilio.aws.util.FacilioProperties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.server.ServerInfo;
import com.facilio.wms.endpoints.SessionManager;
import com.facilio.wms.message.Message;
import com.facilio.wms.util.WmsApi;

public class NotificationProcessor implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(NotificationProcessor.class.getName());

    private KafkaConsumer<String, String> consumer;
    private TopicPartition topicPartition;

    public NotificationProcessor() {
        String streamName =  WmsApi.getKinesisNotificationTopic();
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        consumer = new KafkaConsumer<>(getProperties(applicationName));
        topicPartition = new TopicPartition(streamName, 0);
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        topicPartitionList.add(topicPartition);
        consumer.assign(topicPartitionList);
        // consumer.seekToEnd(topicPartitionList);
        LOGGER.info("Notification processor has been initialized");
    }

    private Properties getProperties(String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", FacilioProperties.getKafkaConsumer());
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "latest");
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        return props;
    }

    public void run() {
        Thread thread = Thread.currentThread();
        String threadName = "facilio-notifications";
        thread.setName(threadName);
        try {
            LOGGER.info("Running notification processor");
            while (true) {
                try {
                    ConsumerRecords<String, String> records = consumer.poll(500L);
                    long startTime = System.currentTimeMillis();
                    long timeToSendMessage = 0L;
                    for (ConsumerRecord<String, String> record : records) {
                        try {
                            JSONParser parser = new JSONParser();
                            JSONObject data = (JSONObject) parser.parse(record.value());
                            JSONObject messageData = (JSONObject) data.get("data");
                            Message message = Message.getMessage(messageData);
                            LOGGER.debug("Going to send message to " + message.getTo() + " from " + message.getFrom());
                            timeToSendMessage = timeToSendMessage + SessionManager.getInstance().sendMessage(message);
                            consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(record.offset() + 1)));
                        } catch (ParseException e) {
                            LOGGER.log(Priority.INFO, "Exception while parsing data to JSON ", e);
                        }
                    }
                    LOGGER.debug("Processed " + records.count() + " in " + (System.currentTimeMillis() - startTime) + " ms. Time to send Messaage " + timeToSendMessage);
                } catch (Exception e) {
                    LOGGER.info("Exception occurred ", e);
                }
            }
        } catch (Exception e){
            LOGGER.info("Exception occurred ", e);
        }
    }
}
