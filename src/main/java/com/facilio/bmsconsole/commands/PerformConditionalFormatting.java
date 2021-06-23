package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.conditionalformatting.context.ConditionalFormattingContext;
import com.facilio.constants.FacilioConstants;

public class PerformConditionalFormatting extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<Map<String,Object>> data  = (List<Map<String,Object>>) context.get(FacilioConstants.ContextNames.DATA);
		
		List<ConditionalFormattingContext> conditionalFormatings = (List<ConditionalFormattingContext>) context.get(FacilioConstants.ContextNames.CONDITIONAL_FORMATTINGS);
		
		int rowNo = 1;
		JSONObject result = new JSONObject();
		
		List<String> alreadyFilledCollumns = null;
		for(Map<String,Object> dataMap :data) {
			
			alreadyFilledCollumns = new ArrayList<String>();
			JSONObject resJson = null;
			
			for(ConditionalFormattingContext conditionalFormating :conditionalFormatings) {
				
				if(alreadyFilledCollumns.containsAll(conditionalFormating.getApplyToFields())) {
					continue;
				}
				boolean criteriaRes = conditionalFormating.evaluateCriteria(dataMap, null);
				boolean workflowRes = conditionalFormating.evaluateWorkflowExpression(dataMap, null);
				
				if(criteriaRes && workflowRes) {
					resJson = resJson == null ? new JSONObject() : resJson; 
					for(String applyto: conditionalFormating.getApplyToFields()) {
						if(!resJson.containsKey(applyto)) {
							resJson.put(applyto, conditionalFormating.getActions());
							alreadyFilledCollumns.add(applyto);
						}
					}
				}
			}
			if(resJson != null) {
				result.put(rowNo+"", resJson);
			}
			rowNo++;
		}
		
		context.put(FacilioConstants.ContextNames.CONDITIONAL_FORMATTING_RESULT, result);
		
		return false;
	}

}
