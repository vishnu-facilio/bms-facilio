package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddPMReminderCommand implements Command {
	
	private boolean isBulkUpdate = false;
	
	public AddPMReminderCommand() {}
	
	public AddPMReminderCommand(boolean isBulkUpdate) {
		this.isBulkUpdate = isBulkUpdate;
	}

	@Override
	public boolean execute(Context context) throws Exception {
		List<PreventiveMaintenance> pms;
		if (isBulkUpdate) {
			pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		}
		else {
			pms = Collections.singletonList((PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE));
		}
		
		if (pms == null) {
			return false;
		}
		
		for(PreventiveMaintenance pm: pms) {
			List<PMReminder> reminders = pm.getReminders();
			if(reminders != null && !reminders.isEmpty()) {
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
									case BEFORE_EXECUTION: action.setDefaultTemplateId(10);break;
									case AFTER_EXECUTION: action.setDefaultTemplateId(11);break;
									case BEFORE_DUE: action.setDefaultTemplateId(11);break;
									case AFTER_DUE: action.setDefaultTemplateId(57);break;
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
					
					Map<String,PMReminder> reminderMap = new HashMap<>();
					for(int i=0 ;i<reminderProps.size() ;i++) {
						Long id = (Long) reminderProps.get(i).get("id");
						reminders.get(i).setId(id);
						reminderMap.put(reminders.get(i).getName(), reminders.get(i));
					}
					pm.setReminderMap(reminderMap);
				}
			}
		}
		return false;
	}
	
}
