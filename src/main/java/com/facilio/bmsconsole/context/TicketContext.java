package com.facilio.bmsconsole.context;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.bmsconsole.commands.FacilioContext;

public class TicketContext extends FacilioContext {
	
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
	
	private String requester;
	public String getRequester() {
		return requester;
	}
	public void setRequester(String requester) {
		this.requester = requester;
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
	private static final Map<Integer, String> ALL_STATUS = new HashMap<>();
	
	static {
		ALL_STATUS.put(OPEN, "Open");
		ALL_STATUS.put(PENDING, "Pending");
		ALL_STATUS.put(RESOLVED, "Resolved");
		ALL_STATUS.put(CLOSED, "Closed");
	}
	public static  Map<Integer, String> getAllStatus() {
		return ALL_STATUS;
	}
	
	private int statusCode = OPEN;
	public int getStatusCode() {
		return statusCode;
	} 
	public void setStatusCode(int status) {
		this.statusCode = status;
	}
	public String getStatus() {
		return ALL_STATUS.get(statusCode);
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
