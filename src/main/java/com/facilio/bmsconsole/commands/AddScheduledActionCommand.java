package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.FacilioTimer;
import com.facilio.taskengine.ScheduleInfo;

public class AddScheduledActionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ScheduledActionContext scheduledAction = (ScheduledActionContext) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		scheduledAction.setActionId(actions.get(0).getId());
		scheduledAction.setOrgId(AccountUtil.getCurrentOrg().getId());
		if (scheduledAction.getScheduleInfo() != null) {
			scheduledAction.setFrequency(scheduledAction.getScheduleInfo().getFrequencyType());
			if (scheduledAction.getScheduleInfo().getTimeObjects() != null) {
				scheduledAction.setTime(scheduledAction.getScheduleInfo().getTimeObjects().get(0));
			}
		}
		
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		
		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledAction);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getScheduledActionFields())
				.addRecord(prop);
		insertBuilder.save();
		
		scheduledAction.setId((long) prop.get("id"));
		
		ScheduleInfo info = scheduledAction.getScheduleInfo() != null ? scheduledAction.getScheduleInfo() :  getDateTimeSchedule(scheduledAction);
		FacilioTimer.scheduleCalendarJob(scheduledAction.getId(), FacilioConstants.Job.DIGEST_JOB_NAME, System.currentTimeMillis(), info, "facilio");
		
		return false;
	}
	
	private static ScheduleInfo getDateTimeSchedule(ScheduledActionContext scheduledAction) {
		ScheduleInfo info = new ScheduleInfo();
		info.setFrequencyType(scheduledAction.getFrequency());
		
		if (scheduledAction.getTimeObj() != null) {
			info.addTime(scheduledAction.getTimeObj());
		}
		return info;
	}

}
