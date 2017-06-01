package com.facilio.incidents.actions;

import java.util.List;

import com.facilio.fw.OrgInfo;
import com.facilio.incidents.tickets.TicketApi;
import com.facilio.incidents.tickets.TicketContext;
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
