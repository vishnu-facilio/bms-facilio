package com.facilio.cb.context;

import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;

public class ChatBotParamContext {
	
	List<String> message;
	public List<String> getMessage() {
		return message;
	}
	public void setMessage(List<String> message) {
		this.message = message;
	}
	String paramName;
	String moduleName;		// for criteria Type
	Criteria criteria;
	JSONObject dateSlot;
	List<JSONObject> options;
	
	public String getParamName() {
		return paramName;
	}
	public JSONObject getDateSlot() {
		return dateSlot;
	}
	public void setDateSlot(JSONObject dateSlot) {
		this.dateSlot = dateSlot;
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
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
