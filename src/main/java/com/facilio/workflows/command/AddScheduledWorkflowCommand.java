package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class AddScheduledWorkflowCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext)context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
		
		WorkflowContext workflow = (WorkflowContext)context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if (scheduledWorkflowContext.getStartTime() < DateTimeUtil.getCurrenTime()) {
			scheduledWorkflowContext.setStartTime(DateTimeUtil.getCurrenTime());
		}
		
		scheduledWorkflowContext.setWorkflowId(workflow.getId());
		scheduledWorkflowContext.setIsActive(true);
		scheduledWorkflowContext.setOrgid(AccountUtil.getCurrentOrg().getId());
		scheduledWorkflowContext.setTimeZone(AccountUtil.getCurrentAccount().getTimeZone());
		
		addScheduledWorkflow(scheduledWorkflowContext);
		
		FacilioTimer.scheduleCalendarJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME, scheduledWorkflowContext.getStartTime(), scheduledWorkflowContext.getSchedule(), "facilio");
		
		return false;
	}

	
	private void addScheduledWorkflow(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
		
		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.fields(FieldFactory.getScheduledWorkflowFields())
				.addRecord(prop);
				
		insertRecordBuilder.save();
		
		if(prop.containsKey("id")) {
			scheduledWorkflowContext.setId((Long)prop.get("id"));
		}
	}
}
