package com.facilio.workflows.command;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteNameSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
		workflowNamespaceContext.setDeleted(Boolean.TRUE);
		workflowNamespaceContext.setDeletedBy(AccountUtil.getCurrentUser().getId());
		workflowNamespaceContext.setDeletedTime(DateTimeUtil.getCurrenTime());
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.fields(FieldFactory.getWorkflowNamespaceFields())
				.andCondition(CriteriaAPI.getIdCondition(workflowNamespaceContext.getId(), ModuleFactory.getWorkflowNamespaceModule()));

		updateBuilder.update(FieldUtil.getAsProperties(workflowNamespaceContext));
		
		return false;
	}
	
}
