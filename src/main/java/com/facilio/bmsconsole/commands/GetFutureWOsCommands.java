package com.facilio.bmsconsole.commands;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.ScheduleInfo;

public class GetFutureWOsCommands implements Command {

	private static final Logger LOGGER = LogManager.getLogger(GetFutureWOsCommands.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long processStartTime = System.currentTimeMillis();
		FacilioField dateField = (FacilioField) context.get(FacilioConstants.ContextNames.DATE_FIELD);
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long endTime = (long) context.get(FacilioConstants.ContextNames.END_TIME);
		
		if (dateField.getName().equals("createdTime") && endTime < System.currentTimeMillis()) {
			return false;
		}
		
		FacilioView view = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		
		Criteria filterCriteria = null;
		if (dateField.getName().equals("dueDate")) {
			FacilioField durationField = FieldFactory.getAsMap(FieldFactory.getWorkOrderTemplateFields()).get("duration");
			
			filterCriteria = new Criteria();
			filterCriteria.addAndCondition(CriteriaAPI.getCondition(durationField, CommonOperators.IS_NOT_EMPTY));
		}
		
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getAllActivePMs(filterCriteria);
		
		if(pms != null && !pms.isEmpty()) 
		{
			LOGGER.info("PM Size : "+pms.size());
			List<WorkOrderContext> wos = new ArrayList<>();
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			for(PreventiveMaintenance pm : pms) {
				WorkorderTemplate woTemplate = pm.getWoTemplate();
				
				long currentStartTime = startTime / 1000;
				long currentEndTime = endTime / 1000;
				if (dateField.getName().equals("dueDate")) {
					currentStartTime = startTime - woTemplate.getDuration();
					currentEndTime = endTime - woTemplate.getDuration();
				}
				
				long currentTime = Instant.now().getEpochSecond();
				if (currentStartTime > currentTime) {
					currentStartTime = currentTime;
				}
				
				List<PMTriggerContext> pmTrigggers = pmTriggersMap.get(pm.getId());
				if (pmTrigggers != null) {
					long triggerStartTime = System.currentTimeMillis();
					LOGGER.info("Trigger Size for "+pm.getId()+" is "+pmTrigggers.size());
					for (PMTriggerContext trigger : pmTrigggers) {
						if(trigger.getSchedule() != null) {
							if(trigger.getSchedule().getFrequencyTypeEnum() == ScheduleInfo.FrequencyType.DO_NOT_REPEAT) {
								if(trigger.getStartTime() > currentStartTime && trigger.getStartTime() <= currentEndTime) {
									PMJobsContext pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentStartTime, false);
									checkAndAddWOs(wos, Collections.singletonList(pmJob), woTemplate, view.getCriteria(), trigger);
								}
							}
							else {
								long virtualJobsStartTime = -1;
								switch(pm.getTriggerTypeEnum()) {
									case ONLY_SCHEDULE_TRIGGER: 
										List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.getNextPMJobs(trigger, currentStartTime, currentEndTime);
										checkAndAddWOs(wos, pmJobs, woTemplate, view.getCriteria(), trigger);
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
											checkAndAddWOs(wos, Collections.singletonList(pmJob), woTemplate, view.getCriteria(), trigger);
											virtualJobsStartTime = pmJob.getNextExecutionTime();
										}
										else {
											virtualJobsStartTime = currentStartTime;
										}
										break;
								}
								if(virtualJobsStartTime != -1) {
									List<PMJobsContext> pmJobs = PreventiveMaintenanceAPI.createPMJobs(pm, trigger, virtualJobsStartTime, currentEndTime, false);
									checkAndAddWOs(wos, pmJobs, woTemplate, view.getCriteria(), trigger);
								}
							}
						}
					}
					LOGGER.info("Time taken for checking of Triggers for pm : "+pm.getName()+" is " + (System.currentTimeMillis() - triggerStartTime));
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
		LOGGER.info("Time taken for future calendar wo to be executed : " + (System.currentTimeMillis() - processStartTime));
		return false;
	}
	
	private void checkAndAddWOs (List<WorkOrderContext> woList, List<PMJobsContext> pmJobs, WorkorderTemplate template, Criteria criteria, PMTriggerContext trigger) throws Exception {
		if(pmJobs != null && !pmJobs.isEmpty()) {
			long startTime = System.currentTimeMillis();
			WorkOrderContext wo = template.getWorkorder();
			TicketAPI.loadWorkOrderLookups(Collections.singletonList(wo));
			long evalStartTime = System.currentTimeMillis();
			boolean isPassed = criteria == null ? true : criteria.computePredicate().evaluate(wo);
			LOGGER.info("Time taken for evaluation of WOs for trigger : "+trigger.getId()+" is " + (System.currentTimeMillis() - evalStartTime));
			if (isPassed) {
				long woCreationTime = System.currentTimeMillis();
				for (PMJobsContext pmJob : pmJobs) {
					WorkOrderContext currentWo = template.getWorkorder();
					currentWo.setCreatedTime(pmJob.getNextExecutionTime() * 1000);
					if (currentWo.getDuration() != -1) {
						currentWo.setDueDate((pmJob.getNextExecutionTime() + currentWo.getDuration()) * 1000);
					}
					woList.add(currentWo);
				}
				LOGGER.info("Time taken for WO Creation for trigger : "+trigger.getId()+" is " + (System.currentTimeMillis() - woCreationTime));
			}
			LOGGER.info("Time taken for checking of WOs for trigger : "+trigger.getId()+" is " + (System.currentTimeMillis() - startTime));
		}
	}
	
	

}
