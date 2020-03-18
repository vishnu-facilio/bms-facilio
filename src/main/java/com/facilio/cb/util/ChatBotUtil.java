package com.facilio.cb.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.cb.context.ChatBotConfirmContext;
import com.facilio.cb.context.ChatBotExecuteContext;
import com.facilio.cb.context.ChatBotIntent;
import com.facilio.cb.context.ChatBotIntentAction;
import com.facilio.cb.context.ChatBotIntentInvokeSample;
import com.facilio.cb.context.ChatBotIntentParam;
import com.facilio.cb.context.ChatBotModel;
import com.facilio.cb.context.ChatBotModel.App_Type;
import com.facilio.cb.context.ChatBotModelVersion;
import com.facilio.cb.context.ChatBotParamContext;
import com.facilio.cb.context.ChatBotSession;
import com.facilio.cb.context.ChatBotSessionConversation;
import com.facilio.cb.context.ChatBotSessionParam;
import com.facilio.cb.util.ChatBotConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowContext.WorkflowUIMode;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

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
			fillIntentExtraParams(chatBotIntent);
			return chatBotIntent;
		}
		return null;
	}
	
	public static void fillIntentExtraParams(ChatBotIntent chatBotIntent) throws Exception {
		
		if(chatBotIntent.isWithParams()) {
			chatBotIntent.setChatBotIntentParamList(getIntentParamsList(chatBotIntent.getId()));
		}
		chatBotIntent.setActions(ActionAPI.getActionsFromChatBotIntent(chatBotIntent.getId()));
		if(chatBotIntent.getContextWorkflowId() > 0) {
			chatBotIntent.setContextWorkflow(WorkflowUtil.getWorkflowContext(chatBotIntent.getContextWorkflowId()));
		}
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
			fillIntentExtraParams(chatBotIntent);
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
	
	public static List<ChatBotIntentParam> getIntentParamsList(long intentId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.orderBy("LOCAL_ID");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<ChatBotIntentParam> chatBotIntentParams = FieldUtil.getAsBeanListFromMapList(props, ChatBotIntentParam.class);
			
			return chatBotIntentParams;
		}
		return null;
	}
	
	public static Map<Long,ChatBotIntentParam> getIntentParamsMap(long intentId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.orderBy("LOCAL_ID");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<Long,ChatBotIntentParam> chatBotIntentMap = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				
				ChatBotIntentParam chatBotIntentParam = FieldUtil.getAsBeanFromMap(prop, ChatBotIntentParam.class);
				
				chatBotIntentMap.put(chatBotIntentParam.getId(), chatBotIntentParam);
			}
		}
			return chatBotIntentMap;
	}
		
	
	public static void executeIntentAction(ChatBotSession session,ChatBotIntent intent,Context context) throws Exception {
		Map<String,Object> props = ChatBotUtil.fetchAllSessionParams(session.getId());
		
		context.put(ChatBotConstants.CHAT_BOT_INTENT, intent);
		
		JSONArray response = intent.executeActions(context, Collections.singletonList(props));
		
		session.setResponse(response.toJSONString());
		session.setState(ChatBotSession.State.RESPONDED.getIntVal());
		
		context.put(ChatBotConstants.CHAT_BOT_SESSION, session);
		context.put(ChatBotConstants.CHAT_BOT_IS_ACTION_EXECUTED, true);
		
		updateChatBotSession(session);
	}
	
	public static void executeContextWorkflow(ChatBotIntent intent,ChatBotSession session,Context context) throws Exception {
		
		Map<String,Object> props = ChatBotUtil.fetchAllSessionParams(session.getId());
		
		WorkflowContext contextWorkflow = intent.getContextWorkflow();
		
		contextWorkflow.setParams(Collections.singletonList(props));
		
		FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
		
		FacilioContext newContext = chain.getContext();
		newContext.put(WorkflowV2Util.WORKFLOW_CONTEXT, contextWorkflow);
		
		chain.execute();
		Object value = contextWorkflow.getReturnValue();
		
		if(value instanceof ChatBotParamContext) {
			
			ChatBotParamContext params = (ChatBotParamContext) value;
			
			ChatBotIntentParam intentParam = ChatBotUtil.getIntentParam(intent.getId(), params.getParamName());
			intentParam.setOptions(params.getOptions());
			intentParam.setModuleName(params.getModuleName());
			intentParam.setCriteria(params.getCriteria());
			
			ChatBotSessionConversation newChatBotSessionConversation = ChatBotUtil.constructAndAddCBSessionConversationParams(intentParam, session,null,null);
			
			context.put(ChatBotConstants.CHAT_BOT_SESSION_CONVERSATION, newChatBotSessionConversation);
			
		}
		else if (value instanceof ChatBotConfirmContext) {
			
		}
		else if (value instanceof ChatBotExecuteContext) {
			
			ChatBotUtil.executeIntentAction(session, intent, context);
		}
		
	}
	
	private static ChatBotIntentParam getIntentParam(long intentId,String paramName) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), paramName, StringOperators.IS))
				.orderBy("LOCAL_ID");
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			return FieldUtil.getAsBeanFromMap(props.get(0), ChatBotIntentParam.class);
		}
		return null;
	}
	
	public static ChatBotIntentParam getIntentParam(long intentParamId) throws Exception {
		
		
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
	
	public static Map<Long,List<ChatBotSessionConversation>> getSessionConversationMap(List<Long> sessionIds) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionConversationFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getCBSessionConversationModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sessionId"), sessionIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			Map<Long,List<ChatBotSessionConversation>> sessionConversationMap = new HashMap<>();
			
			for(Map<String, Object> prop :props) {
				ChatBotSessionConversation chatBotSessionConversation = FieldUtil.getAsBeanFromMap(prop, ChatBotSessionConversation.class);
				
				if(sessionConversationMap.containsKey(chatBotSessionConversation.getSessionId())) {
					sessionConversationMap.get(chatBotSessionConversation.getSessionId()).add(chatBotSessionConversation);
				}
				else {
					List<ChatBotSessionConversation> chatBotSessionConversations = new ArrayList<>();
					chatBotSessionConversations.add(chatBotSessionConversation);
					sessionConversationMap.put(chatBotSessionConversation.getSessionId(), chatBotSessionConversations);
				}
			}
			return sessionConversationMap;
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
		
		chatBotSession.setEndTime(DateTimeUtil.getCurrenTime());
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getCBSessionModule().getTableName())
				.fields(FieldFactory.getCBSessionFields())
				.andCondition(CriteriaAPI.getIdCondition(chatBotSession.getId(), ModuleFactory.getCBSessionModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(chatBotSession);
		
		update.update(props);
	}
	
	public static void addChatBotSession(ChatBotSession chatBotSession) throws Exception {
		
		chatBotSession.setEndTime(DateTimeUtil.getCurrenTime());
		
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
	
	public static ChatBotSessionConversation constructAndAddCBSessionConversationParams(ChatBotIntentParam param,ChatBotSession session,ChatBotSessionConversation parentChatBotConversation,JSONObject responseJson) throws Exception {
		
		
		ChatBotSessionConversation chatBotSessionConversation1 = new ChatBotSessionConversation();
		
		chatBotSessionConversation1.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		if(responseJson != null) {
			chatBotSessionConversation1.setResponseJson(responseJson);
			chatBotSessionConversation1.setState(ChatBotSessionConversation.State.REPLIED_CORRECTLY.getIntVal());
		}
		else {
			chatBotSessionConversation1.setState(ChatBotSessionConversation.State.QUERY_RAISED.getIntVal());
		}
		
		chatBotSessionConversation1.setSessionId(session.getId());
		chatBotSessionConversation1.setChatBotSession(session);
		
		chatBotSessionConversation1.setIntentParamId(param.getId());
		
		chatBotSessionConversation1.setRequestedTime(DateTimeUtil.getCurrenTime());
		
		JSONArray resArray = new JSONArray();
		JSONObject result = new JSONObject();
		resArray.add(result);
		if(parentChatBotConversation != null) {
			
			if(parentChatBotConversation.getParentConversationId() > 0) {
				chatBotSessionConversation1.setParentConversationId(parentChatBotConversation.getParentConversationId());
			}
			else {
				chatBotSessionConversation1.setParentConversationId(parentChatBotConversation.getId());
			}
			result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, ChatBotIntentAction.ResponseType.STRING.getIntVal());
			result.put(ChatBotConstants.CHAT_BOT_RESPONSE, "The given value seems to be invalid.\n"+param.getAskAs());
			
		}
		else {
			if(param.getOptions() != null && !param.getOptions().isEmpty()) {
				result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, ChatBotIntentAction.ResponseType.SINGLE_SELECT.getIntVal());
				
				result.put(ChatBotConstants.CHAT_BOT_RESPONSE_OPTIONS, param.getOptions());
			}
			else if(param.getCriteria() != null) {
				result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, ChatBotIntentAction.ResponseType.LOOKUP.getIntVal());
				result.put(FacilioConstants.ContextNames.CRITERIA, FieldUtil.getAsJSON(param.getCriteria()));
				result.put(FacilioConstants.ContextNames.MODULE_NAME, param.getModuleName());
			}
			else {
				result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, ChatBotIntentAction.ResponseType.STRING.getIntVal());
			}
			
			result.put(ChatBotConstants.CHAT_BOT_RESPONSE, param.getAskAs());
		}
		
		chatBotSessionConversation1.setQuery(resArray.toJSONString());
		
		ChatBotUtil.addChatBotSessionConversation(chatBotSessionConversation1);
		
		return chatBotSessionConversation1;

	}
	
	public static ChatBotSessionConversation constructAndAddConfirmationCBSessionConversationParams(ChatBotSession session) throws Exception {
		
		ChatBotSessionConversation chatBotSessionConversation1 = new ChatBotSessionConversation();
		
		chatBotSessionConversation1.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		chatBotSessionConversation1.setState(ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal());
		
		chatBotSessionConversation1.setSessionId(session.getId());
		
		chatBotSessionConversation1.setChatBotSession(session);
		
		chatBotSessionConversation1.setRequestedTime(DateTimeUtil.getCurrenTime());
		
		JSONArray resArray = new JSONArray();
		JSONObject result = new JSONObject();
		resArray.add(result);
		
		Map<Long, List<ChatBotSessionConversation>> conversationMap = ChatBotUtil.getSessionConversationMap(Collections.singletonList(session.getId()));
		
		List<ChatBotSessionConversation> conversations = conversationMap.get(session.getId());
		
		Map<Long, ChatBotIntentParam> intentParams = ChatBotUtil.getIntentParamsMap(session.getIntentId());
		
		for(ChatBotSessionConversation conversation :conversations) {
			
			conversation.setIntentParam(intentParams.get(conversation.getIntentParamId()));
		}
		
		result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, ChatBotIntentAction.ResponseType.CONFIRMATION_CARD.getIntVal());
		
		result.put(ChatBotConstants.CHAT_BOT_RESPONSE, "Please confirm the changes");
		
		result.put(ChatBotConstants.CHAT_BOT_CONFIRMATION_RESPONSE, FieldUtil.getAsJSONArray(conversations, ChatBotSessionConversation.class));
		
		chatBotSessionConversation1.setQuery(resArray.toJSONString());
		
		ChatBotUtil.addChatBotSessionConversation(chatBotSessionConversation1);
		
		return chatBotSessionConversation1;
	}

	public static void addSessionParams(ChatBotSessionParam sessionParam) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCBSessionParamsModule().getTableName())
				.fields(FieldFactory.getCBSessionParamsFields());

		Map<String, Object> props = FieldUtil.getAsProperties(sessionParam);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		sessionParam.setId((Long) props.get("id"));
		
