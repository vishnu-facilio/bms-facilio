package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

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
	
	public String getCardData() throws Exception {
		
		FacilioChain chain = TransactionChainFactory.getExecuteCardWorkflowChain();
		chain.getContext().put(FacilioConstants.ContextNames.CARD_CONTEXT, cardContext);
		chain.getContext().put(FacilioConstants.ContextNames.CARD_ID, cardId);
		
		chain.execute();
			
		WorkflowContext workflow = (WorkflowContext) chain.getContext().get(WorkflowV2Util.WORKFLOW_CONTEXT);
		
		setResult("cardContext", chain.getContext().get(FacilioConstants.ContextNames.CARD_CONTEXT));
		setResult("data", workflow.getReturnValue());
		return SUCCESS;
	}
}
