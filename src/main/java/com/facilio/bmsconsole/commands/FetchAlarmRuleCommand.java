package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;

public class FetchAlarmRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
		if (id == null || id == -1) {
			throw new IllegalArgumentException("Invalid ID to fetch workflow");
		}
		AlarmRuleContext alarmRule = new AlarmRuleContext(ReadingRuleAPI.getReadingRules(id));
		ReadingRuleAPI.setMatchedResources(alarmRule.getPreRequsite());
		context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
		
		return false;
	}

}
