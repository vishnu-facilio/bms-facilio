package com.facilio.bmsconsole.templates;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PushNotificationTemplate extends UserTemplate{

	private String to;
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	
	private String body;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
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
		JSONObject data = new JSONObject();
		obj.put("to", getTo(StrSubstitutor.replace(to, placeHolders)));
		obj.put("body", StrSubstitutor.replace(body, placeHolders));
		obj.put("URL", StrSubstitutor.replace(url, placeHolders));
		obj.put("title", StrSubstitutor.replace(title, placeHolders));
		obj.put("content_available", true);
		obj.put("priority", "high");
		obj.put("sound", "default");
		data.put("data", obj);
		return data;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("to", to);
		obj.put("body", body);
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
		return Type.PUSH_NOTIFICATION.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.PUSH_NOTIFICATION;
	}

	
}
