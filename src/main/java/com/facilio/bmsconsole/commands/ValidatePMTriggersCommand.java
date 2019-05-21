package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMTriggerContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class ValidatePMTriggersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance oldPm = null;
		if(context.containsKey(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST) && context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST) != null) {
			oldPm = ((List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST)).get(0);
		}
		
		PreventiveMaintenance pm = (PreventiveMaintenance) context
				.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		
		List<PMTriggerContext> pmTriggers = pm.getTriggers();
		
		WorkOrderContext workorder = (WorkOrderContext) context.get(FacilioConstants.ContextNames.WORK_ORDER);
		
		if(pmTriggers != null && !pmTriggers.isEmpty()) {
			boolean isScheduleOnly = true;
			boolean isAlarmRule = false;
			for (PMTriggerContext trigger : pmTriggers) {
				switch(trigger.getTriggerExecutionSourceEnum()) {
				case READING:
					createReadingForTrigger(trigger, workorder);
					isScheduleOnly = false;
					break;
				case SCHEDULE:
					if(trigger.getStartTime() == -1) {
						throw new IllegalArgumentException("Starttime cannot be empty for schedule triggers");
					}
					else if (trigger.getStartTime() < System.currentTimeMillis()) {
						trigger.setStartTime(System.currentTimeMillis());
					}
					
					if (trigger.getSchedule() == null) {
						throw new IllegalArgumentException("Schedule cannot be empty for SCHEDULE type trigger");
					}
					break;
				case ALARMRULE:
					if (trigger.getWorkFlowRule() == null) {
						throw new IllegalArgumentException("Rule cannot be empty for ALARMRULE type trigger");
					}
					isAlarmRule = true;
					isScheduleOnly = false;
					break;
				}
			}
			if (isScheduleOnly) {
				pm.setTriggerType(TriggerType.ONLY_SCHEDULE_TRIGGER);
			}
			else if (isAlarmRule) {
				pm.setTriggerType(TriggerType.FLOATING);
			}
			else if(pm.getTriggerTypeEnum() == null) {
				pm.setTriggerType(TriggerType.FIXED);
			}
		}
		else {
			if (oldPm != null && pmTriggers == null) {
				pm.setTriggerType(oldPm.getTriggerTypeEnum());
			}
			else {
				pm.setTriggerType(TriggerType.NONE);
			}
		}
		
		return false;
	}
	
	private void createReadingForTrigger(PMTriggerContext trigger, WorkOrderContext workorder) throws Exception {
		if(trigger.getReadingFieldId() == -1) {
			throw new IllegalArgumentException("Reading field cannot be empty for READING type triggers");
		}
		
		if(trigger.getStartReading() == -1) {
			throw new IllegalArgumentException("Start reading cannot be empty for READING type triggers");
		}
		
		if(trigger.getReadingInterval() == -1) {
			throw new IllegalArgumentException("Reading interval cannot be empty for READING type triggers");
		}
		
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = bean.getField(trigger.getReadingFieldId());
		if(field == null) {
			throw new IllegalArgumentException("Invalid reading field id");
		}
		
		ReadingRuleContext rule = new ReadingRuleContext();
		rule.setRuleType(RuleType.PM_READING_RULE);
		rule.setStartValue(trigger.getStartReading());
		rule.setInterval(trigger.getReadingInterval());
		rule.setReadingFieldId(trigger.getReadingFieldId());
		rule.setThresholdType(ThresholdType.SIMPLE);
		rule.setResourceId(workorder.getResource().getId());
		
		Condition condition = new Condition();
		condition.setField(field);
		condition.setOperator(NumberOperators.GREATER_THAN_EQUAL);
		condition.setValue("rule.lastValue+rule.interval");
		condition.setIsExpressionValue(true);
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(condition);
		rule.setCriteria(criteria);
		
		WorkflowEventContext event = new WorkflowEventContext();
		event.setModuleId(field.getModuleId());
		event.setActivityType(EventType.CREATE);
		rule.setEvent(event);
		
		trigger.setReadingRule(rule);
	}

}
