package com.facilio.bmsconsole.workflow;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.workflow.UserTemplate.Type;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class WebNotificationTemplate extends UserTemplate{

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
	
	private String url;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	private String title;	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		JSONObject obj = new JSONObject();
		obj.put("to", getTo(StrSubstitutor.replace(to, placeHolders)));
		obj.put("message", StrSubstitutor.replace(message, placeHolders));
		obj.put("activityType", placeHolders.get("rule.event.activityType"));
		obj.put("URL", StrSubstitutor.replace(url, placeHolders));
		obj.put("title", StrSubstitutor.replace(title, placeHolders));
		return obj;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("to", to);
		obj.put("message", message);
		obj.put("URL", url);
		obj.put("title", title);
		
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
		return Type.WEB_NOTIFICATION.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.WEB_NOTIFICATION;
	}
}
