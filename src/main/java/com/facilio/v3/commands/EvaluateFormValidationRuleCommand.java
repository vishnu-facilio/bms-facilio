package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.*;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

@Log4j
public class EvaluateFormValidationRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
        List customButtonIds = (List) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST);
        Long customButtonId = org.apache.commons.collections.CollectionUtils.isNotEmpty(customButtonIds) ? (Long) customButtonIds.get(0) : null;
        Long approvalTransitionId = (Long) context.get(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID);

        List<Long> buttonIds = new ArrayList<>();
        List<Long> buttonFormIds = new ArrayList<>();
        List<Long> formIds = new ArrayList<>();

        if (stateTransitionId != null && stateTransitionId > 0) {
            buttonIds.add(stateTransitionId);
            StateflowTransitionContext stateTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(stateTransitionId);
            if (stateTransition.getFormId() > 0 && isCurrentModuleForm(stateTransition.getForm(), moduleName)) {
                buttonFormIds.add(stateTransition.getFormId());
            }
        }

        if (customButtonId != null && customButtonId > 0) {
            buttonIds.add(customButtonId);
            CustomButtonRuleContext customButton = (CustomButtonRuleContext) WorkflowRuleAPI.getWorkflowRule(customButtonId);
            if (customButton.getFormId() > 0 && isCurrentModuleForm(customButton.getForm(), moduleName)) {
                buttonFormIds.add(customButton.getFormId());
            }
        }

        if (approvalTransitionId != null && approvalTransitionId > 0) {
            buttonIds.add(approvalTransitionId);
            ApprovalStateTransitionRuleContext approvalTransition = (ApprovalStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(approvalTransitionId);
            if (approvalTransition.getFormId() > 0 && isCurrentModuleForm(approvalTransition.getForm(), moduleName)) {
                buttonFormIds.add(approvalTransition.getFormId());
            }
        }

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        if(beanClass == null) {
            beanClass = FacilioConstants.ContextNames.getClassFromModule(Constants.getModBean().getModule(moduleName));
        }
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            if (record.getFormId() > 0) {
                formIds.add(record.getFormId());
            }
        }

        if ((CollectionUtils.isEmpty(formIds) && CollectionUtils.isEmpty(buttonFormIds)) || MapUtils.isEmpty(recordMap)) {
            return false;
        }

        Map<Long, List<ValidationContext>> buttonsFormValidationRule = getValidationContextMap(buttonFormIds);
        Map<Long, List<ValidationContext>> formVsRuleMap = getValidationContextMap(formIds);

        if (MapUtils.isNotEmpty(buttonsFormValidationRule) && MapUtils.isNotEmpty(recordMap)) {
            validateRecord(recordMap, buttonFormIds, buttonsFormValidationRule, moduleName, beanClass);
        }

        if (MapUtils.isNotEmpty(formVsRuleMap) && MapUtils.isNotEmpty(recordMap)) {
            validateRecordFormId(recordMap, formVsRuleMap, moduleName, buttonIds, beanClass);
        }

        return false;
    }

    private boolean isCurrentModuleForm(FacilioForm form, String moduleName) {

        if (form == null && form.getModule() == null) {
            return false;
        }

        String formModuleName = form.getModule().getName();
        return Objects.equals(formModuleName, moduleName);

    }

    private Map<Long, List<ValidationContext>> getValidationContextMap(List<Long> formIds) throws Exception {

        if (CollectionUtils.isEmpty(formIds)) {
            return null;
        }

        Map<Long, List<ValidationContext>> formVsRuleMap = new HashMap<>();

        for (long formId : formIds) {

            List<ValidationContext> validations = ValidationRulesAPI.getValidationsByParentId(formId, ModuleFactory.getFormValidationRuleModule(), null, null);
            if (CollectionUtils.isNotEmpty(validations)) {
                formVsRuleMap.put(formId, validations);
            }

        }

        return formVsRuleMap;

    }

    private void validateRecord(Map<String, List<ModuleBaseWithCustomFields>> recordMap, List<Long> formIds, Map<Long, List<ValidationContext>> formVsRuleMap, String moduleName, Class beanClass) throws Exception {

        List<FacilioField> multiCurrencyFields = new ArrayList<>();
        CurrencyContext baseCurrency = new CurrencyContext();
        Map<String, CurrencyContext> currencyCodeVsCurrency = new HashMap<>();
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
            multiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsForModule(moduleName);
            baseCurrency = CurrencyUtil.getBaseCurrency();
            currencyCodeVsCurrency = CurrencyUtil.getCurrencyMap();
        }

        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            for (long formId : formIds) {
                if (formId > 0 && formVsRuleMap.containsKey(formId)) {
                    if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CollectionUtils.isNotEmpty(multiCurrencyFields)) {
                        Map<String, Object> props = FieldUtil.getAsProperties(record);
                        CurrencyUtil.replaceCurrencyValueWithBaseCurrencyValue(props, multiCurrencyFields, baseCurrency, currencyCodeVsCurrency);
                        record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass);
                    }
                    ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(formId));
                }
            }
        }

    }

    private void validateRecordFormId(Map<String, List<ModuleBaseWithCustomFields>> recordMap, Map<Long, List<ValidationContext>> formVsRuleMap, String moduleName, List<Long> buttonIds, Class beanClass) throws Exception {

        // TODO remove after tracking and use validateRecord....
        long orgId = AccountUtil.getCurrentOrg().getOrgId();

        List<FacilioField> multiCurrencyFields = new ArrayList<>();
        CurrencyContext baseCurrency = new CurrencyContext();
        Map<String, CurrencyContext> currencyCodeVsCurrency = new HashMap<>();
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
            multiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsForModule(moduleName);
            baseCurrency = CurrencyUtil.getBaseCurrency();
            currencyCodeVsCurrency = CurrencyUtil.getCurrencyMap();
        }

        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            long formId = record.getFormId();
            if (formId > 0 && formVsRuleMap.containsKey(formId)) {
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CollectionUtils.isNotEmpty(multiCurrencyFields)) {
                    Map<String, Object> props = FieldUtil.getAsProperties(record);
                    CurrencyUtil.replaceCurrencyValueWithBaseCurrencyValue(props, multiCurrencyFields, baseCurrency, currencyCodeVsCurrency);
                    record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass);
                }
                if (CollectionUtils.isNotEmpty(buttonIds)) {
                    try {
                        ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(formId));
                    } catch (Exception e) {
                        LOGGER.debug("Form Validation Executed For formId : " + formId + " and moduleName : " + moduleName + " and orgId : " + orgId);
                        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.DISABLE_FORM_VALIDATION_RULE)) {
                            throw e;
                        }
                    }
                } else {
                    ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(formId));
                }
            }
        }
    }

}
