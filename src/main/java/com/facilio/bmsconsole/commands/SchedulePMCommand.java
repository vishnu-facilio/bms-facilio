package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMResourcePlannerReminderContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class SchedulePMCommand implements Command {

	
private boolean isBulkUpdate = false;
	
	public SchedulePMCommand() {}
	
	public SchedulePMCommand(boolean isBulkUpdate) {
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
			if (pm.getTriggers() != null && pm.isActive()) {
				if(pm.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
					prepareAndAddResourcePlanner(pm);
				}
				schedulePM(pm, context);
			}
		}
		return false;
	}
	private void prepareAndAddResourcePlanner(PreventiveMaintenance pm) throws Exception {
		
		
		if(pm.getResourcePlanners() != null) {
			
			for(PMResourcePlannerContext resourcePlanner :pm.getResourcePlanners()) {
				if(resourcePlanner.getTriggerName() != null) {
					PMTriggerContext trigger = pm.getTriggerMap().get(resourcePlanner.getTriggerName());
					resourcePlanner.setTriggerId(trigger.getId());
					resourcePlanner.setPmId(pm.getId());
				}
				Map<String, Object> prop = FieldUtil.getAsProperties(resourcePlanner);
				GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder();
				insert.table(ModuleFactory.getPMResourcePlannerModule().getTableName());
				insert.fields(FieldFactory.getPMResourcePlannerFields());
				insert.insert(prop);
				resourcePlanner.setId((Long)prop.get("id"));
				
				
				if(resourcePlanner.getPmResourcePlannerReminderContexts() != null) {
					for(PMResourcePlannerReminderContext pmResourcePlannerReminderContext : resourcePlanner.getPmResourcePlannerReminderContexts()) {
						
						if(pmResourcePlannerReminderContext.getReminderName() != null) {
							PMReminder reminder = pm.getReminderMap().get(pmResourcePlannerReminderContext.getReminderName());
							pmResourcePlannerReminderContext.setReminderId(reminder.getId());
							pmResourcePlannerReminderContext.setPmId(pm.getId());
							pmResourcePlannerReminderContext.setResourcePlannerId(resourcePlanner.getId());
							Map<String, Object> prop1 = FieldUtil.getAsProperties(pmResourcePlannerReminderContext);
							
							GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder();
							insert1.table(ModuleFactory.getPMResourcePlannerReminderModule().getTableName());
							insert1.fields(FieldFactory.getPMResourcePlannerReminderFields());
							insert1.insert(prop1);
							
							pmResourcePlannerReminderContext.setId((Long)prop1.get("id"));
						}
					}
				}
			}
		}
	}

	private static void schedulePM(PreventiveMaintenance pm, Context context) throws Exception {
		Map<Long, Long> nextExecutionTimes = new HashMap<>();
		
		if(pm.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
			List<PMJobsContext> pmJobs = null;
			switch (pm.getTriggerTypeEnum()) {
			case ONLY_SCHEDULE_TRIGGER:
				long endTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS + 1, true) - 1;
				pmJobs = PreventiveMaintenanceAPI.createPMJobsForMultipleResourceAndSchedule(pm, endTime,true);
				break;
			case FIXED:
			case FLOATING:
				throw new IllegalArgumentException("PM Of type Multiple cannot have this type of trigger");
			}
			if (pmJobs != null) {
				//nextExecutionTimes.put(trigger.getId(), pmJob.getNextExecutionTime());
			}
		}
		else {
			for (PMTriggerContext trigger : pm.getTriggers()) {
				if (trigger.getSchedule() != null) {
					long startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
					PMJobsContext pmJob = null;
					switch (pm.getTriggerTypeEnum()) {
					case ONLY_SCHEDULE_TRIGGER:
						long endTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS + 1, true) - 1;
						List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createPMJobs(pm, trigger, startTime, endTime);
						if (pmJobs != null && !pmJobs.isEmpty()) {
							pmJob = pmJobs.get(0);
						}
						else {
							pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, startTime);
						}
						break;
					case FIXED:
					case FLOATING:
						pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, startTime);
						break;
					}
					if (pmJob != null) {
						PreventiveMaintenanceAPI.schedulePMJob(pmJob);
						nextExecutionTimes.put(trigger.getId(), pmJob.getNextExecutionTime());
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
	}
}
