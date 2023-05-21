package com.facilio.flows.context;

import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class FlowContext implements Serializable {

    private long id;
    private String name;
    private String description;
    private long moduleId = -1;
    private long createdBy = -1;
    private long modifiedBy = -1;

    private long sysCreatedTime = -1;
    private long sysModifiedTime = -1;

    private List<ParameterContext> parameters;

    private FlowType flowType;
    public int getFlowType() {
        if (flowType == null) {
            return -1;
        }
        return flowType.getIndex();
    }

    public void setFlowType(FlowType flowType) {
        this.flowType = flowType;
    }

    public FlowType getFlowTypeEnum() {
        return flowType;
    }

    public void setFlowType(int flowType) {
        this.flowType = FlowType.valueOf(flowType);
    }

    public enum FlowType implements FacilioIntEnum {

        GENERIC,
        MODULE
        ;

        @Override
        public Integer getIndex() {
            return FacilioIntEnum.super.getIndex();
        }

        @Override
        public String getValue() {
            return FacilioIntEnum.super.getValue();
        }

        public static FlowType valueOf(int value){
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }

}
