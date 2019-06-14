package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class UpdateNameSpaceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
		updateNameSpace(workflowNamespaceContext);
		return false;
	}
	
	private int updateNameSpace(WorkflowNamespaceContext namespaceContext) throws Exception {
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getWorkflowNamespaceModule().getTableName());
		update.fields(FieldFactory.getWorkflowNamespaceFields())
		.andCondition(CriteriaAPI.getIdCondition(namespaceContext.getId(), ModuleFactory.getWorkflowNamespaceModule()));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(namespaceContext);
		return update.update(prop);
	}
}
