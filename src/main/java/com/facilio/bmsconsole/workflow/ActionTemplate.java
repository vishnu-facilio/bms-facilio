package com.facilio.bmsconsole.workflow;

import java.util.Map;

import org.json.simple.JSONObject;

public interface ActionTemplate {
	public JSONObject getTemplate(Map<String, Object> placeHolders);
	public abstract JSONObject getOriginalTemplate();
}
