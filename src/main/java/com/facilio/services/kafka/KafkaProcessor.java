package com.facilio.services.kafka;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
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
    private static final Map<Long,Long> PROP = getRecordValue();
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
        if(!FacilioProperties.isProduction()) {
        	if(MapUtils.isNotEmpty(PROP)){
        	final long offset = PROP.get(orgId);
        		if(offset > 0) {
        			LOGGER.info("Kafka seek method called ......orgid : "+orgId +"  offset :"+offset);
        			getConsumer().seek(topic, offset);
        			LOGGER.info("Kafka seek method completed");
        		}else {
        			LOGGER.info("offset is null ......"+offset);
        		}
        	}
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
    
    private static Map<Long,Long> getRecordValue(){
    	PROP.put(146L, 2637929L);
    	return PROP;
    }
}
