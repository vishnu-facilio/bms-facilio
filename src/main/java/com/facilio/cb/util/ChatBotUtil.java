package com.facilio.cb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentAction;
import com.facilio.cb.context.ChatBotIntentInvokeSample;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotModel.App_Type;
import com.facilio.cb.context.ChatBotModelVersion;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSessionParam;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class ChatBotUtil {

	
	public static ChatBotIntent getIntent(long modelVersionId,String intentName) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("modelVersionId"), modelVersionId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), intentName, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotIntent chatBotIntent = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotIntent.class);
			if(chatBotIntent.isWithParams()) {
				chatBotIntent.setChatBotIntentParamMap(getIntentParamsMap(chatBotIntent.getId()));
			}
			chatBotIntent.setActions(ActionAPI.getActionsFromChatBotIntent(chatBotIntent.getId()));
			return chatBotIntent;
		}
		return null;
	}
	
	public static ChatBotIntent getIntent(long intentId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(intentId, ModuleFactory.getCBIntentModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotIntent chatBotIntent = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotIntent.class);
			if(chatBotIntent.isWithParams()) {
				chatBotIntent.setChatBotIntentParamMap(getIntentParamsMap(chatBotIntent.getId()));
			}
			chatBotIntent.setActions(ActionAPI.getActionsFromChatBotIntent(chatBotIntent.getId()));
			return chatBotIntent;
		}
		return null;
	}
	
	public static ChatBotSession getLastInvalidQuerySession() throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		long last5MinsStartTime = DateTimeUtil.getCurrenTime() - ChatBotConstants.LAST_INVALID_SESSION_BUFFER_TIME; 			
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userId"), AccountUtil.getCurrentUser().getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), last5MinsStartTime+"", NumberOperators.GREATER_THAN))
				.orderBy("ID desc")
				.limit(1);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotSession chatBotSession = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSession.class);
			if(chatBotSession.getState() == ChatBotSession.State.INVALID_QUERY.getIntVal()) {
				if(chatBotSession.getIntentId() > 0) {
					chatBotSession.setIntent(getIntent(chatBotSession.getIntentId()));
				}
				return chatBotSession;
			}
		}
		return null;
	}
	
	public static Map<Integer,ChatBotIntentParam> getIntentParamsMap(long intentId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<ChatBotIntentParam> chatBotIntentParams = FieldUtil.getAsBeanListFromMapList(props, ChatBotIntentParam.class);
			
			Map<Integer,ChatBotIntentParam> chatBotIntentMap = new HashMap<Integer, ChatBotIntentParam>();
			
			for(ChatBotIntentParam chatBotIntentParam :chatBotIntentParams) {
				chatBotIntentMap.put(chatBotIntentParam.getLocalId(), chatBotIntentParam);
			}
			return chatBotIntentMap;
		}
		return null;
	}
	
	public static ChatBotIntentParam getIntentParams(long intentParamId) throws Exception {
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getCBIntentParamFields())
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(intentParamId, ModuleFactory.getCBIntentParamModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			ChatBotIntentParam chatBotIntentParam = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotIntentParam.class);
			return chatBotIntentParam;
		}
		return null;
	}
	
	public static ChatBotSession getSession(long sessionId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(sessionId, ModuleFactory.getCBSessionModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotSession chatBotSession = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSession.class);
			
			if(chatBotSession.getIntentId() > 0) {
				chatBotSession.setIntent(getIntent(chatBotSession.getIntentId()));
			}
			return chatBotSession;
		}
		return null;
	}
	
	public static List<ChatBotIntentAction> getIntentActions(long intentId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentActionFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<ChatBotIntentAction> chatBotIntentAction = FieldUtil.getAsBeanListFromMapList(props, ChatBotIntentAction.class);
			return chatBotIntentAction;
		}
		return null;
	}
	
	public static ChatBotModel getActiveModel(App_Type appType) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBModelFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBModelModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), appType.getIntVal()+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotModel chatBotModel = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotModel.class);
			chatBotModel.setChatBotModelVersion(getModelVersion(chatBotModel.getMlModel()));
			return chatBotModel;
		}
		return null;
	}
	
	public static ChatBotModelVersion getModelVersion(String mlModel) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBModelVersionFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBModelVersionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mlModel"), mlModel, StringOperators.IS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotModelVersion chatBotModelVersion = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotModelVersion.class);
			return chatBotModelVersion;
		}
		return null;
	}

	public static void addChatBotSessionConversation(ChatBotSessionConversation chatBotSessionConversation) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBSessionConversationModule().getTableName())
				.fields(FieldFactory.getCBSessionConversationFields());

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotSessionConversation);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		chatBotSessionConversation.setId((Long) props.get("id"));
	}
	
	public static void updateChatBotSessionConversation(ChatBotSessionConversation chatBotSessionConversation) throws Exception {
		
		chatBotSessionConversation.setRespondedTime(DateTimeUtil.getCurrenTime());
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getCBSessionConversationModule().getTableName())
				.fields(FieldFactory.getCBSessionConversationFields())
				.andCondition(CriteriaAPI.getIdCondition(chatBotSessionConversation.getId(), ModuleFactory.getCBSessionConversationModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotSessionConversation);
		
		update.update(props);
	}
	
	public static void updateChatBotSession(ChatBotSession chatBotSession) throws Exception {
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.fields(FieldFactory.getCBSessionFields())
				.andCondition(CriteriaAPI.getIdCondition(chatBotSession.getId(), ModuleFactory.getCBSessionModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotSession);
		
		update.update(props);
	}
	
	public static void addChatBotSession(ChatBotSession chatBotSession) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.fields(FieldFactory.getCBSessionFields());

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotSession);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		chatBotSession.setId((Long) props.get("id"));
	}

	public static ChatBotSessionConversation getLastInvalidQueryConversation(long sessionId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionConversationFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionConversationModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sessionId"), sessionId+"", NumberOperators.EQUALS))
				.orderBy("ID desc")
				.limit(1);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotSessionConversation chatBotSessionConversation = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSessionConversation.class);
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.REPLIED_INCORRECTLY.getIntVal()) {
				return chatBotSessionConversation;
			}
		}
		return null;
	}
	
	public static ChatBotSessionConversation constructCBSessionConversationParams(ChatBotIntentParam param,ChatBotSession session,ChatBotSessionConversation parentChatBotConversation) throws Exception {
		
		
		ChatBotSessionConversation chatBotSessionConversation1 = new ChatBotSessionConversation();
		
		chatBotSessionConversation1.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		chatBotSessionConversation1.setSessionId(session.getId());
		chatBotSessionConversation1.setState(ChatBotSessionConversation.State.QUERY_RAISED.getIntVal());
		chatBotSessionConversation1.setIntentParamId(param.getId());
		
		chatBotSessionConversation1.setRequestedTime(DateTimeUtil.getCurrenTime());
		
		if(parentChatBotConversation != null) {
			
			if(parentChatBotConversation.getParentConversationId() > 0) {
				chatBotSessionConversation1.setParentConversationId(parentChatBotConversation.getParentConversationId());
			}
			else {
				chatBotSessionConversation1.setParentConversationId(parentChatBotConversation.getId());
			}
			
			chatBotSessionConversation1.setQuery("The given value seems to be invalid.\n"+param.getAskAs());
		}
		else {
			chatBotSessionConversation1.setQuery(param.getAskAs());
		}
		
		ChatBotUtil.addChatBotSessionConversation(chatBotSessionConversation1);
		
		return chatBotSessionConversation1;

	}

	public static void addSessionParams(ChatBotSessionParam sessionParam, ChatBotSession chatBotSession) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBSessionParamsModule().getTableName())
				.fields(FieldFactory.getCBSessionParamsFields());

		Map<String, Object> props = FieldUtil.getAsProperties(sessionParam);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		sessionParam.setId((Long) props.get("id"));
		
		chatBotSession.setRecievedParamCount(chatBotSession.getRecievedParamCount()+1);
		
		updateChatBotSession(chatBotSession);
	}

	public static List<Object> fetchAllSessionParams(long sessionId) throws Exception {
		
		List<FacilioField> cbIntentFields = FieldFactory.getCBIntentParamFields();
		
		List<FacilioField> cbSessionParamFields = FieldFactory.getCBSessionParamsFields();
		
		Map<String, FacilioField> cbSessionParamFieldsMap = FieldFactory.getAsMap(cbSessionParamFields);
		
		cbIntentFields.addAll(cbSessionParamFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(cbIntentFields)
				.table(ModuleFactory.getCBSessionParamsModule().getTableName())
				.innerJoin(ModuleFactory.getCBIntentParamModule().getTableName())
				.on(ModuleFactory.getCBSessionParamsModule().getTableName()+".INTENT_PARAM_ID = "+ModuleFactory.getCBIntentParamModule().getTableName()+".ID")
				.andCondition(CriteriaAPI.getCondition(cbSessionParamFieldsMap.get("sessionId"), sessionId+"", NumberOperators.EQUALS))
				.orderBy("LOCAL_ID")
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			
			List<Object> returnProps = new ArrayList<Object>();
			
			for(Map<String, Object> prop :props) {
				returnProps.add(prop.get("value"));
			}
			
			return returnProps;
		}
		return null;
	}

	public static ChatBotSession getLastWaitingForParamSession() throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		long last30MinsStartTime = DateTimeUtil.getCurrenTime() - ChatBotConstants.LAST_SESSION_WAITING_FOR_PARAM_BUFFER_TIME;
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("startTime"), last30MinsStartTime+"", NumberOperators.GREATER_THAN))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userId"), AccountUtil.getCurrentUser().getId()+"", NumberOperators.EQUALS))
				.orderBy("ID desc")
				.limit(1);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotSession chatBotSession = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSession.class);
			if(chatBotSession.getState() == ChatBotSession.State.WAITING_FOR_PARAMS.getIntVal()) {
				if(chatBotSession.getIntentId() > 0) {
					chatBotSession.setIntent(getIntent(chatBotSession.getIntentId()));
				}
				return chatBotSession;
			}
		}
		return null;
		
	}

	public static ChatBotSessionConversation getLastWaitingForParamConversation(long sessionId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionConversationFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionConversationModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sessionId"), sessionId+"", NumberOperators.EQUALS))
				.orderBy("ID desc")
				.limit(1);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			ChatBotSessionConversation chatBotSessionConversation = FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSessionConversation.class);
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.QUERY_RAISED.getIntVal()) {
				return chatBotSessionConversation;
			}
		}
		return null;
	}

	public static void addChatbotIntent(ChatBotIntent chatBotIntent) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBIntentModule().getTableName())
				.fields(FieldFactory.getCBIntentFields());

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotIntent);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		chatBotIntent.setId((Long) props.get("id"));
	}

	public static void addChatbotIntentParam(ChatBotIntentParam params) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.fields(FieldFactory.getCBIntentParamFields());

		Map<String, Object> props = FieldUtil.getAsProperties(params);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		params.setId((Long) props.get("id"));
	}

	public static void addChatbotIntentInvokeSample(ChatBotIntentInvokeSample invokeSample) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBIntentInvokeSamplesModule().getTableName())
				.fields(FieldFactory.getCBIntentInvokeSamplesFields());

		Map<String, Object> props = FieldUtil.getAsProperties(invokeSample);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		invokeSample.setId((Long) props.get("id"));
	}

	public static void addChatbotIntentAction(ChatBotIntentAction cbaction) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBIntentActionModule().getTableName())
				.fields(FieldFactory.getCBIntentActionFields());

		Map<String, Object> props = FieldUtil.getAsProperties(cbaction);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		cbaction.setId((Long) props.get("id"));
	}
	
	public static ChatBotModel prepareAndAddDefaultModel(String mlModelName) throws Exception {
		
		ChatBotModel model = new ChatBotModel();
		
		model.setOrgId(AccountUtil.getCurrentOrg().getId());
		model.setType(ChatBotModel.App_Type.APP.getIntVal());
		model.setMlModel(mlModelName);
		
		return addModel(model);
	}
	
	public static ChatBotModel addModel(ChatBotModel model) throws Exception {
		
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBModelModule().getTableName())
				.fields(FieldFactory.getCBModelFields());

		Map<String, Object> props = FieldUtil.getAsProperties(model);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		model.setId((Long) props.get("id"));
		return model;
	}

	public static ChatBotModelVersion prepareAndAddModelVersion(ChatBotModel model) throws Exception {
		
		ChatBotModelVersion modelVersion = new ChatBotModelVersion();
		
		modelVersion.setLatestVersion(true);
		modelVersion.setOrgId(AccountUtil.getCurrentOrg().getId());
		modelVersion.setAccuracyRate(40);
		modelVersion.setVersionNo(1);
		modelVersion.setMlModel(model.getMlModel());
		modelVersion.setModelId(model.getId());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBModelVersionModule().getTableName())
				.fields(FieldFactory.getCBModelVersionFields());

		Map<String, Object> props = FieldUtil.getAsProperties(modelVersion);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		modelVersion.setId((Long) props.get("id"));
		return modelVersion;
		
	}

}
