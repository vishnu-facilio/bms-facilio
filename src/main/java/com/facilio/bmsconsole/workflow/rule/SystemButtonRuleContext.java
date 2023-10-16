package com.facilio.bmsconsole.workflow.rule;

import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioIntEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class SystemButtonRuleContext extends ApproverWorkflowRuleContext {

    private String identifier;
    public String getIdentifier() {
        return identifier;
    }
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Getter
    @Setter
    private boolean permissionRequired;
    @Getter
    @Setter
    private String permission;

    private CustomButtonRuleContext.PositionType positionType;

    public int getPositionType() {
        if (positionType != null) {
            return positionType.getIndex();
        }
        return -1;
    }

    public CustomButtonRuleContext.PositionType getPositionTypeEnum() {
        return positionType;
    }

    public void setPositionType(int positionType) {
        this.positionType = CustomButtonRuleContext.PositionType.valueOf(positionType);
    }

    @Getter
    @Setter
    List<SystemButtonAppRelContext> systemButtonAppRels;

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext
            context) throws Exception {
        boolean result;

        if (!isActive()) {
            return false;
        }

        result = super.evaluateMisc(moduleName, record, placeHolders, context);
        return result;
    }

    @Override
    public boolean evaluateCriteria(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (positionType == CustomButtonRuleContext.PositionType.LIST_TOP) {
            return true;
        }
        return super.evaluateCriteria(moduleName, record, placeHolders, context);
    }

    @Override
    public boolean evaluateWorkflowExpression(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (positionType == CustomButtonRuleContext.PositionType.LIST_TOP) {
            return true;
        }
        return super.evaluateWorkflowExpression(moduleName, record, placeHolders, context);
    }

    private ButtonType buttonType;
    public void setButtonType(int buttonType) {
        this.buttonType = ButtonType.valueOf(buttonType);
    }
    public int getButtonType() {
        if (buttonType != null) {
            return buttonType.getIndex();
        }
        return -1;
    }

    public ButtonType getButtonTypeEnum() {
        return buttonType;
    }
    public enum ButtonType implements FacilioIntEnum {
        CREATE("create"),
        EDIT("edit"),
        DELETE("delete"),
        OTHERS("others"),
        ;

        private String name;

        ButtonType(String name) {
            this.name = name;
        }

        public static ButtonType valueOf(int value) {
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
