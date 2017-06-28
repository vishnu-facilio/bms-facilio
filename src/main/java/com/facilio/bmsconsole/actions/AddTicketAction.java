package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class AddTicketAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		Command addTicket = FacilioChainFactory.getAddIncidentChain();
		TicketContext context = new TicketContext();
		System.out.println(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		context.setRequester(getRequestor());
		context.setSubject(getSubject());
		context.setDescription(getDescription());
		
		if(getAgent() != -1)  {
			context.setAgentId(getAgent());
		}
		
		if(getAsset() != -1) {
			context.setFailedAssetId(getAsset());
		}
		
		for(Map.Entry<String, String> entry : customFields.entrySet()) {
			context.put(entry.getKey(), entry.getValue());
		}
		
//		try {
			addTicket.execute(context);
			System.out.println("Ticket ID : "+context.getTicketId());
			setTicketId(context.getTicketId());
			return SUCCESS;
//		}
//		catch(Exception e) {
//			return ERROR;
//		}
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
 	
 	private Map<String, String> customFields = new HashMap<>();
 	public Map<String, String> getCustomFields() {
 		return customFields;
 	}
 	public String getCustomFields(String key) {
 		return customFields.get(key);
 	}
 	public void setCustomFields(String key, String value) {
 		customFields.put(key, value);
 	}
 	
 	private long ticketId;
 	public long getTicketId() {
 		return ticketId;
 	}
 	public void setTicketId(long ticketId) {
 		this.ticketId = ticketId;
 	}
}
