package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GetPMJobsCommand implements Command {

	private static final Logger LOGGER = Logger.getLogger(GetPMJobsCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
		String currentCalendarView = (String) context.get(FacilioConstants.ContextNames.CURRENT_CALENDAR_VIEW);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		FacilioField triggerField = FieldFactory.getAsMap(fields).get("triggerType");

		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("pmjobs");

		long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);

		Map<Long, List<Map<String, Object>>> pmJobsMap = PreventiveMaintenanceAPI.getPMJobs(startTime, endTime, scopeCriteria);

		List<PreventiveMaintenance> pms = null;
		Condition triggerCondition = CriteriaAPI.getCondition(triggerField, String.valueOf(TriggerType.NONE.getVal()), NumberOperators.NOT_EQUALS);
		if(filterCriteria == null) {
			filterCriteria = new Criteria();
		}
		filterCriteria.addAndCondition(triggerCondition);
		Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("planned","read");
		if(permissionCriteria != null) {
			filterCriteria.andCriteria(permissionCriteria);
		}
		if (pmJobsMap != null) {
			Collection<List<Map<String, Object>>> pmJobs = pmJobsMap.values();
			List<Long> pmIds = new ArrayList<>();
			for (List<Map<String, Object>> props: pmJobs) {
				for (Map<String, Object> prop: props) {
					pmIds.add((Long) prop.get("pmId"));
				}
			}
			FacilioModule pmModule = ModuleFactory.getPreventiveMaintenanceModule();
			Criteria pmIdCriteria = new Criteria();
			pmIdCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(pmModule), pmIds, NumberOperators.EQUALS));
			filterCriteria.andCriteria(pmIdCriteria);
			pms = PreventiveMaintenanceAPI.getAllActivePMs(filterCriteria);
		}

		if(pms != null && !pms.isEmpty()) 
		{
			Map<Long, PreventiveMaintenance> pmMap = pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			Map<Long, PMTriggerContext> pmTriggerMap = new HashMap<>();
			
			Map<Long,Map<Long,List<Map<String, Object>>>> pmTriggerTimeBasedGroupedMap = new HashMap<>();
			List<Long> resourceIds = new ArrayList<>();
			List<Map<String, Object>> pmJobList = new ArrayList<>();
			Map<Long,WorkorderTemplate> workorderTemplateMap = new HashMap<>();
			for(PreventiveMaintenance pm : pms) 
			{
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				if (pmTrigggers != null && !pmTrigggers.isEmpty()) {
					if(pm.getWoTemplate() != null && pm.getWoTemplate().getResourceIdVal() != -1 && !resourceIds.contains(pm.getWoTemplate().getResourceIdVal()))
					{
						resourceIds.add(pm.getWoTemplate().getResourceIdVal());
					}
					for (PMTriggerContext trigger : pmTrigggers) {
						if(trigger.getSchedule() != null) {
							pmTriggerMap.put(trigger.getId(), trigger);
							if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
								if(trigger.getStartTime() > startTime && trigger.getStartTime() <= endTime) {
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
											LOGGER.log(Level.SEVERE, "pmJobs -- "+pmJobs.size() +"startTime -- "+System.currentTimeMillis());
											for(Map<String, Object> pmJob : pmJobs) {
												
												if(pmJob.get("templateId") != null && (long) pmJob.get("templateId") != -1)
												{
													long templateId = (long) pmJob.get("templateId");
													WorkorderTemplate template = null;
													if(workorderTemplateMap.containsKey(templateId)) {
														template = workorderTemplateMap.get(templateId);
													}
													else {
														template = (WorkorderTemplate) TemplateAPI.getTemplate(templateId);
														workorderTemplateMap.put(templateId, template);
													}
													pmJob.put("template", template);
												}
												if(pmJob.get("resourceId") != null) {
													ResourceContext resource = ResourceAPI.getResource((long) pmJob.get("resourceId"));
													pmJob.put("resource", resource);
													if (!resourceIds.contains(pmJob.get("resourceId"))) {
														resourceIds.add((long) pmJob.get("resourceId"));
													}
												}
												if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE && currentCalendarView != null && currentCalendarView.equals("planned")) {
													Long executionTime = (Long) pmJob.get("nextExecutionTime");
													 Map<Long, List<Map<String, Object>>> triggerMap = pmTriggerTimeBasedGroupedMap.get(executionTime);
													if(triggerMap == null) {
														triggerMap = new HashMap<>();
													}
													List<Map<String, Object>> jobList = triggerMap.get(trigger.getId());
													if(jobList == null) {
														jobList = new ArrayList<>();
													}
													jobList.add(pmJob);
													triggerMap.put(trigger.getId(), jobList);
													pmTriggerTimeBasedGroupedMap.put(executionTime, triggerMap);
												}
												else {
													pmJobList.add(pmJob);
												}
											}
											LOGGER.log(Level.SEVERE, "pmJobs -- "+pmJobs.size() +"endTime -- "+System.currentTimeMillis());
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
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGER_VS_PMJOB_MAP, pmTriggerTimeBasedGroupedMap);
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
