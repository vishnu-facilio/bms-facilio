package com.facilio.workflowv2.contexts;

import java.util.Map;

import com.facilio.workflows.context.WorkflowContext;

public class WorkflowNamespaceContext {

	long id;
	long orgid;
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
