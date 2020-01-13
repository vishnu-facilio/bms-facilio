package com.facilio.cb.context;

public class ChatBotSessionParam {

	long id = -1;
	long orgId = -1;
	long sessionId = -1;
	long intentParamId = -1;
	
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
	public long getIntentParamId() {
		return intentParamId;
	}
	public void setIntentParamId(long intentParamId) {
		this.intentParamId = intentParamId;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	String Value;
}
