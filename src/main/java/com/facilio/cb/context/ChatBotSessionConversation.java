package com.facilio.cb.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

public class ChatBotSessionConversation {

	long id = -1;
	long orgId = -1;
	long sessionId = -1;
	long parentConversationId = -1;
	String query;
	String response;
	State state;
	ChatBotSession chatBotSession;
	long intentParamId = -1;

	long requestedTime;
	long respondedTime;
	
	public long getRequestedTime() {
		return requestedTime;
	}

	public void setRequestedTime(long requestedTime) {
		this.requestedTime = requestedTime;
	}

	public long getRespondedTime() {
		return respondedTime;
	}

	public void setRespondedTime(long respondedTime) {
		this.respondedTime = respondedTime;
	}

	public long getIntentParamId() {
		return intentParamId;
	}

	public void setIntentParamId(long intentParamId) {
		this.intentParamId = intentParamId;
	}

	public ChatBotSession getChatBotSession() {
		return chatBotSession;
	}


	public void setChatBotSession(ChatBotSession chatBotSession) {
		this.chatBotSession = chatBotSession;
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


	public long getSessionId() {
		return sessionId;
	}


	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}


	public long getParentConversationId() {
		return parentConversationId;
	}


	public void setParentConversationId(long parentConversationId) {
		this.parentConversationId = parentConversationId;
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
		REPLIED_CORRECTLY(2, "Replied Correctly"),
		REPLIED_INCORRECTLY(3, "Replied Incorrectly"),
		TERMINATE_GRACEFULLY_RISED(4,"terminate gracefully rised");
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
