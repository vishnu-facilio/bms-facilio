package com.facilio.incidents.actions;

import org.apache.commons.chain.Command;

import com.facilio.fw.OrgInfo;
import com.facilio.incidents.IncidentCommandFactory;
import com.facilio.incidents.tickets.TicketContext;
import com.opensymphony.xwork2.ActionSupport;

public class AddTicketAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		Command addTicket = IncidentCommandFactory.getAddIncidentCommand();
		TicketContext context = new TicketContext();
		System.out.println(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.setRequestor(getRequestor());
		context.setSubject(getSubject());
		context.setDescription(getDescription());
		
		if(getAgent() != -1)  {
			context.setAgentId(getAgent());
		}
		
		if(getAsset() != -1) {
			context.setFailedAssetId(getAsset());
		}
		
		if(addTicket.execute(context)) {
			System.out.println("Ticket ID : "+context.getTicketId());
			setTicketId(context.getTicketId());
			return SUCCESS;
		}
		else {
			return ERROR;
		}
	}
	
	
	private String requestor;
	public String getRequestor() {
		return requestor;
	}
	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}
	
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private long agent;
	public long getAgent() {
		return agent;
	}
 	public void setAgent(long agent) {
 		this.agent = agent;
 	}
 	
 	private long asset;
 	public long getAsset() {
 		return asset;
 	}
 	public void setAsset(long asset) {
 		this.asset = asset;
 	}
  	
 	private String description;
 	public String getDescription() {
 		return description;
 	}
 	public void setDescription(String description) {
 		this.description = description;
 	}
 	
 	private int status;
 	public int getStatus() {
 		return status;
 	}
 	public void setStatus(int status) {
 		this.status = status;
 	}
 	
 	private long ticketId;
 	public long getTicketId() {
 		return ticketId;
 	}
 	public void setTicketId(long ticketId) {
 		this.ticketId = ticketId;
 	}
}
