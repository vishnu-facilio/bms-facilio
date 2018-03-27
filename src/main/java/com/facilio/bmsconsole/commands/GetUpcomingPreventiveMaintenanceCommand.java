package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.tasker.ScheduleInfo;

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
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs(filterCriteria);
		if(pms != null && !pms.isEmpty()) 
		{
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);
			
			Map<Long, PMTriggerContext> pmTriggerMap = new HashMap<>();
			List<Long> resourceIds = new ArrayList<>();
			List<Pair<PMJobsContext, PreventiveMaintenance>> pmPairs = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) 
			{
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
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
								PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger, startTime, false);
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
											if(pmJob.getTemplateId() != -1)
											{
												Map<String, Object> pmProps = FieldUtil.getAsProperties(pm);
												PreventiveMaintenance clonedPM =  FieldUtil.getAsBeanFromMap(pmProps, PreventiveMaintenance.class);
												
												/*JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), pmJob.getTemplateId());
												JSONObject content = template.getTemplate(null);
												WorkOrderContext wo = FieldUtil.getAsBeanFromJson((JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
												clonedPM.setAssignedToid(wo.getAssignedTo().getId());*/
												
												WorkorderTemplate template = (WorkorderTemplate) TemplateAPI.getTemplate(pmJob.getTemplateId());
												clonedPM.setWoTemplate(template);
												
												Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, clonedPM);
												pmPairs.add(pair);
											}
											else
											{
												Pair<PMJobsContext, PreventiveMaintenance> pair = new ImmutablePair<PMJobsContext, PreventiveMaintenance>(pmJob, pm);
												pmPairs.add(pair);
											}
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
									PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger, startTime, false);
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
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_TRIGGERS_LIST, pmTriggerMap);
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_RESOURCES, ResourceAPI.getResourceAsMapFromIds(resourceIds));
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
