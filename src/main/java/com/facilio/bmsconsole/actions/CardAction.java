package com.facilio.bmsconsole.actions;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class CardAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WidgetCardContext cardContext;
	
	public void setCardContext(WidgetCardContext cardContext) {
		this.cardContext = cardContext;
	}
	
	public WidgetCardContext getCardContext() {
		return this.cardContext;
	}
	
	private Long cardId;
	
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	
	public Long getCardId() {
		return this.cardId;
	}
	
	private JSONObject cardFilters;
	
	public JSONObject getCardFilters() {
		return cardFilters;
	}
	public void setCardFilters(JSONObject cardFilters) {
		this.cardFilters = cardFilters;
	}
	
	public String getCardData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getExecuteCardWorkflowChain();
		chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
		chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, cardId);
		chain.getContext().put(FacilioConstants.ContextNames.CARD_FILTERS, cardFilters);
		
		chain.execute();
			
		setResult("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
		setResult("data", chain.getContext().get(FacilioConstants.ContextNames.CARD_RETURN_VALUE));
		if (chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE) != null) {
			setResult("state", chain.getContext().get(FacilioConstants.ContextNames.CARD_STATE));
		}
		else {
			setResult("state", ((WidgetCardContext) chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT)).getCardState());
		}
		return SUCCESS;
	}
}
