package com.facilio.agent.agentcontrol;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.actions.AgentActionV2;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.kafka.KafkaProcessor;

public class AgentControl extends AgentActionV2 {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AgentControl.class.getName());
	private static final List<FacilioField> MESSAGE_TOPIC_FIELDS = AgentFieldFactory.getMessageTopicFields();
	private static final FacilioModule MESSAGE_TOIPC_MODULE = AgentModuleFactory.getMessageToipcModule();
	private static final FacilioModule AGENT_MODULE = ModuleFactory.getNewAgentModule();
	private static final List<FacilioField> AGENT_FIELDS = FieldFactory.getNewAgentFields();
	private static final FacilioModule AGENT_DISABLE_MODULE = AgentModuleFactory.getAgentDisableModule();
	private static final List<FacilioField> AGENT_DISABLE_FIELDS = AgentFieldFactory.getAgentDisableFields();
	private static final String LAST_ACTION_MODIFIED = "lastActionModified";
	private String agentName;
	private long agentId;
	private boolean action;
	private long orgId;

	public String agentAction() {
		try {
//			String topicName = getTopic();
			KafkaProcessor processor = new KafkaProcessor(orgId, agentName);
			long recordId = processor.send(agentName,createRecord());
			LOGGER.info("Agent control action recordId :"+recordId);
			if(recordId > 0) {
				updateAgent(recordId);
			}else {
				throw new IllegalArgumentException("Exception occurred in sending msg to kafka recordId : "+recordId);
			}
		}catch(Exception e) {
			LOGGER.error("Exception occurred while agent enable/disable action..",e);
		}
		return SUCCESS;
	}

	public void updateAgent(long recordId) throws SQLException {

		Map<String, Object> prop = new HashMap<>();
		prop.put(AgentConstants.IS_DISABLE,isAction());
		prop.put(LAST_ACTION_MODIFIED, System.currentTimeMillis());
		prop.put(AgentConstants.RECORD_ID, recordId);

		int count = new GenericUpdateRecordBuilder().fields(AGENT_FIELDS).table(AGENT_MODULE.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(agentId, AGENT_MODULE))
				.update(prop);
		if(count > 0 ) {
			LOGGER.info("disabled action set to "+action+" for "+agentName);
		}
	}
	
	public void insertAgentDisable(long recordId) throws Exception {
		
		Map<String, Object> prop = new HashMap<>();
		prop.put(AgentConstants.IS_DISABLE, isAction());
		prop.put("disabledTime", System.currentTimeMillis());
		prop.put(AgentConstants.RECORD_ID, recordId);
		prop.put(AgentConstants.ORGID, getOrgId());
		long count = new GenericInsertRecordBuilder().fields(AGENT_DISABLE_FIELDS).table(AGENT_DISABLE_MODULE.getTableName())
				.insert(prop);
		if(count > 0 ) {
			LOGGER.info("added entry in agentDisable table for agent : "+agentName);
		}
	}
	
	public void updateAgentDisable(long recordId) throws Exception {

		Map<String, Object> prop = new HashMap<>();
		prop.put(AgentConstants.IS_DISABLE, action);
		prop.put("enabledTime", System.currentTimeMillis());
		prop.put(AgentConstants.RECORD_ID, recordId);

		int count = new GenericUpdateRecordBuilder().fields(AGENT_DISABLE_FIELDS).table(AGENT_DISABLE_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_DISABLE_FIELDS).get(AgentConstants.RECORD_ID),String.valueOf(recordId),NumberOperators.EQUALS))
				.update(prop);
		if(count > 0 ) {
			LOGGER.info("updated agentDisable table for agent : "+agentName);
		}
	}
	
	private JSONObject createRecord() throws Exception {
		JSONObject data = new JSONObject();
		JSONObject prop = new JSONObject();
		prop.put(AgentConstants.AGENT,agentName);
		prop.put(AgentConstants.PUBLISH_TYPE, AgentConstants.AGENT_ACTION);
		prop.put(AgentConstants.MESSAGE, action);
		prop.put(AgentConstants.ORGID, orgId);
		prop.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
		data.putAll(prop);
		return data;
	}

	private String getTopic() throws Exception {
		
		Map<String,Object> prop =  new GenericSelectRecordBuilder()
				.select(MESSAGE_TOPIC_FIELDS)
				.table(MESSAGE_TOIPC_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS).get(AgentConstants.ORGID), String.valueOf(orgId),NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(MESSAGE_TOPIC_FIELDS).get(AgentConstants.IS_DISABLE),String.valueOf(false), BooleanOperators.IS))
				.fetchFirst();
		if( MapUtils.isNotEmpty(prop)) {
			return prop.get(AgentConstants.MESSAGE_TOPIC).toString();
		}else {
			throw new IllegalArgumentException("This agent's org is already disabled.. : "+orgId);
		}
	}


	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public long getAgentId() {
		return agentId;
	}

	public void setAgentId(long agentId) {
		this.agentId = agentId;
	}

	public boolean isAction() {
		return action;
	}

	public void setAction(boolean action) {
		this.action = action;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
}
