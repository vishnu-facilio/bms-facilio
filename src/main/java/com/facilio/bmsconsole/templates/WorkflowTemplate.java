package com.facilio.bmsconsole.templates;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
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
		if(metaJson == null && meta != null) {
			JSONParser parser = new JSONParser();
			this.metaJson = (JSONObject) parser.parse(meta);
		}
		return metaJson;
	}
	public void setMetaJson(JSONObject metaJson) {
		this.metaJson = metaJson;
		if(metaJson != null) {
			setMeta(metaJson.toJSONString());
		}
	}
	
	private WorkflowContext resultWorkflowContext;
	
	public WorkflowContext getResultWorkflowContext() {
		return resultWorkflowContext;
	}
	public void setResultWorkflowContext(WorkflowContext resultWorkflowContext) {
		this.resultWorkflowContext = resultWorkflowContext;
	}

	long resultWorkflowId = -1l;
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		
		WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(resultWorkflowId);
		JSONObject json = new JSONObject();
		if(workflowContext != null) {
			json.put("WorkflowString", workflowContext.getWorkflowString());
			json.put("workflowContext", WorkflowUtil.getWorkflowContext(workflowContext.getId(),true));
		}
		if(getMetaJson() != null) {
			json.putAll(getMetaJson());
		}
		return json;
	}
	public long getResultWorkflowId() {
		return resultWorkflowId;
	}
	public void setResultWorkflowId(long resultWorkflowId) {
		this.resultWorkflowId = resultWorkflowId;
	}

}
