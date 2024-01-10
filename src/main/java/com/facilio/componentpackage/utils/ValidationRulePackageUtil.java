package com.facilio.componentpackage.utils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
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
import java.util.stream.Collectors;

public class ValidationRulePackageUtil {

    public static Map<Long, ValidationContext> getAllValidationRule(List<Long> ids) throws Exception {

        FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();
        Map<Long, ValidationContext> validationRuleIdVsFormId = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);
        List<Long> idsSubList;

        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);
            List<ValidationContext> validationRules = ValidationRulesAPI.getValidationsById(idsSubList, validationModule);

            if (CollectionUtils.isEmpty(validationRules)) {
                return validationRuleIdVsFormId;
            }

            validationRules.forEach(rule -> validationRuleIdVsFormId.put(rule.getId(), rule));

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());

        }

        return validationRuleIdVsFormId;

    }

    public static Map<Long, Long> getFormValidationRuleIdVsFormId() throws Exception {

        Map<Long, Long> formValidationRuleIdVsFormId = new HashMap<>();
        List<Long> applicationIds = ApplicationApi.getAllApplicationIds(true);

        FacilioModule validationModule = ModuleFactory.getFormValidationRuleModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getIdField(validationModule));
            add(FieldFactory.getNumberField("parentId", "PARENT_ID", validationModule));
        }};

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(validationModule.getTableName())
                .select(selectableFields);

        if(CollectionUtils.isNotEmpty(applicationIds)){
            Criteria appIdCriteria = PackageBeanUtil.getFormAppIdCriteria(applicationIds);

            builder.innerJoin(ModuleFactory.getFormModule().getTableName()).on(ModuleFactory.getFormModule().getTableName() + ".ID = " +validationModule.getTableName()+".PARENT_ID");
            builder.andCriteria(appIdCriteria);
        }
        List<Map<String, Object>> list = builder.get();

        List<ValidationContext> validationContexts = FieldUtil.getAsBeanListFromMapList(list, ValidationContext.class);

        if (CollectionUtils.isNotEmpty(validationContexts)) {
            formValidationRuleIdVsFormId = validationContexts.stream().collect(Collectors.toMap(ValidationContext::getId, ValidationContext::getParentId));
        }

        return formValidationRuleIdVsFormId;
    }

    public static void convertValidationRuleToXMLComponent(ValidationContext validationRule, XMLBuilder validationRuleElement) throws Exception {

        NamedCriteria namedCriteria = null;
        WorkflowContext errorMessagePlaceHolderScript = null;
        String parentFormName, parentFormModuleName;

        FacilioForm parentForm = FormsAPI.getFormFromDB(validationRule.getParentId());
        if (parentForm == null) {
            return;
        }

        parentFormName = parentForm.getName();
        parentFormModuleName = parentForm.getModule().getName();

        if (validationRule.getNamedCriteria() != null) {
            namedCriteria = validationRule.getNamedCriteria();
        } else if (validationRule.getNamedCriteriaId() > 0) {
            namedCriteria = NamedCriteriaAPI.getNamedCriteria(validationRule.getNamedCriteriaId());
        } else {
            return;
        }

        if (validationRule.getErrorMessagePlaceHolderScript() != null) {
            errorMessagePlaceHolderScript = validationRule.getErrorMessagePlaceHolderScript();
        } else if (validationRule.getErrorMessagePlaceHolderScriptId() > 0) {
            errorMessagePlaceHolderScript = WorkflowUtil.getWorkflowContext(validationRule.getErrorMessagePlaceHolderScriptId());
        }

        // Validation Rule

        validationRuleElement.element(PackageConstants.ValidationRuleConstants.NAME).text(validationRule.getName());
        validationRuleElement.element(PackageConstants.ValidationRuleConstants.PARENT_FORM_NAME).text(parentFormName);
        validationRuleElement.element(PackageConstants.ValidationRuleConstants.PARENT_FORM_MODULE_NAME).text(parentFormModuleName);
        validationRuleElement.element(PackageConstants.ValidationRuleConstants.ERROR_MESSAGE).text(validationRule.getErrorMessage());

        // Error Message Placeholder

        if (errorMessagePlaceHolderScript != null && StringUtils.isNotEmpty(errorMessagePlaceHolderScript.getWorkflowV2String())) {

            validationRuleElement.element(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).text(String.valueOf(errorMessagePlaceHolderScript.getIsV2Script()));
            validationRuleElement.element(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).cData(String.valueOf(errorMessagePlaceHolderScript.getWorkflowV2String()));

        }

        //Named Criteria

        validationRuleElement.element(PackageConstants.ValidationRuleConstants.NAMED_CRITERIA_NAME).text(namedCriteria.getName());


    }

    public static ValidationContext constructValidationRuleFromXMLBuilder(XMLBuilder validationRuleElement, ModuleBean moduleBean) throws Exception {

        String name = validationRuleElement.getElement(PackageConstants.ValidationRuleConstants.NAME).text();
        String formName = validationRuleElement.getElement(PackageConstants.ValidationRuleConstants.PARENT_FORM_NAME).text();
        String moduleName = validationRuleElement.getElement(PackageConstants.ValidationRuleConstants.PARENT_FORM_MODULE_NAME).text();
        String errorMessage = validationRuleElement.getElement(PackageConstants.ValidationRuleConstants.ERROR_MESSAGE).text();
        String namedCriteriaName = validationRuleElement.getElement(PackageConstants.ValidationRuleConstants.NAMED_CRITERIA_NAME).text();
        String workflowV2String = null;
        boolean isV2Script = false;
        if(validationRuleElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING)!=null) {
            workflowV2String = validationRuleElement.getElement(PackageConstants.WorkFlowRuleConstants.WORKFLOW_STRING).getCData();
            isV2Script = Boolean.parseBoolean(validationRuleElement.getElement(PackageConstants.WorkFlowRuleConstants.IS_V2_SCRIPT).getText());
        }

        FacilioModule module = StringUtils.isNotEmpty(moduleName) ? moduleBean.getModule(moduleName) : null;
        FacilioForm form = StringUtils.isNotEmpty(formName) ? FormsAPI.getFormFromDB(formName, module) : null;

        WorkflowContext workflowContext = new WorkflowContext();
        if(StringUtils.isNotEmpty(workflowV2String)){
            workflowContext.setWorkflowV2String(workflowV2String);
            workflowContext.setIsV2Script(isV2Script);
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", namedCriteriaName, StringOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_MODULEID", "namedCriteriaModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        NamedCriteria namedCriteria = ValidationRulesAPI.getNamedCriteria(criteria);
        if (namedCriteria == null) {
            return null;
        }

        ValidationContext validationRule = new ValidationContext();
        validationRule.setName(name);
        if (form != null) {
            validationRule.setParentId(form.getId());
        }
        validationRule.setErrorMessage(errorMessage);
        validationRule.setErrorMessagePlaceHolderScript(workflowContext);
        validationRule.setNamedCriteriaId(namedCriteria.getId());

        return validationRule;
    }

    public static Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {

        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsRuleId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {

            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder ruleElement = idVsData.getValue();

            String name = ruleElement.getElement(PackageConstants.ValidationRuleConstants.NAME).text();
            String formName = ruleElement.getElement(PackageConstants.ValidationRuleConstants.PARENT_FORM_NAME).text();
            String moduleName = ruleElement.getElement(PackageConstants.ValidationRuleConstants.PARENT_FORM_MODULE_NAME).text();
            String errorMessage = ruleElement.getElement(PackageConstants.ValidationRuleConstants.ERROR_MESSAGE).text();


            FacilioModule module = moduleBean.getModule(moduleName);
            FacilioForm form = FormsAPI.getFormFromDB(formName, module);

            ValidationContext validationRule = new ValidationContext();
            validationRule.setName(name);
            if (form != null) {
                validationRule.setParentId(form.getId());
            }
            validationRule.setErrorMessage(errorMessage);

            long validationRuleId = ValidationRulesAPI.getFormValidationId(validationRule);

            if (validationRuleId > 0) {
                uniqueIdentifierVsRuleId.put(uniqueIdentifier, validationRuleId);
            }

        }
        return uniqueIdentifierVsRuleId;
    }

}
