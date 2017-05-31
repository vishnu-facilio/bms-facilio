package com.facilio.incidents.tickets;

import org.apache.commons.chain.impl.ContextBase;

public class TicketContext extends ContextBase {
	
	private long orgId = 0;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long ticketId;
	public long getTicketId() {
		return ticketId;
	}
	public void setTicketId(long ticketId) {
		this.ticketId = ticketId;
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
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public static final int OPEN = 1, PENDING = 2, RESOLVED = 3, CLOSED = 4;
	private int status = OPEN;
	public int getStatus() {
		return status;
	} 
	public void setStatus(int status) {
		this.status = status;
	}
	
	private Long agentId;
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	
	private Long failedAssetId;
	public Long getFailedAssetId() {
		return failedAssetId;
	}
	public void setFailedAssetId(Long failedAssetId) {
		this.failedAssetId = failedAssetId;
	}
	
	public static final long DEFAULT_DURATION = 3*24*60*60; //3 days in seconds
	private Long dueTime ;
	public Long getDueTime() {
		return dueTime;
	}
	public void setDueTime(Long dueTime) {
		this.dueTime = dueTime;
	}
}
