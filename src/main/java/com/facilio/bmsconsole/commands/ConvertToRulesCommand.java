package com.facilio.bmsconsole.commands;

import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.RuleTemplateAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.constants.FacilioConstants;

public class ConvertToRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
		AlarmRuleContext alarmRule = RuleTemplateAPI.convertTemplateToRule((int) templateId);
		context.put(FacilioConstants.ContextNames.READING_RULE_ID, alarmRule.getPreRequsite().getId());
		context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
		Chain addRule = TransactionChainFactory.addAlarmRuleChain();
		addRule.execute(context);
		RuleTemplateAPI.addRuleTemplateRel(templateId, alarmRule.getPreRequsite().getId());
		return false;
	}
	
	
}
