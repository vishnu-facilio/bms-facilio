package com.facilio.bmsconsole.context;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class TicketContext extends ModuleBaseWithCustomFields {
	
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
	
	private UserContext assignedTo;
	public UserContext getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(UserContext assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	private TicketStatusContext status;
	public TicketStatusContext getStatus() {
		return status;
	}
	public void setStatus(TicketStatusContext status) {
		this.status = status;
	}
	
	private TicketPriorityContext priority;
	public TicketPriorityContext getPriority() {
		return priority;
	}
	public void setPriority(TicketPriorityContext priority) {
		this.priority = priority;
	}
	
	private long assetId = 0;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	public static final long DEFAULT_DURATION = 3*24*60*60*1000; //3 days in milliseconds
	
	private long dueDate = 0;
	public long getDueDate() {
		return dueDate;
	}
	@TypeConversion(converter = "java.lang.String")
	public void setDueDate(String dueDate) {
		if(dueDate != null && !dueDate.isEmpty()) {
			try {
				this.dueDate = FacilioConstants.HTML5_DATE_FORMAT.parse(dueDate).getTime();
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	public void setDueDate(long dueTime) {
		this.dueDate = dueTime;
	}
	
	private long openedDate = 0;
	public long getOpenedDate() {
		return openedDate;
	}
	public void setOpenedDate(long openedDate) {
		this.openedDate = openedDate;
	}	
		
	private List<TaskContext> tasks;
	public List<TaskContext> getTasks()
	{
		return this.tasks;
	}
	
	public void setTasks(List<TaskContext> tasks)
	{
		this.tasks = tasks;
	}
	
	private List<NoteContext> notes;
	public List<NoteContext> getNotes()
	{
		return this.notes;
	}
	
	public void setNotes(List<NoteContext> notes)
	{
		this.notes = notes;
	}
	
	private List<FileContext> attachments;
	public List<FileContext> getAttachments()
	{
		return this.attachments;
	}
	
	public void setAttachments(List<FileContext> attachments)
	{
		this.attachments = attachments;
	}
	
	public List<TicketStatusContext> getStatuses() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		return (List<TicketStatusContext>) context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST);
	}
}
