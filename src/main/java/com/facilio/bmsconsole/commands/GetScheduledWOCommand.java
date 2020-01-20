package com.facilio.bmsconsole.commands;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.PermissionUtil;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class GetScheduledWOCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(GetScheduledWOCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		LOGGER.log(Level.SEVERE, "startTime -- "+System.currentTimeMillis());
		String currentCalendarView = (String) context.get(FacilioConstants.ContextNames.CURRENT_CALENDAR_VIEW);
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if(filterCriteria == null) {
			filterCriteria = new Criteria();
		}
		boolean isCriteriaAdded = false;
		Criteria scopeCriteria = PermissionUtil.getCurrentUserScopeCriteria("workorder");
		if(scopeCriteria != null) {
			isCriteriaAdded = true;
			filterCriteria.andCriteria(scopeCriteria);
		}
		
		Criteria permissionCriteria = PermissionUtil.getCurrentUserPermissionCriteria("workorder","read");
		if(permissionCriteria != null) {
			isCriteriaAdded = true;
			filterCriteria.andCriteria(permissionCriteria);
		}

		if (!isCriteriaAdded) {
			filterCriteria = null;
		}
			
		long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
		long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);

		Map<Long, List<Map<String, Object>>> pmJobsMap = PreventiveMaintenanceAPI.getPMScheduledWOsFromPMIds(startTime, endTime, filterCriteria);
		List<Long> pmIds = new ArrayList<>();
		if (pmJobsMap == null || pmJobsMap.isEmpty()) {
			return false;
		}

		List<Map<String, Object>> pmJobList = new ArrayList<>();

		Set<Long> keys = pmJobsMap.keySet();
		for (Long key: keys) {
			List<Map<String, Object>> jobList = pmJobsMap.get(key);
			for (Map<String, Object> job: jobList) {
				pmIds.add((Long) job.get("pmId"));
			}
		}

		Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pmIds);

		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getActivePMs(pmIds, null, null);
		if (pms == null || pms.isEmpty()) {
			return false;
		}

		Map<Long, PreventiveMaintenance> pmMap = pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));

		Map<Long, PMTriggerContext> pmTriggerMap = new HashMap<>();

		Map<Long,Map<Long,List<Map<String, Object>>>> pmTriggerTimeBasedGroupedMap = new HashMap<>();
		Set<Long> resourceIds = new HashSet<>();
		Map<Long,WorkorderTemplate> workorderTemplateMap = new HashMap<>();

		fillResourceIds(pmJobsMap, pmTriggersMap, pms, resourceIds);

		Map<Long, ResourceContext> resourceAsMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);

		for(PreventiveMaintenance pm : pms)
		{
			List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
			if (pmTrigggers != null && !pmTrigggers.isEmpty()) {
				for (PMTriggerContext trigger : pmTrigggers) {
					if(trigger.getSchedule() != null) {
						pmTriggerMap.put(trigger.getId(), trigger);
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
											ResourceContext resource = resourceAsMap.get(pmJob.get("resourceId"));
											pmJob.put("resource", resource);
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
								break;
						}
					}
				}
			}

//			sortPMs(pmJobList);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pmMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGER_VS_PMJOB_MAP, pmTriggerTimeBasedGroupedMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_JOBS_LIST, pmJobList);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST, pmTriggerMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES, resourceAsMap);
		}
		
		return false;
	}

	private void fillResourceIds(Map<Long, List<Map<String, Object>>> pmJobsMap, Map<Long, List<PMTriggerContext>> pmTriggersMap, List<PreventiveMaintenance> pms, Set<Long> resourceIds) {
		for (PreventiveMaintenance pm: pms) {
			List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
			if(pm.getWoTemplate() != null && pm.getWoTemplate().getResourceIdVal() != -1 && !resourceIds.contains(pm.getWoTemplate().getResourceIdVal()))
			{
				resourceIds.add(pm.getWoTemplate().getResourceIdVal());
			}
			for (PMTriggerContext trigger : pmTrigggers) {
				if(trigger.getSchedule() != null) {
					switch(pm.getTriggerTypeEnum()) {
						case ONLY_SCHEDULE_TRIGGER:
							List<Map<String, Object>> pmJobs = pmJobsMap.get(trigger.getId());
							if(pmJobs != null && !pmJobs.isEmpty()) {
								for(Map<String, Object> pmJob : pmJobs) {
									if(pmJob.get("resourceId") != null) {
										if (!resourceIds.contains(pmJob.get("resourceId"))) {
											resourceIds.add((long) pmJob.get("resourceId"));
										}
									}
								}
							}
					}
				}
			}
		}
	}
}
