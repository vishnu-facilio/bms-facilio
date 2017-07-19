package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class SupportMailParseAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		TicketContext ticket = new TicketContext();
		long orgId = OrgApi.getOrgIdFromDomain(getOrgDomain());
		
		if(orgId == -1) {
			throw new IllegalArgumentException("Invalid Org Domain");
		}
		ticket.setOrgId(orgId);
		ticket.setRequester((String) getFrom().get("address"));
		ticket.setSubject(getSubject());
		ticket.setDescription(getBody());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TICKET, ticket);
		
		Command addTicket = FacilioChainFactory.getAddTicketChain();
		addTicket.execute(context);
		System.out.println("Ticket ID : "+ticket.getTicketId());
		setTicketId(ticket.getTicketId());
		return SUCCESS;
	}
	
	private JSONObject from;
	public JSONObject getFrom() {
		return from;
	}
	public void setFrom(JSONObject from) {
		this.from = from;
	}
	
	private String orgDomain;
	public String getOrgDomain() {
		return orgDomain;
	}
	public void setOrgDomain(String orgDomain) {
		this.orgDomain = orgDomain;
	}
	
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	private String body;
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	private long ticketId;
 	public long getTicketId() {
 		return ticketId;
 	}
 	public void setTicketId(long ticketId) {
 		this.ticketId = ticketId;
 	}
}
