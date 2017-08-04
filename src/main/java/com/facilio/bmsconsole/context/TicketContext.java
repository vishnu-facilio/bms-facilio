package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.amazonaws.services.rds.model.SourceType;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TicketStatusContext.StatusType;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
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
	
	private GroupContext assignmentGroup;
	public GroupContext getAssignmentGroup() {
		return assignmentGroup;
	}
	public void setAssignmentGroup(GroupContext assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
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
	
	private TicketCategoryContext category;
	public TicketCategoryContext getCategory() {
		return category;
	}
	public void setCategory(TicketCategoryContext category) {
		this.category = category;
	}
	
	private SourceType sourceType;
	public int getSourceType() {
		if(sourceType != null) {
			return sourceType.getIntVal();
		}
		else {
			return 0;
		}
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
	public SourceType getSourceTypeEnum() {
		return sourceType;
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
	
	private long createdDate = 0;
	public long getCreatedDate() {
		return createdDate;
	}
	@TypeConversion(converter = "java.lang.String")
	public void setCreatedDate(String createdDate) {
		if(createdDate != null && !createdDate.isEmpty()) {
			try {
				this.createdDate = FacilioConstants.HTML5_DATE_FORMAT.parse(createdDate).getTime();
			}
			catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
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
	
	public static List<TicketStatusContext> getStatuses() throws Exception 
	{
		FacilioContext context = new FacilioContext();
		Chain statusListChain = FacilioChainFactory.getTicketStatusListChain();
		statusListChain.execute(context);
		
		return (List<TicketStatusContext>) context.get(FacilioConstants.ContextNames.TICKET_STATUS_LIST);
	}
	
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}
	
	public static enum SourceType {
		
		WEB(1, "Web"),
		EMAIL(2, "E Mail"),
		SMS(3, "SMS")
		;
		
		private int intVal;
		private String strVal;
		
		private SourceType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		public String getStringVal() {
			return strVal;
		}
		
		private static final Map<Integer, SourceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SourceType> initTypeMap() {
			Map<Integer, SourceType> typeMap = new HashMap<>();
			
			for(SourceType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public Map<Integer, SourceType> getAllTypes() {
			return typeMap;
		}
	}
}
