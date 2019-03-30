package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetUpcomingPreventiveMaintenanceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		FacilioField triggerField = FieldFactory.getAsMap(fields).get("triggerType");
		Condition triggerCondition = CriteriaAPI.getCondition(triggerField, String.valueOf(TriggerType.NONE.getVal()), NumberOperators.NOT_EQUALS);
		if(filterCriteria == null) {
			filterCriteria = new Criteria();
		}
		filterCriteria.addAndCondition(triggerCondition);
		Criteria scopeCriteria = AccountUtil.getCurrentUser().scopeCriteria("planned");
		if(scopeCriteria != null) {
			filterCriteria.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = AccountUtil.getCurrentUser().getRole().permissionCriteria("planned","read");
		if(permissionCriteria != null) {
			filterCriteria.andCriteria(permissionCriteria);
		}
		
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs(filterCriteria);
		if(pms != null && !pms.isEmpty()) 
		{
			Map<Long, PreventiveMaintenance> pmMap = pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);
			
			Map<Long, PMTriggerContext> pmTriggerMap = new HashMap<>();
			List<Long> resourceIds = new ArrayList<>();
			List<PMJobsContext> pmJobList = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) 
			{
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				if(pm.getWoTemplate() != null && pm.getWoTemplate().getResourceId() != -1 && !resourceIds.contains(pm.getWoTemplate().getResourceId()))
				{
					resourceIds.add(pm.getWoTemplate().getResourceId());
				}
				//pm.setTriggerContexts(pmTrigggers);
				for (PMTriggerContext trigger : pmTrigggers) {
					if(trigger.getSchedule() != null) {
						pmTriggerMap.put(trigger.getId(), trigger);
						if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
							if(trigger.getStartTime() > startTime && trigger.getStartTime() <= endTime) {
								PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), startTime, false);
								new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
								pmJobList.add(pmJob);
							}
						}
						else {
							long virtualJobsStartTime = -1;
							switch(pm.getTriggerTypeEnum()) {
								case ONLY_SCHEDULE_TRIGGER: 
									List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getNextPMJobs(trigger, startTime, endTime);
									if(pmJobs != null && !pmJobs.isEmpty()) {
										for(PMJobsContext pmJob : pmJobs) {
											if(pmJob.getTemplateId() != -1)
											{
												WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(pmJob.getTemplateId());
												pmJob.setTemplate(template);
											}
											pmJobList.add(pmJob);
										}
										// virtualJobsStartTime = pmJobs.get(pmJobs.size() - 1).getNextExecutionTime();
									}
									long plannedEndTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS+1, true) - 1;
									if(startTime > plannedEndTime) {
										virtualJobsStartTime = startTime;
									}
									else if(endTime > plannedEndTime) {
										virtualJobsStartTime = plannedEndTime+1;
									}
									break;
								case FIXED:
								case FLOATING:
									PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), startTime, false);
									if(pmJob.getNextExecutionTime() > endTime) {
										virtualJobsStartTime = -1;
									}
									else if(pmJob.getNextExecutionTime() > startTime) {
										pmJobList.add(pmJob);
										virtualJobsStartTime = pmJob.getNextExecutionTime();
									}
									else {
										virtualJobsStartTime = startTime;
									}
									break;
							}
							if(virtualJobsStartTime != -1) {
								List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createPMJobs(pm, trigger, virtualJobsStartTime, endTime, false);
								pmJobList.addAll(pmJobs);
							}
						}
					}
				}
			}
//			sortPMs(pmJobList);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pmMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST, pmJobList);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST, pmTriggerMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES, ResourceAPI.getResourceAsMapFromIds(resourceIds));
		}
		
		return false;
	}
	
	private void sortPMs(List<PMJobsContext> pmJobs) {
		pmJobs.sort(Comparator.comparing(PMJobsContext::getNextExecutionTime, (s1, s2) -> {
			return Long.compare(s1, s2);
		}));
	}

}
