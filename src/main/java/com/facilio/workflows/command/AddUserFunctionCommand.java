package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bundle.enums.BundleComponentsEnum;
import com.facilio.bundle.enums.BundleModeEnum;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.DisplayNameToLinkNameUtil;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddUserFunctionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		WorkflowUserFunctionContext userFunctionContext = (WorkflowUserFunctionContext) context.get(WorkflowV2Util.WORKFLOW_USER_FUNCTION_CONTEXT);
		
		userFunctionContext.fillFunctionHeaderFromScript();
		
		if(userFunctionContext.getLinkName() == null) {
			
			String linkName = DisplayNameToLinkNameUtil.getLinkName(userFunctionContext.getName(), ModuleFactory.getWorkflowUserFunctionModule(), FieldFactory.getAsMap(FieldFactory.getWorkflowUserFunctionFields()).get("linkName"));
			
			userFunctionContext.setLinkName(linkName);
		}
		
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