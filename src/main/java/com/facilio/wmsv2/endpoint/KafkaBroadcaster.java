package com.facilio.wmsv2.endpoint;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import com.facilio.wmsv2.message.Message;

public class KafkaBroadcaster extends AbstractBroadcaster {

    private static final Logger LOGGER = LogManager.getLogger(KafkaBroadcaster.class.getName());

    private static KafkaBroadcaster broadcaster = new KafkaBroadcaster();
    public static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    private KafkaProcessor allConsumerProcessor;
    private KafkaProcessor signleConsumerProcessor;

    // executors
    private ScheduledExecutorService executor = null;

    private KafkaBroadcaster() { // Making it singleton
        String kinesisNotificationTopic;
        if(FacilioProperties.isProduction()) {
            kinesisNotificationTopic = "production-notifications-new";
        } else {
            kinesisNotificationTopic = FacilioProperties.getEnvironment() + "-notifications-new";
        }

        LOGGER.debug("Notification topic: " + kinesisNotificationTopic);

        // consumer initialization
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        allConsumerProcessor = new KafkaProcessor(kinesisNotificationTopic, applicationName, ServerInfo.getHostname());
        String singleConsumerTopic = kinesisNotificationTopic + "-singleConsumer";
        signleConsumerProcessor = new KafkaProcessor(singleConsumerTopic, singleConsumerTopic, ServerInfo.getHostname());
    }

    private Properties getProperties(String groupId, String client) {
		// Assuming default source is kafka type. If not, source should be passed as a param from some config
		KafkaMessageSource source = MessageSourceUtil.getDefaultSource();

        Properties props = new Properties();
        props.put("bootstrap.servers", source.getBroker());
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "300000");
        props.put("auto.offset.reset", "latest");
        props.put("max.poll.records", 10);
        props.put("max.poll.interval.ms", 600000);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        props.put("group.instance.id", client);
        props.put("client.id", client);
        KafkaUtil.setKafkaAuthProps(props, source);
        return props;
    }

    @Override
    protected void outgoingMessage(Message message, boolean sendToAllWorkers) throws Exception {
        if (sendToAllWorkers) {
            allConsumerProcessor.sendMessage(message);
        } else {
            signleConsumerProcessor.sendMessage(message);
        }
    }

    @Override
    protected void incomingMessage(Message message) {
        // read incoming message from kafka servers
        pushToLiveSession(message);
    }

    private class KafkaProcessor {
        private FacilioProducer producer;
        private String topic;
        private KafkaConsumer<String, String> consumer;

        private ScheduledExecutorService executor = null;

        public KafkaProcessor(String topic, String groupId, String client) {
            this.topic = topic;

            producer = new FacilioKafkaProducer();

            if (FacilioProperties.isWMSConsumerEnable()) {
                consumer = new KafkaConsumer<>(getProperties(groupId, client));
                consumer.subscribe(Collections.singletonList(topic));

                executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        final JSONParser parser = new JSONParser();
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                        for (ConsumerRecord<String, String> record : records) {
                            String value = record.value();
                            try {
                                Message message = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(value), Message.class);
                                incomingMessage(message);
                            } catch (Exception ex) {
                                LOGGER.error("Exception while parsing data to JSON ", ex);
                            } finally {
                                consumer.commitSync();
                            }
                        }
                    }
                }, 0, 3, TimeUnit.SECONDS);
            }
        }

        public void sendMessage(Message message) throws Exception {
            JSONObject data = message.toJson();

            String partitionKey = message.getTopic();
            LOGGER.debug("Outgoing message: " + message);
            RecordMetadata future = (RecordMetadata) producer.putRecord(this.topic, new FacilioRecord(partitionKey, data));
        }
    }
}
