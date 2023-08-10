package com.facilio.componentpackage.implementation;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.FormRulePackageUtil;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SubFormRulePackageBeanImpl implements PackageBean<FormRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return FormRulePackageUtil.getFormRuleIdVsFormId(true, true);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return FormRulePackageUtil.getFormRuleIdVsFormId(false, true);
    }

    @Override
    public Map<Long, FormRuleContext> fetchComponents(List<Long> ids) throws Exception {
        return FormRulePackageUtil.fetchFormRules(ids);
    }

    @Override
    public void convertToXMLComponent(FormRuleContext subFormRule, XMLBuilder subFormRuleElement) throws Exception {

        if (subFormRule == null && subFormRule.getFormId() < 0 && subFormRule.getSubFormId() < 0 && CollectionUtils.isEmpty(subFormRule.getActions())) {
            return;
        }

        if (subFormRule.getTriggerTypeEnum() == FormRuleContext.TriggerType.FIELD_UPDATE && CollectionUtils.isEmpty(subFormRule.getTriggerFields())) {
            return;
        }

        FormRulePackageUtil.convertFormRuleToXMLComponent(subFormRule,subFormRuleElement);

    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        return null;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        return FormRulePackageUtil.getExistingIdsByXMLData(uniqueIdVsXMLData);
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();

        FormRuleContext formRule;

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder formElement = idVsData.getValue();
            ModuleBean moduleBean = Constants.getModBean();

            formRule = constructRuleFromXMLBuilder(formElement, moduleBean);

            FacilioChain addRuleChain = TransactionChainFactory.getAddFormRuleChain();
            FacilioContext context = addRuleChain.getContext();
            context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRule);
            addRuleChain.execute();

            FormRuleContext newFormRule = (FormRuleContext) context.get(FormRuleAPI.FORM_RULE_CONTEXT);
            uniqueIdentifierVsComponentId.put(idVsData.getKey(), newFormRule.getId());

        }

        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();

        for (Map.Entry<Long, XMLBuilder> idVsData : idVsXMLComponents.entrySet()) {
            Long ruleId = idVsData.getKey();

            XMLBuilder ruleElement = idVsData.getValue();

            FormRuleContext formRule = constructRuleFromXMLBuilder(ruleElement, moduleBean);
            formRule.setId(ruleId);

            FacilioChain c = TransactionChainFactory.getUpdateFormRuleChain();
            Context context = c.getContext();

            context.put(FormRuleAPI.FORM_RULE_CONTEXT, formRule);

            c.execute();
        }
    }

    @Override
    public void postComponentAction(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {

    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

        if (CollectionUtils.isNotEmpty(ids)) {
            FormRuleAPI.deleteFormRule(ids);
        }

    }



    private FormRuleContext constructRuleFromXMLBuilder(XMLBuilder ruleElement, ModuleBean moduleBean) throws Exception {

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String formName = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
        String subFormName = ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_NAME).text();
        String subFormModuleName = ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_MODULE_NAME).text();
        String moduleName = ruleElement.getElement(PackageConstants.MODULENAME).getText();
        String ruleName = ruleElement.getElement(PackageConstants.NAME).text();
        String description = ruleElement.getElement(PackageConstants.DESCRIPTION).text();
        String typeEnum = ruleElement.getElement(PackageConstants.FormRuleConstants.TYPE).text();
        String executeTypeEnum = ruleElement.getElement(PackageConstants.FormRuleConstants.EXECUTE_TYPE).text();
        String triggerTypeEnum = ruleElement.getElement(PackageConstants.FormRuleConstants.TRIGGER_TYPE).text();
        boolean ruleStatus = Boolean.parseBoolean(ruleElement.getElement(PackageConstants.FormRuleConstants.STATUS).getText());

        FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;
        FacilioModule subFormModule = StringUtils.isNotEmpty(subFormModuleName) ? moduleBean.getModule(subFormModuleName) : null;
        FacilioForm form = StringUtils.isNotEmpty(formName) ? FormsAPI.getFormFromDB(formName, module) : null;
        FacilioForm subForm = StringUtils.isNotEmpty(subFormName) ? FormsAPI.getFormFromDB(subFormName, subFormModule) : null;
        FormRuleContext.FormRuleType type = StringUtils.isNotEmpty(typeEnum) ? FormRuleContext.FormRuleType.valueOf(typeEnum) : null;
        FormRuleContext.ExecuteType executeType = StringUtils.isNotEmpty(executeTypeEnum) ? FormRuleContext.ExecuteType.valueOf(executeTypeEnum) : null;
        FormRuleContext.TriggerType triggerType = StringUtils.isNotEmpty(triggerTypeEnum) ? FormRuleContext.TriggerType.valueOf(triggerTypeEnum) : null;


        // Form Rule
        FormRuleContext formRuleContext = new FormRuleContext();
        formRuleContext.setOrgId(orgId);
        formRuleContext.setStatus(ruleStatus);
        if (form != null) {
            formRuleContext.setFormId(form.getId());
        }
        if(subForm!=null){
            formRuleContext.setSubFormId(subForm.getId());
        }
        formRuleContext.setName(ruleName);
        formRuleContext.setDescription(description);
        formRuleContext.setType(type != null ? type.getIntVal() : -1);
        formRuleContext.setExecuteType(executeType != null ? executeType.getIntVal() : -1);
        formRuleContext.setTriggerType(triggerType != null ? triggerType.getIntVal() : -1);
        formRuleContext.setRuleType(1);

        XMLBuilder criteriaElement = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_RULE_CRITERIA);
        XMLBuilder subFormCriteriaElement = ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_RULE_CRITERIA);
        // Criteria
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            formRuleContext.setCriteria(criteria);
        }
        //Sub Form Criteria
        if(subFormCriteriaElement!=null){
            Criteria subFormCriteria = PackageBeanUtil.constructCriteriaFromBuilder(subFormCriteriaElement);
            formRuleContext.setSubFormCriteria(subFormCriteria);
        }

        // trigger fields
        XMLBuilder triggerFieldsElementsList = ruleElement.getElement(PackageConstants.FormRuleConstants.TRIGGER_FIELDS_LIST);
        if (triggerFieldsElementsList != null) {
            List<FormRuleTriggerFieldContext> triggerFieldList = new ArrayList<>();
            List<XMLBuilder> triggerFieldsElementList = ruleElement.getElementList(PackageConstants.FormRuleConstants.TRIGGER_FIELD);
            for (XMLBuilder triggerFieldElement : triggerFieldsElementList) {
                FormRuleTriggerFieldContext triggerFieldContext = new FormRuleTriggerFieldContext();
                long fieldId = -1;
                String displayName = triggerFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).getText();
                if (triggerFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME) != null) {
                    String facilioFieldName = triggerFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).getText();
                    String fieldModuleName = triggerFieldElement.getElement(PackageConstants.MODULENAME).getText();
                    FacilioField facilioField = moduleBean.getField(facilioFieldName, fieldModuleName);
                    fieldId = facilioField != null ? facilioField.getFieldId() : -1;
                }
                FormField formField = new FormField();
                formField.setFormId(form.getId());
                formField.setFieldId(fieldId);
                formField.setDisplayName(displayName);
                long formFieldId = PackageBeanUtil.getFormFieldId(formField);

                triggerFieldContext.setFieldId(formFieldId);
                triggerFieldList.add(triggerFieldContext);
            }
            formRuleContext.setTriggerFields(triggerFieldList);
        }

        // Form Rule Actions

        XMLBuilder formRuleActionsList = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_ACTIONS_LIST);
        if (formRuleActionsList != null) {
            List<FormRuleActionContext> formRuleActions = FormRulePackageUtil.constructActionFieldsFromBuilder(formRuleActionsList);
            formRuleContext.setActions(formRuleActions);
        }

        return formRuleContext;
    }

}
