package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.tasker.executor.ScheduleInfo;

public class GetUpcomingPreventiveMaintenanceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs();
		Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
		if(pms != null && !pms.isEmpty()) 
		{
			long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);
			
			List<Pair<PMJobsContext, PreventiveMaintenance>> pmPairs = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) 
			{
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				pm.setTriggers(pmTrigggers);
				for (PMTriggerContext trigger : pmTrigggers) {
					if(trigger.getSchedule() != null) {
						if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
							if(trigger.getStartTime() > startTime && trigger.getStartTime() <= endTime) {
								PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger, startTime);
								Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
								pmPairs.add(pair);
							}
						}
						else {
							long virtualJobsStartTime = -1;
							switch(pm.getTriggerTypeEnum()) {
								case ONLY_SCHEDULE_TRIGGER: 
									List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getNextPMJobs(trigger, startTime, endTime);
									if(pmJobs != null && !pmJobs.isEmpty()) {
										for(PMJobsContext pmJob : pmJobs) {
											Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
											pmPairs.add(pair);
										}
										virtualJobsStartTime = pmJobs.get(pmJobs.size() - 1).getNextExecutionTime();
									}
									else {
										virtualJobsStartTime = startTime;
									}
									break;
								case FIXED:
								case FLOATING:
									PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger, startTime);
									if(pmJob.getNextExecutionTime() > endTime) {
										virtualJobsStartTime = -1;
									}
									else if(pmJob.getNextExecutionTime() > startTime) {
										Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
										pmPairs.add(pair);
										virtualJobsStartTime = pmJob.getNextExecutionTime();
									}
									else {
										virtualJobsStartTime = startTime;
									}
									break;
							}
							if(virtualJobsStartTime != -1) {
								List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createPMJobs(pm, trigger, virtualJobsStartTime, endTime, false);
								for(PMJobsContext pmJob : pmJobs) {
									Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
									pmPairs.add(pair);
								}
							}
						}
					}
				}
			}
			sortPMs(pmPairs);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pmPairs);
		}
		
		return false;
	}
	
	private void sortPMs(List<Pair<PMJobsContext, PreventiveMaintenance>> pms) {
		pms.sort(new Comparator<Pair<PMJobsContext, PreventiveMaintenance>>() {

			@Override
			public int compare(Pair<PMJobsContext, PreventiveMaintenance> o1, Pair<PMJobsContext, PreventiveMaintenance> o2) {
				if (o1.getLeft().getNextExecutionTime() < o2.getLeft().getNextExecutionTime()) {
	                return -1;
	            } else if (o2.getLeft().equals(o1.getLeft())) {
	                return 0; 
	            } else {
	                return 1;
	            }
			}
		});
	}

}
