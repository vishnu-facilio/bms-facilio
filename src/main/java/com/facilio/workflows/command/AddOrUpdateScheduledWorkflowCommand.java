package com.facilio.workflows.command;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bundle.context.InstalledBundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.workflows.context.ScheduledWorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.json.simple.JSONObject;

public class AddOrUpdateScheduledWorkflowCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledWorkflowContext scheduledWorkflowContext = (ScheduledWorkflowContext)context.get(WorkflowV2Util.SCHEDULED_WORKFLOW_CONTEXT);

//		WorkflowContext workflow = (WorkflowContext)context.get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		if (scheduledWorkflowContext.getStartTime() < DateTimeUtil.getCurrenTime()) {
			scheduledWorkflowContext.setStartTime(DateTimeUtil.getCurrenTime());
		}

//		scheduledWorkflowContext.setWorkflowId(workflow.getId());
		scheduledWorkflowContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		scheduledWorkflowContext.setTimeZone(AccountUtil.getCurrentAccount().getTimeZone());
		
		scheduledWorkflowContext.setModifiedTime(DateTimeUtil.getCurrenTime());

		Boolean isActive =  scheduledWorkflowContext.getIsActive();
		if(isActive == null){
			isActive = true;
		}
		scheduledWorkflowContext.setIsActive(isActive);

		if (scheduledWorkflowContext.getId() < 0) {
			scheduledWorkflowContext.setCreatedTime(DateTimeUtil.getCurrenTime());
			scheduledWorkflowContext.setLinkName(scheduledWorkflowContext.getName());
			addScheduledWorkflow(scheduledWorkflowContext);
		} else {
			FacilioTimer.deleteJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME);
			SchedulerAPI.deleteAllActions(scheduledWorkflowContext.getId());
			// update
			updateScheduledWorkflow(scheduledWorkflowContext);
		}

		if(isActive) {
			FacilioTimer.scheduleCalendarJob(scheduledWorkflowContext.getId(), WorkflowV2Util.SCHEDULED_WORKFLOW_JOB_NAME, scheduledWorkflowContext.getStartTime(), scheduledWorkflowContext.getSchedule(), "facilio");
		}

		if (scheduledWorkflowContext.getWorkflowContext() != null) {	// temp handling till client moves
			ActionContext executeAction = new ActionContext();
			executeAction.setActionType(ActionType.WORKFLOW_ACTION);
			JSONObject templateJson = new JSONObject();
			templateJson.put("resultWorkflowContext", FieldUtil.getAsJSON(scheduledWorkflowContext.getWorkflowContext()));
			executeAction.setTemplateJson(templateJson);

			List<ActionContext> actions = new ArrayList<>();
			actions.add(executeAction);
			scheduledWorkflowContext.setActions(actions);
		}
		SchedulerAPI.addActions(scheduledWorkflowContext, true);
		
		return false;
	}

	private void updateScheduledWorkflow(ScheduledWorkflowContext scheduledWorkflowContext) throws Exception {
		GenericUpdateRecordBuilder update = new GenericUpdateRecordBuilder();
		update.table(ModuleFactory.getScheduledWorkflowModule().getTableName());
		update.fields(FieldFactory.getScheduledWorkflowFields())
				.andCondition(CriteriaAPI.getIdCondition(scheduledWorkflowContext.getId(), ModuleFactory.getScheduledWorkflowModule()));

		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledWorkflowContext);
		update.update(prop);
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
