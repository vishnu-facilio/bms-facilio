package com.facilio.bmsconsoleV3.context.controlActions;

import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.V3Context;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3ActionContext extends V3Context {
    private V3ControlActionContext controlAction;
    private ActionVariableTypeEnum actionVariableType;
    private Long readingFieldId;
    private Integer readingFieldDataType;
    private ActionOperatorTypeEnum scheduledActionOperatorType;
    private Object scheduleActionValue;
    private ActionOperatorTypeEnum revertActionOperatorType;
    private Object revertActionValue;
    private FacilioField readingField;

    public void setActionVariableType(Integer type) {
        if (type != null) {
            this.actionVariableType = ActionVariableTypeEnum.valueOf(type);
        }
    }

    public ActionVariableTypeEnum getActionVariableTypeEnum() {
        return actionVariableType;
    }
    public Integer getActionVariableType() {
        if(actionVariableType != null) {
            return actionVariableType.getVal();
        }
        return -1;
    }

    public static enum ActionVariableTypeEnum implements FacilioIntEnum{
        ASSET_READING("Asset Reading"),
        RELATED_READING("Related Reading");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ActionVariableTypeEnum(String name) {
            this.name = name;
        }

        public static ActionVariableTypeEnum valueOf(int index) {
            if (index >= 1 && index <= values().length) {
                return values()[index - 1];
            }
            return null;
        }

        public int getVal() {
            return ordinal() + 1;
        }
    }

    public void setScheduledActionOperatorType(Integer type) {
        if (type != null) {
            this.scheduledActionOperatorType = ActionOperatorTypeEnum.valueOf(type);
        }
    }

    public ActionOperatorTypeEnum getScheduledActionOperatorTypeEnum() {
        return scheduledActionOperatorType;
    }
    public Integer getScheduledActionOperatorType() {
        if(scheduledActionOperatorType != null) {
            return scheduledActionOperatorType.getVal();
        }
        return -1;
    }

    public void setRevertActionOperatorType(Integer type) {
        if (type != null) {
            this.revertActionOperatorType = ActionOperatorTypeEnum.valueOf(type);
        }
    }

    public ActionOperatorTypeEnum getRevertActionOperatorTypeEnum() {
        return revertActionOperatorType;
    }
    public Integer getRevertActionOperatorType() {
        if(revertActionOperatorType != null) {
            return revertActionOperatorType.getVal();
        }
        return -1;
    }

    public static enum ActionOperatorTypeEnum implements FacilioIntEnum{
        FIXED("Fixed"),
        INCREASED_BY("Increased By"),
        DECREASED_BY("Decreased By");

        @Override
        public String getValue() {
            return this.name;
        }

        private String name;

        ActionOperatorTypeEnum(String name) {
            this.name = name;
        }

        public static ActionOperatorTypeEnum valueOf(int index) {
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
