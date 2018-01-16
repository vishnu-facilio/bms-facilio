package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPMReminderCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PMReminder> reminders = (List<PMReminder>) context.get(FacilioConstants.ContextNames.PM_REMINDERS);
		if(reminders != null && !reminders.isEmpty()) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			if(pm != null && pm.getId() != -1) {
				List<ActionContext> actions = new ArrayList<>();
				for(PMReminder reminder : reminders) {
					if(reminder.getDuration() != -1) {
						reminder.setPmId(pm.getId());
						ActionContext action = reminder.getAction();
						if(action == null) {
							action = new ActionContext();
							action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
							
							switch(reminder.getTypeEnum()) {
								case BEFORE: action.setDefaultTemplateId(10);break;
								case AFTER: action.setDefaultTemplateId(11);break;
							}
							reminder.setAction(action);
						}
						actions.add(action);
					}
				}
				ActionAPI.addActions(actions);
				
				GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																.fields(FieldFactory.getPMReminderFields())
																.table(ModuleFactory.getPMReminderModule().getTableName());
				List<Map<String, Object>> reminderProps = new ArrayList<>();
				for(PMReminder reminder : reminders) {
					reminder.setOrgId(AccountUtil.getCurrentOrg().getId());
					reminder.setActionId(reminder.getAction().getId());
					Map<String, Object> reminderProp = FieldUtil.getAsProperties(reminder);
					insertBuilder.addRecord(reminderProp);
					reminderProps.add(reminderProp);
				}
				insertBuilder.save();
				
				Map<Long, Long> nextExecutionTimes = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES);
				for(int i=0; i<reminders.size(); i++) {
					long schedulerId = (long) reminderProps.get(i).get("id");
					PMReminder reminder = reminders.get(i);
					reminder.setId(schedulerId);
					
					if(reminder.getTypeEnum() == ReminderType.BEFORE) {
						for(PMTriggerContext trigger : pm.getTriggers()) {
							Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
							if(nextExecutionTime != null) {
								CommonCommandUtil.scheduleBeforePMReminder(reminder, nextExecutionTime, trigger.getId());
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	private static long getStartTimeInSecond(long startTime) {
		long startTimeInSecond = startTime/1000;
		startTimeInSecond = startTimeInSecond - 300; //for calculating next execution time
		
		return startTimeInSecond;
	}
}
