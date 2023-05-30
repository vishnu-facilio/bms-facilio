package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminderAction;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.Level;
import org.apache.logging.log4j.util.Strings;

@Log4j
public class DeletePMAndDependenciesCommand extends FacilioCommand {
	private boolean isPMDelete;
	private boolean isStatusUpdate = false;
	public DeletePMAndDependenciesCommand(boolean isDelete, boolean... isStatusUpdate) {
		this.isPMDelete = isDelete;
		if (isStatusUpdate != null && isStatusUpdate.length > 0) {
			this.isStatusUpdate = isStatusUpdate[0];
		}
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		LOGGER.log(Level.WARN, "DeletePMAndDependenciesCommand");
		List<Long> ruleIds = new ArrayList<>();
		List<Long> pmIds = new ArrayList<>();
		List<Long> triggerPMIds = new ArrayList<>();
		
		PreventiveMaintenance newPm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		boolean deleteOnStatusUpdate = isStatusUpdate && newPm != null && newPm.isActive();
		
		List<PreventiveMaintenance> oldPms = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST));
		List<Long> actionIds = new ArrayList<>();
		
		if (oldPms != null) {
			for(PreventiveMaintenance oldPm: oldPms) {
				pmIds.add(oldPm.getId());

				if(oldPm.hasTriggers() && oldPm.getTriggers() != null && (isPMDelete || newPm.getTriggers() != null || deleteOnStatusUpdate)) {
					List<Long> triggerIds = new ArrayList<>();
					oldPm.getTriggers().forEach(trigger -> {
						if(trigger.getRuleId() != -1) {
							ruleIds.add(trigger.getRuleId());
						}
						triggerIds.add(trigger.getId());
					});


					triggerPMIds.add(oldPm.getId());
					
					List<PMReminder> reminders = oldPm.getReminders();
					if (reminders != null) {
						for(PMReminder reminder :reminders) {
							for(PMReminderAction reminderAction : reminder.getReminderActions()) {
								actionIds.add(reminderAction.getActionId());
							}
						}
					}
					
				}
			}
		}

		if (!ruleIds.isEmpty()) {
			LOGGER.log(Level.WARN, "Rule IDs isn't empty: " + Strings.join(ruleIds, ','));
			WorkflowRuleAPI.deleteWorkFlowRules(ruleIds);
		}

		// Deleting trigger happens there
		PreventiveMaintenanceAPI.deletePmResourcePlanner(pmIds);
		PreventiveMaintenanceAPI.deletePmIncludeExclude(pmIds);
		PreventiveMaintenanceAPI.deleteTriggers(triggerPMIds);
		ActionAPI.deleteActions(actionIds);
		PreventiveMaintenanceAPI.deletePMReminders(pmIds);
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		PreventiveMaintenanceAPI.deleteMultiWoPMReminders(pmIds);

//		if(isPMDelete || deleteOnStatusUpdate || (newPm != null && newPm.getId() != -1 && newPm.getReminders() != null)) {
//			
//			List<PMReminder> reminders = PreventiveMaintenanceAPI.getPMReminders(pmIds);
//			if (reminders != null) {
//				List<Long> actionIds = new ArrayList<>();
//				for(PMReminder reminder :reminders) {
//					for( PMReminderAction reminderAction : reminder.getReminderActions()) {
//						actionIds.add(reminderAction.getActionId());
//					}
//					reminder.setReminderActions(null);			// temp fix, should handle differently for custom template case
//				}
//				ActionAPI.deleteActions(actionIds);
//				deletePMReminders(pmIds);
//			}
//		}
		
		if(isPMDelete) {
			int count = PreventiveMaintenanceAPI.markAsDelete(recordIds);
			context.put(FacilioConstants.ContextNames.ROWS_UPDATED, count);
		}
		
		return false;
	}

}
