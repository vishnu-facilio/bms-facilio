package com.facilio.services.kafka;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.server.ServerInfo;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

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
}
