package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.util.RuleTemplateAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ConvertToRulesCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long templateId = (long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
		JSONObject placeholder = (JSONObject) context.get(FacilioConstants.ContextNames.PLACE_HOLDER);
		JSONObject templateJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.RULE, (int) templateId).getJson();
		Boolean isEditTemplate = (Boolean) context.get(FacilioConstants.ContextNames.IS_EDIT_TEMPLATE); 
		templateJson.put("placeHolder", placeholder);
		JSONObject mappedplaced  = null;
		if (templateJson != null) {
			mappedplaced = RuleTemplateAPI.setPlaceHolder(templateJson);
		}
		AlarmRuleContext alarmRule = RuleTemplateAPI.convertTemplateToRule(mappedplaced);
		context.put(FacilioConstants.ContextNames.READING_RULE_ID, alarmRule.getPreRequsite().getId());
		context.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
		if (isEditTemplate) {
			return false;
		}
		FacilioChain addRule = TransactionChainFactory.addAlarmRuleChain();
		addRule.execute(context);
		RuleTemplateAPI.addRuleTemplateRel(templateId, alarmRule.getPreRequsite().getId());
		return false;
	}
	
	
}
