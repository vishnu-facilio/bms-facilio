package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.fw.OrgInfo;

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
	
	public String getSubject() {
		if(ticket != null) {
			return ticket.getSubject();
		}
		return null;
	}
	
	public String getDescription() {
		if(ticket != null) {
			return ticket.getDescription();
		}
		return null;
	}
	
	public TicketStatusContext getStatus() {
		if(ticket != null) {
			return ticket.getStatus();
		}
		return null;
	}
	
	public TicketPriorityContext getPriority() {
		if(ticket != null) {
			return ticket.getPriority();
		}
		return null;
	}
	
	public TicketCategoryContext getCategory() {
		if(ticket != null) {
			return ticket.getCategory();
		}
		return null;
	}
	
	public int getSourceType() {
		if(ticket != null) {
			return ticket.getSourceType();
		}
		return 0;
	}
	
	public SourceType getSourceTypeEnum() {
		if(ticket != null) {
			return ticket.getSourceTypeEnum();
		}
		return null;
	}
	
	public GroupContext getAssignmentGroup() {
		if(ticket != null) {
			return ticket.getAssignmentGroup();
		}
		return null;
	}
	
	public UserContext getAssignedTo() {
		if(ticket != null) {
			return ticket.getAssignedTo();
		}
		return null;
	}
	
	public long getScheduleId() {
		if(ticket != null) {
			return ticket.getScheduleId();
		}
		return 0;
	}
	
	public ScheduleContext getSchedule() {
		if(ticket != null) {
			return ticket.getSchedule();
		}
		return null;
	}
	
	public long getAssetId() {
		if(ticket != null) {
			return ticket.getAssetId();
		}
		return 0;
	}
	
	public BaseSpaceContext getSpace() {
		if(ticket != null) {
			return ticket.getSpace();
		}
		return null;
	}
	
	public long getCreatedDate() {
		if(ticket != null) {
			return ticket.getCreatedDate();
		}
		return 0;
	}
	
	public long getDueDate() {
		if(ticket != null) {
			return ticket.getDueDate();
		}
		return 0;
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
