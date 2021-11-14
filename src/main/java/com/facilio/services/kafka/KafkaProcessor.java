package com.facilio.services.kafka;

import java.util.List;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.dataprocessor.DataProcessorUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.server.ServerInfo;
import com.facilio.service.FacilioService;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;

import lombok.NonNull;

//Renamed from Processor
public class KafkaProcessor extends FacilioProcessor {


    private static final Logger LOGGER = LogManager.getLogger(KafkaProcessor.class.getName());
    private static final List<FacilioField> MESSAGE_TOPIC_FIELDS = AgentFieldFactory.getMessageTopicFields();
	private static final FacilioModule MESSAGE_TOIPC_MODULE = AgentModuleFactory.getMessageToipcModule();

	private DataProcessorUtil dataProcessorUtil;
    private int processorId;

	public KafkaProcessor(long orgId, String topic, String consumerGroup, int processorId, @NonNull KafkaMessageSource source) {
        super(orgId, topic);
        this.processorId = processorId;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname()+"_"+processorId, consumerGroup, getTopic(), source));
		setProducer(new FacilioKafkaProducer(source));
        setEventType("processor");
        dataProcessorUtil = new DataProcessorUtil(orgId, topic, source);
        LOGGER.info("Initializing processor " + topic + " - " + getThreadName());
    }


	@Override
	protected String getThreadName() {
        return "kafka-" + getOrgDomainName() + "-" + processorId + "-" + getEventType();
	}

	@Override
	public void processRecords(List<FacilioRecord> records) {
		try {
			if (!FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE,()->isTopicEnabled())) {
				LOGGER.info("Org Topic is disabled : "+getTopic());
				return;
			}
			for (FacilioRecord record : records) {
				LOGGER.debug("Getting Kafka recordId : "+record.getId());
                if (!dataProcessorUtil.processRecord(record)) {
					LOGGER.error("Exception while processing ->" + record.getData());
				}
				getConsumer().commit(record);
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while processing  ", e);
		}
	}
    
    private boolean isTopicEnabled() throws Exception {
    	
    	if(StringUtils.isEmpty(getTopic())) {
    		return false;
    	}
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(MESSAGE_TOPIC_FIELDS)
				.table(MESSAGE_TOIPC_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS).get(AgentConstants.MESSAGE_TOPIC),getTopic(),StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS).get(AgentConstants.IS_DISABLE),String.valueOf(false), BooleanOperators.IS));
    	return MapUtils.isEmpty(builder.fetchFirst())?false:true;
	}

	public long sendMsgToKafka(String partitionKey, JSONObject data) throws Exception {
    	return send(partitionKey,data);
    }

	private long send(String partitionKey, JSONObject data) throws Exception {
		RecordMetadata metaData = (RecordMetadata) getProducer().putRecord(getTopic(), new FacilioRecord(partitionKey, data));
    	return metaData.offset();
    }
    
}
