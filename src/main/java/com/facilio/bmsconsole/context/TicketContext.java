package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.impl.ContextBase;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class TicketContext extends ModuleBaseWithCustomFields {
	
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
	
	private long assignedToId = 0;
	public long getAssignedToId() {
		return assignedToId;
	}
	public void setAssignedToId(long assignedToId) {
		this.assignedToId = assignedToId;
	}
	
	private long failedAssetId = 0;
	public long getFailedAssetId() {
		return failedAssetId;
	}
	public void setFailedAssetId(long failedAssetId) {
		this.failedAssetId = failedAssetId;
	}
	
	public static final long DEFAULT_DURATION = 3*24*60*60; //3 days in seconds
	private long dueTime = 0;
	public long getDueTime() {
		return dueTime;
	}
	public void setDueTime(String dueTime) {
		if(dueTime != null && !dueTime.isEmpty()) {
			try {
				this.dueTime = FacilioConstants.HTML5_DATE_FORMAT.parse(dueTime).getTime()/1000;
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	public void setDueTimeFromTimestamp(long dueTime) {
		this.dueTime = dueTime;
	}
}
