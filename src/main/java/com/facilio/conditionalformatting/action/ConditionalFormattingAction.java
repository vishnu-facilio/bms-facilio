package com.facilio.conditionalformatting.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.conditionalformatting.context.ConditionalFormattingContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.inject.Context;

public class ConditionalFormattingAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<Map<String,Object>> data;
	
	List<ConditionalFormattingContext> conditionalFormatings;
	
	public String getConditionalFormattingResult() throws Exception {

		FacilioChain chain = ReadOnlyChainFactory.performConditionalFormattings();
		
		FacilioContext context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.CONDITIONAL_FORMATTINGS, conditionalFormatings);
		context.put(FacilioConstants.ContextNames.DATA, data);
		
		chain.execute();
		setResult("result", context.get(FacilioConstants.ContextNames.CONDITIONAL_FORMATTING_RESULT));
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
