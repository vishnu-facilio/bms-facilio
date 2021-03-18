package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class ExecuteFormActionRulesForNextRoundCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context facilioContext) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> valueFilledFields = (List<Long>) facilioContext.get(FormRuleAPI.VALUE_FILLED_FIELD_IDS);
		
		Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND);
		
		FacilioForm form = (FacilioForm) facilioContext.get(FacilioConstants.ContextNames.FORM );
		
		if(valueFilledFields != null && !valueFilledFields.isEmpty()) {
			
			Map<String,Object> formData = (Map<String, Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			FacilioModule module = form.getModule();
			
			for(String fieldName : formDataToBeAddedforNextRound.keySet()) {
				
				FacilioField field = modBean.getField(fieldName, module.getName());
				
				if(field.isDefault()) {
					formData.put(fieldName, formDataToBeAddedforNextRound.get(fieldName));
				}
				else {
					Map<String,Object>  data = (Map<String, Object>) formData.getOrDefault("data", new HashMap<String, Object>());
					
					data.put(fieldName, formDataToBeAddedforNextRound.get(fieldName));
					
					formData.put("data", data);
				}
			}
			
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
