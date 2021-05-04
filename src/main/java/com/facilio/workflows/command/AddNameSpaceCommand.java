package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddNameSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
		checkWhetherNameSpaceAlreadyExist(workflowNamespaceContext.getName());
		
		workflowNamespaceContext.setSysCreatedBy(AccountUtil.getCurrentUser().getOuid());
		workflowNamespaceContext.setSysModifiedBy(AccountUtil.getCurrentUser().getOuid());
		workflowNamespaceContext.setSysCreatedTime(DateTimeUtil.getCurrenTime());
		workflowNamespaceContext.setSysModifiedTime(DateTimeUtil.getCurrenTime());
		
		addNameSpace(workflowNamespaceContext);
		return false;
	}
	
	private void addNameSpace(WorkflowNamespaceContext namespaceContext) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.fields(FieldFactory.getWorkflowNamespaceFields());

		Map<String, Object> props = FieldUtil.getAsProperties(namespaceContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		namespaceContext.setId((long)props.get("id"));
	}

	private void checkWhetherNameSpaceAlreadyExist(String name) throws Exception {
		if(UserFunctionAPI.getNameSpace(name) != null) {
			throw new Exception("Namespace name already exist");
		}

	}
}