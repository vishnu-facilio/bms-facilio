package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class UpdateMultiCurrencyDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                || MapUtils.isEmpty(recordMap)
                || CollectionUtils.isEmpty(recordMap.get(moduleName))) {
            return false;
        }

        List<FacilioField> multiCurrencyFields = CurrencyUtil.getMultiCurrencyFieldsForModule(moduleName);
        if(CollectionUtils.isEmpty(multiCurrencyFields)){
            return false;
        }

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        if(beanClass == null) {
            beanClass = FacilioConstants.ContextNames.getClassFromModule(Constants.getModBean().getModule(moduleName));
        }

        Map<Long, ModuleBaseWithCustomFields> oldRecord = Constants.getOldRecordMap(context);
        List<ModuleBaseWithCustomFields> newRecords = new ArrayList<>();
        Map<Long, Set<String>> patchFieldNames = (Map<Long, Set<String>>) context.get(FacilioConstants.ContextNames.PATCH_FIELD_NAMES);

        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        Map<String, CurrencyContext> currencyCodeVsCurrency = CurrencyUtil.getCurrencyMap();

        for (ModuleBaseWithCustomFields newProp : records) {
            Set<String> currRecordPatchFieldNames = patchFieldNames.getOrDefault(newProp.getId(), new HashSet<>());
            ModuleBaseWithCustomFields oldProp = oldRecord.get(newProp.getId());

            Map<String, Object> newPropAsProperties = FieldUtil.getAsProperties(newProp);
            Map<String, Object> oldPropAsProperties = FieldUtil.getAsProperties(oldProp);

            String baseCurrencyCurrencyCode = baseCurrency != null ? baseCurrency.getCurrencyCode() : null;
            String oldCurrencyCode = StringUtils.defaultIfEmpty(oldProp.getCurrencyCode(), baseCurrencyCurrencyCode);
            String newCurrencyCode = StringUtils.defaultIfEmpty(newProp.getCurrencyCode(), oldCurrencyCode);

            CurrencyContext currency = (newCurrencyCode != null && MapUtils.isNotEmpty(currencyCodeVsCurrency))
                    ? (currencyCodeVsCurrency.containsKey(newCurrencyCode) ? currencyCodeVsCurrency.get(newCurrencyCode) : currencyCodeVsCurrency.get(oldCurrencyCode)) : null;
            newCurrencyCode = (currencyCodeVsCurrency.containsKey(newCurrencyCode)) ? newCurrencyCode : oldCurrencyCode;

            if(baseCurrency == null || (StringUtils.equals(oldCurrencyCode, baseCurrencyCurrencyCode) && StringUtils.equals(newCurrencyCode, baseCurrencyCurrencyCode))){
                newProp.setCurrencyCode(null);
                newProp.setExchangeRate(1);
            }
            else if (currency != null && currencyCodeVsCurrency.size() > 1) {
                if (!newCurrencyCode.equals(oldCurrencyCode)) {
                    double exchangeRate = currency.getExchangeRate();
                    newProp.setExchangeRate(exchangeRate);
                    computeCurrencyValueForFieldsNotInPatch(newCurrencyCode, newPropAsProperties, oldPropAsProperties, currRecordPatchFieldNames, multiCurrencyFields, currencyCodeVsCurrency);
                } else {
                    newProp.setCurrencyCode(oldProp.getCurrencyCode());
                    newProp.setExchangeRate(oldProp.getExchangeRate());
                }
            } else {
                newProp.setCurrencyCode(null);
                newProp.setExchangeRate(1);
            }

            CurrencyUtil.setBaseCurrencyValueForRecord(newPropAsProperties, multiCurrencyFields);
            newRecords.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(newPropAsProperties, beanClass));
        }

        recordMap.put(moduleName, newRecords);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }

    private void computeCurrencyValueForFieldsNotInPatch(String newCurrencyCode, Map<String, Object> newProp,
                                                         Map<String, Object> oldProp, Set<String> patchFieldNames, List<FacilioField> multiCurrencyFields,
                                                         Map<String, CurrencyContext> currencyCodeVsCurrency) throws Exception {
        CurrencyContext newCurrency = currencyCodeVsCurrency.get(newCurrencyCode);

        for (FacilioField multiCurrencyField : multiCurrencyFields) {
            if(!patchFieldNames.contains(multiCurrencyField.getName())) {
                Double oldCurrencyValue = oldProp.containsKey(multiCurrencyField.getName()) ? (Double) oldProp.get(multiCurrencyField.getName()) : null;
                if (oldCurrencyValue != null) {
                    double oldExchangeRate = oldProp.get("exchangeRate") != null ? Double.parseDouble(oldProp.get("exchangeRate") + "") : 1;
                    double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(oldCurrencyValue, oldExchangeRate);
                    double updatedCurrencyValue = CurrencyUtil.getEquivalentCurrencyValue(baseCurrencyValue, newCurrency.getExchangeRate());
                    newProp.put(multiCurrencyField.getName(), updatedCurrencyValue);
                }
            }
        }
    }
}
