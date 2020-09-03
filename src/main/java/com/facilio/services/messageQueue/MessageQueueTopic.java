package com.facilio.services.messageQueue;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class MessageQueueTopic {

	private static final Logger LOGGER = LogManager.getLogger(MessageQueueTopic.class.getName());

	private static final List<FacilioField> MESSAGE_TOPIC_FIELDS = AgentFieldFactory.getMessageTopicFields();
	private static final FacilioModule MESSAGE_TOIPC_MODULE = AgentModuleFactory.getMessageToipcModule();

	public static boolean addMsgTopic(String topicName,long orgId) throws Exception {
		Map<String, Object> prop = new HashedMap<>();
		prop.put(AgentConstants.MESSAGE_TOPIC, topicName);
		prop.put(AgentConstants.CREATED_TIME, System.currentTimeMillis());
		prop.put(AgentConstants.ORGID,orgId);
		long count = new GenericInsertRecordBuilder().fields(MESSAGE_TOPIC_FIELDS)
				.table(MESSAGE_TOIPC_MODULE.getTableName())
				.insert(prop);
		if (count > 0) {
			LOGGER.info("New MessageQueue topic added. Topic name: " + topicName + " orgId :"+ orgId);
			return true;
		}
		return false;
	}

	public static List<Map<String, Object>> getAllMessageTopics() throws Exception {
		return new GenericSelectRecordBuilder().select(MESSAGE_TOPIC_FIELDS)
				.table(MESSAGE_TOIPC_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS).get(AgentConstants.IS_DISABLE),String.valueOf(Boolean.FALSE), BooleanOperators.IS))
				.get();
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
		return new GenericUpdateRecordBuilder().fields(MESSAGE_TOPIC_FIELDS).table(MESSAGE_TOIPC_MODULE.getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(orgId, MESSAGE_TOIPC_MODULE))
				.update(prop);
	}
}
