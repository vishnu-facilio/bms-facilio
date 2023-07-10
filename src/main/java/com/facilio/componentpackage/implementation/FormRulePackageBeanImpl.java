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
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.bmsconsole.forms.FormRuleContext.FormRuleType;
import com.facilio.bmsconsole.forms.FormRuleContext.RuleType;
import com.facilio.bmsconsole.forms.FormRuleContext.Form_On_Load_Rule_Type;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.forms.FormRuleContext.ExecuteType;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FormRulePackageBeanImpl implements PackageBean<FormRuleContext> {
    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return PackageBeanUtil.getFormRuleIdVsFormId(true,false);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return PackageBeanUtil.getFormRuleIdVsFormId(false,false);
    }

    @Override
    public Map<Long, FormRuleContext> fetchComponents(List<Long> ids) throws Exception {

        FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
        Map<Long, FormRuleContext> ruleIdVsFormRule = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;

        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            Criteria ruleIdsCriteria = new Criteria();
            ruleIdsCriteria.addAndCondition(CriteriaAPI.getIdCondition(idsSubList, formRuleModule));

            List<FacilioField> formRuleFields = FieldFactory.getFormRuleFields();

            List<FormRuleContext> formRuleContexts = PackageBeanUtil.getFormRules(formRuleFields, ruleIdsCriteria);

            if (CollectionUtils.isNotEmpty(formRuleContexts)) {

                for (FormRuleContext formRuleContext : formRuleContexts) {

                    List<FormRuleActionContext> formRuleActionContexts = FormRuleAPI.getFormRuleActionContext(formRuleContext.getId());
                    formRuleContext.setActions(formRuleActionContexts);

                    List<FormRuleTriggerFieldContext> formRuleTriggerFieldContexts = FormRuleAPI.getFormRuleTriggerFields(formRuleContext);
                    formRuleContext.setTriggerFields(formRuleTriggerFieldContexts);

                    if (formRuleContext.getCriteriaId() > 0) {
                        formRuleContext.setCriteria(CriteriaAPI.getCriteria(formRuleContext.getCriteriaId()));
                    }
                }
            }

            formRuleContexts.forEach(rule -> ruleIdVsFormRule.put(rule.getId(), rule));

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());

        }

        return ruleIdVsFormRule;
    }

    @Override
    public void convertToXMLComponent(FormRuleContext formRule, XMLBuilder formRuleElement) throws Exception {

        if (formRule != null && (formRule.getFormId() > 0)) {

            long formId = formRule.getFormId();

            String formName = null;
            String moduleName = null;

            if (formId > 0) {
                FacilioForm form = FormsAPI.getFormFromDB(formId);
                formName = form != null ? form.getName() : null;
                moduleName = form.getModule().getName();
            }

            //Form Rule
            formRuleElement.element(PackageConstants.NAME).text(formRule.getName());
            formRuleElement.element(PackageConstants.DESCRIPTION).text(formRule.getDescription());
            formRuleElement.element(PackageConstants.FormRuleConstants.RULE_TYPE).text(String.valueOf(formRule.getRuleType()));
            formRuleElement.element(PackageConstants.FormRuleConstants.TYPE).text(String.valueOf(formRule.getType()));
            formRuleElement.element(PackageConstants.FormRuleConstants.TRIGGER_TYPE).text(String.valueOf(formRule.getTriggerType()));
            formRuleElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(formName);
            formRuleElement.element(PackageConstants.MODULENAME).text(moduleName);
            formRuleElement.element(PackageConstants.FormRuleConstants.FORM_ON_LOAD_RULE_TYPE).text(String.valueOf(formRule.getFormOnLoadRuleType()));
            formRuleElement.element(PackageConstants.FormRuleConstants.EXECUTE_TYPE).text(String.valueOf(formRule.getExecuteType()));
            formRuleElement.element(PackageConstants.FormRuleConstants.STATUS).text(String.valueOf(formRule.getStatus()));

            //criteria
            if (formRule.getCriteria() != null) {
                formRuleElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(formRule.getCriteria(), formRuleElement.element(PackageConstants.FormRuleConstants.FORM_RULE_CRITERIA), moduleName));
            }

            //trigger fields
            if (CollectionUtils.isNotEmpty(formRule.getTriggerFields())) {
                XMLBuilder triggerFieldElementsList = formRuleElement.element(PackageConstants.FormRuleConstants.TRIGGER_FIELDS_LIST);

                for (FormRuleTriggerFieldContext triggerFieldContext : formRule.getTriggerFields()) {
                    XMLBuilder triggerFieldElement = triggerFieldElementsList.element(PackageConstants.FormRuleConstants.TRIGGER_FIELD);

                    if (triggerFieldContext.getFieldId() > 0) {
                        FormField formField = FormsAPI.getFormFieldFromId(triggerFieldContext.getFieldId());

                        if (formField != null && formField.getField() != null) {
                            FacilioField field = formField.getField();

                            triggerFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                            triggerFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                            triggerFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());

                        }

                    }
                }
            }

            // form rule actions

            if (CollectionUtils.isNotEmpty(formRule.getActions())) {
                XMLBuilder formRuleActionsList = formRuleElement.element(PackageConstants.FormRuleConstants.FORM_ACTIONS_LIST);

                PackageBeanUtil.constructBuilderFromActionType(formRuleActionsList, formRule, moduleName);

            }
        }
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

        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsRuleId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {

            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder ruleElement = idVsData.getValue();

            String moduleName, formName, ruleName, description;

            moduleName = ruleElement.getElement(PackageConstants.MODULENAME).getText();
            formName = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
            ruleName = ruleElement.getElement(PackageConstants.NAME).text();
            description = ruleElement.getElement(PackageConstants.DESCRIPTION).text();

            FacilioModule module = moduleBean.getModule(moduleName);
            FacilioForm form = FormsAPI.getFormFromDB(formName, module);
            long formId = -1;
            if (form != null) {
                formId = form.getId();
            }
            FormRuleContext formRule = new FormRuleContext();
            formRule.setName(ruleName);
            formRule.setDescription(description);
            formRule.setFormId(formId);

            long ruleId = PackageBeanUtil.getFormRuleId(formRule);

            if (ruleId > 0) {
                uniqueIdentifierVsRuleId.put(uniqueIdentifier, ruleId);
            }
        }

        return uniqueIdentifierVsRuleId;
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
            if (ruleId == null || ruleId < 0) {
                continue;
            }

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
            deleteFormRule(ids);
        }

    }

    private void deleteFormRule(List<Long> componentIds) throws Exception {

        FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
        List<FacilioField> selectableFields = FieldFactory.getFormRuleFields();

        Criteria ruleIdsCriteria = new Criteria();
        ruleIdsCriteria.addAndCondition(CriteriaAPI.getIdCondition(componentIds, formRuleModule));

        List<FormRuleContext> formRuleContexts = PackageBeanUtil.getFormRules(selectableFields, ruleIdsCriteria);

        for (FormRuleContext formRuleContext : formRuleContexts) {

            List<FormRuleActionContext> oldactions = FormRuleAPI.getFormRuleActionContext(formRuleContext.getId());

            for (FormRuleActionContext action : oldactions) {
                if (action.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
                    long oldWorkflowId = action.getWorkflowId();
                    WorkflowUtil.deleteWorkflow(oldWorkflowId);
                } else {
                    for (FormRuleActionFieldsContext actionField : action.getFormRuleActionFieldsContext()) {

                        if (actionField.getCriteriaId() > 0) {
                            CriteriaAPI.deleteCriteria(actionField.getCriteriaId());
                        }
                    }
                }

                FormRuleAPI.deleteFormRuleActionContext(action);
            }

            FormRuleAPI.deleteFormRuleContext(formRuleContext);
        }
    }


    private FormRuleContext constructRuleFromXMLBuilder(XMLBuilder ruleElement, ModuleBean moduleBean) throws Exception {

        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String formName = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
        String moduleName = ruleElement.getElement(PackageConstants.MODULENAME).getText();
        String ruleName = ruleElement.getElement(PackageConstants.NAME).text();
        String description = ruleElement.getElement(PackageConstants.DESCRIPTION).text();
        String ruleTypeStr = ruleElement.getElement(PackageConstants.FormRuleConstants.RULE_TYPE).text();
        String typeStr = ruleElement.getElement(PackageConstants.FormRuleConstants.TYPE).text();
        String formOnLoadRuleTypeStr = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_ON_LOAD_RULE_TYPE).text();
        String executeTypeStr = ruleElement.getElement(PackageConstants.FormRuleConstants.EXECUTE_TYPE).text();
        String triggerTypeStr = ruleElement.getElement(PackageConstants.FormRuleConstants.TRIGGER_TYPE).text();
        boolean ruleStatus = Boolean.parseBoolean(ruleElement.getElement(PackageConstants.FormRuleConstants.STATUS).getText());

        FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;
        FacilioForm form = StringUtils.isNotEmpty(formName) ? FormsAPI.getFormFromDB(formName, module) : null;
        FormRuleType formRuleType = StringUtils.isNotEmpty(ruleTypeStr) ? FormRuleContext.FormRuleType.getAllRuleType().get(Integer.parseInt(ruleTypeStr)) : null;
        RuleType type = StringUtils.isNotEmpty(typeStr) ? FormRuleContext.RuleType.getAllRuleType().get(Integer.parseInt(typeStr)) : null;
        Form_On_Load_Rule_Type formOnLoadRuleType = StringUtils.isNotEmpty(formOnLoadRuleTypeStr) ? FormRuleContext.Form_On_Load_Rule_Type.getAllTriggerType().get(Integer.parseInt(formOnLoadRuleTypeStr)) : null;
        ExecuteType executeType = StringUtils.isNotEmpty(executeTypeStr) ? FormRuleContext.ExecuteType.getAllExecuteType().get(Integer.parseInt(executeTypeStr)) : null;
        TriggerType triggerType = StringUtils.isNotEmpty(triggerTypeStr) ? FormRuleContext.TriggerType.getAllTriggerType().get(Integer.parseInt(triggerTypeStr)) : null;


        // Form Rule
        FormRuleContext formRuleContext = new FormRuleContext();
        formRuleContext.setOrgId(orgId);
        formRuleContext.setStatus(ruleStatus);
        if (form != null) {
            formRuleContext.setFormId(form.getId());
        }
        formRuleContext.setName(ruleName);
        formRuleContext.setDescription(description);
        formRuleContext.setFormOnLoadRuleType(formOnLoadRuleType != null ? formOnLoadRuleType.getIntVal() : -1);
        formRuleContext.setRuleType(formRuleType != null ? formRuleType.getIntVal() : -1);
        formRuleContext.setType(type != null ? type.getIntVal() : -1);
        formRuleContext.setExecuteType(executeType != null ? executeType.getIntVal() : -1);
        formRuleContext.setTriggerType(triggerType != null ? triggerType.getIntVal() : -1);

        XMLBuilder criteriaElement = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_RULE_CRITERIA);

        // Criteria
        if (criteriaElement != null) {
            Criteria criteria = PackageBeanUtil.constructCriteriaFromBuilder(criteriaElement);
            formRuleContext.setCriteria(criteria);
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
            List<FormRuleActionContext> formRuleActions = PackageBeanUtil.constructActionFieldsFromBuilder(formRuleActionsList);
            formRuleContext.setActions(formRuleActions);
        }

        return formRuleContext;
    }

}
