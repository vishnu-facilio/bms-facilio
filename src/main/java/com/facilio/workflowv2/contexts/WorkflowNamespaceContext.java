package com.facilio.workflowv2.contexts;

import com.facilio.workflows.context.WorkflowContext;

import java.util.Map;

public class WorkflowNamespaceContext {

	long id = -1;
	long orgid = -1;
	String name;
	Map<String,WorkflowContext> workflowMap;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgid() {
		return orgid;
	}
	public void setOrgid(long orgid) {
		this.orgid = orgid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<String, WorkflowContext> getWorkflowMap() {
		return workflowMap;
	}
	public void setWorkflowMap(Map<String, WorkflowContext> workflowMap) {
		this.workflowMap = workflowMap;
	}
}
