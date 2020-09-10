package com.facilio.services.kafka;

import java.util.List;

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
    private DataProcessorUtil dataProcessorUtil;

    public KafkaProcessor(long orgId, String orgDomainName) {
        super(orgId, orgDomainName);
        String clientName = orgDomainName + "-processor-";
        String environment = FacilioProperties.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        setEventType("processor");
        dataProcessorUtil = new DataProcessorUtil(orgId, orgDomainName);
        LOGGER.info("Initializing processor " + orgDomainName);
    }


    @Override
    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
            LOGGER.info(" getting messages via kafka Record Id is : "+record.getId());
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
    
    public long send(String orgDomain,JSONObject data) {
    	FacilioRecord record= new FacilioRecord(orgDomain, data);
    	RecordMetadata metaData = (RecordMetadata) getProducer().putRecord(record);
    	return metaData.offset();
    }
    
}
