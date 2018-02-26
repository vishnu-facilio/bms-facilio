package com.facilio.bmsconsole.templates;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SMSTemplate extends UserTemplate {

	private String from;
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	
	private String to;
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	private String message;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		obj.put("to", getTo(StrSubstitutor.replace(to, placeHolders)));
		obj.put("message", StrSubstitutor.replace(message, placeHolders));
		return obj;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("to", to);
		obj.put("message", message);
		
		return obj;
	}
	
	
	private Object getTo(String to) {
		if(to != null && !to.isEmpty()) {
			if(to.contains(",")) {
				String[] tos = to.trim().split("\\s*,\\s*");
				JSONArray toList = new JSONArray();
				for(String toAddr : tos) {
					toList.add(toAddr);
				}
				return toList;
			}
			else {
				return to;
			}
		}
		return null;
	}

	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.SMS.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.SMS;
	}
}
