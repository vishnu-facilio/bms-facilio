package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class TicketContext extends ModuleBaseWithCustomFields {
	
	public static final String[] DEFAULT_TICKET_FIELDS = new String[] {"ticketId", "requester", "subject", "description", "statusCode", "assignedToId", "assetId", "dueDate"};
	
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
	
	private long assetId = 0;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	public static final long DEFAULT_DURATION = 3*24*60*60; //3 days in seconds
	
	private long dueDate = 0;
	public long getDueDate() {
		return dueDate;
	}
	@TypeConversion(converter = "java.lang.String")
	public void setDueDate(String dueDate) {
		if(dueDate != null && !dueDate.isEmpty()) {
			try {
				this.dueDate = FacilioConstants.HTML5_DATE_FORMAT.parse(dueDate).getTime()/1000;
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	public void setDueDate(long dueTime) {
		this.dueDate = dueTime;
	}
}
