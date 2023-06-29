package com.facilio.workflowv2.bean;

import com.facilio.beans.RootBean;
import com.facilio.chain.FacilioContext;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;

import java.util.List;

public interface ScriptBean extends RootBean {

	public WorkflowNamespaceContext addNameSpace(WorkflowNamespaceContext nameSpace) throws Exception;
	public WorkflowNamespaceContext updateNameSpace(WorkflowNamespaceContext nameSpace) throws Exception;
	public WorkflowNamespaceContext deleteNameSpace(WorkflowNamespaceContext nameSpace) throws Exception;
	public void deleteNameSpacesForIds(List<Long> ids) throws Exception;
	public WorkflowNamespaceContext getNameSpace(Long nameSpaceId) throws Exception;
	public WorkflowNamespaceContext getNameSpace(String nameSpaceName) throws Exception;


	// Why are we using FacilioContext as return type?
	public FacilioContext addFunction(WorkflowUserFunctionContext function) throws Exception;
	public FacilioContext updateFunction(WorkflowUserFunctionContext function) throws Exception;
	public WorkflowUserFunctionContext deleteFunction(WorkflowUserFunctionContext function) throws Exception;
	public WorkflowContext deleteFunction(WorkflowContext function) throws Exception;
	public void deleteFunctionsForIds(List<Long> ids) throws Exception;
	public WorkflowUserFunctionContext getFunction(Long functionId) throws Exception;
	public WorkflowUserFunctionContext getFunction(String nameSpaceName,String functionName) throws Exception;
}
