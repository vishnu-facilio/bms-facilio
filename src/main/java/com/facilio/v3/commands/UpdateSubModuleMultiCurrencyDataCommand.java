package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.enums.MultiCurrencyParentVsSubModule;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class UpdateSubModuleMultiCurrencyDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String  moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        Map<Long, ModuleBaseWithCustomFields> oldRecordsMap = Constants.getOldRecordMap(context);

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                || MapUtils.isEmpty(recordMap)
                || MapUtils.isEmpty(oldRecordsMap)
                || CollectionUtils.isEmpty(recordMap.get(moduleName))) {
            return false;
        }

        Map<Long, List<String>> patchFieldNamesMap = (Map<Long, List<String>>) context.get(FacilioConstants.ContextNames.PATCH_FIELD_NAMES);
        Map<String, List<MultiCurrencyParentVsSubModule.SubModule>> parentVsSubModulesMap = MultiCurrencyParentVsSubModule.PARENT_MODULE_VS_SUB_MODULES;
        List<MultiCurrencyParentVsSubModule.SubModule> subModulesList = parentVsSubModulesMap.get(moduleName);

        if(CollectionUtils.isEmpty(subModulesList) || MapUtils.isEmpty(patchFieldNamesMap)){
            return false;
        }

        List<ModuleBaseWithCustomFields> parentRecords = recordMap.get(moduleName);

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyCodeVsCurrency = Constants.getCurrencyMap(context);

        for (ModuleBaseWithCustomFields parentRecord : parentRecords) {
            long parentRecordId = parentRecord.getId();
            ModuleBaseWithCustomFields oldRecord = oldRecordsMap.get(parentRecordId);
            // compare currency change in parent module if not break
            String oldCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(oldRecord), baseCurrency);
            String newCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(parentRecord), baseCurrency);
            newCurrencyCode = (currencyCodeVsCurrency.containsKey(newCurrencyCode)) ? newCurrencyCode : oldCurrencyCode;

            List<String> patchFieldNames = patchFieldNamesMap.get(parentRecordId);
            if(!patchFieldNames.contains("currencyCode") || StringUtils.equals(oldCurrencyCode, newCurrencyCode)){
                break;
            }

            for (MultiCurrencyParentVsSubModule.SubModule subModule : subModulesList) {
                List<FacilioField> multiCurrencyFields = CurrencyUtil.getMultiCurrencyFields(subModule.getModuleName());
                String parentModuleCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(parentRecord), baseCurrency);

                if (CollectionUtils.isNotEmpty(multiCurrencyFields) && StringUtils.isNotEmpty(parentModuleCurrencyCode)) {
                    FacilioField parentModuleField = subModule.getParentModuleField();
                    ModuleBean modBean = Constants.getModBean();
                    FacilioModule subModModule = modBean.getModule(subModule.getModuleName());
                    // select submodule records
                    List<FacilioField> selectFields = new ArrayList<>(multiCurrencyFields);
                    selectFields.addAll(FieldFactory.getCurrencyPropsFields(subModModule));
                    selectFields.add(FieldFactory.getIdField(subModModule));

                    SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                            .module(subModModule)
                            .select(selectFields)
                            .andCondition(CriteriaAPI.getCondition(parentModuleField, String.valueOf(parentRecordId), NumberOperators.EQUALS));

                    List<Map<String, Object>> subModuleRecords = selectRecordsBuilder.getAsProps();

                    if (CollectionUtils.isNotEmpty(subModuleRecords)) {
                        List<FacilioField> updateFields = new ArrayList<>(multiCurrencyFields);
                        updateFields.addAll(FieldFactory.getCurrencyPropsFields(subModModule));

                        List<FacilioField> baseCurrencyValueFields = CurrencyUtil.getBaseCurrencyFieldsForModule(subModModule);

                        if (CollectionUtils.isNotEmpty(baseCurrencyValueFields)) {
                            updateFields.addAll(baseCurrencyValueFields);
                        }

                        for (Map<String, Object> subModuleRecord : subModuleRecords) {
                            String subModuleCurrencyCode = (String) subModuleRecord.get("currencyCode");
                            CurrencyContext currency = currencyCodeVsCurrency.get(parentModuleCurrencyCode);
                            double oldExchangeRate = subModuleRecord.get("exchangeRate") != null ? Double.parseDouble(String.valueOf(subModuleRecord.get("exchangeRate"))) : 1;

                            boolean isCurrencyChanged = CurrencyUtil.checkAndUpdateCurrencyCodeAndExchangeRate(subModuleRecord, parentRecord,
                                    baseCurrency, currencyCodeVsCurrency, parentModuleCurrencyCode, subModuleCurrencyCode);
                            if(isCurrencyChanged){
                                updateCurrencyValueForSubModule(subModuleRecord, subModModule, multiCurrencyFields, updateFields, parentModuleField, parentRecordId, currency, oldExchangeRate);
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    private void updateCurrencyValueForSubModule(Map<String, Object> subModuleRecord, FacilioModule subModModule, List<FacilioField> multiCurrencyFields, List<FacilioField> updateFields,
                                                 FacilioField parentModuleField, long parentRecordId, CurrencyContext currency, double oldExchangeRate) throws Exception {

        for (FacilioField multiCurrencyField : multiCurrencyFields) {
            Double currencyValue = subModuleRecord.get(multiCurrencyField.getName()) != null && StringUtils.isNotEmpty(String.valueOf(subModuleRecord.get(multiCurrencyField.getName())))
                    ? Double.parseDouble(String.valueOf(subModuleRecord.get(multiCurrencyField.getName()))) : null;
            if (currencyValue != null) {
                double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, oldExchangeRate);
                double exchangeRate = currency.getExchangeRate();
                double updatedCurrencyValue = CurrencyUtil.getEquivalentCurrencyValue(baseCurrencyValue, exchangeRate);
                subModuleRecord.put(multiCurrencyField.getName(), updatedCurrencyValue);
            }
        }

        CurrencyUtil.setBaseCurrencyConvertedValuesForRecord(subModuleRecord, multiCurrencyFields);

        UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<>()
                .module(subModModule)
                .fields(updateFields)
                .andCondition(CriteriaAPI.getCondition(parentModuleField, String.valueOf(parentRecordId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(subModModule), String.valueOf(subModuleRecord.get("id")), NumberOperators.EQUALS));
        updateBuilder.ignoreSplNullHandling();
        updateBuilder.updateViaMap(subModuleRecord);
    }
}

