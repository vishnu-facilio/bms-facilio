package com.facilio.workflows.command;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteNameSpaceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
		deleteNameSpace(workflowNamespaceContext);
		return false;
	}
	
	private void deleteNameSpace(WorkflowNamespaceContext namespaceContext) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(namespaceContext.getId(), ModuleFactory.getWorkflowNamespaceModule()));

		deleteBuilder.delete();
	}

}
