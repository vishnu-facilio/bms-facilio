package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.cb.util.ChatBotConstants;

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

	public void setInvokeSamples(List<ChatBotIntentInvokeSample> invokeSamples) {
		this.invokeSamples = invokeSamples;
	}

	public List<ChatBotIntentAction> getActions() {
		return actions;
	}

	public void setActions(List<ChatBotIntentAction> actions) {
		this.actions = actions;
	}
	
	public String executeActions(Context context, List<Object> params) throws Exception {
		
		StringBuilder returnString = new StringBuilder();
		if(actions != null) {
			for(ChatBotIntentAction cbaction : actions) {
				if(cbaction.getAction() != null) {
					cbaction.getAction().executeAction(null, context, null, params);
				}
				
				if(context.containsKey(ChatBotConstants.CHAT_BOT_RESPONSE_STRING) && context.get(ChatBotConstants.CHAT_BOT_RESPONSE_STRING) != null && !((String)context.get(ChatBotConstants.CHAT_BOT_RESPONSE_STRING)).isEmpty()) {
					returnString.append((String)context.get(ChatBotConstants.CHAT_BOT_RESPONSE_STRING));
				}
				else if(cbaction.getResponse() != null) {
					returnString.append(cbaction.getResponse());
				}
			}
		}
		return returnString.toString();
	}
	
	Map<Integer,ChatBotIntentParam> chatBotIntentParamMap;


	public Map<Integer, ChatBotIntentParam> getChatBotIntentParamMap() {
		return chatBotIntentParamMap;
	}

	public void setChatBotIntentParamMap(Map<Integer, ChatBotIntentParam> chatBotIntentParamMap) {
		this.chatBotIntentParamMap = chatBotIntentParamMap;
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