//		chatBotSession.setRecievedParamCount(chatBotSession.getRecievedParamCount()+1);
//		
//		updateChatBotSession(chatBotSession);
	}

	public static Map<String,Object> fetchAllSessionParams(long sessionId) throws Exception {
		
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
		
		Map<String,Object> returnProps = new HashMap<>();
		
		if(props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				int datatype = (int) prop.get("dataType");
				Object value = null;
				if(prop.get("value") != null) {
					value = FacilioUtil.castOrParseValueAsPerType(FieldType.getCFType(datatype), prop.get("value"));
				}
				returnProps.put(prop.get("name").toString(), value);
			}
			
		}
		return returnProps;
	}
	
	public static ChatBotSessionParam fetchSessionParam(long sessionId,long intentParamId) throws Exception {
		
		List<FacilioField> cbSessionParamFields = FieldFactory.getCBSessionParamsFields();
		
		Map<String, FacilioField> cbSessionParamFieldsMap = FieldFactory.getAsMap(cbSessionParamFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(cbSessionParamFields)
				.table(ModuleFactory.getCBSessionParamsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(cbSessionParamFieldsMap.get("sessionId"), sessionId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(cbSessionParamFieldsMap.get("intentParamId"), intentParamId+"", NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			
			return FieldUtil.getAsBeanFromMap(props.get(0), ChatBotSessionParam.class);
		}
		return null;
	}
	
	public static List<ChatBotIntentParam> fetchRemainingMainChatBotIntentParams(long intentId,long sessionId) throws Exception {
		
List<FacilioField> cbIntentFields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> cbIntentFieldsMap = FieldFactory.getAsMap(cbIntentFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(cbIntentFields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(cbIntentFieldsMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(cbIntentFieldsMap.get("optional"),Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCustomWhere("ID NOT IN (select INTENT_PARAM_ID from CB_Session_Params where SESSION_ID = ?)", sessionId)
				.orderBy("LOCAL_ID")
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			
			List<ChatBotIntentParam> intentParams = FieldUtil.getAsBeanListFromMapList(props, ChatBotIntentParam.class);
			return intentParams;
		}
		return null;
	}
	
	public static List<ChatBotIntentParam> fetchRemainingOptionalChatBotIntentParams(long intentId,long sessionId) throws Exception {
		
		List<FacilioField> cbIntentFields = FieldFactory.getCBIntentParamFields();
		
		Map<String, FacilioField> cbIntentFieldsMap = FieldFactory.getAsMap(cbIntentFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(cbIntentFields)
				.table(ModuleFactory.getCBIntentParamModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(cbIntentFieldsMap.get("intentId"), intentId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(cbIntentFieldsMap.get("optional"),Boolean.TRUE.toString(), BooleanOperators.IS))
				.andCustomWhere("ID NOT IN (select INTENT_PARAM_ID from CB_Session_Params where SESSION_ID = ?)", sessionId)
				.orderBy("LOCAL_ID")
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			
			List<ChatBotIntentParam> intentParams = FieldUtil.getAsBeanListFromMapList(props, ChatBotIntentParam.class);
			return intentParams;
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
	
	public static ChatBotSession getLastWaitingForConfirmationSession() throws Exception {
		
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
			if(chatBotSession.getState() == ChatBotSession.State.WAITING_FOR_CONFIRMATION.getIntVal()) {
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
	
	public static ChatBotSessionConversation getLastWaitingForConfirmationConversation(long sessionId) throws Exception {
		
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
			if(chatBotSessionConversation.getState() == ChatBotSessionConversation.State.CONFIRMATION_RAISED.getIntVal()) {
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
		modelVersion.setAccuracyRate(ChatBotConstants.DEFAULT_ACCURACY_RATE);
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
	
	public static void deleteAndAddSessionParam(long intentParamId,long sessionId, String value) throws Exception {
		
		deleteSessionParam(intentParamId, sessionId);
		
		ChatBotSessionParam sessionParam = new ChatBotSessionParam();
		
		sessionParam.setOrgId(AccountUtil.getCurrentOrg().getId());
		sessionParam.setIntentParamId(intentParamId);
		sessionParam.setSessionId(sessionId);
		sessionParam.setValue(value);
		
		
		ChatBotUtil.addSessionParams(sessionParam);
	}
	
	public static void deleteSessionParam(long intentParamId,long sessionId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getCBSessionParamsFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getCBSessionParamsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("intentParamId"), intentParamId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sessionId"), sessionId+"", NumberOperators.EQUALS));
		
		deleteRecordBuilder.delete();
		
	}
	
	public static int getRequiredParamCount(List<ChatBotIntentParam> intentParams) throws Exception {
		
		int i=0;
		
		for(ChatBotIntentParam intentParam :intentParams) {
			if(!intentParam.isOptional()) {
				i++;
			}
		}
		return i;
	}
	
	public static Object getRequiredFieldFromQueryJson(JSONObject queryJson) throws Exception {
		
		if(queryJson.containsKey(ChatBotConstants.CHAT_BOT_ID) && queryJson.get(ChatBotConstants.CHAT_BOT_ID) != null) {
			return queryJson.get(ChatBotConstants.CHAT_BOT_ID);
		}
		else {
			return queryJson.get(ChatBotConstants.CHAT_BOT_LABEL);
		}
	}

}
