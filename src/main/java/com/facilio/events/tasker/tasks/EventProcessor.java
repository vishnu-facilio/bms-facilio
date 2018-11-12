package com.facilio.events.tasker.tasks;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.*;

import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fw.BeanFactory;

public class EventProcessor implements IRecordProcessor {

    public static final String DATA_TYPE = "PUBLISH_TYPE";
//    private List<EventRule> eventRules = new ArrayList<>();
    private List<EventRuleContext> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long orgId;
    private long lastEventTime = System.currentTimeMillis();
    private String shardId;
    private String orgDomainName;
    private String errorStream;
    private Producer<String, String> producer;

    private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    private static Logger log = LogManager.getLogger(EventProcessor.class.getName());

    public EventProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
        try {
            producer = new KafkaProducer<>(getKafkaProducerProperties());
            log.info("Initialized kafka producer for stream " + errorStream);
        } catch (Exception e) {
            log.info("Exception while constructing kafka producer for stream "+ errorStream +" ", e);
        }
    }

    @Override
    public void initialize(InitializationInput initializationInput) {
        this.shardId = initializationInput.getShardId();
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName +"-event";
        thread.setName(threadName);
    }

    @Override
    public void processRecords(ProcessRecordsInput processRecordsInput) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<EventRuleContext> ruleList = bean.getActiveEventRules();
            if(ruleList != null){
                eventRules = ruleList;
            }
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }
        for (Record record : processRecordsInput.getRecords()) {
            String data = "";
            try {
                data = decoder.decode(record.getData()).toString();
                JSONParser parser = new JSONParser();
                JSONObject object = (JSONObject) parser.parse(data);
                if(object.containsKey(DATA_TYPE)){
                    String dataType = (String)object.get(DATA_TYPE);
                    if("event".equalsIgnoreCase(dataType)){
                        boolean alarmCreated = processEvents(record.getApproximateArrivalTimestamp().getTime(), object, record.getPartitionKey());
                        if (alarmCreated) {
                            processRecordsInput.getCheckpointer().checkpoint(record);
                        }
                    } else if ("device_all_points".equals(dataType)) {
                        String fileName = orgDomainName+"/devices/"+object.get("device");
                        FileStoreFactory.getInstance().getFileStore().addFile(fileName, object.toJSONString(), "application/json");
                        processRecordsInput.getCheckpointer().checkpoint(record);
                    }
                } else {
                    boolean alarmCreated = processEvents(record.getApproximateArrivalTimestamp().getTime(), object, record.getPartitionKey());
                    if (alarmCreated) {
                        processRecordsInput.getCheckpointer().checkpoint(record);
                    }
                }
            } catch (Exception e) {
                try {
                    if(AwsUtil.isProduction()) {
                        sendToKafka(record, data);
                        /*PutRecordResult recordResult = AwsUtil.getKinesisClient().putRecord(errorStream, record.getData(), record.getPartitionKey());
                        int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
                        if (status != 200) {
                            log.info("Couldn't add data to " + errorStream + " " + record.getSequenceNumber());
                        }*/
                    }
                } catch (Exception e1) {
                    log.info("Exception while sending data to " + errorStream, e1);
                }
            		CommonCommandUtil.emailException("EventProcessor", "Error in processing records : "
            			+record.getSequenceNumber()+ " in EventProcessor ", e,  data);
            		log.info("Exception occurred ", e);
            }
        }
    }

    private Properties getKafkaProducerProperties() {
        Properties props = new Properties();
        props.put("bootstrap.servers", AwsUtil.getConfig("kafka.producer"));
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return  props;
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
            log.info(errorStream + " : " + dataMap);
            log.info("Exception while producing to kafka ", e);
        }
    }

    @Override
    public void shutdown(ShutdownInput shutdownInput) {
        System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);
    }


    private boolean processEvents(long timestamp, JSONObject object, String partitionKey) throws Exception {
    	
    	ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
        long currentExecutionTime = bean.processEvents(timestamp, object, eventRules, eventCountMap, lastEventTime, partitionKey);
        if(currentExecutionTime != -1) {
        	lastEventTime = currentExecutionTime;
        	return true;
        }
        return false;
    }
}
