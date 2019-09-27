package com.facilio.conditionalformatting.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.conditionalformatting.context.ConditionalFormattingContext;

public class ConditionalFormattingAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<Map<String,Object>> data;
	
	List<ConditionalFormattingContext> conditionalFormatings;
	
	public String getConditionalFormattingResult() throws Exception {

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
		setResult("result", result);
		return SUCCESS;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public void setData(List<Map<String, Object>> data) {
		this.data = data;
	}

	public List<ConditionalFormattingContext> getConditionalFormatings() {
		return conditionalFormatings;
	}

	public void setConditionalFormatings(List<ConditionalFormattingContext> conditionalFormatings) {
		this.conditionalFormatings = conditionalFormatings;
	}
}
