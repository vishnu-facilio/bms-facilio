package com.facilio.services.messageQueue;

import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSource;
import com.facilio.service.FacilioService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class MessageQueueTopic {

    private static final Logger LOGGER = LogManager.getLogger(MessageQueueTopic.class.getName());

    private static final List<FacilioField> MESSAGE_TOPIC_FIELDS = AgentFieldFactory.getMessageTopicFields();
    private static final FacilioModule MESSAGE_TOPIC_MODULE = AgentModuleFactory.getMessageToipcModule();

    public static boolean addMsgTopic(String topicName, long orgId, MessageSource source) throws Exception {
        Map<String, Object> prop = new HashedMap<>();
        prop.put(AgentConstants.MESSAGE_TOPIC, topicName);
        prop.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
        prop.put(AgentConstants.ORGID, orgId);
        prop.put(AgentConstants.MESSAGE_SOURCE, source.getName());
        prop.put(AgentConstants.MAX_CONSUMERS_PER_INSTANCE, 1);
        prop.put(AgentConstants.MAX_CONSUMERS, 2);
        long count = new GenericInsertRecordBuilder().fields(MESSAGE_TOPIC_FIELDS)
                .table(MESSAGE_TOPIC_MODULE.getTableName())
                .insert(prop);
        if (count > 0) {
            LOGGER.info("New MessageQueue topic added. Topic name: " + topicName + " orgId :" + orgId);
            return true;
        }
        return false;
    }

    public static List<Map<String, Object>> getTopics(List<Long> orgId, MessageSource source) throws Exception {
        return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.AGENT_SERVICE, () -> getAllMessageTopics(orgId, source));
    }

    private static List<Map<String, Object>> getAllMessageTopics(List<Long> orgId, MessageSource source) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(MESSAGE_TOPIC_FIELDS)
                .table(MESSAGE_TOPIC_MODULE.getTableName());
        if (CollectionUtils.isNotEmpty(orgId)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ORGID), StringUtils.join(orgId, ","), NumberOperators.EQUALS));
        }
        if (source != null) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.MESSAGE_SOURCE), source.getName(), StringOperators.IS));
        }
        setVersionCriteria(builder, fieldMap);
        builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.IS_DISABLE), String.valueOf(false), BooleanOperators.IS));
        return builder.get();
    }

    private static void setVersionCriteria(GenericSelectRecordBuilder builder, Map<String, FacilioField> fieldMap) {
        if (FacilioProperties.isNewVersion()) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.VERSION), "v2", StringOperators.IS));
        } else {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.VERSION), CommonOperators.IS_EMPTY));
        }
    }

	public static void disableOrEnableMessageTopic(boolean disable,long orgId) throws SQLException {
		Map<String, Object> prop = new HashedMap<>();
		long currentTime = System.currentTimeMillis();
		prop.put(AgentConstants.IS_DISABLE,disable);
		if(disable) {
			prop.put(AgentConstants.LAST_DISABLED_TIME, currentTime);
		}else {
			prop.put(AgentConstants.LAST_ENBLED_TIME, currentTime);
		}
		prop.put(AgentConstants.LAST_MODIFIED_TIME, currentTime);
		int count = updateValue(prop ,orgId);
		if (count > 0) {
			LOGGER.info("selected ORG MessageQueue topic is disabled|enabled.  orgId :"+ orgId);
		}
	}

	public static void updateMessageTopic(String topicName,long orgId) throws SQLException {
		Map<String, Object> prop = new HashedMap<>();
		prop.put(AgentConstants.MESSAGE_TOPIC, topicName);
		prop.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
		int count = updateValue(prop,orgId);
		if (count > 0) {
			LOGGER.info("Renamed MessageQueue topic. Topic name: " + topicName + " orgId :"+ orgId);
		}
	}
	
	public static int updateValue(Map<String,Object> prop,long orgId) throws SQLException {
		return new GenericUpdateRecordBuilder().fields(MESSAGE_TOPIC_FIELDS).table(MESSAGE_TOPIC_MODULE.getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(orgId, MESSAGE_TOPIC_MODULE))
				.update(prop);
	}
}
