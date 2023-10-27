package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Iterator;

import com.facilio.analytics.v2.context.V2CardContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ApplyConditionalFormattingForCard extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(ApplyConditionalFormattingForCard.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		Long cardId= null;
		JSONObject conditional_formatting = null;
		JSONObject cardState = null;
		String cardLayout=null;
		Object cardValue=null;


		if(context.containsKey("v2"))
		{
			V2CardContext v2cardContext = (V2CardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
			conditional_formatting = v2cardContext.getConditionalFormatting();
			cardState = v2cardContext.getCardState();
			cardLayout = v2cardContext.getCardLayout();
			cardId = v2cardContext.getCardId();
			cardValue = v2cardContext.getCardParams().getResult().get("value");
		}
		else
		{
			WidgetCardContext cardContext = (WidgetCardContext) context.get(FacilioConstants.ContextNames.CARD_CONTEXT);
			conditional_formatting = cardContext.getConditionalFormatting();
			cardState = cardContext.getCardState();
			cardLayout = cardContext.getCardLayout();
			cardId = cardContext.getId();
			cardValue = context.get(FacilioConstants.ContextNames.CARD_RETURN_VALUE);
		}
		try {
			if (conditional_formatting != null && conditional_formatting.containsKey("conditionalFormatting"))
			{
				HashMap<String, Object> variables = new HashMap<>();

				if (cardValue != null) {
					if (cardValue instanceof Map) {
						Map<String, Object> data = (Map<String, Object>) cardValue;
						Iterator itr = data.keySet().iterator();
						while (itr.hasNext()) {
							String key = (String) itr.next();
							Object value = data.get(key);
							if (value != null && value instanceof Map) {
								Map<String, Object> valueData = (Map<String, Object>) value;
								if(valueData.get("actualValue") != null) {
									variables.put(key, valueData.get("actualValue"));
								} else {
									variables.put(key, valueData.get("value"));
								}
							}
							else {
								variables.put(key, value);
							}
						}
					}
				}

				List<Object> paramsList = new ArrayList<Object>();
				paramsList.add(variables);
				paramsList.add(conditional_formatting);
				paramsList.add(cardState);

				FacilioChain chain = TransactionChainFactory.getExecuteDefaultWorkflowChain();
				chain.getContext().put(WorkflowV2Util.DEFAULT_WORKFLOW_ID, 10);
				chain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);

				chain.execute();

				WorkflowContext workflow = (WorkflowContext) chain.getContext().get(WorkflowV2Util.WORKFLOW_CONTEXT);

				Map formatState = (Map) workflow.getReturnValue();

				context.put(FacilioConstants.ContextNames.CARD_STATE, mergeCardState(cardState, formatState));
			}
		}
		catch (Exception e) {
			LOGGER.log(Level.WARNING, "Exception when applying conditional formatting for card. layout: "+ cardLayout + " id: "+cardId, e);
		}
		return false;
	}

	private JSONObject mergeCardState(JSONObject cardState, Map formatState) throws Exception {
		if (cardState == null || formatState == null) {
			return null;
		}

		JSONObject cloned = (JSONObject) new JSONParser().parse(cardState.toJSONString());
		HashMap styles = (HashMap) cloned.get("styles");
		HashMap formatStyles = (HashMap) formatState.get("styles");

		if (styles != null && formatStyles != null) {
			Iterator<String> itr = formatStyles.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				Object value = formatStyles.get(key);

				styles.put(key, value);
			}
		}
		return cloned;
	}
}
