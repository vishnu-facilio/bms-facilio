package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.TicketApi;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class ViewTicketAction extends ActionSupport {
	
	
	@Override
	public String execute() throws Exception {
		
		TicketContext tc = TicketApi.getTicketDetails(getTicketId(), OrgInfo.getCurrentOrgInfo().getOrgid());
		
		if( tc != null) {
			ticket = new HashMap<>();
			for(Object key : tc.keySet()) {
				if(!key.equals("connection")) {
					ticket.put((String) key, tc.get(key));
				}
			}
		}
		
		return SUCCESS;
	}
	
	private Map<String, Object> ticket;
	public Map<String, Object> getTicket() {
		return ticket;
	}
	public void setTicket(Map<String, Object> ticket) {
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
