package com.facilio.workflowv2.bean;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.util.DBConf;
import com.facilio.scriptengine.context.WorkflowFunctionContext;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

import java.util.List;

public class ScriptBeanImpl implements ScriptBean {

	@Override
	public long getOrgId() {
		return DBConf.getInstance().getCurrentOrgId();
	}

	@Override
	public WorkflowNamespaceContext addNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, nameSpace);
		
		addWorkflowChain.execute();
		return nameSpace;
	}

	@Override
	public WorkflowNamespaceContext updateNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowNameSpaceChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, nameSpace);
		addWorkflowChain.execute();
		return nameSpace;
	}

	@Override
	public WorkflowNamespaceContext deleteNameSpace(WorkflowNamespaceContext nameSpace) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowNameSpaceChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, nameSpace);
		addWorkflowChain.execute();
		
		return nameSpace;
	}

	@Override
	public void deleteNameSpacesForIds(List<Long> ids) throws Exception {
		FacilioChain deleteFunctionChain = TransactionChainFactory.getBulkDeleteWorkflowNameSpaceChain();

		FacilioContext chainContext = deleteFunctionChain.getContext();
		chainContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		deleteFunctionChain.execute();
	}

	@Override
	public WorkflowNamespaceContext getNameSpace(Long nameSpaceId) throws Exception {
		// TODO Auto-generated method stub
		return UserFunctionAPI.getNameSpace(nameSpaceId);
	}

	@Override
	public WorkflowNamespaceContext getNameSpace(String nameSpaceName) throws Exception {
		// TODO Auto-generated method stub
		return UserFunctionAPI.getNameSpace(nameSpaceName);
	}

	@Override
	public FacilioContext addFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowUserFunctionChain();
		
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, function);
		addWorkflowChain.execute();
		
		return context;
	}

	@Override
	public FacilioContext updateFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getUpdateWorkflowUserFunctionChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, function);
		
		addWorkflowChain.execute();
		
		return context;
	}

	@Override
	public WorkflowUserFunctionContext deleteFunction(WorkflowUserFunctionContext function) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, function);
		
		addWorkflowChain.execute();
		
		return function;
	}
	
	@Override
	public WorkflowContext deleteFunction(WorkflowContext function) throws Exception {
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getDeleteWorkflowChain();
		FacilioContext context = addWorkflowChain.getContext();
		
		context.put(WorkflowV2Util.WORKFLOW_CONTEXT, function);
		
		addWorkflowChain.execute();
		
		return function;
	}

	@Override
	public void deleteFunctionsForIds(List<Long> ids) throws Exception {
		FacilioChain deleteFunctionChain = TransactionChainFactory.getBulkDeleteWorkFlowChain();

		FacilioContext chainContext = deleteFunctionChain.getContext();
		chainContext.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		deleteFunctionChain.execute();
	}

	@Override
	public WorkflowUserFunctionContext getFunction(Long functionId) throws Exception {
		// TODO Auto-generated method stub
		return UserFunctionAPI.getUserFunction(functionId);
	}

	@Override
	public WorkflowUserFunctionContext getFunction(String nameSpaceName, String functionName) throws Exception {
		// TODO Auto-generated method stub
		return UserFunctionAPI.getWorkflowFunction(nameSpaceName, functionName);
	}


}
