package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class CustomButtonRuleContext extends ApproverWorkflowRuleContext implements FormInterface {

    private static final long serialVersionUID = 1L;

    private FacilioForm form;
    public FacilioForm getForm() {
        return form;
    }
    public void setForm(FacilioForm form) {
        this.form = form;
    }

    private long formId = -1; // check whether it is good to have
    public long getFormId() {
        return formId;
    }
    public void setFormId(long formId) {
        this.formId = formId;
    }

    private ButtonType buttonType;
    public int getButtonType() {
        if (buttonType != null) {
            return buttonType.getIndex();
        }
        return -1;
    }
    public ButtonType getButtonTypeEnum() {
        return buttonType;
    }
    public void setButtonType(int buttonType) {
        this.buttonType = ButtonType.valueOf(buttonType);
    }

    private PositionType positionType;
    public int getPositionType() {
        if (positionType != null) {
            return positionType.getIndex();
        }
        return -1;
    }
    public PositionType getPositionTypeEnum() {
        return positionType;
    }
    public void setPositionType(int positionType) {
        this.positionType = PositionType.valueOf(positionType);
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        boolean result;

        result = super.evaluateMisc(moduleName, record, placeHolders, context);
        return result;
    }

    @Override
    public boolean evaluateCriteria(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (positionType == PositionType.LIST_TOP) {
            return true;
        }
        return super.evaluateCriteria(moduleName, record, placeHolders, context);
    }

    @Override
    public boolean evaluateWorkflowExpression(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        if (positionType == PositionType.LIST_TOP) {
            return true;
        }
        return super.evaluateWorkflowExpression(moduleName, record, placeHolders, context);
    }

    @Override
    public void executeTrueActions(Object record, Context context, Map<String, Object> placeHolders) throws Exception {
        ModuleBaseWithCustomFields moduleRecord = (ModuleBaseWithCustomFields) record;
        boolean shouldExecuteTrueActions = true;
        shouldExecuteTrueActions = super.validateApproversForTrueAction(record);

        if (shouldExecuteTrueActions) {
            boolean isValid = super.validationCheck(moduleRecord);
            if (isValid) {
                // write the logic
                if (buttonType == ButtonType.ACTION) {
                    super.executeTrueActions(record, context, placeHolders);
                }
            }
        }
    }

    public enum PositionType implements FacilioEnum {
        SUMMARY("Summary"),
        LIST_ITEM("List Item"),
        LIST_BAR("List Bar"),
        LIST_TOP("List Top"),
        ;

        private String name;

        PositionType(String name) {
            this.name = name;
        }

        public static PositionType valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public enum ButtonType implements FacilioEnum {
        ACTION("Action"),
        SHOW_WIDGET("Show Widget")
        ;

        private String name;

        ButtonType(String name) {
            this.name = name;
        }

        public static ButtonType valueOf(int type) {
            if (type > 0 && type <= values().length) {
                return values()[type - 1];
            }
            return null;
        }

        @Override
        public int getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }
}
