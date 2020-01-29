package com.facilio.cb.context;

import java.util.List;

import org.json.simple.JSONObject;

public class ChatBotParamContext {
	
	String paramName;
	List<JSONObject> options;
	
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public List<JSONObject> getOptions() {
		return options;
	}
	public void setOptions(List<JSONObject> options) {
		this.options = options;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	String message;
}
