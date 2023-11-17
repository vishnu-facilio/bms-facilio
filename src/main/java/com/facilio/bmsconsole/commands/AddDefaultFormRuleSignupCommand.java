package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddDefaultFormRuleSignupCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        FacilioForm facilioForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
        long formId = facilioForm.getId();

        Map<String, FormField> formFieldMap = FormsAPI.getFormFieldsFromSections(facilioForm.getSections()).stream().collect(Collectors.toMap(FormField::getDisplayName, Function.identity()));

        if(facilioForm.getDefaultFormRules()!=null){
            String appLinkName = facilioForm.getAppLinkName();
            List<FormRuleContext> defaultFormRules = facilioForm.getDefaultFormRules();
            for(FormRuleContext formRuleContext:defaultFormRules ){
                List<String> getAppLinkNamesForRule = formRuleContext.getAppLinkNamesForRule();
                if(getAppLinkNamesForRule.contains(appLinkName)){
                    formRuleContext.setIsDefault(true);
                    formRuleContext.setFormId(formId);

                    List<FormRuleActionContext> actions = formRuleContext.getActions();
                    for(FormRuleActionContext formRuleAction : actions){
                        List<FormRuleActionFieldsContext> formRuleActionFieldsContext = formRuleAction.getFormRuleActionFieldsContext();
                        for(FormRuleActionFieldsContext actionField: formRuleActionFieldsContext){
                            if(actionField.getFormFieldId() <0){
                                String formFieldName = actionField.getFormFieldName();
                                actionField.setFormFieldId(formFieldMap.get(formFieldName).getId());
                            }
                        }
                    }
                    if(formRuleContext.getTriggerTypeEnum() != FormRuleContext.TriggerType.FORM_ON_LOAD) {
                        for (FormRuleTriggerFieldContext triggerFieldContext : formRuleContext.getTriggerFields()) {
                            if (triggerFieldContext.getFieldId() < 0) {
                                String formFieldName = triggerFieldContext.getFieldName();
                                triggerFieldContext.setFieldId(formFieldMap.get(formFieldName).getId());
                            }
                        }
                    }

                    FacilioChain chain = TransactionChainFactory.getAddFormRuleChain();
                    Context ruleContext = chain.getContext();

                    ruleContext.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);

                    chain.execute();
                }
            }
        }
        return false;
    }
}
