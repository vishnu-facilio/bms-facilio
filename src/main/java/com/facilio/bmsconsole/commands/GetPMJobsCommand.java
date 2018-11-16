package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.ResourceContext;
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
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class GetPMJobsCommand implements Command {

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
			
			Map<Long, List<Map<String, Object>>> pmJobsMap = PreventiveMaintenanceAPI.getPMJobsFromPMIds(new ArrayList<>(pmTriggersMap.keySet()), startTime, endTime);
			
			Map<Long, PMTriggerContext> pmTriggerMap = new HashMap<>();
			
			Map<Long,List<Map<String, Object>>> pmTriggerGroupedMap = new HashMap<>();
			List<Long> resourceIds = new ArrayList<>();
			List<Map<String, Object>> pmJobList = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) 
			{
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				if (pmTrigggers != null && !pmTrigggers.isEmpty()) {
					if(pm.getWoTemplate() != null && pm.getWoTemplate().getResourceId() != -1 && !resourceIds.contains(pm.getWoTemplate().getResourceId()))
					{
						resourceIds.add(pm.getWoTemplate().getResourceId());
					}
					//pm.setTriggers(pmTrigggers);
					for (PMTriggerContext trigger : pmTrigggers) {
						if(trigger.getSchedule() != null) {
							pmTriggerMap.put(trigger.getId(), trigger);
							if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
								if(trigger.getStartTime() > startTime && trigger.getStartTime() <= endTime) {
									// PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), startTime, false);
									Map<String, Object> pmJob = pmJobsMap.get(trigger.getId()).get(0);
									pmJobList.add(pmJob);
								}
							}
							else {
								long virtualJobsStartTime = -1;
								switch(pm.getTriggerTypeEnum()) {
									case ONLY_SCHEDULE_TRIGGER: 
										List<Map<String, Object>> pmJobs = pmJobsMap.get(trigger.getId());
										if(pmJobs != null && !pmJobs.isEmpty()) {
											for(Map<String, Object> pmJob : pmJobs) {
												
												if(pmJob.get("templateId") != null && (long) pmJob.get("templateId") != -1)
												{
													WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate((long) pmJob.get("templateId"));
													pmJob.put("template", template);
												}
												if(pmJob.get("resourceId") != null) {
													ResourceContext resource = ResourceAPI.getResource((long) pmJob.get("resourceId"));
													pmJob.put("resource", resource);
												}
												if(pm.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
													List<Map<String, Object>> jobList = pmTriggerGroupedMap.get(trigger.getId());
													if(jobList == null) {
														jobList = new ArrayList<>();
													}
													jobList.add(pmJob);
													pmTriggerGroupedMap.put(trigger.getId(), jobList);
												}
												else {
													pmJobList.add(pmJob);
												}
											}
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
										// PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), startTime, false);
										Map<String, Object> pmJob = pmJobsMap.get(trigger.getId()).get(0);
										if((long) pmJob.get("nextExecutionTime") > endTime) {
											virtualJobsStartTime = -1;
										}
										else if((long) pmJob.get("nextExecutionTime") > startTime) {
											pmJobList.add(pmJob);
											virtualJobsStartTime = (long) pmJob.get("nextExecutionTime");
										}
										else {
											virtualJobsStartTime = startTime;
										}
										break;
								}
								if(virtualJobsStartTime != -1) {
									List<Map<String, Object>> pmJobs = PreventiveMaintenanceAPI.createProjectedPMJobs(pm, trigger, virtualJobsStartTime, endTime);
									pmJobList.addAll(pmJobs);
								}
							}
						}
					}
				}
			}
//			sortPMs(pmJobList);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pmMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGER_VS_PMJOB_MAP, pmTriggerGroupedMap);
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
