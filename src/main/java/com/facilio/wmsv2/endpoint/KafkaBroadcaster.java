package com.facilio.wmsv2.endpoint;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.modules.FieldUtil;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.kafka.FacilioKafkaProducer;
import com.facilio.services.kafka.KafkaUtil;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.handler.BaseHandler;
import com.facilio.wmsv2.handler.Processor;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import lombok.SneakyThrows;
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
        private int recordCount = 10;

        public KafkaConsumerThread(Group group)  {
            kafkaTopicName = getTopicName(group);
            Thread.currentThread().setName(kafkaTopicName);

            List<BaseHandler> list = Processor.getInstance().getGroupTopics(group);
            int maxTimeOut = list.stream().max(Comparator.comparing(BaseHandler::getRecordTimeout)).get().getRecordTimeout();

            kafkaGroupName = FacilioProperties.getEnvironment()+"-"+ group.getName();
            if(group.isSendToAllWorker()) {
                kafkaGroupName = kafkaGroupName +"-"+ ServerInfo.getHostname();
                recordCount = 500;  // assumption is default topic should handle granular tasks. so will complete sooner even for 500 records
            }

            consumer = new KafkaConsumer<>(getProperties(maxTimeOut, group.getName()));
            consumer.subscribe(Collections.singleton(kafkaTopicName));

            LOGGER.info("Kafka-consumer create for :: "+kafkaTopicName);
        }

        private Properties getProperties(int maxTimeOut, String groupName) {
            // Assuming default source is kafka type. If not, source should be passed as a param from some config
            int pollInterval = (recordCount+1) * maxTimeOut; // in seconds

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
            props.put("client.id", ServerInfo.getHostname()+"-"+groupName);  //for each consumer in same machine should have diff client id
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
            int timeout = 0;
            String recordThreadName = null;
            for (ConsumerRecord<String, String> record : records) {
                partition = record.partition();
                offset = record.offset();
                ExecutorService executor = Executors.newSingleThreadExecutor();
                try {
                    Message message = FieldUtil.getAsBeanFromJson((JSONObject) parser.parse(record.value()), Message.class);
                    BaseHandler handler = Processor.getInstance().getHandler(message.getTopic());
                    timeout = handler.getRecordTimeout();
                    recordThreadName = getRecordThreadName(record);
                    executor.submit(new KafkaRecordThread(message, recordThreadName, record.key())).get(timeout, TimeUnit.SECONDS);
                } catch (Exception e) {
                    if(e instanceof TimeoutException) {
                        LOGGER.error("Time limit exceeded. Possible rogue record ("+recordThreadName+"). " +
                                "Process killed after "+timeout+" seconds");
                        executor.shutdownNow();
                    }
                    LOGGER.error("Exception while processing record ("+recordThreadName+")", e);
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
        private String key; //wmsTopicName

        KafkaRecordThread(Message message, String recordThreadName, String key) {
            this.message = message;
            this.threadName = recordThreadName;
            this.key = key;
        }

        @SneakyThrows
        @Override
        public void run()  {
            long startTime = System.currentTimeMillis();
            try {
                Thread.currentThread().setName(threadName);
                if (message.getOrgId() != null && message.getOrgId()!=-1) {
                    AccountUtil.setCurrentAccount(message.getOrgId());
                }
                incomingMessage(message);
            }
            finally {
                if(!Topics.System.ping.equals(key)) {
                    LOGGER.info("Time taken to process wms record ["+ threadName + ", "+key+ "] is " + (System.currentTimeMillis() - startTime) + " ms");
                }
                AccountUtil.cleanCurrentAccount();
            }
        }
    }

}