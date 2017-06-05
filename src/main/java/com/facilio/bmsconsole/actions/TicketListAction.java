package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketApi;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class TicketListAction extends ActionSupport {
	
	private List<TicketContext> tickets = null;
	public List<TicketContext> getTickets() {
		return tickets;
	}
	public void setTickets(List<TicketContext> tickets) {
		this.tickets = tickets;
	}
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		tickets = TicketApi.getTicketsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return SUCCESS;
	}
}
