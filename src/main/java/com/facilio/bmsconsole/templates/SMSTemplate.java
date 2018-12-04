package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class SMSTemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("to", to);
		obj.put("message", message);
		
		return obj;
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
