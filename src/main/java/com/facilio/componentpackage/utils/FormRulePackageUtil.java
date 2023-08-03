package com.facilio.componentpackage.utils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FormRulePackageUtil {


    public static void constructBuilderFromActionType(XMLBuilder formRuleActionsList, FormRuleContext formRule, String moduleName) throws Exception {

        Map<Long,FormField> formFieldMap = new HashMap<>();
        Map<Long,FacilioForm> formMap = new HashMap<>();
        Map<Long,FormSection> sectionMap = new HashMap<>();

        for (FormRuleActionContext formRuleActionContext : formRule.getActions()) {

            XMLBuilder actionElement = formRuleActionsList.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION);
            actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_TYPE).text(String.valueOf(formRuleActionContext.getActionType()));

            switch (formRuleActionContext.getActionTypeEnum()) {

                case SHOW_FIELD:         //   1
                case HIDE_FIELD:         //   2
                case ENABLE_FIELD:       //   3
                case DISABLE_FIELD:      //   4
                case SET_MANDATORY:      //   11
                case REMOVE_MANDATORY:   //   12

                    if (CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
                        break;
                    }

                    for (FormRuleActionFieldsContext actionFieldContext : formRuleActionContext.getFormRuleActionFieldsContext()) {

                        if (actionFieldContext.getFormFieldId() > 0) {
                            FormField formField = null;
                            if(formFieldMap.containsKey(actionFieldContext.getFormFieldId())){
                                formField = formFieldMap.get(actionFieldContext.getFormFieldId());
                            }else {
                                formField = FormsAPI.getFormFieldFromId(actionFieldContext.getFormFieldId());
                                if(formField!=null){
                                    formFieldMap.put(actionFieldContext.getFormFieldId(),formField);
                                }
                            }
                            if (formField != null) {
                                FacilioForm form = null;
                                if(formMap.containsKey(formField.getFormId())){
                                    form = formMap.get(formField.getFormId());
                                }else {
                                    form = FormsAPI.getFormFromDB(formField.getFormId());
                                    if(form!=null){
                                        formMap.put(formField.getFormId(),form);
                                    }
                                }
                                if (formField.getField() != null) {
                                    XMLBuilder actionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
                                    FacilioField field = formField.getField();
                                    actionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    actionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());

                                } else if (Objects.equals(formField.getName(), "siteId")) {
                                    XMLBuilder actionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
                                    FacilioField siteId = FieldFactory.getSiteIdField();

                                    actionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    actionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(siteId.getName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                                } else if (Objects.equals(formField.getName(), "tasks") || Objects.equals(formField.getName(), "assignment")) {
                                    XMLBuilder actionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);

                                    actionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    actionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(formField.getName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    actionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                                }
                            }

                        }

                    }
                    break;

                case SET_FIELD_VALUE:    //    5
                case APPLY_FILTER:       //    6

                    if (CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
                        break;
                    }

                    for (FormRuleActionFieldsContext actionFieldContext : formRuleActionContext.getFormRuleActionFieldsContext()) {

                        if (actionFieldContext.getFormFieldId() > 0) {
                            FormField formField = null;
                            if(formFieldMap.containsKey(actionFieldContext.getFormFieldId())){
                                formField = formFieldMap.get(actionFieldContext.getFormFieldId());
                            }else {
                                formField = FormsAPI.getFormFieldFromId(actionFieldContext.getFormFieldId());
                                if(formField!=null){
                                    formFieldMap.put(actionFieldContext.getFormFieldId(),formField);
                                }
                            }
                            if (formField != null) {
                                FacilioForm form = null;
                                if(formMap.containsKey(formField.getFormId())){
                                    form = formMap.get(formField.getFormId());
                                }else {
                                    form = FormsAPI.getFormFromDB(formField.getFormId());
                                    if(form!=null){
                                        formMap.put(formField.getFormId(),form);
                                    }
                                }
                                if (formField.getField() != null) {

                                    XMLBuilder filterAndValueActionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
                                    FacilioField field = formField.getField();

                                    filterAndValueActionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    filterAndValueActionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_META).cData(String.valueOf(actionFieldContext.getActionMeta()));

                                    if (actionFieldContext.getCriteria() != null || actionFieldContext.getCriteriaId() > 0) {
                                        Criteria actionFieldCriteria = actionFieldContext.getCriteria();
                                        if (actionFieldCriteria == null && actionFieldContext.getCriteriaId() > 0) {
                                            actionFieldCriteria = CriteriaAPI.getCriteria(actionFieldContext.getCriteriaId());
                                        }
                                        filterAndValueActionFieldElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(actionFieldCriteria, filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FILTER_CRITERIA), moduleName));
                                    }

                                } else if (Objects.equals(formField.getName(), "siteId")) {

                                    XMLBuilder filterAndValueActionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
                                    FacilioField siteId = FieldFactory.getSiteIdField();

                                    filterAndValueActionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    filterAndValueActionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(siteId.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_META).cData(String.valueOf(actionFieldContext.getActionMeta()));

                                } else if (Objects.equals(formField.getName(), "tasks")|| Objects.equals(formField.getName(), "assignment")) {

                                    XMLBuilder filterAndValueActionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);

                                    filterAndValueActionFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                                    filterAndValueActionFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(formField.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(form.getName());
                                    filterAndValueActionFieldElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_META).cData(String.valueOf(actionFieldContext.getActionMeta()));

                                }
                            }
                        }
                    }

                    break;

                case EXECUTE_SCRIPT:     //8

                    if (!formRuleActionContext.getWorkflow().getWorkflowV2String().isEmpty()) {
                        XMLBuilder scriptActionFieldElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);

                        scriptActionFieldElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(formRuleActionContext.getWorkflow().isV2Script()));
                        scriptActionFieldElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(String.valueOf(formRuleActionContext.getWorkflow().getWorkflowV2String()));
                    }
                    break;

                case HIDE_SECTION:       //9
                case SHOW_SECTION:       //10

                    if (CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
                        break;
                    }

                    for (FormRuleActionFieldsContext actionFieldContext : formRuleActionContext.getFormRuleActionFieldsContext()) {

                        long sectionId = actionFieldContext.getFormSectionId();

                        if (sectionId > 0) {
                            FormSection section = null;
                            if(sectionMap.containsKey(sectionId)){
                                section = sectionMap.get(sectionId);
                            }else {
                                section = FormsAPI.getFormSection(sectionId);
                                if(section!=null){
                                    sectionMap.put(sectionId,section);
                                }
                            }

                            if (section != null) {
                                XMLBuilder sectionActionElement = actionElement.element(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
                                FacilioForm form = null;
                                if(formMap.containsKey(section.getFormId())){
                                    form = formMap.get(section.getFormId());
                                }else {
                                    form = FormsAPI.getFormFromDB(section.getFormId());
                                    if(form!=null){
                                        formMap.put(section.getFormId(),form);
                                    }
                                }
                                String formName = null;
                                if (form != null) {
                                    formName = form.getName();
                                }
                                sectionActionElement.element(PackageConstants.FormXMLComponents.SECTION_NAME).text(section.getName());
                                sectionActionElement.element(PackageConstants.FormXMLComponents.FORM_NAME).text(formName);
                                sectionActionElement.element(PackageConstants.MODULENAME).text(moduleName);

                            }
                        }
                    }

                    break;

            }

        }

    }

    public static List<FormRuleActionContext> constructActionFieldsFromBuilder(XMLBuilder formRuleActionsList) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        List<FormRuleActionContext> actionContextList = new ArrayList<>();
        List<XMLBuilder> actionsListElementList = formRuleActionsList.getElementList(PackageConstants.FormRuleConstants.FORM_RULE_ACTION);

        for (XMLBuilder actionElement : actionsListElementList) {
            FormRuleActionContext actionContext = new FormRuleActionContext();
            List<FormRuleActionFieldsContext> actionFieldsContexts = new ArrayList<>();
            String actionTypeStr = actionElement.getElement(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_TYPE).text();
            FormActionType actionType = StringUtils.isNotEmpty(actionTypeStr) ? FormActionType.getActionType(Integer.parseInt(actionTypeStr)) : null;
            List<XMLBuilder> actionFieldsElementList = actionElement.getElementList(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_FIELD);
            actionContext.setActionType(actionType.getVal());

            switch (actionType) {

                case SHOW_FIELD:         //   1
                case HIDE_FIELD:         //   2
                case ENABLE_FIELD:       //   3
                case DISABLE_FIELD:      //   4
                case SET_MANDATORY:      //   11
                case REMOVE_MANDATORY:   //   12

                    for (XMLBuilder actionFieldElement : actionFieldsElementList) {
                        FormRuleActionFieldsContext actionFieldsContext = new FormRuleActionFieldsContext();
                        String fieldModuleName = null;
                        long fieldId = -1;
                        String displayName = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).getText();
                        if (actionFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME) != null) {
                            String facilioFieldName = actionFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).getText();
                            fieldModuleName = actionFieldElement.getElement(PackageConstants.MODULENAME).getText();
                            FacilioField facilioField = moduleBean.getField(facilioFieldName, fieldModuleName);
                            fieldId = facilioField != null ? facilioField.getFieldId() : -1;
                        }
                        String formName = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
                        FacilioModule module = moduleBean.getModule(fieldModuleName);
                        FacilioForm form = FormsAPI.getFormFromDB(formName, module);
                        long formId = form.getId();
                        FormField formField = new FormField();
                        formField.setFormId(formId);
                        formField.setFieldId(fieldId);
                        formField.setDisplayName(displayName);
                        long formFieldId = PackageBeanUtil.getFormFieldId(formField);

                        actionFieldsContext.setFormFieldId(formFieldId);
                        actionFieldsContexts.add(actionFieldsContext);

                    }

                    break;

                case SET_FIELD_VALUE:    //    5
                case APPLY_FILTER:       //    6

                    for (XMLBuilder actionFieldElement : actionFieldsElementList) {
                        FormRuleActionFieldsContext actionFieldsContext = new FormRuleActionFieldsContext();

                        XMLBuilder filterElement = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FILTER_CRITERIA);
                        String actionMeta = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_RULE_ACTION_META).getCData();

                        String fieldModuleName = null;
                        long fieldId = -1;
                        String displayName = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).getText();
                        if (actionFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME) != null) {
                            String facilioFieldName = actionFieldElement.getElement(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).getText();
                            fieldModuleName = actionFieldElement.getElement(PackageConstants.MODULENAME).getText();
                            FacilioField facilioField = moduleBean.getField(facilioFieldName, fieldModuleName);
                            fieldId = facilioField != null ? facilioField.getFieldId() : -1;
                        }

                        String formName = actionFieldElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
                        FacilioModule module = moduleBean.getModule(fieldModuleName);
                        FacilioForm form = FormsAPI.getFormFromDB(formName, module);
                        long formId = form.getId();
                        FormField formField = new FormField();
                        formField.setFormId(formId);
                        formField.setFieldId(fieldId);
                        formField.setDisplayName(displayName);
                        long formFieldId = PackageBeanUtil.getFormFieldId(formField);

                        if (filterElement != null) {
                            Criteria filterCriteria = PackageBeanUtil.constructCriteriaFromBuilder(filterElement);
                            actionFieldsContext.setCriteria(filterCriteria);
                        }

                        actionFieldsContext.setFormFieldId(formFieldId);
                        actionFieldsContext.setActionMeta(actionMeta);

                        actionFieldsContexts.add(actionFieldsContext);
                    }

                    break;

                case EXECUTE_SCRIPT:     //8

                    String workflowV2String = actionElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
                    if (!workflowV2String.isEmpty()) {
                        boolean isV2Script = Boolean.parseBoolean(actionElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
                        WorkflowContext workflowContext = new WorkflowContext();

                        workflowContext.setIsV2Script(isV2Script);
                        workflowContext.setWorkflowV2String(workflowV2String);
                        actionContext.setWorkflow(workflowContext);
                        actionContext.setFormRuleActionFieldsContext(null);
                        actionContext.setActionType(FormActionType.EXECUTE_SCRIPT.getVal());
                        actionContextList.add(actionContext);
                    }
                    break;

                case HIDE_SECTION:       //9
                case SHOW_SECTION:       //10
                    for (XMLBuilder actionFieldElement : actionFieldsElementList) {
                        FormRuleActionFieldsContext actionFieldsContext = new FormRuleActionFieldsContext();

                        String sectionName = actionFieldElement.getElement(PackageConstants.FormXMLComponents.SECTION_NAME).getText();
                        String formName = actionFieldElement.getElement(PackageConstants.FormXMLComponents.FORM_NAME).getText();
                        String moduleName = actionFieldElement.getElement(PackageConstants.MODULENAME).getText();

                        FacilioModule module = moduleBean.getModule(moduleName);
                        FacilioForm form = FormsAPI.getFormFromDB(formName, module);
                        long sectionId = PackageBeanUtil.getSectionIdFromName(form.getId(), sectionName);

                        actionFieldsContext.setFormSectionId(sectionId);
                        actionFieldsContexts.add(actionFieldsContext);
                    }

                    break;
            }

            actionContext.setFormRuleActionFieldsContext(actionFieldsContexts);
            actionContextList.add(actionContext);

        }

        return actionContextList;
    }


    public static Map<Long, Long> getFormRuleIdVsFormId(boolean fetchSystem, boolean fetchSubFormRulesOnly) throws Exception {

        Map<Long, Long> formRuleIdVsFormId = new HashMap<>();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(formRuleModule));
            add(FieldFactory.getNumberField("formId", "FORM_ID", formRuleModule));
        }};

        Criteria defaultRuleCriteria = new Criteria();
        defaultRuleCriteria.addAndCondition(CriteriaAPI.getCondition("IS_DEFAULT_RULE", "isDefault", String.valueOf(fetchSystem), BooleanOperators.IS));
        if (fetchSubFormRulesOnly) {
            defaultRuleCriteria.addAndCondition(CriteriaAPI.getCondition("SUB_FORM_ID", "subFormId", "", CommonOperators.IS_NOT_EMPTY));
        } else {
            defaultRuleCriteria.addAndCondition(CriteriaAPI.getCondition("SUB_FORM_ID", "subFormId", "", CommonOperators.IS_EMPTY));
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(formRuleModule.getTableName())
                .select(selectableFields);

        if (defaultRuleCriteria != null && !defaultRuleCriteria.isEmpty()) {
            selectRecordBuilder.andCriteria(defaultRuleCriteria);
        }

        if (CollectionUtils.isNotEmpty(applicationIds)) {
            Criteria appIdCriteria = PackageBeanUtil.getFormAppIdCriteria(applicationIds);

            if (fetchSubFormRulesOnly) {
                selectRecordBuilder.innerJoin(ModuleFactory.getFormModule().getTableName())
                        .on(ModuleFactory.getFormModule().getTableName() + ".ID = " + formRuleModule.getTableName() + ".SUB_FORM_ID");
            } else {
                selectRecordBuilder.innerJoin(ModuleFactory.getFormModule().getTableName())
                        .on(ModuleFactory.getFormModule().getTableName() + ".ID = " + formRuleModule.getTableName() + ".FORM_ID");
            }
            selectRecordBuilder.andCriteria(appIdCriteria);
        }

        List<Map<String, Object>> propsList = selectRecordBuilder.get();

        List<FormRuleContext> defaultFormRules = FieldUtil.getAsBeanListFromMapList(propsList, FormRuleContext.class);

        if (CollectionUtils.isNotEmpty(defaultFormRules)) {

            formRuleIdVsFormId = defaultFormRules.stream().collect(Collectors.toMap(FormRuleContext::getId, FormRuleContext::getFormId));

        }
        return formRuleIdVsFormId;
    }


    public static Map<Long, FormRuleContext> fetchFormRules(List<Long> ids) throws Exception {

        FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
        Map<Long, FormRuleContext> ruleIdVsFormRule = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);

        List<Long> idsSubList;

        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            Criteria ruleIdsCriteria = new Criteria();
            ruleIdsCriteria.addAndCondition(CriteriaAPI.getIdCondition(idsSubList, formRuleModule));

            List<FormRuleContext> formRuleContexts = FormRuleAPI.getFormRules(ruleIdsCriteria);

            if (CollectionUtils.isEmpty(formRuleContexts)) {
                return ruleIdVsFormRule;
            }

            FormRuleAPI.setFormRuleActionAndTriggerFieldContext(formRuleContexts);
            FormRuleAPI.setFormRuleCriteria(formRuleContexts);

            formRuleContexts.forEach(rule -> ruleIdVsFormRule.put(rule.getId(), rule));

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());

        }

        return ruleIdVsFormRule;

    }


    public static void convertFormRuleToXMLComponent(FormRuleContext formRuleContext, XMLBuilder formRuleContextElement) throws Exception {

        long formId = formRuleContext.getFormId();
        long subFormId = formRuleContext.getSubFormId();
        String formName = null;
        String subFormName = null;
        String moduleName = null;
        String subFormModuleName = null;
        FacilioForm form, subForm;

        if (formId > 0) {
            form = FormsAPI.getFormFromDB(formId);
            formName = form.getName();
            moduleName = form.getModule().getName();
        }
        if (subFormId > 0) {
            subForm = FormsAPI.getFormFromDB(subFormId);
            subFormName = subForm.getName();
            subFormModuleName = subForm.getModule().getName();
        }

        // Form Rule :
        formRuleContextElement.element(PackageConstants.NAME).text(formRuleContext.getName());
        formRuleContextElement.element(PackageConstants.DESCRIPTION).text(formRuleContext.getDescription());
        formRuleContextElement.element(PackageConstants.FormRuleConstants.TYPE).text(String.valueOf(formRuleContext.getTypeEnum()));
        formRuleContextElement.element(PackageConstants.FormRuleConstants.TRIGGER_TYPE).text(String.valueOf(formRuleContext.getTriggerTypeEnum()));
        formRuleContextElement.element(PackageConstants.FormRuleConstants.FORM_NAME).text(formName);
        formRuleContextElement.element(PackageConstants.FormRuleConstants.SUB_FORM_NAME).text(subFormName);
        formRuleContextElement.element(PackageConstants.MODULENAME).text(moduleName);
        formRuleContextElement.element(PackageConstants.FormRuleConstants.SUB_FORM_MODULE_NAME).text(subFormModuleName);
        formRuleContextElement.element(PackageConstants.FormRuleConstants.EXECUTE_TYPE).text(String.valueOf(formRuleContext.getExecuteTypeEnum()));
        formRuleContextElement.element(PackageConstants.FormRuleConstants.STATUS).text(String.valueOf(formRuleContext.getStatus()));


        //Form Criteria
        if (formRuleContext.getCriteria() != null) {
            formRuleContextElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(formRuleContext.getCriteria(), formRuleContextElement.element(PackageConstants.FormRuleConstants.FORM_RULE_CRITERIA), moduleName));
        }

        //Sub Form Criteria :
        if (formRuleContext.getSubFormCriteria() != null) {
            formRuleContextElement.addElement(PackageBeanUtil.constructBuilderFromCriteria(formRuleContext.getSubFormCriteria(), formRuleContextElement.element(PackageConstants.FormRuleConstants.SUB_FORM_RULE_CRITERIA), subFormModuleName));
        }

        // Form Rule Trigger Fields :
        if (CollectionUtils.isNotEmpty(formRuleContext.getTriggerFields())) {
            XMLBuilder triggerFieldElementsList = formRuleContextElement.element(PackageConstants.FormRuleConstants.TRIGGER_FIELDS_LIST);

            for (FormRuleTriggerFieldContext triggerFieldContext : formRuleContext.getTriggerFields()) {
                if (triggerFieldContext.getFieldId() > 0) {
                    FormField formField = FormsAPI.getFormFieldFromId(triggerFieldContext.getFieldId());

                    if (formField != null) {
                        if (formField.getField() != null) {
                            XMLBuilder triggerFieldElement = triggerFieldElementsList.element(PackageConstants.FormRuleConstants.TRIGGER_FIELD);
                            FacilioField field = formField.getField();

                            triggerFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                            triggerFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(field.getName());
                            triggerFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                        } else if (Objects.equals(formField.getName(), "siteId")) {
                            XMLBuilder triggerFieldElement = triggerFieldElementsList.element(PackageConstants.FormRuleConstants.TRIGGER_FIELD);
                            FacilioField siteId = FieldFactory.getSiteIdField();
                            triggerFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                            triggerFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(siteId.getName());
                            triggerFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                        } else if (Objects.equals(formField.getName(), "tasks")) {
                            XMLBuilder triggerFieldElement = triggerFieldElementsList.element(PackageConstants.FormRuleConstants.TRIGGER_FIELD);
                            triggerFieldElement.element(PackageConstants.MODULENAME).text(moduleName);
                            triggerFieldElement.element(PackageConstants.FormXMLComponents.FACILIO_FIELD_NAME).text(formField.getName());
                            triggerFieldElement.element(PackageConstants.FormRuleConstants.FORM_FIELD_DISPLAY_NAME).text(formField.getDisplayName());
                        }
                    }
                }
            }
        }

        //Form Rule Actions :

        XMLBuilder formRuleActionsList = formRuleContextElement.element(PackageConstants.FormRuleConstants.FORM_ACTIONS_LIST);
        String actionModuleName = StringUtils.isNotEmpty(subFormModuleName) ? subFormModuleName : moduleName;
        FormRulePackageUtil.constructBuilderFromActionType(formRuleActionsList, formRuleContext, actionModuleName);

    }


    public static Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsRuleId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {

            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder ruleElement = idVsData.getValue();

            String moduleName, formName, ruleName, description;
            String subFormModuleName = null;
            String subFormName = null;

            moduleName = ruleElement.getElement(PackageConstants.MODULENAME).getText();
            formName = ruleElement.getElement(PackageConstants.FormRuleConstants.FORM_NAME).text();
            ruleName = ruleElement.getElement(PackageConstants.NAME).text();
            description = ruleElement.getElement(PackageConstants.DESCRIPTION).text();
            if (ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_NAME) != null && ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_MODULE_NAME) != null) {
                subFormName = ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_NAME).text();
                subFormModuleName = ruleElement.getElement(PackageConstants.FormRuleConstants.SUB_FORM_MODULE_NAME).text();
            }

            FormRuleContext formRule = new FormRuleContext();
            if (StringUtils.isNotEmpty(subFormName) && StringUtils.isNotEmpty(subFormModuleName)) {
                FacilioModule subFormModule = moduleBean.getModule(subFormModuleName);
                FacilioForm subForm = FormsAPI.getFormFromDB(subFormName, subFormModule);
                if (subForm != null) {
                    formRule.setSubFormId(subForm.getId());
                }
            }
            FacilioModule module = moduleBean.getModule(moduleName);
            FacilioForm form = FormsAPI.getFormFromDB(formName, module);
            long formId = -1;
            if (form != null) {
                formId = form.getId();
            }
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

}
