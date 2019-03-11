package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowTemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String meta;
	private JSONObject metaJson;
	
	public String getMeta() {
		if(metaJson != null) {
			metaJson.toJSONString();
		}
		return meta;
	}
	public void setMeta(String meta) {
		this.meta = meta;
	}
	public JSONObject getMetaJson() throws ParseException {
		if(metaJson == null) {
			JSONParser parser = new JSONParser();
			this.metaJson = (JSONObject) parser.parse(meta);
		}
		return metaJson;
	}
	public void setMetaJson(JSONObject metaJson) {
		this.metaJson = metaJson;
	}
	
	long resultWorkflowId = -1l;
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		
		WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(resultWorkflowId);
		JSONObject json = new JSONObject();
		json.put("WorkflowString", workflowContext.getWorkflowString());
		json.put("cost", 10);
		json.putAll(getMetaJson());
		return json;
	}
	public long getResultWorkflowId() {
		return resultWorkflowId;
	}
	public void setResultWorkflowId(long resultWorkflowId) {
		this.resultWorkflowId = resultWorkflowId;
	}

}
