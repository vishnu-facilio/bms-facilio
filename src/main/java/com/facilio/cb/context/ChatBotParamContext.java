package com.facilio.cb.context;

import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.db.criteria.Criteria;

public class ChatBotParamContext {
	
	String message;
	String paramName;
	String moduleName;		// for criteria Type
	Criteria criteria;
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
