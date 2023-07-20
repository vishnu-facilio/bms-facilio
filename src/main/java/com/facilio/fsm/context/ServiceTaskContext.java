package com.facilio.fsm.context;

import com.facilio.fsm.signup.ServiceOrderPlannedToolsModule;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class ServiceTaskContext extends V3Context {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private WorkTypeContext workType;
    private List<ServiceTaskSkillsContext> skills;
    private Integer sequence;
    private Double estimatedDuration;
    private Double actualDuration;
    private Long actualStartTime;
    private Long actualEndTime;
    private ServiceTaskStatus status;
    private String remarks;
    private ServiceOrderContext serviceOrder;
    private ServiceAppointmentContext serviceAppointment;
    private Boolean isPhotoMandatory;
    private List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems;
    private List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools;
    private List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public WorkTypeContext getWorkType() {
        return workType;
    }

    public void setWorkType(WorkTypeContext workType) {
        this.workType = workType;
    }

    public List<ServiceTaskSkillsContext> getSkills() {
        return skills;
    }

    public void setSkills(List<ServiceTaskSkillsContext> skills) {
        this.skills = skills;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public Double getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(Double estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public Double getActualDuration() {
        return actualDuration;
    }

    public void setActualDuration(Double actualDuration) {
        this.actualDuration = actualDuration;
    }

    public Long getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Long actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Long getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Long actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public Integer getStatus() {
        if (status != null) {
            return status.getIndex();
        }
        return null;
    }

    public void setStatus(Integer status) {
        if(status != null) {
            this.status = ServiceTaskStatus.valueOf(status);
        }
    }
    public ServiceTaskStatus getStatusEnum(){
        return status;
    }
    public static enum ServiceTaskStatus implements FacilioIntEnum {
        NEW("New"),
        SCHEDULED("Scheduled"),
        DISPATCHED("Dispatched"),
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        COMPLETED("Completed"),
        REOPENED("Reopened"),
        CANCELLED("Cancelled");

        private String name;

        ServiceTaskStatus(String name) {
            this.name = name;
        }

        public static ServiceTaskStatus valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public ServiceOrderContext getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrderContext serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public ServiceAppointmentContext getServiceAppointment() {
        return serviceAppointment;
    }

    public void setServiceAppointment(ServiceAppointmentContext serviceAppointment) {
        this.serviceAppointment = serviceAppointment;
    }

    public Boolean getIsPhotoMandatory() {
        if(isPhotoMandatory!=null){
            return isPhotoMandatory.booleanValue();
        }
        return false;
    }

    public void setIsPhotoMandatory(Boolean photoMandatory) {
        isPhotoMandatory = photoMandatory;
    }

    public List<ServiceOrderPlannedItemsContext> getServiceOrderPlannedItems() {
        return serviceOrderPlannedItems;
    }

    public void setServiceOrderPlannedItems(List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems) {
        this.serviceOrderPlannedItems = serviceOrderPlannedItems;
    }

    public List<ServiceOrderPlannedToolsContext> getServiceOrderPlannedTools() {
        return serviceOrderPlannedTools;
    }

    public void setServiceOrderPlannedTools(List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools) {
        this.serviceOrderPlannedTools = serviceOrderPlannedTools;
    }

    public List<ServiceOrderPlannedServicesContext> getServiceOrderPlannedServices() {
        return serviceOrderPlannedServices;
    }

    public void setServiceOrderPlannedServices(List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices) {
        this.serviceOrderPlannedServices = serviceOrderPlannedServices;
    }
}
