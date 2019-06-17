package com.facilio.workflowv2.contexts;

import java.util.List;

import com.facilio.workflows.context.WorkflowUserFunctionContext;

public class WorkflowNamespaceContext {

	long id = -1;
	long orgid = -1;
	String name;
	List<WorkflowUserFunctionContext> functions;
	
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
	public List<WorkflowUserFunctionContext> getFunctions() {
		return functions;
	}
	public void setFunctions(List<WorkflowUserFunctionContext> functions) {
		this.functions = functions;
	}
}
