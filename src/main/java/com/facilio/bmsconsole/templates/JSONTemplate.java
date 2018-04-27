package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JSONTemplate extends Template {

	private long contentId = -1;
	public long getContentId() {
		return contentId;
	}
	public void setContentId(long contentId) {
		this.contentId = contentId;
	}
	
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		if (content != null) {
			try {
				JSONParser parser = new JSONParser();
				return (JSONObject) parser.parse(content);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	@Override
	@JsonInclude(Include.ALWAYS)
	public int getType() {
		return Type.JSON.getIntVal();
	}
	@Override
	@JsonInclude(Include.ALWAYS)
	public Type getTypeEnum() {
		return Type.JSON;
	}
}
