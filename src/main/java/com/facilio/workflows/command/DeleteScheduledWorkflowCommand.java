package com.facilio.workflows.command;

import java.util.Map;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class DeleteScheduledWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext)context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);
		ScheduledWorkflowContext scheduledWorkflow = getScheduler(scheduledWorkflowContext.getId());
		context.put(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT,scheduledWorkflow);
		
		FacilioTimer.deleteJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);

		scheduledWorkflowContext.setDeleted(Boolean.TRUE);
		
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getScheduledWorkflowModule().getTableName());
		update.fields(FieldFactory.getScheduledWorkflowFields())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowContext.getId(), ModuleFactory.getScheduledWorkflowModule()));

		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
		update.update(prop);
		
		return false;
	}

	public static ScheduledWorkflowContext getScheduler(long id) throws Exception{
		if (id <= 0){
			return null;
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getScheduledWorkflowModule().getTableName())
				.select(FieldFactory.getScheduledWorkflowFields())
				.andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getScheduledWorkflowModule()));

		ScheduledWorkflowContext scheduledWorkflow = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), ScheduledWorkflowContext.class);
		return scheduledWorkflow;
	}
}
