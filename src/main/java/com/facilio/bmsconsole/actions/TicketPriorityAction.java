package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;

import java.util.List;

public class TicketPriorityAction extends ActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String priorityList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain statusListChain = FacilioChainFactory.getTicketPriorityListChain();
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
