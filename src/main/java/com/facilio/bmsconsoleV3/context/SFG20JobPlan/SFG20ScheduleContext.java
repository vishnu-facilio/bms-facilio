package com.facilio.bmsconsoleV3.context.SFG20JobPlan;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class SFG20ScheduleContext implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long scheduleId;
    private Long jobPlanId;
    private Long fileId;
    private Long syncId;
    private String code;
    private String title;
    private String version;
    private String oldVersion;
    private String ScheduleGroupPaths;

    public Integer getFlowType() {
        if (flowType != null) {
            return flowType.getIndex();
        }
        return null;
    }

    public void setFlowType(Integer flowType) {
        if(flowType != null) {
            this.flowType = Type.valueOf(flowType);
        }
    }
    public Type getTypeEnum() {
        return flowType;
    }

    public Integer getType() {
        if (flowType != null) {
            return flowType.getIndex();
        }
        return null;
    }

    private Type flowType;

    public static enum Type implements FacilioIntEnum {
        Create("Create"),
        Update("Update"),
        Revised("Revised");

        private String name;

        Type(String name) {
            this.name = name;
        }

        public static Type valueOf(int value) {
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
