package com.facilio.fsm.context;

import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServiceOrderContext extends V3Context {

    private static final long serialVersionUID = 1L;
    private ServiceOrderMaintenanceType maintenanceType;
    private ServiceOrderCategory category;
    private ServiceOrderPriority priority;

    private ServiceOrderSourceType sourceType;
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

}
