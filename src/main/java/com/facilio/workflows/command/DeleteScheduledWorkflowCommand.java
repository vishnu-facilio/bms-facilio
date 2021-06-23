package com.facilio.workflows.command;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteScheduledWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext)context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
		
		FacilioTimer.deleteJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);

		SchedulerAPI.deleteAllActions(scheduledWorkflowContext.getId());
		deleteScheduledWorkflows(scheduledWorkflowContext);
		
		return false;
	}
	
	private void deleteScheduledWorkflows(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
		FacilioModule module = ModuleFactory.getScheduledWorkflowModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowContext.getId(), module));
		
		deleteBuilder.delete();
	}

}
