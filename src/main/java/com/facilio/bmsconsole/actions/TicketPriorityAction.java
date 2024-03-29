package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TicketPriorityAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String priorityList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		FacilioChain statusListChain = FacilioChainFactory.getTicketPriorityListChain();
		statusListChain.execute(context);
		
		setPriorities((List<TicketPriorityContext>) context.get(FacilioConstants.ContextNames.TICKET_PRIORITY_LIST));
		
		return SUCCESS;
	}
	
	private List<TicketPriorityContext> priorities = null;
	public List<TicketPriorityContext> getPriorities() {
		return priorities;
	}
	public void setPriorities(List<TicketPriorityContext> priorities) {
		this.priorities = priorities;
	}
	
	public SetupLayout getSetup() {
		return SetupLayout.getTicketCategoryListLayout();
	}
}
