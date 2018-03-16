package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.DefaultTemplates;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
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
								case BEFORE_EXECUTION: action.setDefaultTemplateId(DefaultTemplates.PM_EMAIL_PRE_REMINDER.getVal());break;
								case AFTER_EXECUTION: action.setDefaultTemplateId(DefaultTemplates.PM_EMAIL_DUE_REMINDER.getVal());break;
								case BEFORE_DUE: action.setDefaultTemplateId(DefaultTemplates.PM_EMAIL_DUE_REMINDER.getVal());break;
								case AFTER_DUE: action.setDefaultTemplateId(DefaultTemplates.PM_EMAIL_OVERDUE_REMINDER.getVal());break;
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
				scheduleBeforePMReminders(pm, reminders, reminderProps, nextExecutionTimes);
			}
		}
		return false;
	}
	
	private void scheduleBeforePMReminders(PreventiveMaintenance pm, List<PMReminder> reminders, List<Map<String, Object>> reminderProps, Map<Long, Long> nextExecutionTimes) throws Exception {
		if (pm.getTriggers() != null && !pm.getTriggers().isEmpty()) {
			for(int i=0; i<reminders.size(); i++) {
				long schedulerId = (long) reminderProps.get(i).get("id");
				PMReminder reminder = reminders.get(i);
				reminder.setId(schedulerId);
				
				if(reminder.getTypeEnum() == ReminderType.BEFORE_EXECUTION) {
					for(PMTriggerContext trigger : pm.getTriggers()) {
						Long nextExecutionTime = nextExecutionTimes.get(trigger.getId());
						if(nextExecutionTime != null) {
							PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId());
						}
					}
				}
			}
		}
	}
}
