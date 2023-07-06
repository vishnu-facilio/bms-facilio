package com.facilio.workflows.command;

import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowRelUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowContext workflow = (WorkflowContext) context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if(workflow == null) {
			workflow = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
			workflow.setType(WorkflowContext.WorkflowType.USER_DEFINED.getValue());
		}
		
		
		workflow.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
		workflow.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
		workflow.setSysCreatedTime(DateTimeUtil.getCurrenTime());
		workflow.setSysModifiedTime(DateTimeUtil.getCurrenTime());
		
		workflow.setOrgId(AccountUtil.getCurrentOrg().getOrgId());

		WorkflowUtil.scriptSyntaxValidation(workflow);
		workflow.validateScript();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowModule().getTableName())
				.fields(FieldFactory.getWorkflowFields());

		Map<String, Object> props = FieldUtil.getAsProperties(workflow);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		workflow.setId((Long) props.get("id"));
		WorkflowRelUtil.addWorkflowRelations(workflow);
		return false;
	}


}