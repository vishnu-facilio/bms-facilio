package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class WorkOrderContext extends ModuleBaseWithCustomFields {
	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
	}
	
	private RequesterContext requester;
	public RequesterContext getRequester() {
		return requester;
	}
	public void setRequester(RequesterContext requester) {
		this.requester = requester;
	}
	
	private long createdTime = -1;
	public long getCreatedTime() {
		return createdTime;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedTime(String createdTime) {
		if(createdTime != null && !createdTime.isEmpty()) {
			try {
				this.createdTime = FacilioConstants.HTML5_DATE_FORMAT.parse(createdTime).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdTime = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdTime).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks() {
		return this.tasks;
	}
	public void setTasks(List<TaskContext> tasks) {
		this.tasks = tasks;
	}
	
	private List<NoteContext> notes;
	public List<NoteContext> getNotes() {
		return this.notes;
	}
	public void setNotes(List<NoteContext> notes){
		this.notes = notes;
	}
	
	private List<AttachmentContext> attachments;
	public void setAttachments(List<AttachmentContext> attachments) {
		this.attachments = attachments;
	}
	public List<AttachmentContext> getAttachments() {
		return this.attachments;
	}
	
	public String getUrl() {
		return "http://"+OrgInfo.getCurrentOrgInfo().getOrgDomain()+".facilstack.com:8080/bms/app/index#workorder/"+getId();
	}
}
