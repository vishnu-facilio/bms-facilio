package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatBotSession {

	long id = -1;
	long orgId = -1;
	long userId = -1;
	long intentId = -1;
	String query;
	String response;
	State state;
	int requiredParamCount = 0;
	int recievedParamCount = 0;
	long startTime = -1;
	long parentSessionId = -1;
	
	public long getParentSessionId() {
		return parentSessionId;
	}

	public void setParentSessionId(long parentSessionId) {
		this.parentSessionId = parentSessionId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	ChatBotIntent intent;
	
	public ChatBotIntent getIntent() {
		return intent;
	}

	public void setIntent(ChatBotIntent intent) {
		this.intent = intent;
	}

	public int getRequiredParamCount() {
		return requiredParamCount;
	}

	public void setRequiredParamCount(int requiredParamCount) {
		this.requiredParamCount = requiredParamCount;
	}

	public int getRecievedParamCount() {
		return recievedParamCount;
	}

	public void setRecievedParamCount(int recievedParamCount) {
		this.recievedParamCount = recievedParamCount;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getIntentId() {
		return intentId;
	}

	public void setIntentId(long intentId) {
		this.intentId = intentId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public int getState() {
		if(state != null) {
			return state.getIntVal();
		}
		return -1;
	}

	public void setState(int state) {
		this.state = State.getAllStates().get(state);
	}

	public enum State {
		
		QUERY_RAISED(1, "Query Raised"),
		WAITING_FOR_PARAMS(2, "Waiting for Params"),
		RESPONDED(3, "Responded"),
		INVALID_QUERY(4, "Invalid Query"),
		TERMINATED_GRACEFULLY(5,"Terminated Gracefully"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public String getName() {
			return name;
		}

		private State(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, State> optionMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, State> initTypeMap() {
			Map<Integer, State> typeMap = new HashMap<>();

			for (State type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, State> getAllStates() {
			return optionMap;
		}
	}
}
