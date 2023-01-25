package com.facilio.wmsv2.endpoint;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

public class KafkaBroadcaster extends AbstractBroadcaster {

    private static final Logger LOGGER = LogManager.getLogger(KafkaBroadcaster.class.getName());

    private static KafkaBroadcaster broadcaster = new KafkaBroadcaster();

    public static DefaultBroadcaster getBroadcaster() {
        return broadcaster;
    }

    private FacilioProducer kafkaProducer;


    private KafkaBroadcaster() { // Making it singleton
        initOldKafkaConsumers();
        // new group implementations
        kafkaProducer = new FacilioKafkaProducer();

        if(!FacilioProperties.isWMSConsumerEnable()) {
            LOGGER.info("WMS consumer creation disabled in this instance");
            return;
        }

        List<String> wmsTopics = FacilioProperties.getWmsTopics(); // to create specific consumers
        if(CollectionUtils.isEmpty(wmsTopics)) {
            wmsTopics = new ArrayList<>();
            for(Group group : Group.values()) {
                wmsTopics.add(group.getName());
            }
        }

        List<Group> consumerGroups = new ArrayList<>();
        for(Group group : Group.values()) {
            if(!wmsTopics.contains(group.getName())) {
                continue;
            }
            if(Processor.getInstance().getGroupTopics(group).isEmpty()) {
                LOGGER.info("Group <"+group.getName()+"> is not been used by any topics. So not creating the kafka consumer");
                continue;
            }
            consumerGroups.add(group);
        }

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(consumerGroups.size());
        for(Group group : consumerGroups) {
            executor.scheduleAtFixedRate(new KafkaConsumerThread(group), 0, 3, TimeUnit.SECONDS);
        }

    }

    // TODO - delete this method after a week from releasing wms-group feature
    private Properties getProperties(String groupId, String client, int recordTimeout) {
        // Assuming default source is kafka type. If not, source should be passed as a param from some config
        KafkaMessageSource source = MessageSourceUtil.getDefaultSource();

        Properties props = new Properties();
        props.put("bootstrap.servers", source.getBroker());
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "false");
        props.put("session.timeout.ms", "300000");
        props.put("auto.offset.reset", "earliest");
        props.put("max.poll.records", 10);
        props.put("max.poll.interval.ms", 11*recordTimeout*1000);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", StringDeserializer.class.getName());

