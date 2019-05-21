package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMJobsContext;
import com.facilio.bmsconsole.context.PMJobsContext.PMJobsStatus;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateTimeUtil;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.serializable.SerializableCommand;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import org.apache.commons.chain.Context;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResetTriggersCommand implements SerializableCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PreventiveMaintenance> pms = CommonCommandUtil.getList((FacilioContext) context, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		if(pms != null && !pms.isEmpty()) {
			Map<Long, List<PMTriggerContext>> pmTriggersMap = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			long currentExecutionTime = (Long) context.get(FacilioConstants.ContextNames.CURRENT_EXECUTION_TIME);
			PMTriggerContext currentTrigger = (PMTriggerContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_TRIGGER); 
			Boolean reset = (Boolean) context.get(FacilioConstants.ContextNames.PM_RESET_TRIGGERS);
			PMJobsContext currentJob = (PMJobsContext) context.get(FacilioConstants.ContextNames.PM_CURRENT_JOB);
			if (reset == null) {
				reset = false;
			}
			if(reset) {
				Map<Long, Long> nextExecutionTimes = new HashMap<>();
				for(PreventiveMaintenance pm : pms) {
					resetPMTriggers(pm, currentTrigger, pmTriggersMap.get(pm.getId()),currentJob, currentExecutionTime, nextExecutionTimes);
				}
				context.put(FacilioConstants.ContextNames.NEXT_EXECUTION_TIMES, nextExecutionTimes);
				context.put(FacilioConstants.ContextNames.PM_TRIGGERS, pmTriggersMap);
			}
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
						if(pm.getPmCreationTypeEnum() == PreventiveMaintenance.PMCreationType.MULTIPLE) {
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
				if(pmJob != null) {
					nextExecutionTimes.put(trigger.getId(), pmJob.getNextExecutionTime());
				}
			}
			else if(trigger.getRuleId() != -1) {
				switch(pm.getTriggerTypeEnum()) {
					case FIXED:
					case FLOATING:
						if(trigger.getId() != currentTrigger.getId()) { //Resetting latest value of other metered triggers
							long latestValue = getLatestReading(trigger.getRuleId());
							if(latestValue != -1) {
								ReadingRuleAPI.updateLastValueInReadingRule(trigger.getRuleId(), latestValue);
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
		Condition condition = criteria.getConditions().get("1");
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
