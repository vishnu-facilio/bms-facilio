package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminderAction;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.Logger;

public class AddPMReminderCommand extends FacilioCommand {
	private static final Logger LOGGER = Logger.getLogger(AddPMReminderCommand.class.getName());
	private boolean isBulkUpdate = false;
	
	public AddPMReminderCommand() {}
	
	public AddPMReminderCommand(boolean isBulkUpdate) {
		this.isBulkUpdate = isBulkUpdate;
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
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
							List<PMReminderAction> reminderActions = reminder.getReminderActions();
							if(reminderActions == null || reminderActions.isEmpty()) {
								reminderActions = reminderActions == null ? new ArrayList<>() : reminderActions;
								
								ActionContext action = new ActionContext();
								action.setActionType(ActionType.BULK_EMAIL_NOTIFICATION);
								
								switch(reminder.getTypeEnum()) {
									case BEFORE_EXECUTION: action.setDefaultTemplateId(10);break;
									case AFTER_EXECUTION: action.setDefaultTemplateId(11);break;
									case BEFORE_DUE: action.setDefaultTemplateId(11);break;
									case AFTER_DUE: action.setDefaultTemplateId(57);break;
								}
								PMReminderAction reminderAction = new PMReminderAction();
								reminderAction.setAction(action);
								reminderActions.add(reminderAction);
								actions.add(action);
								reminder.setReminderActions(reminderActions);
							}
							else {
								for(PMReminderAction reminderAction :reminderActions) {
									if(reminderAction.getAction() == null
											|| (reminderAction.getAction().getTemplate() == null
											&& reminderAction.getAction().getTemplateJson() == null
											&& reminderAction.getAction().getTemplateId() == -1L)) {
										throw new IllegalArgumentException("Reminder Action or Template cannot be null");
									}
									reminderAction.getAction().setId(-1L);
									long templateId;
									if (reminderAction.getAction().getTemplate() != null) {
										templateId = TemplateAPI.addTemplate(reminderAction.getAction().getTemplate());
									} else {
										ActionAPI.addActions(Collections.singletonList(reminderAction.getAction()), null, false);
										templateId = reminderAction.getAction().getTemplateId();
									}
									reminderAction.getAction().setTemplateId(templateId);
									actions.add(reminderAction.getAction());
								}
							}
						}
					}
					ActionAPI.addActions(actions);
					
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
																	.fields(FieldFactory.getPMReminderFields())
																	.table(ModuleFactory.getPMReminderModule().getTableName());
					List<Map<String, Object>> reminderProps = new ArrayList<>();
					for(PMReminder reminder : reminders) {
						reminder.setOrgId(AccountUtil.getCurrentOrg().getId());
						Map<String, Object> reminderProp = FieldUtil.getAsProperties(reminder);
						insertBuilder.addRecord(reminderProp);
						reminderProps.add(reminderProp);
					}
					insertBuilder.save();
					
					insertBuilder = new GenericInsertRecordBuilder()
							.fields(FieldFactory.getPMReminderActionFields())
							.table(ModuleFactory.getPMReminderActionModule().getTableName());
					
					Map<String,PMReminder> reminderMap = new HashMap<>();
					for(int i=0 ;i<reminderProps.size() ;i++) {
						Long id = (Long) reminderProps.get(i).get("id");
						reminders.get(i).setId(id);
						
						reminderMap.put(reminders.get(i).getName(), reminders.get(i));
						
						for(PMReminderAction reminderAction :reminders.get(i).getReminderActions()) {
							reminderAction.setReminderId(id);
							reminderAction.setOrgId(AccountUtil.getCurrentOrg().getId());
							reminderAction.setActionId(reminderAction.getAction().getId());
							Map<String, Object> reminderActionProp = FieldUtil.getAsProperties(reminderAction);
							insertBuilder.addRecord(reminderActionProp);
						}
					}
					insertBuilder.save();
					
					pm.setReminderMap(reminderMap);
				}
			}
		}
		return false;
	}
	
}