        props.put("group.instance.id", client);
        props.put("client.id", client);
        KafkaUtil.setKafkaAuthProps(props, source);
        return props;
    }

    @Override
    protected void outgoingMessage(Message message, Group group) throws Exception {
        String topic = this.getTopicName(group);
        JSONObject data = message.toJson();
        String partitionKey = message.getTopic();
        LOGGER.debug("Outgoing message: " + message);
        FacilioRecord facilioRecord = new FacilioRecord(partitionKey, data);
        kafkaProducer.putRecord(topic, facilioRecord);
    }

    protected String getTopicName(Group group) {
        return FacilioProperties.getEnvironment()+"-wms-"+group.getName();
    }

    @Override
    protected void incomingMessage(Message message) {
        // read incoming message from kafka servers
        pushToLiveSession(message);
    }

    private class KafkaConsumerThread implements Runnable {

        private KafkaConsumer<String, String> consumer;
        private String kafkaTopicName;
        private String kafkaGroupName;
        private final int recordCount = 10;

        public KafkaConsumerThread(Group group)  {
            kafkaTopicName = getTopicName(group);
            Thread.currentThread().setName(kafkaTopicName);

            List<BaseHandler> list = Processor.getInstance().getGroupTopics(group);
            int maxTimeOut = list.stream().max(Comparator.comparing(BaseHandler::getRecordTimeout)).get().getRecordTimeout();
            int pollInterval = (recordCount+1) * maxTimeOut; // in seconds

            kafkaGroupName = FacilioProperties.getEnvironment()+"-"+ group.getName();
            if(group.isSendToAllWorker()) {
                kafkaGroupName = kafkaGroupName +"-"+ ServerInfo.getHostname();
            }

            consumer = new KafkaConsumer<>(getProperties(pollInterval));
            consumer.subscribe(Collections.singleton(kafkaTopicName));

            LOGGER.info("Kafka-consumer create for :: "+kafkaTopicName);
        }

        private Properties getProperties(int pollInterval) {
            // Assuming default source is kafka type. If not, source should be passed as a param from some config
            KafkaMessageSource source = MessageSourceUtil.getDefaultSource();
            Properties props = new Properties();
            props.put("bootstrap.servers", source.getBroker());
            props.put("group.id", kafkaGroupName);
            props.put("enable.auto.commit", "false");
            props.put("session.timeout.ms", "120000"); // 2 mins
            props.put("auto.offset.reset", "earliest");
            props.put("max.poll.records", recordCount);
            props.put("max.poll.interval.ms", pollInterval*1000);
            props.put("key.deserializer", StringDeserializer.class.getName());
            props.put("value.deserializer", StringDeserializer.class.getName());


            props.put("group.instance.id", ServerInfo.getHostname());
            props.put("client.id", ServerInfo.getHostname());
            KafkaUtil.setKafkaAuthProps(props, source);
            return props;
        }

        @Override
        public void run() {
            Thread.currentThread().setName(kafkaTopicName);

            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            final JSONParser parser = new JSONParser();
            int partition;
            long offset;
            for (ConsumerRecord<String, String> record : records) {
                partition = record.partition();
                offset = record.offset();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                try {
                    Message message = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(record.value()), Message.class);
                    BaseHandler handler = Processor.getInstance().getHandler(message.getTopic());
                    executor.submit(new KafkaRecordThread(message, getRecordThreadName(record))).get(handler.getRecordTimeout(), TimeUnit.SECONDS);
                } catch (Exception e) {
                    if(e instanceof TimeoutException) {
                        LOGGER.error("Time limit exceeded. Possible rogue record");
                        executor.shutdownNow();
                    }
                    LOGGER.error("Exception while processing record on <partition="+partition+", offset="+offset+">", e);
                }
                finally {
                    consumer.commitSync(Collections.singletonMap(new TopicPartition(kafkaTopicName, partition), new OffsetAndMetadata(offset + 1)));
                    executor.shutdown();
                }
            }
        }

        private String getRecordThreadName(ConsumerRecord<String, String> record) {
            StringBuilder sb = new StringBuilder();
            sb.append(record.topic());
            sb.append("-");
            sb.append(record.partition());
            sb.append("-");
            sb.append(record.offset());
            return sb.toString();
        }

    }

    private class KafkaRecordThread implements Runnable {

        private Message message;
        private String threadName;

        KafkaRecordThread(Message message, String recordThreadName) {
            this.message = message;
            this.threadName = recordThreadName;
        }

        @Override
        public void run()  {
            Thread.currentThread().setName(threadName);
            long startTime = System.currentTimeMillis();
            incomingMessage(message);
            LOGGER.info("Time taken to process wms record "+threadName+" ::  "+(System.currentTimeMillis() - startTime));
        }
    }

    // TODO - delete this inner class after a week from releasing wms-group feature
    private class KafkaProcessor {
        private KafkaConsumer<String, String> consumer;

        private ScheduledExecutorService executor = null;

        public KafkaProcessor(Set<String> topics, String groupId, String client, int recordTimeout) {

            if (FacilioProperties.isWMSConsumerEnable()) {
                consumer = new KafkaConsumer<>(getProperties(groupId, client, recordTimeout));
                consumer.subscribe(topics);

                executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(new Runnable() {

                    @Override
                    public void run() {
                        final JSONParser parser = new JSONParser();
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                        int partition;
                        long offset;
                        String kafkaTopicName;
                        for (ConsumerRecord<String, String> record : records) {
                            partition = record.partition();
                            offset = record.offset();
                            kafkaTopicName = record.topic();
                            ExecutorService localExecutor = Executors.newSingleThreadExecutor();
                            try {
                                Message message = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(record.value()), Message.class);
                                localExecutor.submit(new Runnable() {
                                    @Override
                                    public void run() {
                                        incomingMessage(message);
                                    }
                                }).get(recordTimeout, TimeUnit.SECONDS);

                            } catch (Exception e) {
                                if(e instanceof TimeoutException) {
                                    LOGGER.error("Time limit exceeded. Possible rogue record");
                                    localExecutor.shutdownNow();
                                }
                                LOGGER.error("Exception while processing record on <partition="+partition+", offset="+offset+">", e);
                            }
                            finally {
                                consumer.commitSync(Collections.singletonMap(new TopicPartition(kafkaTopicName, partition), new OffsetAndMetadata(offset + 1)));
                                localExecutor.shutdown();
                            }
                        }
                        if(!records.isEmpty()) {
                            consumer.commitSync();
                        }
                    }

                }, 0, 3, TimeUnit.SECONDS);
            }
        }
    }

    // TODO - delete this method after a week from releasing wms-group feature
    private void initOldKafkaConsumers() {
        String broadcastTopic;
        if(FacilioProperties.isProduction()) {
            broadcastTopic = "production-notifications-new";
        } else {
            broadcastTopic = FacilioProperties.getEnvironment() + "-notifications-new";
        }

        LOGGER.debug("Notification topic: " + broadcastTopic);

        // consumer initialization
        String environment = FacilioProperties.getConfig("environment");
        String applicationName = environment+"-"+ ServerInfo.getHostname();

        // TODO Start this thread for some period to consume all the messages from old topics - Remove after one week
        new KafkaProcessor(Collections.singleton(broadcastTopic), applicationName, ServerInfo.getHostname(), 300); //in secs
        String singleConsumerTopic = broadcastTopic + "-singleConsumer";
        new KafkaProcessor(Collections.singleton(singleConsumerTopic), singleConsumerTopic, ServerInfo.getHostname(), 300);
    }
}