package com.facilio.bmsconsole.context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.context.failurecode.V3FailureClassContext;
import com.facilio.classification.context.ClassificationContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(TicketContext.class.getName());
	
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
	
	private Group assignmentGroup;
	public Group getAssignmentGroup() {
		return assignmentGroup;
	}
	public void setAssignmentGroup(Group assignmentGroup) {
		this.assignmentGroup = assignmentGroup;
	}
	private  TenantContext tenant;
	public TenantContext getTenant() {
		return tenant;
	}
	public void setTenant(TenantContext tenant) {
		this.tenant = tenant;
	}
	
	private  VendorContext vendor;
	public VendorContext getVendor() {
		return vendor;
	}
	public void setVendor(VendorContext vendor) {
		this.vendor = vendor;
	}
	
	
	private User assignedTo;
	public User getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(User assignedTo) {
		this.assignedTo = assignedTo;
	}
	
	private User createdBy;
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	private User assignedBy;
	public User getAssignedBy() {
		return assignedBy;
	}
	public void setAssignedBy(User assignedBy) {
		this.assignedBy = assignedBy;
	}

	private FacilioStatus status;
	public FacilioStatus getStatus() {
		return status;
	}
	public void setStatus(FacilioStatus status) {
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
	
	private TicketTypeContext type;
	public TicketTypeContext getType() {
		return type;
	}
	public void setType(TicketTypeContext type) {
		this.type = type;
	}

	private SourceType sourceType;
	public int getSourceType() {
		if(sourceType != null) {
			return sourceType.getIndex();
		}
		else {
			return -1;
		}
	}
	public String getSourceTypeVal() {
		if(sourceType != null) {
			return sourceType.getValue();
		}
		return null;
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.typeMap.get(type);
	}
	public void setSourceType(SourceType type) {
		this.sourceType = type;
	}
	public SourceType getSourceTypeEnum() {
		return sourceType;
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
					log.info("Exception occurred ", e1);
				}
			}
		}
	}
	public void setDueDate(long dueDate) {
		this.dueDate = dueDate;
	}
	public String getDueDateString() {
		if(dueDate != -1) {
			return DateTimeUtil.getZonedDateTime(dueDate).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}
	
	private long duration = -1; //In Seconds
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public long getSerialNumber() {
		return super.getLocalId();
	}
	public void setSerialNumber(long serialNumber) {
		super.setLocalId(serialNumber);
	}
	
	private long entityId = -1;
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	private int noOfNotes = -1;
	public int getNoOfNotes() {
		return noOfNotes;
	}
	public void setNoOfNotes(int noOfNotes) {
		this.noOfNotes = noOfNotes;
	}
	
	private int noOfAttachments = -1;
	public int getNoOfAttachments() {
		return noOfAttachments;
	}
	public void setNoOfAttachments(int noOfAttachments) {
		this.noOfAttachments = noOfAttachments;
	}
	
	private int noOfTasks = -1;
	public int getNoOfTasks() {
		return noOfTasks;
	}
	public void setNoOfTasks(int noOfTasks) {
		this.noOfTasks = noOfTasks;
	}
	
	private int noOfClosedTasks = -1;
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
					log.info("Exception occurred ", e1);
				}
			}
 		}
 	}
	public void setScheduledStart(long scheduledStart) {
		this.scheduledStart = scheduledStart;
	}
	public String getScheduledStartString() {
		if(scheduledStart != -1) {
			return DateTimeUtil.getZonedDateTime(scheduledStart).format(DateTimeUtil.READABLE_DATE_FORMAT);
		}
		return null;
	}
	
	public long getEstimatedStart() {
		return getScheduledStart();
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
					log.info("Exception occurred ", e1);
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
					log.info("Exception occurred ", e1);
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
					log.info("Exception occurred ", e1);
				}
			}
 		}
 	}
	public void setActualWorkEnd(long actualWorkEnd) {
		this.actualWorkEnd = actualWorkEnd;
	}
	
	private long resumedWorkStart = -1;
	public long getResumedWorkStart() {
		return resumedWorkStart;
	}
	public void setResumedWorkStart(long resumedWorkStart) {
		this.resumedWorkStart = resumedWorkStart;
	}

	private long estimatedWorkDuration = -1;	// in seconds
	public long getEstimatedWorkDuration() {
		return estimatedWorkDuration;
	}
	public void setEstimatedWorkDuration(long estimatedWorkDuration) {
		this.estimatedWorkDuration = estimatedWorkDuration;
	}
	
	private long actualWorkDuration = -1;	// in seconds
	public long getActualWorkDuration() {
		return actualWorkDuration;
	}
	public void setActualWorkDuration(long actualWorkDuration) {
		this.actualWorkDuration = actualWorkDuration;
	}
	
	private long offlineWorkStart = -1;
	public long getOfflineWorkStart() {
		return offlineWorkStart;
	}
	public void setOfflineWorkStart(long offlineWorkStart) {
		this.offlineWorkStart = offlineWorkStart;
	}
	
	private long offlineWorkEnd = -1;
	public long getOfflineWorkEnd() {
		return offlineWorkEnd;
	}
	public void setOfflineWorkEnd(long offlineWorkEnd) {
		this.offlineWorkEnd = offlineWorkEnd;
	}
	
	private ResourceContext resource;
	public ResourceContext getResource() {
		return resource;
	}
	public void setResource(ResourceContext resource) {
		this.resource = resource;
	}
	
	private AssetContext asset;
	public AssetContext getAsset() {
		return asset;
	}
	public void setAsset(AssetContext asset) {
		this.asset = asset;
	}
	
	private SpaceContext space;
	public SpaceContext getSpace() {
		return space;
	}
	public void setSpace(SpaceContext space) {
		this.space = space;
	}

	private Map<Long, TaskSectionContext> taskSections;
	public Map<Long, TaskSectionContext> getTaskSections() {
		return taskSections;
	}
	public void setTaskSections(Map<Long, TaskSectionContext> taskSections) {
		this.taskSections = taskSections;
	}

	private Map<Long, List<TaskContext>> tasks;
	public Map<Long, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<Long, List<TaskContext>> tasks) {
		this.tasks = tasks;
	}
	
	private Map<String, List<TaskContext>> taskList;
	public Map<String, List<TaskContext>> getTaskList() {
		return taskList;
	}
	public void setTaskList(Map<String, List<TaskContext>> taskList) {
		this.taskList = taskList;
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
	
	public static enum SourceType implements FacilioIntEnum {
		
		WEB_ORDER(1, "Web Work Order"),
		EMAIL_REQUEST(2, "E Mail Request"),
		SMS_REQUEST(3, "SMS Request"),
		ALARM(4, "Alarm"),
		PREVENTIVE_MAINTENANCE(5, "Planned"),
		THRESHOLD_ALARM(6, "Threshold Alarm"),
		WEB_REQUEST(7, "Web Request"),
		ANOMALY_ALARM(9, "Anomaly Alarm"),
		SERVICE_PORTAL_REQUEST(10, "Service Portal Request"),
		WORKFLOW_RULE(11, "Workflow Rule"),
		ML_ALARM(12, "Prediction Alarm"),
		TASK_DEVIATION(13, "Task Deviation"),
		IMPORT(14, "Import")
		;
		
		private int intVal;
		private String strVal;
		
		private SourceType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}

		@Override
		public Integer getIndex() {
			return intVal;
		}

		public String getValue() {
			return strVal;
		}
		
		public static SourceType getType(int val) {
			return typeMap.get(val);
		}
		
		private static final Map<Integer, SourceType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, SourceType> initTypeMap() {
			Map<Integer, SourceType> typeMap = new HashMap<>();
			
			for(SourceType type : values()) {
				typeMap.put(type.getIndex(), type);
			}
			return typeMap;
		}
		public Map<Integer, SourceType> getAllTypes() {
			return typeMap;
		}
	}
	
	private long siteId = -1;
	
	public long getSiteId() {
		return this.siteId;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private V3ServiceRequestContext serviceRequest;
	public V3ServiceRequestContext getServiceRequest() {
		return serviceRequest;
	}
	public void setServiceRequest(V3ServiceRequestContext serviceRequest) {
		this.serviceRequest = serviceRequest;
	}

	public V3FailureClassContext getFailureClass() {
		return failureClass;
	}

	public void setFailureClass(V3FailureClassContext failureClass) {
		this.failureClass = failureClass;
	}

	private V3FailureClassContext failureClass;
	@Getter
	@Setter
	private ClassificationContext classification;
}
