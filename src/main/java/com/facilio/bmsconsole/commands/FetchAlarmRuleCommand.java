package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class FetchAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (id == null || id == -1) {
			throw new IllegalArgumentException("Invalid ID to fetch workflow");
		}
		List<ReadingAlarmRuleContext> readingAlarmRules = WorkflowRuleAPI.getReadingAlarmRulesFromReadingRuleGroupId(id);
		AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRulesList(id),readingAlarmRules);
		ReadingRuleAPI.setMatchedResources(alarmRule.getPreRequsite());
		context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);

		return false;
	}

}
