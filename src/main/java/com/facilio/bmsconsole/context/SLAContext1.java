package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.workflow.rule.SLAWorkflowRuleContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class SLAContext1 extends ModuleBaseWithCustomFields {

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private long slaModuleId;
    public long getSlaModuleId() {
        return slaModuleId;
    }
    public void setSlaModuleId(long slaModuleId) {
        this.slaModuleId = slaModuleId;
    }

    private SLAWorkflowRuleContext slaRule;
    public SLAWorkflowRuleContext getSlaRule() {
        return slaRule;
    }
    public void setSlaRule(SLAWorkflowRuleContext slaRule) {
        this.slaRule = slaRule;
    }

    private Type type;
    public int getType() {
        if (type != null) {
            return type.getIndex();
        }
        return -1;
    }
    public Type getTypeEnum() {
        return type;
    }
    public void setType(int type) {
        this.type = Type.valueOf(type);
    }
    public void setType(Type type) {
        this.type = type;
    }

    public enum Type implements FacilioEnum {
        OPEN("Open"),
        ;

        Type(String value) {
            this.value = value;
        }

        private String value;
        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        public static Type valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
