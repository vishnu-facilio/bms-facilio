package com.facilio.services.kafka.notification;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

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

import com.facilio.aws.util.FacilioProperties;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.wms.endpoints.SessionManager;
import com.facilio.wms.message.Message;
import com.facilio.wms.util.WmsApi;

public class NotificationProcessor implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(NotificationProcessor.class.getName());

    private KafkaConsumer<String, String> consumer;

    public NotificationProcessor() {
        String streamName =  WmsApi.getKinesisNotificationTopic();
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        consumer = new KafkaConsumer<>(getProperties(applicationName));
        consumer.subscribe(Collections.singletonList(streamName));
        // consumer.seekToEnd(topicPartitionList);
        LOGGER.info("Notification processor has been initialized");
    }

    private Properties getProperties(String groupId) {
    		// Assuming default source is kafka type. If not, it should be passed as a param
    		KafkaMessageSource source = MessageSourceUtil.getDefaultSource();
        Properties props = new Properties();
        props.put("bootstrap.servers", source.getBroker());
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("auto.offset.reset", "latest");
        props.put("session.timeout.ms", "300000");
        if ("msk-oci".equals(FacilioProperties.getQueueSource())) {
            props.put("session.timeout.ms", "100000");
        }
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());
        KafkaUtil.setKafkaAuthProps(props, source);        
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
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
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
                            consumer.commitSync();
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
