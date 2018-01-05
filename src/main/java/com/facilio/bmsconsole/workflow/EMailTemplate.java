package com.facilio.bmsconsole.workflow;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class EMailTemplate extends UserTemplate {
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
	
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	private long bodyId;
	public long getBodyId() {
		return bodyId;
	}
	public void setBodyId(long bodyId) {
		this.bodyId = bodyId;
	}
	
	private String body;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		obj.put("sender", StrSubstitutor.replace(from, placeHolders));
		obj.put("to", getTo(StrSubstitutor.replace(to, placeHolders)));
		obj.put("subject", StrSubstitutor.replace(subject, placeHolders));
		obj.put("message", StrSubstitutor.replace(body, placeHolders));
		
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
		return Type.EMAIL.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.EMAIL;
	}
}
