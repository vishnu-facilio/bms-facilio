package com.facilio.bmsconsole.templates;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class PushNotificationTemplate extends Template{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(PushNotificationTemplate.class.getName());

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
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		if (body != null) {
			JSONParser parser = new JSONParser();
	 		try {
	 			obj = (JSONObject) parser.parse(body);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.info("Exception occurred ", e);
			}
		}
//		obj.put("to", to);
//		obj.put("body", body);
//		obj.put("URL", url);
//		obj.put("title", title);
		
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
