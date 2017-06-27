package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.customfields.CFUtil;
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
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		moduleName = CFUtil.getModuleName("Tickets_Objects", orgId);
		tickets = TicketApi.getTicketsOfOrg(orgId);
		
		return SUCCESS;
	}
}
