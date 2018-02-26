package com.facilio.bmsconsole.templates;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class JSONTemplate extends UserTemplate {

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
	
	@SuppressWarnings("unchecked")
	@Override
	public JSONObject getTemplate(Map<String, Object> placeHolders) {
		// TODO Auto-generated method stub
		try {
			JSONParser parser = new JSONParser();
			return (JSONObject) parser.parse(StrSubstitutor.replace(content, placeHolders));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public JSONObject getOriginalTemplate() {
		JSONObject obj = new JSONObject();
		obj.put("content", content);
		
		return obj;
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
