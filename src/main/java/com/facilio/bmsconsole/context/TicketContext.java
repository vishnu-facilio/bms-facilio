package com.facilio.bmsconsole.context;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;

public class TicketContext extends ModuleBaseWithCustomFields {
	
	private boolean sendForApproval = false;
	public boolean getSendForApproval() {
		return sendForApproval;
	}
	public void setSendForApproval(boolean sendForApproval) {
		this.sendForApproval = sendForApproval;
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
			return -1;
		}
	}
	public String getSourceTypeVal() {
		if(sourceType != null) {
			sourceType.getStringVal();
		}
		return null;
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
	public void setSourceType(SourceType type) {
		this.sourceType = type;
	}
		
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	public static final long DEFAULT_DURATION = 3*24*60*60*1000; //3 days in milliseconds
	
	private long dueDate = -1;
	public long getDueDate() {
		return dueDate;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setDueDate(String dueDate) {
		if(dueDate != null && !dueDate.isEmpty()) {
			try {
				this.dueDate = FacilioConstants.HTML5_DATE_FORMAT.parse(dueDate).getTime();
			}
			catch (ParseException e) {
				try {
					this.dueDate = FacilioConstants.HTML5_DATE_FORMAT_1.parse(dueDate).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}
	
	private long serialNumber;
	public long getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(long serialNumber) {
		this.serialNumber = serialNumber;
	}

	private int noOfNotes;
	public int getNoOfNotes() {
		return noOfNotes;
	}
	public void setNoOfNotes(int noOfNotes) {
		this.noOfNotes = noOfNotes;
	}
	
	private int noOfAttachments;
	public int getNoOfAttachments() {
		return noOfAttachments;
	}
	public void setNoOfAttachments(int noOfAttachments) {
		this.noOfAttachments = noOfAttachments;
	}
	
	private int noOfTasks;
	public int getNoOfTasks() {
		return noOfTasks;
	}
	public void setNoOfTasks(int noOfTasks) {
		this.noOfTasks = noOfTasks;
	}
	
	private int noOfClosedTasks;
	public int getNoOfClosedTasks() {
		return noOfClosedTasks;
	}
	public void setNoOfClosedTasks(int noOfClosedTasks) {
		this.noOfClosedTasks = noOfClosedTasks;
	}

	private long scheduledStart = -1;
	public long getScheduledStart() {
		return scheduledStart;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setScheduledStart(String scheduledStart) {
 		if(scheduledStart != null && !scheduledStart.isEmpty()) {
 			try {
				this.scheduledStart = FacilioConstants.HTML5_DATE_FORMAT.parse(scheduledStart).getTime();
			} catch (ParseException e) {
				try {
					this.scheduledStart = FacilioConstants.HTML5_DATE_FORMAT_1.parse(scheduledStart).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
 		}
 	}
	public void setScheduledStart(long scheduledStart) {
		this.scheduledStart = scheduledStart;
	}
	
	private long estimatedEnd = -1;
	public long getEstimatedEnd() {
		return estimatedEnd;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setEstimatedEnd(String estimatedEnd) {
 		if(estimatedEnd != null && !estimatedEnd.isEmpty()) {
 			try {
				this.estimatedEnd = FacilioConstants.HTML5_DATE_FORMAT.parse(estimatedEnd).getTime();
			} catch (ParseException e) {
				try {
					this.estimatedEnd = FacilioConstants.HTML5_DATE_FORMAT_1.parse(estimatedEnd).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
 		}
 	}
	public void setEstimatedEnd(long estimatedEnd) {
		this.estimatedEnd = estimatedEnd;
	}
	
	private long actualWorkStart = -1;
	public long getActualWorkStart() {
		return actualWorkStart;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setActualWorkStart(String actualWorkStart) {
 		if(actualWorkStart != null && !actualWorkStart.isEmpty()) {
 			try {
				this.actualWorkStart = FacilioConstants.HTML5_DATE_FORMAT.parse(actualWorkStart).getTime();
			} catch (ParseException e) {
				try {
					this.actualWorkStart = FacilioConstants.HTML5_DATE_FORMAT_1.parse(actualWorkStart).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
 		}
 	}
	public void setActualWorkStart(long actualWorkStart) {
		this.actualWorkStart = actualWorkStart;
	}
	
	private long actualWorkEnd = -1;
	public long getActualWorkEnd() {
		return actualWorkEnd;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setActualWorkEnd(String actualWorkEnd) {
 		if(actualWorkEnd != null && !actualWorkEnd.isEmpty()) {
 			try {
				this.actualWorkEnd = FacilioConstants.HTML5_DATE_FORMAT.parse(actualWorkEnd).getTime();
			} catch (ParseException e) {
				try {
					this.actualWorkEnd = FacilioConstants.HTML5_DATE_FORMAT_1.parse(actualWorkEnd).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
 		}
 	}
	public void setActualWorkEnd(long actualWorkEnd) {
		this.actualWorkEnd = actualWorkEnd;
	}
	
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
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
	
	public static enum SourceType {
		
		WEB(1, "Web"),
		EMAIL(2, "E Mail"),
		SMS(3, "SMS"),
		ALARM(4, "Alarm")
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
