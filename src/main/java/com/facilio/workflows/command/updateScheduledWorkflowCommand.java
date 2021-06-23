package com.facilio.workflows.command;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.google.api.client.util.DateTime;

public class updateScheduledWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext)context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
		
		updateWorkflow(scheduledWorkflowContext);
		
		FacilioTimer.deleteJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);
		
		if(scheduledWorkflowContext.getStartTime() < DateTimeUtil.getCurrenTime()) {
			scheduledWorkflowContext.setStartTime(DateTimeUtil.getCurrenTime());
		}
		
		FacilioTimer.scheduleCalendarJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME, scheduledWorkflowContext.getStartTime(), scheduledWorkflowContext.getSchedule(), "facilio");
			
		return false;
	}

	public static int updateWorkflow(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getScheduledWorkflowModule().getTableName());
		update.fields(FieldFactory.getScheduledWorkflowFields())
		.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowContext.getId(), ModuleFactory.getScheduledWorkflowModule()));
		
		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
		return update.update(prop);
	}
}
