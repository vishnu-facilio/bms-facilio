package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;

import java.util.Map;

public class CustomButtonRuleContext extends ApproverWorkflowRuleContext implements FormRuleInterface {

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

    private int buttonType = -1;
    public int getButtonType() {
        return buttonType;
    }
    public void setButtonType(int buttonType) {
        this.buttonType = buttonType;
    }

    @Override
    public boolean evaluateMisc(String moduleName, Object record, Map<String, Object> placeHolders, FacilioContext context) throws Exception {
        boolean result;

        result = super.evaluateMisc(moduleName, record, placeHolders, context);
        return result;
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
            }
        }

    }
}
