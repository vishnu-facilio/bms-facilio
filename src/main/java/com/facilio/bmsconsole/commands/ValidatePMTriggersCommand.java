package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.PreventiveMaintenance.TriggerType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowEventContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext.ThresholdType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.leed.context.PMTriggerContext;

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
		
		if(pmTriggers != null && !pmTriggers.isEmpty()) {
			boolean isScheduleOnly = true;
			for (PMTriggerContext trigger : pmTriggers) {
				if(trigger.getSchedule() == null) {
					createReadingForTrigger(trigger);
				}
				else if(trigger.getStartTime() == -1) {
					throw new IllegalArgumentException("Starttime cannot be empty for schedule triggers");
				}
				else if (trigger.getStartTime() < System.currentTimeMillis()) {
					trigger.setStartTime(System.currentTimeMillis());
				}
				
				if (trigger.getReadingRule() != null) {
					if (trigger.getSchedule() == null) {
						isScheduleOnly = false;
					} else {
						throw new IllegalArgumentException("Both schedule and Reading cannot be set for a trigger");
					}
				} else if (trigger.getSchedule() == null) {
					throw new IllegalArgumentException("Both schedule and Reading cannot be null for a trigger");
				}
			}
			if (isScheduleOnly) {
				pm.setTriggerType(TriggerType.ONLY_SCHEDULE_TRIGGER);
			}
			else if(pm.getTriggerTypeEnum() == null) {
				pm.setTriggerType(TriggerType.FIXED);
			}
		}
		else {
			if (oldPm != null && pmTriggers == null) {
				pm.setTriggerType(oldPm.getTriggerType());
			}
			else {
				pm.setTriggerType(TriggerType.NONE);
			}
		}
		
		return false;
	}
	
	private void createReadingForTrigger(PMTriggerContext trigger) throws Exception {
		if(trigger.getReadingFieldId() == -1) {
			throw new IllegalArgumentException("Reading field cannot be empty for reading triggers");
		}
		
		if(trigger.getStartReading() == -1) {
			throw new IllegalArgumentException("Start reading cannot be empty for reading triggers");
		}
		
		if(trigger.getReadingInterval() == -1) {
			throw new IllegalArgumentException("Reading interval cannot be empty for reading triggers");
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
		event.setActivityType(ActivityType.CREATE);
		rule.setEvent(event);
		
		trigger.setReadingRule(rule);
	}

}
