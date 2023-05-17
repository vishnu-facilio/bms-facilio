package com.facilio.bmsconsoleV3.context;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.V3Context;
import com.opensymphony.xwork2.conversion.annotations.TypeConversion;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class V3TicketContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private static Logger log = LogManager.getLogger(V3TicketContext.class.getName());

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
    private V3TenantContext tenant;
    public V3TenantContext getTenant() {
        return tenant;
    }
    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    private V3VendorContext vendor;
    public V3VendorContext getVendor() {
        return vendor;
    }
    public void setVendor(V3VendorContext vendor) {
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

    private V3TicketContext.SourceType sourceType;
    public Integer getSourceType() {
        if(sourceType != null) {
            return sourceType.getIntVal();
        }
        else {
            return null;
        }
    }
    public String getSourceTypeVal() {
        if(sourceType != null) {
            return sourceType.getStringVal();
        }
        return null;
    }
    public void setSourceType(Integer type) {
        if(type != null) {
            this.sourceType = V3TicketContext.SourceType.typeMap.get(type);
        }
    }
    public V3TicketContext.SourceType getSourceTypeEnum() {
        return sourceType;
    }

    public static final long DEFAULT_DURATION = 3*24*60*60*1000; //3 days in milliseconds

    private Long dueDate;
    public Long getDueDate() {
        return dueDate;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setDueDate(String dueDate) {
        if(dueDate != null && !dueDate.isEmpty()) {
            try {
                this.dueDate = Long.valueOf(dueDate);
            }
            catch(NumberFormatException ex) {

                try {
                    this.dueDate = FacilioConstants.HTML5_DATE_FORMAT.parse(dueDate).getTime();
                } catch (ParseException e) {
                    try {
                        this.dueDate = FacilioConstants.HTML5_DATE_FORMAT_1.parse(dueDate).getTime();
                    } catch (ParseException e1) {
                        log.info("Exception occurred ", e1);
                    }
                }
            }
        }
    }
    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }
    public String getDueDateString() {
        if(dueDate != null) {
            return DateTimeUtil.getZonedDateTime(dueDate).format(DateTimeUtil.READABLE_DATE_FORMAT);
        }
        return null;
    }

    private Long duration; //In Seconds
    public Long getDuration() {
        return duration;
    }
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSerialNumber() {
        return super.getLocalId();
    }
    public void setSerialNumber(Long serialNumber) {
        super.setLocalId(serialNumber);
    }

    private Long entityId;
    public Long getEntityId() {
        return entityId;
    }
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    private Integer noOfNotes ;
    public Integer getNoOfNotes() {
        return noOfNotes;
    }
    public void setNoOfNotes(Integer noOfNotes) {
        this.noOfNotes = noOfNotes;
    }

    private Integer noOfAttachments;
    public Integer getNoOfAttachments() {
        return noOfAttachments;
    }
    public void setNoOfAttachments(Integer noOfAttachments) {
        this.noOfAttachments = noOfAttachments;
    }

    private Integer noOfTasks;
    public Integer getNoOfTasks() {
        return noOfTasks;
    }
    public void setNoOfTasks(Integer noOfTasks) {
        this.noOfTasks = noOfTasks;
    }

    private Integer noOfClosedTasks;
    public Integer getNoOfClosedTasks() {
        return noOfClosedTasks;
    }
    public void setNoOfClosedTasks(Integer noOfClosedTasks) {
        this.noOfClosedTasks = noOfClosedTasks;
    }

    private Long scheduledStart;
    public Long getScheduledStart() {
        return scheduledStart;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setScheduledStart(String scheduledStart) {
        if(scheduledStart != null && !scheduledStart.isEmpty()) {
            try {
                this.scheduledStart = Long.valueOf(scheduledStart);
            }
            catch(NumberFormatException ex) {
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
    }
    public void setScheduledStart(Long scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public Long getEstimatedStart() {
        return getScheduledStart();
    }

    private Long estimatedEnd;
    public Long getEstimatedEnd() {
        return estimatedEnd;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setEstimatedEnd(String estimatedEnd) {
        if(estimatedEnd != null && !estimatedEnd.isEmpty()) {
            try {
                this.estimatedEnd = Long.valueOf(estimatedEnd);
            }
            catch(NumberFormatException ex) {
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
    }
    public void setEstimatedEnd(Long estimatedEnd) {
        this.estimatedEnd = estimatedEnd;
    }

    private Long actualWorkStart;
    public Long getActualWorkStart() {
        return actualWorkStart;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setActualWorkStart(String actualWorkStart) {
        if(actualWorkStart != null && !actualWorkStart.isEmpty()) {
            try {
                this.actualWorkStart = Long.valueOf(actualWorkStart);
            }
            catch(NumberFormatException ex) {
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
    }
    public void setActualWorkStart(Long actualWorkStart) {
        this.actualWorkStart = actualWorkStart;
    }

    private Long actualWorkEnd;
    public Long getActualWorkEnd() {
        return actualWorkEnd;
    }
    @TypeConversion(converter = "java.lang.String", value = "java.lang.String")
    public void setActualWorkEnd(String actualWorkEnd) {
        if(actualWorkEnd != null && !actualWorkEnd.isEmpty()) {
            try {
                this.actualWorkEnd = Long.valueOf(actualWorkEnd);
            }
            catch(NumberFormatException ex) {
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
    }
    public void setActualWorkEnd(Long actualWorkEnd) {
        this.actualWorkEnd = actualWorkEnd;
    }

    private Long resumedWorkStart;
    public Long getResumedWorkStart() {
        return resumedWorkStart;
    }
    public void setResumedWorkStart(Long resumedWorkStart) {
        this.resumedWorkStart = resumedWorkStart;
    }

    private Long estimatedWorkDuration;	// in seconds
    public Long getEstimatedWorkDuration() {
        return estimatedWorkDuration;
    }
    public void setEstimatedWorkDuration(Long estimatedWorkDuration) {
        this.estimatedWorkDuration = estimatedWorkDuration;
    }

    private Long actualWorkDuration;	// in seconds
    public Long getActualWorkDuration() {
        return actualWorkDuration;
    }
    public void setActualWorkDuration(Long actualWorkDuration) {
        this.actualWorkDuration = actualWorkDuration;
    }

    private Long offlineWorkStart ;
    public Long getOfflineWorkStart() {
        return offlineWorkStart;
    }
    public void setOfflineWorkStart(Long offlineWorkStart) {
        this.offlineWorkStart = offlineWorkStart;
    }

    private Long offlineWorkEnd;
    public Long getOfflineWorkEnd() {
        return offlineWorkEnd;
    }
    public void setOfflineWorkEnd(Long offlineWorkEnd) {
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

    private Map<Long, List<V3TaskContext>> tasks;
    public Map<Long, List<V3TaskContext>> getTasks() {
        return tasks;
    }
    public void setTasks(Map<Long, List<V3TaskContext>> tasks) {
        this.tasks = tasks;
    }

    private Map<String, List<V3TaskContext>> taskList;
    public Map<String, List<V3TaskContext>> getTaskList() {
        return taskList;
    }
    public void setTaskList(Map<String, List<V3TaskContext>> taskList) {
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

    public static enum SourceType {

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
        IMPORT(14, "Import"),
        PM_TEMPLATE(15, "PMTemplate")
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

        public static V3TicketContext.SourceType getType(int val) {
            return typeMap.get(val);
        }

        private static final Map<Integer, V3TicketContext.SourceType> typeMap = Collections.unmodifiableMap(initTypeMap());
        private static Map<Integer, V3TicketContext.SourceType> initTypeMap() {
            Map<Integer, V3TicketContext.SourceType> typeMap = new HashMap<>();

            for(V3TicketContext.SourceType type : values()) {
                typeMap.put(type.getIntVal(), type);
            }
            return typeMap;
        }
        public Map<Integer, V3TicketContext.SourceType> getAllTypes() {
            return typeMap;
        }
    }

    private V3ServiceRequestContext serviceRequest;
    public V3ServiceRequestContext getServiceRequest() {
        return serviceRequest;
    }
    public void setServiceRequest(V3ServiceRequestContext serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    private Boolean makeRecordOffline;
}
