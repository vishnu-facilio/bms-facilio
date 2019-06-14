package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddNameSpaceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowNamespaceContext workflowNamespaceContext = (WorkflowNamespaceContext) context.get(WorkflowV2Util.WORKFLOW_NAMESPACE_CONTEXT);
		
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


}