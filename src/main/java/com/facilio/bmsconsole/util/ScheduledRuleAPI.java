package com.facilio.bmsconsole.util;

import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.rule.ScheduledRuleContext;

public class ScheduledRuleAPI extends WorkflowRuleAPI {
	protected static void validateScheduledRule(ScheduledRuleContext rule) {
		if (rule.getDateFieldId() == -1) {
			throw new IllegalArgumentException("Date Field Id cannot be null for Scheduled Rule");
		}
		
		if (rule.getScheduleTypeEnum() == null) {
			throw new IllegalArgumentException("Schedule Type cannot be null for Scheduled Rule");
		}
		
		switch (rule.getScheduleTypeEnum()) {
			case BEFORE:
			case AFTER:
				if (rule.getInterval() == -1) {
					throw new IllegalArgumentException("Interval cannot be null for Scheduled Rule with type BEFORE/ AFTER");
				}
				break;
			case ON:
				break;
		}
	}
	
	protected static ScheduledRuleContext constructScheduledRuleFromProps(Map<String, Object> prop, ModuleBean modBean) throws Exception {
		ScheduledRuleContext scheduledRule = FieldUtil.getAsBeanFromMap(prop, ScheduledRuleContext.class);
		scheduledRule.setDateField(modBean.getField(scheduledRule.getDateFieldId()));
		return scheduledRule;
	}
}
