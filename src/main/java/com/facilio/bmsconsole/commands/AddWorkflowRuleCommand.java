package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.WorkflowAPI;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddWorkflowRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		WorkflowRuleContext rule = (WorkflowRuleContext) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE);
		
		rule.setStatus(true);
		rule.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		Map<String, Object> workflowEventProps = new HashMap<String, Object>();
		workflowEventProps.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		workflowEventProps.put("moduleId", rule.getModuleId());
		workflowEventProps.put("activityType", rule.getActivityType());
		GenericInsertRecordBuilder insertBuilder1 = new GenericInsertRecordBuilder()
													.table("Workflow_Event")
													.fields(FieldFactory.getWorkflowEventFields())
													.addRecord(workflowEventProps);
		insertBuilder1.save();
		long workflowEventId = (long) workflowEventProps.get("id");
		
		rule.setEventId(workflowEventId);
		
		long ruleId = WorkflowAPI.addWorkflowRule(rule);
		rule.setId(ruleId);
		return false;
	}
}
