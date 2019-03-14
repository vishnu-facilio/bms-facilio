package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;

public class GetReadingRuleDetailsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		List<AlarmContext> alarms = (List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST);
		
		Boolean isFromSummary = (Boolean) context.get(FacilioConstants.ContextNames.IS_FROM_SUMMARY);
		
		if (alarms != null && !alarms.isEmpty() && isFromSummary) {
			List<Long> ruleIds = alarms.stream()
										.filter(a -> a instanceof ReadingAlarmContext && ((ReadingAlarmContext) a).getRuleId() != -1)
										.map(a -> ((ReadingAlarmContext) a).getRuleId())
										.collect(Collectors.toList())
										;
			
			if (!ruleIds.isEmpty()) {
				
				Map<Long, AlarmRuleContext> alarmRuleContextMap = ReadingRuleAPI.getAlarmRuleMap(ruleIds);
				
				for (AlarmContext alarm : alarms) {
					if (alarm instanceof ReadingAlarmContext && ((ReadingAlarmContext) alarm).getRuleId() != -1) {
						
						ReadingAlarmContext readingAlarmContext = (ReadingAlarmContext) alarm;
						readingAlarmContext.setAlarmRuleContext(alarmRuleContextMap.get(readingAlarmContext.getRuleId()));
						
					}
				}
			}
			
		}
		return false;
	}

}
