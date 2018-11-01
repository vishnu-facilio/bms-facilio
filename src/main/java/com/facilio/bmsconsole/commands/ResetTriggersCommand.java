package com.facilio.bmsconsole.commands;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class ResetTriggersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		if(pms == null) {
			PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
			if (pm != null) {
				pms = Collections.singletonList(pm);
			}
		}
		
		if(pms != null && !pms.isEmpty()) {
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			long currentExecutionTime = (Long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			PMTriggerContext currentTrigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER); 
			Boolean reset = (Boolean) context.get(FacilioConstants.ContextNames.PM_RESET_TRIGGERS);
			PMJobsContext currentJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
			if(reset != null && reset) {
				reset = true;
			}
			else {
				reset = false;
			}
			Map<Long, Long> nextExecutionTimes = new HashMap<>();
			for(PreventiveMaintenance pm : pms) {
				if (pm.getCurrentExecutionCount() == pm.getMaxCount()) {
					PreventiveMaintenanceAPI.setPMInActive(pm.getId());
				}
				else if(reset) {
					resetPMTriggers(pm, currentTrigger, pmTriggersMap.get(pm.getId()),currentJob, currentExecutionTime, nextExecutionTimes);
				}
			}
			if (currentJob != null) {
				PreventiveMaintenanceAPI.updatePMJobStatus(currentJob.getId(), PMJobsStatus.COMPLETED);
			}
			
			context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
			context.put(FacilioConstants.ContextNames.PM_TRIGGERS, pmTriggersMap);
		}
		return false;
	}
	
	private void resetPMTriggers(PreventiveMaintenance pm, PMTriggerContext currentTrigger, List<PMTriggerContext> triggers,PMJobsContext pmJobsContext, long currentExecutionTime, Map<Long, Long> nextExecutionTimes) throws Exception {
		for(PMTriggerContext trigger : triggers) {
			if(trigger.getSchedule() != null && trigger.getSchedule().getFrequencyTypeEnum() != FrequencyType.DO_NOT_REPEAT) {
				PMJobsContext pmJob = null;
				switch(pm.getTriggerTypeEnum()) {
					case FIXED:
					case FLOATING:
						if (trigger.getId() == currentTrigger.getId()) {
							pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, currentExecutionTime);
							if (pmJob != null) {
								PreventiveMaintenanceAPI.schedulePMJob(pmJob);
							}
						}
						else {//Deleting oldJobs of other schedule triggers
							pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentExecutionTime, false);
							PMJobsContext updatedPM = new PMJobsContext();
							ZonedDateTime zdt = DateTimeUtil.getDateTime();
							if(trigger.getSchedule().getTimeObjects() != null && !trigger.getSchedule().getTimeObjects().isEmpty()) {
								List<LocalTime> times = trigger.getSchedule().getTimeObjects();
								zdt = zdt.with(times.get(times.size() - 1));
							}
							long nextExecutionTime = trigger.getSchedule().nextExecutionTime(zdt.toEpochSecond());
							if (pm.getEndTime() == -1 || updatedPM.getNextExecutionTime() < pm.getEndTime()) {
								updatedPM.setNextExecutionTime(nextExecutionTime);
								updatedPM.setId(pmJob.getId());
								updatedPM.setStatus(PMJobsStatus.ACTIVE);
								pmJob = PreventiveMaintenanceAPI.updateAndGetPMJob(updatedPM);
								PreventiveMaintenanceAPI.reSchedulePMJob(pmJob);
							}
							else {
								updatedPM.setStatus(PMJobsStatus.IN_ACTIVE);
								pmJob = PreventiveMaintenanceAPI.updateAndGetPMJob(updatedPM);
							}
						}
						break;
					case ONLY_SCHEDULE_TRIGGER:
						if(pm.getPmCreationType() == PreventiveMaintenance.PMCreationType.MULTIPLE.getVal()) {
							if(pmJobsContext.getPmTriggerId() == trigger.getId()) {
								pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentExecutionTime,pmJobsContext.getResourceId(), true);
							}
						}
						else {
							pmJob = PreventiveMaintenanceAPI.getNextPMJob(trigger.getId(), currentExecutionTime, true);
							if (pmJob == null) {
								pmJob = PreventiveMaintenanceAPI.createPMJobOnce(pm, trigger, currentExecutionTime);
							}
						}
						if (pmJob != null) {
							PreventiveMaintenanceAPI.schedulePMJob(pmJob);
						}
					default:
						break;
				}
				nextExecutionTimes.put(trigger.getId(), pmJob.getNextExecutionTime()); 
			}
			else if(trigger.getReadingRuleId() != -1) {
				switch(pm.getTriggerTypeEnum()) {
					case FIXED:
					case FLOATING:
						if(trigger.getId() != currentTrigger.getId()) { //Resetting latest value of other metered triggers
							long latestValue = getLatestReading(trigger.getReadingRuleId());
							if(latestValue != -1) {
								ReadingRuleAPI.updateLastValueInReadingRule(trigger.getReadingRuleId(), latestValue);
							}
						}
						break;
					default:
						break;
				}
			}
		}
	}
	
	private long getLatestReading(long readingRuleId) throws Exception {
		ReadingRuleContext rule = (ReadingRuleContext) WorkflowRuleAPI.getWorkflowRule(readingRuleId, true);
		WorkflowEventContext event = rule.getEvent();
		
		Criteria criteria = rule.getCriteria();
		Condition condition = criteria.getConditions().get(1);
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule(event.getModuleId());
		
		SelectRecordsBuilder<ReadingContext> selectBuilder = new SelectRecordsBuilder<ReadingContext>()
																			.module(module)
																			.beanClass(ReadingContext.class)
																			.select(bean.getAllFields(module.getName()))
																			.andCondition(CriteriaAPI.getCondition(condition.getColumnName(), condition.getFieldName(), null, CommonOperators.IS_NOT_EMPTY))
																			.orderBy("TTIME DESC")
																			.limit(1);
		
		List<ReadingContext> readings = selectBuilder.get();
		if(readings != null && !readings.isEmpty()) {
			long lastValue = new Double(readings.get(0).getReading(condition.getFieldName()).toString()).longValue();
			return lastValue;
		}
		return -1;
	}
}
