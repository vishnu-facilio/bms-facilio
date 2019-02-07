package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;

import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;

public class GetFutureWOsCommands implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioField dateField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		long startTime1 = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime1 = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		if (dateField.getName().equals("createdTime") && endTime1 < System.currentTimeMillis()) {
			return false;
		}
		
		Criteria filterCriteria = null;
		if (dateField.getName().equals("dueDate")) {
			FacilioField durationField = FieldFactory.getAsMap(FieldFactory.getWorkOrderTemplateFields()).get("duration");
			filterCriteria.addAndCondition(CriteriaAPI.getCondition(durationField, CommonOperators.IS_NOT_EMPTY));
		}
		
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs(filterCriteria);
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		if(pms != null && !pms.isEmpty()) 
		{
			List<WorkOrderContext> wos = new ArrayList<>();
			Map<Long, PreventiveMaintenance> pmMap = pms.stream().collect(Collectors.toMap(PreventiveMaintenance::getId, Function.identity()));
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			for(PreventiveMaintenance pm : pms) {
				WorkorderTemplate woTemplate = pm.getWoTemplate();
				
				long currentStartTime = startTime1 / 1000;
				long currentEndTime = endTime1 / 1000;
				if (dateField.getName().equals("dueDate")) {
					currentStartTime = startTime1 - woTemplate.getDuration();
					currentEndTime = endTime1 - woTemplate.getDuration();
				}
				
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				for (PMTriggerContext trigger : pmTrigggers) {
					if(trigger.getSchedule() != null) {
						if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
							if(trigger.getStartTime() > currentStartTime && trigger.getStartTime() <= currentEndTime) {
								PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentStartTime, false);
								checkAndAddWOs(wos, Collections.singletonList(pmJob), woTemplate, view.getCriteria());
							}
						}
						else {
							long virtualJobsStartTime = -1;
							switch(pm.getTriggerTypeEnum()) {
								case ONLY_SCHEDULE_TRIGGER: 
									List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getNextPMJobs(trigger, currentStartTime, currentEndTime);
									checkAndAddWOs(wos, pmJobs, woTemplate, view.getCriteria());
										// virtualJobsStartTime = pmJobs.get(pmJobs.size() - 1).getNextExecutionTime();
									long plannedEndTime = DateTimeUtil.getDayStartTime(PreventiveMaintenanceAPI.PM_CALCULATION_DAYS+1, true) - 1;
									if(currentStartTime > plannedEndTime) {
										virtualJobsStartTime = currentStartTime;
									}
									else if(currentEndTime > plannedEndTime) {
										virtualJobsStartTime = plannedEndTime+1;
									}
									break;
								case FIXED:
								case FLOATING:
									PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentStartTime, false);
									if(pmJob.getNextExecutionTime() > currentEndTime) {
										virtualJobsStartTime = -1;
									}
									else if(pmJob.getNextExecutionTime() > currentStartTime) {
										checkAndAddWOs(wos, Collections.singletonList(pmJob), woTemplate, view.getCriteria());
										virtualJobsStartTime = pmJob.getNextExecutionTime();
									}
									else {
										virtualJobsStartTime = currentStartTime;
									}
									break;
							}
							if(virtualJobsStartTime != -1) {
								List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createPMJobs(pm, trigger, virtualJobsStartTime, currentEndTime, false);
								checkAndAddWOs(wos, pmJobs, woTemplate, view.getCriteria());
							}
						}
					}
				}
			}
			
			if (!wos.isEmpty()) {
				boolean isCount = (boolean) context.get(FacilioConstants.ContextNames.COUNT);
				if (isCount) {
					List<Map<String, Object>> props = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.WORK_ORDER_COUNT);
//					Map<Long, Map<String, Object>> dateWiseMap = props.stream().collect(Collectors.toMap(p -> (Long) p.get(dateField.getName()), Function.identity()));
					Map<Long, Map<String, Object>> newPropsMap = new HashMap<>();
					List<Map<String, Object>> newProps = new ArrayList<>();
					DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.AGGR_KEY);
					for (WorkOrderContext wo : wos) {
						long createdTime = aggr.getAdjustedTimestamp(wo.getCreatedTime());
						Map<String, Object> prop = newPropsMap.get(createdTime);
						if (prop == null) {
							prop = new HashMap<>();
							prop.put("createdTime", createdTime);
							prop.put("count", 1);
							
							newProps.add(prop);
							newPropsMap.put(createdTime, prop);
						}
						else {
							int count = (int) prop.get("count");
							prop.put("count", count+1);
						}
					}
					props.addAll(newProps);
				}
				else {
					List<WorkOrderContext> existingWOs = (List<WorkOrderContext>) context.get(FacilioConstants.ContextNames.WORK_ORDER_LIST);
					existingWOs.addAll(wos);
				}
			}
		}
		
		return false;
	}
	
	private void checkAndAddWOs (List<WorkOrderContext> woList, List<PMJobsContext> pmJobs, WorkorderTemplate template, Criteria criteria) {
		if(pmJobs != null && !pmJobs.isEmpty()) {
			WorkOrderContext wo = template.getWorkorder();
			boolean isPassed = criteria == null ? true : criteria.computePredicate().evaluate(wo);
			if (isPassed) {
				for (PMJobsContext pmJob : pmJobs) {
					WorkOrderContext currentWo = template.getWorkorder();
					currentWo.setCreatedTime(pmJob.getNextExecutionTime() * 1000);
					if (currentWo.getDuration() != -1) {
						currentWo.setDueDate((pmJob.getNextExecutionTime() + currentWo.getDuration()) * 1000);
					}
					woList.add(currentWo);
				}
			}
		}
	}
	
	

}
