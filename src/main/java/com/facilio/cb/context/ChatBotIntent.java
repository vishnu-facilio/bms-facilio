package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.cb.util.ChatBotConstants;
import com.facilio.cb.util.ChatBotUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ChatBotIntent {

	long id = -1;
	long orgId = -1;
	long modelVersionId = -1;
	String name;
	String displayName;
	long contextWorkflowId= -1;
	boolean deleted;
	boolean withParams;
	Intent_Type type;
	List<ChatBotIntentAction> actions;
	List<ChatBotIntentInvokeSample> invokeSamples;
	WorkflowContext contextWorkflow;
//	boolean confirmationNeeded;
	List<String> childIntentNames;
	String confirmationText;
	
	public String getConfirmationText() {
		return confirmationText;
	}

	public void setConfirmationText(String confirmationText) {
		this.confirmationText = confirmationText;
	}

	public List<String> getChildIntentNames() {
		return childIntentNames;
	}

	public void setChildIntentNames(List<String> childIntentNames) {
		this.childIntentNames = childIntentNames;
	}

	List<ChatBotIntentParam> params;
	
	public List<ChatBotIntentParam> getParams() {
		return params;
	}

	public void setParams(List<ChatBotIntentParam> params) {
		this.params = params;
	}

	public List<ChatBotIntentInvokeSample> getInvokeSamples() {
		return invokeSamples;
	}
	
//	public boolean isConfirmationNeeded() {
//		return confirmationNeeded;
//	}
//
//	public void setConfirmationNeeded(boolean confirmationNeeded) {
//		this.confirmationNeeded = confirmationNeeded;
//	}

	public void setInvokeSamples(List<ChatBotIntentInvokeSample> invokeSamples) {
		this.invokeSamples = invokeSamples;
	}

	public List<ChatBotIntentAction> getActions() {
		return actions;
	}

	public void setActions(List<ChatBotIntentAction> actions) {
		this.actions = actions;
	}
	
	public JSONArray executeActions(Context context, List<Object> params) throws Exception {
		
		JSONArray resArray = new JSONArray(); 
		if(actions != null) {
			for(ChatBotIntentAction cbaction : actions) {
				if(cbaction.getAction() != null) {
					cbaction.getAction().executeAction(null, context, null, params);
					
				}

				JSONObject result = new JSONObject();
				result.put(ChatBotConstants.CHAT_BOT_RESPONSE_TYPE, cbaction.getResponseType());
				
				if(cbaction.getModuleName() != null) {
					result.put(FacilioConstants.ContextNames.MODULE_NAME, cbaction.getModuleName());
				}
				if(cbaction.getViewName() != null) {
					result.put(FacilioConstants.ContextNames.VIEW_NAME, cbaction.getViewName());
				}
				
				if(context.get(WorkflowV2Util.WORKFLOW_RESPONSE) != null) {
					Map<String, Object> resultMap = (Map<String,Object>) context.get(WorkflowV2Util.WORKFLOW_RESPONSE);
					
					if(resultMap.containsKey(ChatBotConstants.CHAT_BOT_WORKFLOW_RETURN_TEXT)) {
						result.put(ChatBotConstants.CHAT_BOT_RESPONSE, resultMap.get(ChatBotConstants.CHAT_BOT_WORKFLOW_RETURN_TEXT));
					}
					else {
						result.put(ChatBotConstants.CHAT_BOT_RESPONSE,resultMap);
					}
					
					if(resultMap.containsKey(ChatBotConstants.CHAT_BOT_WORKFLOW_RETURN_EXTRA_PARAM)) {
						Map<String, Object> extraParam = (Map<String,Object>) resultMap.get(ChatBotConstants.CHAT_BOT_WORKFLOW_RETURN_EXTRA_PARAM);
						
						ChatBotSession session = ChatBotUtil.getChatbotSessionFromContext(context);
						for(String key : extraParam.keySet()) {
							Object param = extraParam.get(key);
							ChatBotIntentParam intentParamContext = ChatBotUtil.getIntentParam(session.getIntentId(),key);
							
							ChatBotUtil.addSessionExtraParam(intentParamContext, session, param.toString());
						}
					}
				}
				else if(cbaction.getResponse() != null) {
					result.put(ChatBotConstants.CHAT_BOT_RESPONSE, cbaction.getResponse());
				}
				 
				resArray.add(result);
			}
		}
		return resArray;
	}
	
	List<ChatBotIntentParam> chatBotIntentParamList;


	public List<ChatBotIntentParam> getChatBotIntentParamList() {
		return chatBotIntentParamList;
	}

	public void setChatBotIntentParamList(List<ChatBotIntentParam> chatBotIntentParamList) {
		this.chatBotIntentParamList = chatBotIntentParamList;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getModelVersionId() {
		return modelVersionId;
	}

	public void setModelVersionId(long modelVersionId) {
		this.modelVersionId = modelVersionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public long getContextWorkflowId() {
		return contextWorkflowId;
	}

	public void setContextWorkflowId(long contextWorkflowId) {
		this.contextWorkflowId = contextWorkflowId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public boolean isWithParams() {
		return withParams;
	}

	public void setWithParams(boolean withParams) {
		this.withParams = withParams;
	}

	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	
	public WorkflowContext getContextWorkflow() {
		return contextWorkflow;
	}

	public void setContextWorkflow(WorkflowContext contextWorkflow) {
		this.contextWorkflow = contextWorkflow;
	}

	public void setType(int type) {
		this.type = Intent_Type.getAllAppTypes().get(type);
	}

	public enum Intent_Type {
		SYSTEM(1, "System"),
		USER(2, "User"),
		SYSTEM_SERVER(3,"System Server");
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private Intent_Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Intent_Type> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Intent_Type> initTypeMap() {
			Map<Integer, Intent_Type> typeMap = new HashMap<>();

			for (Intent_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Intent_Type> getAllAppTypes() {
			return optionMap;
		}
	}
}
