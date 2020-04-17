package com.facilio.cb.context;

import java.util.List;
import java.util.Map;

public class ChatBotConfirmContext {
	
	List<String> message;
	public List<String> getMessage() {
		return message;
	}
	public void setMessage(List<String> message) {
		this.message = message;
	}
	
	String inCardMessage;
	
	public String getInCardMessage() {
		return inCardMessage;
	}
	public void setInCardMessage(String inCardMessage) {
		this.inCardMessage = inCardMessage;
	}

	Map<String,Object> paramMap;

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, Object> paramMap) {
		this.paramMap = paramMap;
	}
}
