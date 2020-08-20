package com.facilio.agentv2;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.PublishType;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
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

public class AgentControlAction {

	private static final Logger LOGGER = LogManager.getLogger(AgentControlAction.class.getName());
	
	private static final String DISABLE = "disable";
	private static final String ENABLE = "enable";
	private static final String IS_DISABLE = "isDisable";
	private static final String COMMIT_ID = "commitId";
	private static final String MESSAGE = "message";
	private static final FacilioModule AGENT_MODULE = ModuleFactory.getNewAgentModule();
	private static final List<FacilioField> AGENT_FIELDS = FieldFactory.getNewAgentFields();
	private static final FacilioModule AGENT_CONTROL_MODULE = ModuleFactory.getAgentControlModule();
	private static final List<FacilioField> AGENT_CONTROL_FIELDS = FieldFactory.getAgentControlFields();

	public static void processAgentAction(long recordId, JSONObject payLoad, Long agentId) throws Exception {

		String msg = (String) payLoad.get(MESSAGE);
		if (StringUtils.isNotEmpty(msg)) {
			if (msg.equals(DISABLE)) {
				if(!isActionExist(agentId,true,recordId)) {
					updateAgent(agentId, recordId, true);
				}
				if(!isAgentControlActionExist(agentId,recordId, true)) {
					insertAgentControl(getAsMap(true, recordId, true, msg, agentId));
				}
			} else if (msg.equals(ENABLE)) {
				if(!isActionExist(agentId,false,recordId)) {
					updateAgent(agentId, recordId, false);
				}
				if(!isAgentControlActionExist(agentId,recordId, false)){
					Map<String,Object> prop = new HashMap<>();
					prop.put(MESSAGE, msg);
					prop.put("enabledTime", System.currentTimeMillis());
					prop.put(IS_DISABLE, false);
					updateAgentControl(agentId,recordId,prop);
				}
			}
		} else {
			throw new IllegalArgumentException("Message shouldn't be empty for Agent Enable|disable  action...");
		}
	}

	private static void updateAgentControl(Long agentId, long recordId, Map<String, Object> prop) throws SQLException {
		new GenericUpdateRecordBuilder().fields(AGENT_CONTROL_FIELDS).table(AGENT_CONTROL_MODULE.getTableName())
		.andCondition(CriteriaAPI.getIdCondition(agentId, AGENT_CONTROL_MODULE))
		.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_CONTROL_FIELDS).get(COMMIT_ID),String.valueOf(recordId), NumberOperators.EQUALS))
		.update(prop);
	}

	private static boolean isAgentControlActionExist(long agentId, long recordId,boolean actionVal) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(AGENT_CONTROL_FIELDS)
				.table(AGENT_CONTROL_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_FIELDS).get(AgentConstants.AGENT_ID),String.valueOf(agentId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_FIELDS).get(COMMIT_ID),String.valueOf(recordId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_FIELDS).get(IS_DISABLE),String.valueOf(actionVal), BooleanOperators.IS));
		return (MapUtils.isNotEmpty(builder.fetchFirst())) ? true : false;
		
	}

	private static void updateAgent(long agentId, long recordId, boolean action) throws SQLException {
		new GenericUpdateRecordBuilder().fields(AGENT_FIELDS).table(AGENT_MODULE.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(agentId, AGENT_MODULE))
				.update(getAsMap(action, recordId, false, null, null));
	}

	private static void insertAgentControl(Map<String, Object> map) throws Exception {
		new GenericInsertRecordBuilder().fields(AGENT_CONTROL_FIELDS).table(AGENT_CONTROL_MODULE.getTableName())
				.insert(map);
	}

	private static boolean isActionExist(Long agentId,boolean actionVal, long recordId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().select(AGENT_FIELDS)
				.table(AGENT_MODULE.getTableName()).andCondition(CriteriaAPI.getIdCondition(agentId, AGENT_MODULE))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_FIELDS).get(COMMIT_ID),String.valueOf(recordId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(AGENT_FIELDS).get(IS_DISABLE),String.valueOf(actionVal), BooleanOperators.IS));
		return (MapUtils.isNotEmpty(builder.fetchFirst())) ? true : false;
	}

	public static void sendMsgToKafkaAndKinesis(String agentName,String action) {

		JSONObject prop = new JSONObject();
		JSONObject payLoad = new JSONObject();
		prop.put(AgentConstants.AGENT, agentName);
		prop.put(AgentConstants.PUBLISH_TYPE,PublishType.AGENT_ACTION);
		prop.put(AgentConstants.TIMESTAMP, System.currentTimeMillis());
		prop.put(MESSAGE,action);
		payLoad.put("data", prop);
		String macAddr = null;
		
		// sending msg to kinesis...
		if(AccountUtil.getCurrentOrg() != null && FacilioProperties.isProduction()) {
			String stream = AccountUtil.getCurrentOrg().getDomain();
			if (macAddr == null) {
				macAddr = stream;
			}
			PutRecordResult recordResult = AwsUtil.getKinesisClient().putRecord(stream, ByteBuffer.wrap(payLoad.toJSONString().getBytes(Charset.defaultCharset())), macAddr);
			int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
			if (status != 200) {
				LOGGER.info("Couldn't add data to " + stream);
			}
		}
		// sending msg to kafka...
		
	}

	private static Map<String, Object> getAsMap(boolean action, long recordId, boolean agentControl,String msg,Long agentId) {
		Map<String, Object> prop = new HashMap<>();
		prop.put(IS_DISABLE, action);
		prop.put(COMMIT_ID, recordId);
		if (agentControl) {
			prop.put(MESSAGE, msg);
			prop.put(AgentConstants.AGENT_ID, agentId);
			if (action) {
				prop.put("disabledTime", System.currentTimeMillis());
			} 
		}
		return prop;
	}
}
