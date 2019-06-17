package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddUserFunctionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userFunctionContext = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		
		userFunctionContext.fillFunctionHeaderFromScript();
		
		addUserFunction(userFunctionContext);
		return false;
	}

	private void addUserFunction(WorkflowUserFunctionContext userFunctionContext) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowUserFunctionModule().getTableName())
				.fields(FieldFactory.getWorkflowUserFunctionFields());

		Map<String, Object> props = FieldUtil.getAsProperties(userFunctionContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
	}

}