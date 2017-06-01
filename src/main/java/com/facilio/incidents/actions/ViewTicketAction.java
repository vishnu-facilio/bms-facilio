package com.facilio.incidents.actions;

import com.facilio.incidents.tickets.TicketApi;
import com.facilio.incidents.tickets.TicketContext;
import com.opensymphony.xwork2.ActionSupport;

public class ViewTicketAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		setTicket(TicketApi.getTicketDetails(getTicketId()));
		
		return SUCCESS;
	}
	
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
}
