package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;

public class ExecuteFormActionRulesCommand extends FacilioCommand {
	private static final Logger LOGGER = LogManager.getLogger(ExecuteFormActionRulesCommand.class.getName());
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<FormRuleContext> formRuleContexts = (List<FormRuleContext>)context.get(FormRuleAPI.FORM_RULE_CONTEXTS);
		
		Map<String,Object> formData = (Map<String,Object>)context.get(FormRuleAPI.FORM_DATA);
		Map<String,Object> allSubFormDatas = null;
		
		if(formData != null) {
			allSubFormDatas = (Map<String, Object>) formData.get(FormRuleAPI.SUB_FORM_DATA_KEY);
		}
		
		FacilioForm form = (FacilioForm)context.get(ContextNames.FORM);
		
		JSONArray resultJson = (JSONArray) context.getOrDefault(FormRuleAPI.FORM_RULE_RESULT_JSON, new JSONArray());
		
		context.put(FormRuleAPI.FORM_RULE_RESULT_JSON, resultJson);
		
		JSONObject subFormRuleResultJSON = (JSONObject) context.getOrDefault(FormRuleAPI.SUB_FORM_RULE_RESULT_JSON, new JSONObject());
		
		context.put(FormRuleAPI.SUB_FORM_RULE_RESULT_JSON, subFormRuleResultJSON);
		
		for(FormRuleContext formRuleContext :formRuleContexts) {
			
			context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRuleContext);
			
			if (formRuleContext.getFormContext() == null && form != null) {
				formRuleContext.setFormContext(form);
			}
			
			if(formRuleContext.getSubFormId() > 0) {
				
				Boolean isSubFormTriggerField = (Boolean) context.getOrDefault(FormRuleAPI.IS_SUB_FORM_TRIGGER_FIELD, Boolean.FALSE);
				
				List<Map<String,Object>> subFormDatas = (List<Map<String, Object>>) (((Map<String, Object>) allSubFormDatas.get(formRuleContext.getSubFormContext().getName())).get("data"));
				
				fillEmptyActionObjectsForAllSubFormData(subFormDatas,(FacilioContext)context);
				
				if(isSubFormTriggerField || formRuleContext.getTriggerTypeEnum() == TriggerType.SUB_FORM_ADD_OR_DELETE) {
					
					for(int i=0;i<subFormDatas.size();i++) {
						
						Map<String, Object> subFormData = subFormDatas.get(i);
						
						if(subFormData.get("sub_form_action") != null) {
							context.put(FormRuleAPI.SUB_FORM_DATA, subFormData);
							context.put(FormRuleAPI.SUB_FORM_DATA_INDEX, i);
							
							boolean flag1 = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
							boolean flag2 = formRuleContext.evaluateSubFormCriteria(subFormData, (FacilioContext)context);
							if(flag1 && flag2) {
								
								formRuleContext.executeAction((FacilioContext)context);
							}
							break;
						}
					}
				}
				else {
					
					for(int i=0;i<subFormDatas.size();i++) {
						
						Map<String, Object> subFormData = subFormDatas.get(i);
						
						context.put(FormRuleAPI.SUB_FORM_DATA, subFormData);
						context.put(FormRuleAPI.SUB_FORM_DATA_INDEX, i);
						
						boolean flag1 = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);
						boolean flag2 = formRuleContext.evaluateSubFormCriteria(subFormData, (FacilioContext)context);
						if(flag1 && flag2) {
							
							formRuleContext.executeAction((FacilioContext)context);
						}
					}
				}
				
			}
			else {
				
				boolean flag = formRuleContext.evaluateCriteria(formData, (FacilioContext)context);

				if(flag) {
					formRuleContext.executeAction((FacilioContext)context);
				}
			}
		}
		
		return false;
	}
	private void fillEmptyActionObjectsForAllSubFormData(List<Map<String, Object>> subFormDatas, FacilioContext context) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<subFormDatas.size();i++) {
			
			Map<String, Object> subFormData = subFormDatas.get(i);
			
			context.put(FormRuleAPI.SUB_FORM_DATA, subFormData);
			context.put(FormRuleAPI.SUB_FORM_DATA_INDEX, i);
			
			FormRuleAPI.AddResultJSONToRespectiveResultSet(context, null);
		}
		
	}

}
