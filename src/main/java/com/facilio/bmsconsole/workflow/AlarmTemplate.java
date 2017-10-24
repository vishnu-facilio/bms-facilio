package com.facilio.bmsconsole.workflow;

import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONObject;

public class AlarmTemplate extends UserTemplate {

	private Long contentId;
	public Long getContentId() {
		return contentId;
	}
	public void setContentId(Long contentId) {
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
		JSONObject obj = new JSONObject();
		obj.put("content", StrSubstitutor.replace(content, placeHolders));
		return obj;
	}
}
