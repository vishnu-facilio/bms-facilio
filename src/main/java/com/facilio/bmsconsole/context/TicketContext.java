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
			return 0;
		}
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
	public SourceType getSourceTypeEnum() {
		return sourceType;
	}
		
	private long assetId = -1;
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
	
	private long createdDate = -1;
	public long getCreatedDate() {
		return createdDate;
	}
	@TypeConversion(converter = "java.lang.String", value = "java.lang.String")
	public void setCreatedDate(String createdDate) {
		if(createdDate != null && !createdDate.isEmpty()) {
			try {
				this.createdDate = FacilioConstants.HTML5_DATE_FORMAT.parse(createdDate).getTime();
			}
			catch (ParseException e) {
				try {
					this.createdDate = FacilioConstants.HTML5_DATE_FORMAT_1.parse(createdDate).getTime();
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	public void setCreatedDate(long createdDate) {
		this.createdDate = createdDate;
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
	
	private BaseSpaceContext space;
	public BaseSpaceContext getSpace() {
		return space;
	}
	public void setSpace(BaseSpaceContext space) {
		this.space = space;
	}
	
	private ScheduleContext schedule;
	public ScheduleContext getSchedule() {
		return schedule;
	}
	public void setSchedule(ScheduleContext schedule) {
		this.schedule = schedule;
	}
	
	private long scheduleId = -1;
	public long getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(long scheduleId) {
		this.scheduleId = scheduleId;
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
