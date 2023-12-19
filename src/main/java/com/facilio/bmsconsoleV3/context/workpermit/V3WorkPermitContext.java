package com.facilio.bmsconsoleV3.context.workpermit;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;

import java.util.List;

public class V3WorkPermitContext extends V3Context {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private V3TicketContext ticket;
    private String name;
    private String description;
    private User requestedBy;
    private User issuedToUser;
    private WorkType workType;
    private V3VendorContext vendor;
    private Boolean isRecurring;
    private BusinessHoursContext recurringInfo;
    private Long recurringInfoId;
    private Long expectedStartTime;
    private Long expectedEndTime;
    private V3TenantContext tenant;
    private ContactsContext vendorContact;
    private BaseSpaceContext space;
    private PermitType permitType;
    private WorkPermitTypeContext workPermitType;
    private V3PeopleContext people;
    private List<WorkPermitChecklistContext> checklist;
    private List<WorkPermitChecklistContext> workpermitchecklist;
    private List<WorkPermitChecklistContext> preRequisites;
    private List<WorkPermitChecklistContext> postRequisites;
    private Boolean isPreValidationDone;
    private Boolean isPostValidationDone;


    public List<WorkPermitChecklistContext> getWorkpermitchecklist() {
        return workpermitchecklist;
    }

    public void setWorkpermitchecklist(List<WorkPermitChecklistContext> workpermitchecklist) {
        this.workpermitchecklist = workpermitchecklist;
    }


    public Boolean getIsPreValidationDone() {
        return isPreValidationDone;
    }

    public void setIsPreValidationDone(Boolean preValidationDone) {
        isPreValidationDone = preValidationDone;
    }

    public Boolean getIsPostValidationDone() {
        return isPostValidationDone;
    }

    public void setIsPostValidationDone(Boolean postValidationDone) {
        isPostValidationDone = postValidationDone;
    }

    public Boolean isPostValidationDone() {
        if (isPostValidationDone != null) {
            return isPostValidationDone.booleanValue();
        }
        return false;
    }

    public Boolean isPreValidationDone() {
        if (isPreValidationDone != null) {
            return isPreValidationDone.booleanValue();
        }
        return false;
    }

    public List<WorkPermitChecklistContext> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<WorkPermitChecklistContext> checklist) {
        this.checklist = checklist;
    }


    public WorkPermitTypeContext getWorkPermitType() {
        return workPermitType;
    }

    public void setWorkPermitType(WorkPermitTypeContext workPermitType) {
        this.workPermitType = workPermitType;
    }

    public V3PeopleContext getPeople() {
        return people;
    }

    public void setPeople(V3PeopleContext people) {
        this.people = people;
    }


    public PermitType getPermitTypeEnum() {
        return permitType;
    }

    public Integer getPermitType() {
        if (permitType != null) {
            return permitType.getIndex();
        }
        return null;
    }

    public void setPermitType(Integer permitType) {
        if (permitType != null) {
            this.permitType = PermitType.valueOf(permitType);
        }
    }


    public User getIssuedToUser() {
        return issuedToUser;
    }

    public void setIssuedToUser(User issuedToUser) {
        this.issuedToUser = issuedToUser;
    }

    public Integer getWorkType() {
        if (workType != null) {
            return workType.getIndex();
        }
        return null;
    }

    public void setWorkType(Integer workType) {
        if (workType != null) {
            this.workType = WorkType.valueOf(workType);
        }
    }

    public WorkType getWorkTypeEnum() {
        if (workType != null) {
            return workType;
        }
        return null;
    }

    public Boolean getIsRecurring() {
        return isRecurring;
    }

    public void setIsRecurring(Boolean isRecurring) {
        this.isRecurring = isRecurring;
    }

    public Boolean isRecurring() {
        if (isRecurring != null) {
            return isRecurring.booleanValue();
        }
        return false;
    }

    public V3TicketContext getTicket() {
        return ticket;
    }

    public void setTicket(V3TicketContext ticket) {
        this.ticket = ticket;
    }

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

    public V3VendorContext getVendor() {
        return vendor;
    }

    public void setVendor(V3VendorContext vendor) {
        this.vendor = vendor;
    }

    public BusinessHoursContext getRecurringInfo() {
        return recurringInfo;
    }

    public void setRecurringInfo(BusinessHoursContext recurringInfo) {
        this.recurringInfo = recurringInfo;
    }

    public Long getRecurringInfoId() {
        return recurringInfoId;
    }

    public void setRecurringInfoId(Long recurringInfoId) {
        this.recurringInfoId = recurringInfoId;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }


    public Long getExpectedStartTime() {
        return expectedStartTime;
    }

    public void setExpectedStartTime(Long expectedStartTime) {
        this.expectedStartTime = expectedStartTime;
    }

    public Long getExpectedEndTime() {
        return expectedEndTime;
    }

    public void setExpectedEndTime(Long expectedEndTime) {
        this.expectedEndTime = expectedEndTime;
    }


    public V3TenantContext getTenant() {
        return tenant;
    }

    public void setTenant(V3TenantContext tenant) {
        this.tenant = tenant;
    }

    public ContactsContext getVendorContact() {
        return vendorContact;
    }

    public void setVendorContact(ContactsContext vendorContact) {
        this.vendorContact = vendorContact;
    }

    public BaseSpaceContext getSpace() {
        return space;
    }

    public void setSpace(BaseSpaceContext space) {
        this.space = space;
    }

    public static enum PermitType implements FacilioIntEnum {
        VENDOR("Vendor"),
        USER("User");

        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        private String name;

        PermitType(String name) {

            this.name = name;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static V3WorkPermitContext.PermitType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    // TODO @Aashiq Remove Work Type
    public static enum WorkType implements FacilioIntEnum {
        HOT_WORK_PERMIT("Hot Work Permit"), COLD_WORK_PERMIT("Cold Work Permit"), EXCAVATION_WORK_PERMIT("Excavation Work Permit"), CONFINED_SPACE_WORK_PERMIT("Confined Space Work Permit"), EARTHMOVING_EQUIPMENT_VEHICULAR_WORK_PERMIT("EarthMoving Equipment/Vehicular Work Permit");


        @Override
        public Integer getIndex() {
            return ordinal() + 1;
        }

        private String name;


        WorkType(String name) {

            this.name = name;
        }

        @Override
        public String getValue() {
            return name;
        }

        public static V3WorkPermitContext.WorkType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

    public List<WorkPermitChecklistContext> getPreRequisites() {
        return preRequisites;
    }

    public void setPreRequisites(List<WorkPermitChecklistContext> preRequisites) {
        this.preRequisites = preRequisites;
    }

    public List<WorkPermitChecklistContext> getPostRequisites() {
        return postRequisites;
    }

    public void setPostRequisites(List<WorkPermitChecklistContext> postRequisites) {
        this.postRequisites = postRequisites;
    }
}
