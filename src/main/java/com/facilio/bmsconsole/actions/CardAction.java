package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.cards.util.CardLayout;
import com.facilio.chain.FacilioChain;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class CardAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String cardLayout;
	
	public void setCardLayout(String cardLayout) {
		this.cardLayout = cardLayout;
	}
	
	public String getCardLayout() {
		return this.cardLayout;
	}
	
	private long cardId;
	
	public void setCardId(long cardId) {
		this.cardId = cardId;
	}
	
	public long getCardId() {
		return this.cardId;
	}
	
	private JSONObject params;
	
	public void setParams(JSONObject params) {
		this.params = params;
	}
	
	public JSONObject getParams() {
		return this.params;
	}
	
	public String getCardData() throws Exception {
		
		if (getCardLayout() != null && getParams() != null) {
			CardLayout cl = CardLayout.getCardLayout(getCardLayout());
	
			List<Object> paramsList = new ArrayList<Object>();
			paramsList.add(this.params);
			
			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(true);
			workflow.setWorkflowV2String(cl.getScript());
			
			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			chain.getContext().put(WorkflowV2Util.WORKFLOW_CONTEXT, workflow);
			chain.getContext().put(WorkflowV2Util.WORKFLOW_PARAMS, paramsList);
			
			chain.execute();
			
			setResult("parameters", getParams());
			setResult("data", workflow.getReturnValue());
		}
		else {
			setResult("message", "Mandatory params missing...");
		}
		return SUCCESS;
	}
}
