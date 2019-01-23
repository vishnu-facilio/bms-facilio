package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;

public class UpdateAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AlarmRuleContext alarmRule = (AlarmRuleContext) context.get(FacilioConstants.ContextNames.ALARM_RULE);
		int i=0;
		for(ReadingRuleContext rule : alarmRule.getAllRuleList()) {
			i++;
			if(alarmRule.getAllRuleList().size() == i && alarmRule.isAutoClear()) {
				rule.setClearAlarm(true);
			}
			rule = ReadingRuleAPI.updateReadingRuleWithChildren( rule);
		}
		return false;
	}

}
