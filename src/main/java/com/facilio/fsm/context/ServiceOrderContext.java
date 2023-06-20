package com.facilio.fsm.context;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.workflow.rule.SLAPolicyContext;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.fw.validators.DateTime;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderContext extends V3Context {

    private static final long serialVersionUID = 1L;

    private String subject;
    private String description;
    private SiteContext site;
    private ClientContext client;
    private ServiceOrderMaintenanceType maintenancetype;
    private ServiceOrderCategory category;
    private ServiceOrderPriority priority;
    private SpaceContext space;
    private AssetContext asset;
    private ServiceOrderStatus status;
    private VendorContext vendor;
    private DateTime preferredstarttime;
    private DateTime preferredendtime;
    private Boolean autocreatesa;
    private DateTime responseduedate;
    private DateTime resolutionduedate;
    private DateTime resolvedtime;
    private StateFlowRuleContext stateflow;
    private ServiceOrderSourceType sourceType;
    private PeopleContext createdby;
    private DateTime createdtime;
    private PeopleContext modifiedby;
    private DateTime modifiedtime;
    private PeopleContext sysdeletedby;
    private DateTime sysdeletedtime;
    private Boolean sysdeleted;
    private DateTime actualstarttime;
    private DateTime actualendtime;
    private DateTime actualduration;
    private PeopleContext assignedto;
    private PeopleContext assignedby;
    private DateTime duedate;
    private DateTime estimatedstarttime;
    private DateTime estimatedendtime;
    private DateTime estimatedduration;
    private Long modulestate;
    private Long noOfAttachments;
    private Long noOfClosedTasks;
    private Long noOfNotes;
    private Long noOfTasks;
    private SLAPolicyContext slapolicyid;
    private Long approvalruleid;
    private Long approvalState;
    private Long parentso;
    private PlannedMaintenance pm;
    private ServiceOrderPrerequisiteStatus prerequeststatus;
    private Boolean prerequisiteapproved;
    private Boolean prerequisiteenabled;
    private Boolean qrenabled;
    private PeopleContext requestedby;
    private Long localid;

    public static enum ServiceOrderPrerequisiteStatus implements FacilioIntEnum {
        BREAKDOWN("Breakdown"),
        COMPLIANCE("Compliance"),
        CORRECTIVE("Corrective"),
        PREVENTIVE("Preventive"),
        ROUNDS("Rounds");

        private String name;

        ServiceOrderPrerequisiteStatus(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderPrerequisiteStatus valueOf(int value) {
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

    public static enum ServiceOrderMaintenanceType implements FacilioIntEnum {
        BREAKDOWN("Breakdown"),
        COMPLIANCE("Compliance"),
        CORRECTIVE("Corrective"),
        PREVENTIVE("Preventive"),
        ROUNDS("Rounds");

        private String name;

        ServiceOrderMaintenanceType(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderMaintenanceType valueOf(int value) {
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

    public static enum ServiceOrderCategory implements FacilioIntEnum {
        ELECTRICAL("Electrical"),
        ENERGY("Energy"),
        FIRE_SAFETY("Fire Safety"),
        HOUSE_KEEPING("House Keeping"),
        HVAC("HVAC"),
        PLUMBING("Plumbing");

        private String name;

        ServiceOrderCategory(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderCategory valueOf(int value) {
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

    public static enum ServiceOrderPriority implements FacilioIntEnum {
        LOW("Low"),
        MEDIUM("Medium"),
        HIGH("High");

        private String name;

        ServiceOrderPriority(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderPriority valueOf(int value) {
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

    public static enum ServiceOrderSourceType implements FacilioIntEnum {
        WEB("Web"),
        Mobile("Mobile");

        private String name;

        ServiceOrderSourceType(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderSourceType valueOf(int value) {
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

    public static enum ServiceOrderStatus implements FacilioIntEnum {
        NEW("New"),
        IN_PROGRESS("In Progress"),
        ON_HOLD("On Hold"),
        CANNOT_COMPLETE("Cannot Complete"),
        COMPLETED("Completed"),
        CLOSED("Closed"),
        CANCELLED("Cancelled");

        private String name;

        ServiceOrderStatus(String name) {
            this.name = name;
        }

        public static ServiceOrderContext.ServiceOrderStatus valueOf(int value) {
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

}
