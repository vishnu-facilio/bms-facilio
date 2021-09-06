package com.facilio.workflows.command;

import org.apache.commons.chain.Context;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteNameSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
		deleteNameSpace(workflowNamespaceContext);
		
		BundleUtil.markModifiedComponent(BundleComponentsEnum.FUNCTION_NAME_SPACE, workflowNamespaceContext.getId(), workflowNamespaceContext.getName(), BundleModeEnum.DELETE);
		
		return false;
	}
	
	private void deleteNameSpace(WorkflowNamespaceContext namespaceContext) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getWorkflowNamespaceModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(namespaceContext.getId(), ModuleFactory.getWorkflowNamespaceModule()));

		deleteBuilder.delete();
	}

}
