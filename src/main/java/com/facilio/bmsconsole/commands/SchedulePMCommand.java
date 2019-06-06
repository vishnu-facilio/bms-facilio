package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMResourcePlannerContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;

public class SchedulePMCommand implements Command {

	
private boolean isBulkUpdate = false;
private List<PMJobsContext> pmJobsToBeScheduled;

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
				schedulePM(pm, context);
			}
		}
		context.put(FacilioConstants.ContextNames.SCHEDULED_PM_JOBS_MAP, getSchdueledPMJobMap(pmJobsToBeScheduled));
		return false;
	}
	private void schedulePM(PreventiveMaintenance pm, Context context) throws Exception {
		Map<Long, Long> nextExecutionTimes = new HashMap<>();
		
		if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
			List<PMJobsContext> pmJobs = null;
			switch (pm.getTriggerTypeEnum()) {
				case ONLY_SCHEDULE_TRIGGER:
					long endTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS + 1, true) - 1;
					pmJobs = createPMJobsForMultipleResourceAndSchedule(pm, endTime,true);
					break;
				case FIXED:
				case FLOATING:
					throw new IllegalArgumentException("PM Of type Multiple cannot have this type of trigger");
				default:
					break;
			}
			if (pmJobs != null) {
				context.put(FacilioConstants.ContextNames.PM_JOBS, pmJobs);
			}
		}
		else {
			for (PMTriggerContext trigger : pm.getTriggers()) {
				if (trigger.getSchedule() != null) {
					long startTime;
					if (trigger.getStartTime() < System.currentTimeMillis()) {
						startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(System.currentTimeMillis());
					} else {
						startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
					}
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
	
	public List<PMJobsContext> createPMJobsForMultipleResourceAndSchedule (PreventiveMaintenance pm , long endTime, boolean addToDb) throws Exception { //Both in seconds
		
		List<PMJobsContext> pmJobs = new ArrayList<>();
		pmJobsToBeScheduled = new ArrayList<>();
		
		Long baseSpaceId = pm.getBaseSpaceId();
		if (baseSpaceId == null || baseSpaceId < 0) {
			baseSpaceId = pm.getSiteId();
		}
		List<Long> resourceIds = PreventiveMaintenanceAPI.getMultipleResourceToBeAddedFromPM(pm.getAssignmentTypeEnum(),baseSpaceId,pm.getSpaceCategoryId(),pm.getAssetCategoryId(),null,pm.getPmIncludeExcludeResourceContexts());
		
		Map<Long, PMResourcePlannerContext> pmResourcePlanner = PreventiveMaintenanceAPI.getPMResourcesPlanner(pm.getId());
		for(Long resourceId :resourceIds) {
			List<PMTriggerContext> triggers  = null;
			if(pmResourcePlanner.get(resourceId) != null) {
				PMResourcePlannerContext currentResourcePlanner = pmResourcePlanner.get(resourceId);
				triggers = new ArrayList<>();
				for (PMTriggerContext trig: currentResourcePlanner.getTriggerContexts()) {
					if (pm.getTriggerMap() != null && pm.getTriggerMap().get(trig.getName()) != null) {
						triggers.add(pm.getTriggerMap().get(trig.getName()));
					}
				}
			}
			if(triggers == null) {
				triggers = new ArrayList<>();
				if (PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()) != null) {
					triggers.add(PreventiveMaintenanceAPI.getDefaultTrigger(pm.getTriggers()));
				}
			}

			if (triggers != null) {
				for (PMTriggerContext trigger : triggers) {
					long startTime;
					if (trigger.getStartTime() < System.currentTimeMillis()) {
						startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(System.currentTimeMillis());
					} else {
						startTime = PreventiveMaintenanceAPI.getStartTimeInSecond(trigger.getStartTime());
					}
					long nextExecutionTime = trigger.getSchedule().nextExecutionTime(startTime);

					boolean isFirst = true;
					while(nextExecutionTime <= endTime && (pm.getEndTime() == -1 || nextExecutionTime <= pm.getEndTime())) {
						PMJobsContext pmJob = PreventiveMaintenanceAPI.getpmJob(pm, trigger, resourceId, nextExecutionTime, addToDb);
						pmJobs.add(pmJob);
						if(isFirst) {
							pmJobsToBeScheduled.add(pmJob);
						}
						nextExecutionTime = trigger.getSchedule().nextExecutionTime(nextExecutionTime);
						isFirst = false;
					}
				}
			}
		}
		
		if(addToDb) {
			PreventiveMaintenanceAPI.addPMJobs(pmJobs);
		}
		PreventiveMaintenanceAPI.schedulePMJob(pmJobsToBeScheduled);
		return pmJobs;
	}
	
	public Map<String,PMJobsContext> getSchdueledPMJobMap(List<PMJobsContext> pmJobsToBeScheduledList) {
		
		Map<String,PMJobsContext> scheduledPmJobs = new HashMap<>();
		if(pmJobsToBeScheduledList != null) {
			for(PMJobsContext pmJob :pmJobsToBeScheduledList) {
				scheduledPmJobs.put(pmJob.getResourceId()+"-"+pmJob.getPmTriggerId(), pmJob);
			}
		}
		return scheduledPmJobs;
	}
}
