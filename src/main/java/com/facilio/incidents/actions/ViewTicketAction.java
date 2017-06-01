package com.facilio.incidents.actions;

import java.util.Map;

import com.facilio.incidents.tickets.TicketApi;
import com.opensymphony.xwork2.ActionSupport;

public class ViewTicketAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		setTicketProps(TicketApi.getTicketDetails(getTicketId()));
		
		return SUCCESS;
	}
	
	private Map<String, String> ticketProps;
	public Map<String, String> getTicketProps() {
		return ticketProps;
	}
	public void setTicketProps(Map<String, String> ticketProps) {
		this.ticketProps = ticketProps;
	}
	
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
	}
}
