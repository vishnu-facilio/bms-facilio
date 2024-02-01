package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ApprovalStateTransitionRuleContext;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class EvaluateFormValidationRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long stateTransitionId = (Long) context.get(FacilioConstants.ContextNames.TRANSITION_ID);
        List customButtonIds = (List) context.get(FacilioConstants.ContextNames.WORKFLOW_RULE_ID_LIST);
        Long customButtonId = org.apache.commons.collections.CollectionUtils.isNotEmpty(customButtonIds) ? (Long) customButtonIds.get(0) : null;
        Long approvalTransitionId = (Long) context.get(FacilioConstants.ContextNames.APPROVAL_TRANSITION_ID);

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = (Map<String, List<ModuleBaseWithCustomFields>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        List<Long> buttonIds = new ArrayList<>();
        List<Long> buttonFormIds = new ArrayList<>();
        List<Long> formIds = new ArrayList<>();

        if (stateTransitionId != null && stateTransitionId > 0) {
            buttonIds.add(stateTransitionId);
            StateflowTransitionContext stateTransition = (StateflowTransitionContext) WorkflowRuleAPI.getWorkflowRule(stateTransitionId);
            addFormIds(buttonFormIds, moduleName, recordMap, stateTransition.getFormId());
        }

        if (customButtonId != null && customButtonId > 0) {
            buttonIds.add(customButtonId);
            CustomButtonRuleContext customButton = (CustomButtonRuleContext) WorkflowRuleAPI.getWorkflowRule(customButtonId);
            addFormIds(buttonFormIds, moduleName, recordMap, customButton.getFormId());
        }

        if (approvalTransitionId != null && approvalTransitionId > 0) {
            buttonIds.add(approvalTransitionId);
            ApprovalStateTransitionRuleContext approvalTransition = (ApprovalStateTransitionRuleContext) WorkflowRuleAPI.getWorkflowRule(approvalTransitionId);
            addFormIds(buttonFormIds, moduleName, recordMap, approvalTransition.getFormId());
        }

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        if (beanClass == null) {
            beanClass = FacilioConstants.ContextNames.getClassFromModule(Constants.getModBean().getModule(moduleName));
        }

        if (MapUtils.isEmpty(recordMap)) {
            return false;
        }
        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            long formId = getActionOrFormId(record);
            if (formId > 0) {
                formIds.add(formId);
            }
        }

        if ((CollectionUtils.isEmpty(formIds) && CollectionUtils.isEmpty(buttonFormIds))) {
            return false;
        }

        Map<Long, List<ValidationContext>> buttonsFormValidationRule = getValidationContextMap(buttonFormIds);
        Map<Long, List<ValidationContext>> formVsRuleMap = getValidationContextMap(formIds);

        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);
        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);

        if (CollectionUtils.isNotEmpty(buttonIds)) {
            validateRecord(recordMap, buttonFormIds, buttonsFormValidationRule, moduleName, beanClass, currencyMap, baseCurrency);
        } else {
            validateRecordFormId(recordMap, formVsRuleMap, moduleName, beanClass, currencyMap, baseCurrency);
        }

        return false;
    }

    private void addFormIds(List<Long> buttonFormIds, String moduleName, Map<String, List<ModuleBaseWithCustomFields>> recordMap, long transitionFormId) throws Exception {
        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            long formId = getActionOrFormId(record);
            if (formId > 0 && transitionFormId > 0 && formId == transitionFormId) {
                buttonFormIds.add(formId);
            }
        }
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

    private void validateRecord(Map<String, List<ModuleBaseWithCustomFields>> recordMap, List<Long> formIds, Map<Long, List<ValidationContext>> formVsRuleMap,
                                String moduleName, Class beanClass, Map<String, CurrencyContext> currencyMap, CurrencyContext baseCurrency) throws Exception {

        if (MapUtils.isEmpty(formVsRuleMap)) {
            return;
        }

        List<FacilioField> multiCurrencyFields = new ArrayList<>();
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
            multiCurrencyFields = CurrencyUtil.getMultiCurrencyFields(moduleName);
        }

        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            for (long formId : formIds) {
                if (formId > 0 && formVsRuleMap.containsKey(formId)) {
                    if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CollectionUtils.isNotEmpty(multiCurrencyFields)) {
                        Map<String, Object> props = FieldUtil.getAsProperties(record);
                        CurrencyUtil.replaceCurrencyValueWithBaseCurrencyValue(props, multiCurrencyFields, baseCurrency, currencyMap);
                        record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass);
                    }
                    ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(formId));
                }
            }
        }

    }

    private void validateRecordFormId(Map<String, List<ModuleBaseWithCustomFields>> recordMap, Map<Long, List<ValidationContext>> formVsRuleMap,
                                      String moduleName, Class beanClass, Map<String, CurrencyContext> currencyMap, CurrencyContext baseCurrency) throws Exception {

        if (MapUtils.isEmpty(formVsRuleMap)) {
            return;
        }

        List<FacilioField> multiCurrencyFields = new ArrayList<>();
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)) {
            multiCurrencyFields = CurrencyUtil.getMultiCurrencyFields(moduleName);
        }

        for (ModuleBaseWithCustomFields record : recordMap.get(moduleName)) {
            long formId = getActionOrFormId(record);
            if (formId > 0 && formVsRuleMap.containsKey(formId)) {
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY) && CollectionUtils.isNotEmpty(multiCurrencyFields)) {
                    Map<String, Object> props = FieldUtil.getAsProperties(record);
                    CurrencyUtil.replaceCurrencyValueWithBaseCurrencyValue(props, multiCurrencyFields, baseCurrency, currencyMap);
                    record = (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass);
                }

                ValidationRulesAPI.validateRecord(moduleName, record, formVsRuleMap.get(formId));

            }
        }
    }

    private long getActionOrFormId(ModuleBaseWithCustomFields record) throws Exception {
        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.ENABLE_ACTION_FORM)) {
            return record.getActionFormId();
        } else {
            return record.getFormId();
        }
    }
}
