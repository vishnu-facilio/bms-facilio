package com.facilio.bmsconsole.workflow.rule;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioContext;
import com.facilio.delegate.context.DelegationType;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.util.List;
import java.util.Map;

public class CustomButtonRuleContext extends ApproverWorkflowRuleContext implements FormInterface {

    private static final long serialVersionUID = 1L;

    private FacilioForm form;
    @Override
    public FacilioForm getForm() {
        return form;
    }
    @Override
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

    private boolean shouldFormInterfaceApply = true;
    public void setShouldFormInterfaceApply(boolean shouldFormInterfaceApply) {
        this.shouldFormInterfaceApply = shouldFormInterfaceApply;
    }
    @Override
    public boolean shouldFormInterfaceApply() {
        return shouldFormInterfaceApply;
    }

    @Override
    public String getSuggestedFormName() {
        return "__custom_button_" + getId();
    }

    @Override
    protected DelegationType getDelegationType() {
        return DelegationType.CUSTOM_BUTTON;
    }

    private String formModuleName;
    public String getFormModuleName() {
        return formModuleName;
    }
    public void setFormModuleName(String formModuleName) {
        this.formModuleName = formModuleName;
    }

    private long lookupFieldId = -1;
    public long getLookupFieldId() {
        return lookupFieldId;
    }
    public void setLookupFieldId(long lookupFieldId) {
        this.lookupFieldId = lookupFieldId;
    }

    private FacilioField lookupField;
    public FacilioField getLookupField() throws Exception {
        if (lookupField == null) {
            if (lookupFieldId > 0 && StringUtils.isNotEmpty(formModuleName)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                lookupField = modBean.getField(lookupFieldId, formModuleName);
            }
        }
        return lookupField;
    }
    public void setLookupField(FacilioField lookupField) {
        this.lookupField = lookupField;
    }

    private ButtonType buttonType;
    @Getter
    @Setter
    private int iconType=-1;
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

    private JSONObject config;

    public JSONObject getConfig() {
        return config;
    }
    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @JSON(serialize=false)
    public String getConfigStr() {
        if (config != null) {
            return config.toJSONString();
        }
        return null;
    }
    public void setConfigStr(String configStr) throws ParseException {
        this.config = FacilioUtil.parseJson(configStr);
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        boolean result;

        if (!isActive()) {
            return false;
        }

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
            boolean isValid = super.validationCheck(moduleRecord, context, placeHolders);
            if (isValid) {
                // write the logic
                if (buttonType == ButtonType.ACTION) {
                    super.executeTrueActions(record, context, placeHolders);
                }
            }
        }
    }

    public enum PositionType implements FacilioIntEnum {
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
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    public enum ButtonType implements FacilioIntEnum {
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
        public Integer getIndex() {
            return ordinal() + 1;
        }

        @Override
        public String getValue() {
            return name;
        }
    }

    @Getter
    @Setter
    List<CustomButtonAppRelContext> customButtonAppRel;

}
