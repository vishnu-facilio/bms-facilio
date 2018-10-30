package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScheduledActionContext;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;

public class AddScheduledActionCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		ScheduledActionContext scheduledAction = (ScheduledActionContext) context.get(FacilioConstants.ContextNames.SCHEDULE_INFO);
		List<ActionContext> actions = (List<ActionContext>) context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		scheduledAction.setActionId(actions.get(0).getId());
		scheduledAction.setOrgId(AccountUtil.getCurrentOrg().getId());
		
		FacilioModule module = ModuleFactory.getScheduledActionModule();
		
		Map<String, Object> prop = FieldUtil.getAsProperties(scheduledAction);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getScheduledActionFields())
				.addRecord(prop);
		insertBuilder.save();
		
		scheduledAction.setId((long) prop.get("id"));
		
		FacilioTimer.scheduleCalendarJob(scheduledAction.getId(), "ScheduledActionExecution", System.currentTimeMillis(), getDateTimeSchedule(scheduledAction), "facilio");
		
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
