package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetReadingRuleNameCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<AlarmContext> alarms = (List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST);
		
		if (alarms != null && !alarms.isEmpty()) {
			List<Long> ruleIds = alarms.stream()
										.filter(a -> a instanceof ReadingAlarmContext && ((ReadingAlarmContext) a).getRuleId() != -1)
										.map(a -> ((ReadingAlarmContext) a).getRuleId())
										.collect(Collectors.toList())
										;
			
			if (!ruleIds.isEmpty()) {
				Map<Long, WorkflowRuleContext> rules = WorkflowRuleAPI.getWorkflowRulesAsMap(ruleIds, false, false, false);
				
				for (AlarmContext alarm : alarms) {
					if (alarm instanceof ReadingAlarmContext && ((ReadingAlarmContext) alarm).getRuleId() != -1) {
						alarm.setCondition(rules.get(((ReadingAlarmContext) alarm).getRuleId()).getName());
					}
				}
			}
		}
		
		return false;
	}

}
