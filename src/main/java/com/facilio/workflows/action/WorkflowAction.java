package com.facilio.workflows.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.mv.util.MVUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflows.functions.FacilioWorkflowFunctionInterface;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static org.apache.log4j.Logger log = LogManager.getLogger(WorkflowUtil.class.getName());
	
	List<FacilioSystemFunctionNameSpace> namespaces;
	int defaultWorkflowId = -1;
	List<Object> paramList;
	public int nameSpaceValue;
	List<FacilioWorkflowFunctionInterface> functions;
	
	public List<Object> getParamList() {
		return paramList;
	}

	public void setParamList(List<Object> paramList) {
		this.paramList = paramList;
	}

	public int getDefaultWorkflowId() {
		return defaultWorkflowId;
	}

	public void setDefaultWorkflowId(int defaultWorkflowId) {
		this.defaultWorkflowId = defaultWorkflowId;
	}

	public List<FacilioSystemFunctionNameSpace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<FacilioSystemFunctionNameSpace> namespaces) {
		this.namespaces = namespaces;
	}
	
	public int getNameSpaceValue() {
		return nameSpaceValue;
	}

	public void setNameSpaceValue(int nameSpaceValue) {
		this.nameSpaceValue = nameSpaceValue;
	}
	
	public List<FacilioWorkflowFunctionInterface> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FacilioWorkflowFunctionInterface> functions) {
		this.functions = functions;
	}

	public String getAllFunctions() {
		
		try {

			functions = WorkflowUtil.getFacilioFunctions(FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue).getName());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}

	public String getAllNameSpace() {
		
		try {
			namespaces = new ArrayList(FacilioSystemFunctionNameSpace.getNamespaceMap().values());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}
	
	// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  V2 APIS $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	private WorkflowNamespaceContext namespace;
	private WorkflowUserFunctionContext userFunction;
	
	public WorkflowNamespaceContext getNamespace() {
		return namespace;
	}

	public void setNamespace(WorkflowNamespaceContext namespace) {
		this.namespace = namespace;
	}

	public WorkflowUserFunctionContext getUserFunction() {
		return userFunction;
	}

	public void setUserFunction(WorkflowUserFunctionContext userFunction) {
		this.userFunction = userFunction;
	}

	public String getWorkflowXmlFromObject() throws Exception {
		setResult("workflowString", WorkflowUtil.getXmlStringFromWorkflow(workflow));
		return SUCCESS;
	}

	public String runWorkflow() throws Exception {
	    workflow = WorkflowV2API.executeWorkflow(workflow, paramList);
	    setResult(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return SUCCESS;
	}
	
	public String addWorkflow() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		Chain addWorkflowChain =  TransactionChainFactory.getAddWorkflowChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return SUCCESS;
	}
	public String updateWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		Chain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return SUCCESS;
	}
	public String deleteWorkflow() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		Chain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
		return SUCCESS;
	}
	
	public String addNameSpace() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		Chain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	public String updateNameSpace() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		Chain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowNameSpaceChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	
	public String deleteNameSpace() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		Chain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowNameSpaceChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, namespace);
		return SUCCESS;
	}
	
	public String addUserFunction() throws Exception {
		
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		Chain addWorkflowChain =  TransactionChainFactory.getAddWorkflowUserFunctionChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		return SUCCESS;
	}
	
	public String updateUserFunction() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		Chain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowUserFunctionChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		return SUCCESS;
	}
	public String deleteUserFunction() throws Exception {
		FacilioContext context = new FacilioContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		Chain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowUserFunctionChain(); 
		addWorkflowChain.execute(context);
		setResult(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, userFunction);
		return SUCCESS;
	}
	
	public String getDefaultWorkflowResultV2() throws Exception {
		if(defaultWorkflowId > -1) {
			setResult(WorkflowV2Util.WORKFLOW_CONTEXT, WorkflowV2API.getDefaultWorkflowResult(defaultWorkflowId, paramList));
		}
		return SUCCESS;
	}
}
