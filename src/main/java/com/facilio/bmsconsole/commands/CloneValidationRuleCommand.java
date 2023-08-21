package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class CloneValidationRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
        Long newFormId = (Long) context.get(FacilioConstants.ContextNames.CLONED_FORM_ID);

        FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();
        List<ValidationContext> validationRules = ValidationRulesAPI.getValidationsByParentId(formId, validationModule, null, null);

        if(CollectionUtils.isEmpty(validationRules)){
            return false;
        }

        for(ValidationContext validationRule : validationRules){

            ValidationContext clonedValidationRule = FieldUtil.cloneBean(validationRule,ValidationContext.class);
            clonedValidationRule.setId(-1l);
            clonedValidationRule.setParentId(newFormId);
            clonedValidationRule.setErrorMessagePlaceHolderScriptId(-1l);
            if(clonedValidationRule.getErrorMessagePlaceHolderScript()!=null) {
                clonedValidationRule.getErrorMessagePlaceHolderScript().setId(-1l);
            }
            ValidationRulesAPI.addOrUpdateFormValidations(clonedValidationRule,validationModule);
        }

        return false;
    }
}
