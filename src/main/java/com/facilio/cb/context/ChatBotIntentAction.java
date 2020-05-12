package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.ActionContext;

public class ChatBotIntentAction {

	long id = -1;
	long orgId = -1;
	long intentId = -1;
	long actionId = -1;
	String response;
	
	ActionContext action;
	
	String moduleName;
	String viewName;
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public ActionContext getAction() {
		return action;
	}
	public void setAction(ActionContext action) {
		this.action = action;
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
	public long getIntentId() {
		return intentId;
	}
	public void setIntentId(long intentId) {
		this.intentId = intentId;
	}
	public long getActionId() {
		return actionId;
	}
	public void setActionId(long actionId) {
		this.actionId = actionId;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	ResponseType responseType;
	
	
	public int getResponseType() {
		if(responseType != null) {
			return responseType.getIntVal();
		}
		return ResponseType.STRING.getIntVal();
	}
	public void setResponseType(int responseType) {
		if(responseType > 0) {
			this.responseType = ResponseType.getAllAppTypes().get(responseType);
		}
	}


	public enum ResponseType {
		STRING(1, "String"),
		COUNT_CARD(2, "Count Card"),
		LIST_CARD(3,"List Card"),
		SINGLE_SELECT(4,"Single Select"),
		GRAPH(5,"Graph"),
		CONFIRMATION_CARD(6,"Confirmation Card"),
		LOOKUP(7,"Lookup"),
		INTENT_LIST(8,"Intent List"),
		SLOTED_DATES(9,"Sloted Dated"),
		LIST_RESPONSE(10,"List Response"),
		SUMMARY_CARD(11,"Summary Card"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private ResponseType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, ResponseType> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, ResponseType> initTypeMap() {
			Map<Integer, ResponseType> typeMap = new HashMap<>();

			for (ResponseType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, ResponseType> getAllAppTypes() {
			return optionMap;
		}
	}
}
