package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingAlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;

public class AddReadingAlarmRuleCommand implements Command {

	
	
	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String,Long> readingAlarmRuleNameVsIdMap = new HashMap<>();
		
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		
		if(alarmRule.getReadingAlarmRuleContexts() != null) {
			
			for(ReadingAlarmRuleContext readingAlarmRuleContext : alarmRule.getReadingAlarmRuleContexts()) {
				readingAlarmRuleContext.setReadingRuleGroupId(alarmRule.getPreRequsite().getRuleGroupId());
				
				readingAlarmRuleContext.setRuleType(RuleType.READING_ALARM_RULE);
				
				if(readingAlarmRuleContext.getParentRuleName() != null) {
					readingAlarmRuleContext.setParentRuleId(readingAlarmRuleNameVsIdMap.get(readingAlarmRuleContext.getParentRuleName()));
				}
				
				WorkflowRuleAPI.addWorkflowRule(readingAlarmRuleContext);
				
				readingAlarmRuleNameVsIdMap.put(readingAlarmRuleContext.getName(),readingAlarmRuleContext.getId());
				
				List<ActionContext> actions = ActionAPI.addActions(readingAlarmRuleContext.getActions(), readingAlarmRuleContext);
				ActionAPI.addWorkflowRuleActionRel(readingAlarmRuleContext.getId(), actions);
			}
		}
		
		return false;
	}

}
