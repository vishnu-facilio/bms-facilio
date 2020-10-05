package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class ExecuteFormActionRulesForNextRoundCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context facilioContext) throws Exception {
		
		List<Long> valueFilledFields = (List<Long>) facilioContext.get(FormRuleAPI.VALUE_FILLED_FIELD_IDS);
		
		Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND);
		
		
		if(valueFilledFields != null && !valueFilledFields.isEmpty()) {
			
			Map<String,Object> formData = (Map<String, Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			formData.putAll(formDataToBeAddedforNextRound);
			
			JSONArray result = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(Long valueFilledField :valueFilledFields) {
				
				FacilioChain c = TransactionChainFactory.getExecuteFormActionRules();
				
				Context context = c.getContext();
				
				context.put(FacilioConstants.ContextNames.FORM_ID, facilioContext.get(FacilioConstants.ContextNames.FORM_ID));
				context.put(FacilioConstants.ContextNames.FORM_FIELD_ID, valueFilledField);
				context.put(FormRuleAPI.FORM_RULE_TRIGGER_TYPE,FormRuleContext.TriggerType.FIELD_UPDATE);
				context.put(FormRuleAPI.FORM_DATA,formData);
				
				c.execute();
				
				result.addAll((JSONArray) context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
			}
		}
		
		return false;
	}


}
