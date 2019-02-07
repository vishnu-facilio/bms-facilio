package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;

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
			if(pm.getPmCreationTypeEnum().equals(PreventiveMaintenance.PMCreationType.MULTIPLE)) {
				Map<String,PMJobsContext> scheduledPMJobMap = (Map<String,PMJobsContext>) context.get(FacilioConstants.ContextNames.SCHEDULED_PM_JOBS_MAP);
				scheduleBeforePMRemindersForMultiplePM(pm, scheduledPMJobMap);
			}
			else {
				scheduleBeforePMReminders(pm, nextExecutionTimes);
			}
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
							PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, nextExecutionTime, trigger.getId(),-1l);
						}
					}
				}
			}
		}
	}
	
	private void scheduleBeforePMRemindersForMultiplePM(PreventiveMaintenance pm,Map<String,PMJobsContext> scheduledPMJobMap) throws Exception {
		
		if(pm.getReminders() != null && !pm.getReminders().isEmpty()) {
			Long baseSpaceId = pm.getBaseSpaceId();
			if (baseSpaceId == null || baseSpaceId < 0) {
				baseSpaceId = pm.getSiteId();
			}
			List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
			
			Map<Long, PMReminder> pmReminderMap = getPMReminderMap(pm.getReminders());
			
			Map<Long, PMResourcePlannerContext> resourcePlannerMap = pm.getResourcePlannersMap();
			
			for(Long resourceId :resourceIds) {
				
				List<PMReminder> reminders = new ArrayList<>();
				PMTriggerContext pmTriggerContext = null;
				if(resourcePlannerMap.containsKey(resourceId)) {
					
					PMResourcePlannerContext resourcePlanner = resourcePlannerMap.get(resourceId);
					
					if(resourcePlanner.getTriggerName() != null) {
						pmTriggerContext = pm.getTriggerMap().get(resourcePlanner.getTriggerName());
					}
					
					if(resourcePlanner.getPmResourcePlannerReminderContexts() != null && !resourcePlanner.getPmResourcePlannerReminderContexts().isEmpty()) {
						
						for(PMResourcePlannerReminderContext pmResourcePlannerReminderContext : resourcePlanner.getPmResourcePlannerReminderContexts()) {
							PMReminder reminder = pmReminderMap.get(pmResourcePlannerReminderContext.getReminderId());
							reminders.add(reminder);
						}
					}
					
				}
				if(reminders.isEmpty()) {
					reminders.add(pm.getReminders().get(0));
				}
				if(pmTriggerContext == null) {
					pmTriggerContext = pm.getTriggers().get(0);
				}
				
				for(PMReminder reminder :reminders) {
					if(reminder.getTypeEnum().equals(PMReminder.ReminderType.BEFORE_EXECUTION) && pmTriggerContext != null) {
						PMJobsContext pmJob = scheduledPMJobMap.get(resourceId+"-"+pmTriggerContext.getId());
						if (pmJob == null) {
							CommonCommandUtil.emailAlert("Invalid State! pmjob should not be null", "scheduledPMJobMap = " + scheduledPMJobMap.toString() + " resourceid = " + resourceId + " TriggerId = " + pmTriggerContext.getId());
						}
						PreventiveMaintenanceAPI.schedulePrePMReminder(reminder, pmJob.getNextExecutionTime(), pmTriggerContext.getId(),resourceId);
					}
				}
			}
		}
	}
	
	private Map<Long,PMReminder> getPMReminderMap(List<PMReminder> pmReminders) {
		
		Map<Long,PMReminder> pmReminderMap = new HashMap<>();
		
		for(PMReminder pmReminder :pmReminders) {
			pmReminderMap.put(pmReminder.getId(), pmReminder);
		}
		return pmReminderMap;
	}
}
