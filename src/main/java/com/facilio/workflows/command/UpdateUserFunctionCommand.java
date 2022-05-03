package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class UpdateUserFunctionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userFunctionContext = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		
		userFunctionContext.fillFunctionHeaderFromScript();
		
		WorkflowUserFunctionContext olduserFunction = UserFunctionAPI.getUserFunction(userFunctionContext.getId());
		
		checkIfFunctionAlreadyExist(userFunctionContext,olduserFunction);
		
		updateUserFunction(userFunctionContext);
		
		return false;
	}
	
	private void checkIfFunctionAlreadyExist(WorkflowUserFunctionContext userFunctionContext, WorkflowUserFunctionContext olduserFunction) throws Exception {

		if(!userFunctionContext.getName().equals(olduserFunction.getName())) {
			WorkflowContext function = UserFunctionAPI.getWorkflowFunction(userFunctionContext.getNameSpaceId(), userFunctionContext.getName());
			if(function != null) {
				 throw new RESTException(ErrorCode.VALIDATION_ERROR, "function name already exist in the namespace");
			}
		}
	}
	
	private int updateUserFunction(WorkflowUserFunctionContext userFunctionContext) throws Exception {
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getWorkflowUserFunctionModule().getTableName());
		update.fields(FieldFactory.getWorkflowUserFunctionFields())
		.andCondition(CriteriaAPI.getIdCondition(userFunctionContext.getId(), ModuleFactory.getWorkflowUserFunctionModule()));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(userFunctionContext);
		return update.update(prop);
	}

}
