package com.facilio.bundle.context;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2API;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class FunctionBundleComponent implements BundleComponentInterface {

	@Override
	public JSONObject getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext fucntion = (WorkflowUserFunctionContext) context.get(BundleConstants.COMPONENT_OBJECT);
		return FieldUtil.getAsJSON(fucntion);
	}

	@Override
	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		List<WorkflowUserFunctionContext> functions = WorkflowV2API.getAllFunctions();
		
		JSONArray returnList = new JSONArray();
		
		for(WorkflowUserFunctionContext function : functions) {
			context.put(BundleConstants.COMPONENT_OBJECT,function);
			JSONObject formattedObject = getFormatedObject(context);
			
			returnList.add(formattedObject);
		}
		
		return returnList;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		JSONObject workflowJSON = (JSONObject) context.get(BundleConstants.COMPONENT_OBJECT);
		
		WorkflowUserFunctionContext workflow = FieldUtil.getAsBeanFromJson(workflowJSON, WorkflowUserFunctionContext.class);
		
		WorkflowNamespaceContext nameSpace = UserFunctionAPI.getNameSpace(workflow.getNameSpaceName());
		
		if(nameSpace == null) {
			nameSpace = new WorkflowNamespaceContext();
			nameSpace.setName(workflow.getNameSpaceName());
			FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowNameSpaceChain();
			FacilioContext newContext = addWorkflowChain.getContext();
			
			newContext.put(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT, nameSpace);
			addWorkflowChain.execute();
			
		}
		
		workflow.setNameSpaceId(nameSpace.getId());
		
		FacilioChain addWorkflowChain =  TransactionChainFactory.getAddWorkflowUserFunctionChain();
		FacilioContext addContext = addWorkflowChain.getContext();
		
		addContext.put(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT, workflow);
		addWorkflowChain.execute();
		
	}

	@Override
	public void postInstall(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
