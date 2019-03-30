package com.facilio.bmsconsole.templates;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WorkflowTemplate extends Template {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JSONObject metaJson;
	public String getMeta() {
		if(metaJson != null) {
			return metaJson.toJSONString();
		}
		return null;
	}
	public void setMeta(String meta) throws ParseException {
		JSONParser parser = new JSONParser();
		this.metaJson = (JSONObject) parser.parse(meta);
	}
	public JSONObject getMetaJson() throws ParseException {
		return metaJson;
	}
	public void setMetaJson(JSONObject metaJson) {
		this.metaJson = metaJson;
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
			json.put("workflowContext", FieldUtil.getAsJSON(WorkflowUtil.getWorkflowContext(workflowContext.getId(),true)));
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
