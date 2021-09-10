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
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class UpdateUserFunctionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userFunctionContext = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		
		userFunctionContext.fillFunctionHeaderFromScript();
		
		updateUserFunction(userFunctionContext);
		
		return false;
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
