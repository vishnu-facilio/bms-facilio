package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.constants.FacilioConstants;

public class SetMissingRelInResourcePlannersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		if (context.containsKey(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)) {
			List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
			if (pms == null) {
				return false;
			}
			
			for (int i = 0; i < pms.size(); i++) {
				PreventiveMaintenance pm = pms.get(i);
				if (pm.getPmCreationType() == 2) {
					Map<Long, String> triggerMap = new HashMap<>();
					Map<Long, String> reminderMap = new HashMap<>();
					if (pm.getTriggers() != null) {
						for (int j = 0; j < pm.getTriggers().size(); j++) {
							PMTriggerContext trigger = pm.getTriggers().get(j);
							triggerMap.put(trigger.getId(), trigger.getName());
						}
					}
					
					if (pm.getReminders() != null) {
						for (int j = 0; j < pm.getReminders().size(); j++) {
							PMReminder reminder = pm.getReminders().get(j);
							reminderMap.put(reminder.getId(), reminder.getName());
						}
					}
					
					if (pm.getResourcePlanners() != null) {
						for (int k = 0; k < pm.getResourcePlanners().size(); k++) {
							PMResourcePlannerContext resourcePlan = pm.getResourcePlanners().get(k);

							if (resourcePlan.getPmResourcePlannerReminderContexts() != null) {
								for (int l = 0; l < resourcePlan.getPmResourcePlannerReminderContexts().size(); l++) {
									PMResourcePlannerReminderContext resourcePlannerRemContext = resourcePlan.getPmResourcePlannerReminderContexts().get(l);
									if (resourcePlannerRemContext.getReminderId() != null && resourcePlannerRemContext.getReminderId() > 0) {
										resourcePlannerRemContext.setReminderName(reminderMap.get(resourcePlannerRemContext.getReminderId()));
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

}
