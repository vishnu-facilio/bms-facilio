package com.facilio.incidents.actions;

import java.util.Map;

import com.facilio.fw.OrgInfo;
import com.facilio.incidents.tickets.TicketApi;
import com.opensymphony.xwork2.ActionSupport;

public class TicketListAction extends ActionSupport {
	
	private Map<Long, String> ticketsId = null;
	public Map<Long, String> getTicketsId() {
		return ticketsId;
	}
	public void setTicketsId(Map<Long, String> ticketsId) {
		this.ticketsId = ticketsId;
	}
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		ticketsId = TicketApi.getTicketIdAndSubject(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		return SUCCESS;
	}
}
