package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TicketStatusAction extends ActionSupport {
	
	public String statusList() throws Exception {
		FacilioContext context = new FacilioContext();
		
		Chain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		setStatuses((List<TicketStatusContext>) context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST));
		
		return SUCCESS;
	}
	
	private List<TicketStatusContext> statuses = null;
	public List<TicketStatusContext> getStatuses() {
		return statuses;
	}
	public void setStatuses(List<TicketStatusContext> statuses) {
		this.statuses = statuses;
	}
	
	public SetupLayout getSetup() {
		return SetupLayout.getTicketStatusListLayout();
	}
}
