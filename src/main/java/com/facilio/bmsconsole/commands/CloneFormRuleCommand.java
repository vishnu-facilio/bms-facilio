package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CloneFormRuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long formId = (Long) context.get(FacilioConstants.ContextNames.FORM_ID);
        FacilioForm oldForm = (FacilioForm) context.get(FacilioConstants.ContextNames.FORM);
        FacilioForm clonedForm = (FacilioForm) context.get(FacilioConstants.ContextNames.CLONED_FORM);

        String moduleName = oldForm.getModule().getName();
        List<Long> subFormIds = new ArrayList<>();
        Map<Long,String> oldSubFormIdVsOldSectionName = new HashMap<>();

        if(CollectionUtils.isNotEmpty(oldForm.getSections())) {
            oldForm.getSections().forEach(section -> {
                if(section.getSectionTypeEnum()== FormSection.SectionType.SUB_FORM){
                    subFormIds.add(section.getSubFormId());
                    oldSubFormIdVsOldSectionName.put(section.getSubFormId(),section.getLinkName());
                }
            });
        }

        List<FormRuleContext> formRules = FormRuleAPI.getFormRuleContexts(moduleName, formId, false);

        if(CollectionUtils.isNotEmpty(formRules)){
            FormRuleAPI.setFormRuleActionAndTriggerFieldContext(formRules);
            FormRuleAPI.setFormRuleCriteria(formRules);
            cloneFormRule(oldForm,clonedForm,formRules,null);
        }

        if(CollectionUtils.isNotEmpty(subFormIds)){
            List<FormRuleContext> subFormRules = FormRuleAPI.getFormRuleContexts(moduleName, formId, true);
            FormRuleAPI.setFormRuleActionAndTriggerFieldContext(subFormRules);
            FormRuleAPI.setFormRuleCriteria(subFormRules);
            cloneFormRule(oldForm,clonedForm,subFormRules,oldSubFormIdVsOldSectionName);
        }

        return false;
    }

    private void cloneFormRule(FacilioForm oldForm,FacilioForm clonedForm,List<FormRuleContext> formRules,Map<Long,String> oldSubFormIdVsOldSectionName) throws Exception{

        Map<Long,String> oldFieldIdVsName = new HashMap<>();
        Map<String,FormField> newFieldNameVsField = new HashMap<>();
        Map<Long,String> oldSubFormFieldIdVsName = new HashMap<>();
        Map<String,FormField> newSubFormFieldNameVsField = new HashMap<>();
        Map<Long,String> oldSectionIdVsName = new HashMap<>();
        Map<String,FormSection> newSectionNameVsSection = new HashMap<>();
        Map<String,Long> newSectionNameVsSubFormId = new HashMap<>();


        List<FormSection> oldFormSections = oldForm.getSections();
        List<FormSection> newFormSections = clonedForm.getSections();

        if(CollectionUtils.isEmpty(oldFormSections) || CollectionUtils.isEmpty(newFormSections)){
            return;
        }

        for(FormSection section : oldFormSections){
            oldSectionIdVsName.put(section.getId(), section.getLinkName());
            if(section.getSectionTypeEnum()== FormSection.SectionType.SUB_FORM){
                List<FormSection> subFormSections = section.getSubForm().getSections();
                List<FormField> subFormFields = FormsAPI.getFormFieldsFromSections(subFormSections);
                Map<Long,String> oldSubFormFieldIdVsFieldName = subFormFields.stream().collect(Collectors.toMap(FormField::getId, FormField::getName));
                oldSubFormFieldIdVsName.putAll(oldSubFormFieldIdVsFieldName);
            }
            List<FormField> oldFormFields = section.getFields();
            if(CollectionUtils.isNotEmpty(oldFormFields)){
                Map<Long,String> oldFieldIdVsFieldName = oldFormFields.stream().collect(Collectors.toMap(FormField::getId, FormField::getName));
                oldFieldIdVsName.putAll(oldFieldIdVsFieldName);
            }
        }

        for(FormSection section : newFormSections){
            if(section.getSectionTypeEnum()== FormSection.SectionType.SUB_FORM){

                newSectionNameVsSubFormId.put(section.getLinkName(), section.getSubFormId());

                List<FormSection> subFormSections = section.getSubForm().getSections();
                List<FormField> subFormFields = FormsAPI.getFormFieldsFromSections(subFormSections);
                Map<String,FormField> newSubFormFieldNameVsFormField = subFormFields.stream().collect(Collectors.toMap(FormField::getName, Function.identity()));
                newSubFormFieldNameVsField.putAll(newSubFormFieldNameVsFormField);

            }
            newSectionNameVsSection.put(section.getLinkName(), section);
            List<FormField> newFormFields = section.getFields();
            if(CollectionUtils.isNotEmpty(newFormFields)){
                Map<String,FormField> newFieldNameVsFormField = newFormFields.stream().collect(Collectors.toMap(FormField::getName,Function.identity()));
                newFieldNameVsField.putAll(newFieldNameVsFormField);
            }
        }

        for(FormRuleContext formRuleContext : formRules){

            FormRuleContext newFormRule = FieldUtil.cloneBean(formRuleContext,FormRuleContext.class);
            newFormRule.setId(-1l);
            if(formRuleContext.getSubFormId()>0){
                String oldSectionName = oldSubFormIdVsOldSectionName.get(formRuleContext.getSubFormId());
                long newSubFormId = newSectionNameVsSubFormId.get(oldSectionName);
                newFormRule.setSubFormId(newSubFormId);
            }
            newFormRule.setFormContext(clonedForm);
            newFormRule.setFormId(clonedForm.getId());

            newFormRule.setCriteriaId(-1l);
            newFormRule.setSubFormCriteriaId(-1l);

            List<FormRuleTriggerFieldContext> newTriggerFields = new ArrayList<>();
            if(newFormRule.getTriggerTypeEnum()== FormRuleContext.TriggerType.FIELD_UPDATE){
                List<FormRuleTriggerFieldContext> triggerFields = newFormRule.getTriggerFields();
                for(FormRuleTriggerFieldContext triggerField : triggerFields){
                    long triggerFieldId = triggerField.getFieldId();
                    String triggerFieldName = oldFieldIdVsName.get(triggerFieldId);
                    if(StringUtils.isEmpty(triggerFieldName)){
                        triggerFieldName = oldSubFormFieldIdVsName.get(triggerFieldId);
                    }
                    FormField newTriggerField = newFieldNameVsField.get(triggerFieldName);
                    if(newTriggerField==null){
                        newTriggerField = newSubFormFieldNameVsField.get(triggerFieldName);
                    }

                    FormRuleTriggerFieldContext newTriggerFieldContext = new FormRuleTriggerFieldContext();
                    newTriggerFieldContext.setFieldId(newTriggerField.getId());

                    newTriggerFields.add(newTriggerFieldContext);
                }
                newFormRule.setTriggerFields(newTriggerFields);
            }

            List<FormRuleActionContext> oldRuleActions = newFormRule.getActions();

            for(FormRuleActionContext oldAction : oldRuleActions){

                oldAction.setId(-1l);
                oldAction.setFormRuleId(-1l);

                if(oldAction.getActionTypeEnum()==FormActionType.EXECUTE_SCRIPT){
                    oldAction.setWorkflowId(-1l);
                    oldAction.getWorkflow().setId(-1l);
                } else if (oldAction.getActionTypeEnum()==FormActionType.SHOW_SECTION || oldAction.getActionTypeEnum()==FormActionType.HIDE_SECTION){

                    for(FormRuleActionFieldsContext formRuleActionFieldContext : oldAction.getFormRuleActionFieldsContext()){
                        formRuleActionFieldContext.setId(-1l);
                        formRuleActionFieldContext.setFormRuleActionId(-1l);
                        long formRuleActionSectionId = formRuleActionFieldContext.getFormSectionId();
                        String formRuleActionSectionName = oldSectionIdVsName.get(formRuleActionSectionId);
                        FormSection newActionSection = newSectionNameVsSection.get(formRuleActionSectionName);
                        formRuleActionFieldContext.setFormSectionId(newActionSection.getId());
                    }

                }else {

                    for(FormRuleActionFieldsContext formRuleActionFieldContext : oldAction.getFormRuleActionFieldsContext()){
                        formRuleActionFieldContext.setId(-1l);
                        formRuleActionFieldContext.setFormRuleActionId(-1l);
                        long formRuleActionFieldId = formRuleActionFieldContext.getFormFieldId();
                        String actionFieldName = oldFieldIdVsName.get(formRuleActionFieldId);
                        if(StringUtils.isEmpty(actionFieldName)){
                            actionFieldName = oldSubFormFieldIdVsName.get(formRuleActionFieldId);
                        }
                        FormField newActionField = newFieldNameVsField.get(actionFieldName);
                        if(newActionField==null){
                            newActionField = newSubFormFieldNameVsField.get(actionFieldName);
                        }
                        formRuleActionFieldContext.setFormFieldId(newActionField.getId());
                    }

                }

            }

            addNewFormRule(newFormRule);
        }
    }

    private void addNewFormRule(FormRuleContext formRuleContext) throws Exception {

        FacilioChain c = TransactionChainFactory.getAddFormRuleChain();
        Context context = c.getContext();

        context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);

        c.execute();

    }
}
