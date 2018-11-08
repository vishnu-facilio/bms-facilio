package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class scheduleBeforePMRemindersCommand implements Command {
	
	private boolean isBulkUpdate = false;
	
	public scheduleBeforePMRemindersCommand() {}
	
	public scheduleBeforePMRemindersCommand(boolean isBulkUpdate) {
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
			Map<Long, Long> nextExecutionTimes = (Map<Long, Long>) context.get(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES);
			scheduleBeforePMReminders(pm, nextExecutionTimes);
		}
		return false;
	}
	
	private void scheduleBeforePMReminders(PreventiveMaintenance pm, Map<Long, Long> nextExecutionTimes) throws Exception {
		if (pm.getTriggers() != null && !pm.getTriggers().isEmpty() && pm.getReminders() != null && !pm.getReminders().isEmpty()) {
			for(int i=0; i<pm.getReminders().size(); i++) {
				PMReminder reminder = pm.getReminders().get(i);
				long schedulerId = reminder.getId();
				
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
