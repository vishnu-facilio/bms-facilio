package com.facilio.services.kafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;

//Renamed from Processor
public class KafkaProcessor extends FacilioProcessor {


    private static final Logger LOGGER = LogManager.getLogger(KafkaProcessor.class.getName());
    private static final Map<Long,Long> PROP = new HashMap<>();
    private DataProcessorUtil dataProcessorUtil;

    public KafkaProcessor(long orgId, String topic) {
        super(orgId, topic);
        String clientName = topic + "-processor-";
        String environment = FacilioProperties.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        setEventType("processor");
        dataProcessorUtil = new DataProcessorUtil(orgId, topic);
        LOGGER.info("Initializing processor " + topic);
        initMap();
        if(!FacilioProperties.isProduction() && PROP.containsKey(orgId)) {
        	Long offset = PROP.get(orgId);
        	FacilioRecord record = new FacilioRecord(topic, new JSONObject());
        	record.setId(offset);
        	LOGGER.info("Kafka commit method called ......orgid : "+orgId +"  offset :"+offset);
        	getConsumer().commit(record);
        	LOGGER.info("Kafka commit method completed");
        }
    }


    @Override
    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
           System.out.println(" getting messages via kafka Record Id is : "+record.getId());
            try {
                if (!dataProcessorUtil.processRecord(record)) {
                    LOGGER.info("Exception while processing ->" + record.getData());
                }
                getConsumer().commit(record);
            } catch (Exception e){
                LOGGER.info("Exception occurred while processing  ",e);
            }
        }
    }
    
//    public long send(String orgDomain,JSONObject data) {
//    	FacilioRecord record= new FacilioRecord(orgDomain, data);
//    	RecordMetadata metaData = (RecordMetadata) getProducer().putRecord(record);
//    	return metaData.offset();
//    }
    
    private static void initMap(){
    	PROP.put(146L, 2637929L);
    }
}
