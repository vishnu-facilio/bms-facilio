package com.facilio.bmsconsoleV3.context.controlActions;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.facilio.bmsconsoleV3.context.calendar.V3CalendarContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class V3ControlActionTemplateContext extends V3ControlActionContext{
    private String subject;
    private V3CalendarContext calendar;
    private ControlActionTemplateStatus controlActionTemplateStatus;
    private ControlActionTemplateType controlActionTemplateType;
    private Boolean isEnableRevert;
    public void setControlActionTemplateStatus(Integer type) {
        if (type != null) {
            this.controlActionTemplateStatus = ControlActionTemplateStatus.valueOf(type);
        }
    }

    public ControlActionTemplateStatus getControlActionTemplateStatusEnum() {
        return controlActionTemplateStatus;
    }
    public Integer getControlActionTemplateStatus() {
        if(controlActionTemplateStatus != null) {
            return controlActionTemplateStatus.getVal();
        }
        return -1;
    }

    public static enum ControlActionTemplateStatus implements FacilioIntEnum {
        IN_ACTIVE("In Active"),
        ACTIVE("Active");
        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionTemplateStatus(String name) {
            this.name = name;
        }

        public static ControlActionTemplateStatus valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }
    public void setControlActionTemplateType(Integer type) {
        if (type != null) {
            this.controlActionTemplateType = ControlActionTemplateType.valueOf(type);
        }
    }

    public  ControlActionTemplateType getControlActionTemplateTypeEnum() {
        return controlActionTemplateType;
    }
    public Integer getControlActionTemplateType() {
        if(controlActionTemplateType != null) {
            return controlActionTemplateType.getVal();
        }
        return -1;
    }
    public static enum ControlActionTemplateType implements FacilioIntEnum{
        NOT_SCHEDULED("Un Scheduled"),
        SCHEDULED("Scheduled"),
        FLAGGED_EVENT("Flagged Event")
        ;
        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ControlActionTemplateType(String name) {
            this.name = name;
        }

        public static ControlActionTemplateType valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }

}
