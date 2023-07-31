package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.MultiCurrencyActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.chain.FacilioContext;
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
import org.json.simple.JSONObject;

import java.util.*;

public class UpdateMultiCurrencyDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        Map<Long, ModuleBaseWithCustomFields> oldRecordsMap = Constants.getOldRecordMap(context);


        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                || MapUtils.isEmpty(recordMap)
                || MapUtils.isEmpty(oldRecordsMap)
                || CollectionUtils.isEmpty(recordMap.get(moduleName))) {
            return false;
        }

        List<FacilioField> multiCurrencyFields = CurrencyUtil.getMultiCurrencyFields(moduleName);
        if(CollectionUtils.isEmpty(multiCurrencyFields)){
            return false;
        }

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);
        if(beanClass == null) {
            beanClass = FacilioConstants.ContextNames.getClassFromModule(Constants.getModBean().getModule(moduleName));
        }

        List<ModuleBaseWithCustomFields> newRecords = new ArrayList<>();
        Map<Long, List<String>> patchFieldNames = (Map<Long, List<String>>) context.getOrDefault(FacilioConstants.ContextNames.PATCH_FIELD_NAMES, new HashMap<>());

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyCodeVsCurrency = Constants.getCurrencyMap(context);

        for (ModuleBaseWithCustomFields newRecord : records) {
            List<String> currRecordPatchFieldNames = patchFieldNames.getOrDefault(newRecord.getId(), new ArrayList<>());
            ModuleBaseWithCustomFields oldRecord = oldRecordsMap.get(newRecord.getId());

            Map<String, Object> newRecordAsMap = FieldUtil.getAsProperties(newRecord);
            Map<String, Object> oldRecordAsMap = FieldUtil.getAsProperties(oldRecord);

            String oldCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(oldRecordAsMap, baseCurrency);
            String newCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOldCurrencyDefault(newRecordAsMap, oldCurrencyCode);
            newCurrencyCode = (currencyCodeVsCurrency.containsKey(newCurrencyCode)) ? newCurrencyCode : oldCurrencyCode;

            boolean isCurrencyCodeChanged = CurrencyUtil.checkAndUpdateCurrencyCodeAndExchangeRate(newRecordAsMap, oldRecord,
                    baseCurrency, currencyCodeVsCurrency, newCurrencyCode, oldCurrencyCode);
            if(isCurrencyCodeChanged){
                CurrencyUtil.computeCurrencyValueForFieldsNotInPatch(newCurrencyCode, newRecordAsMap, oldRecordAsMap, currRecordPatchFieldNames, multiCurrencyFields, currencyCodeVsCurrency);
                addCurrencyUpdateActivity(oldCurrencyCode, newCurrencyCode, newRecord, context);
            }
            CurrencyUtil.setBaseCurrencyConvertedValuesForRecord(newRecordAsMap, multiCurrencyFields);
            newRecords.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(newRecordAsMap, beanClass));
        }

        recordMap.put(moduleName, newRecords);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }

    private void addCurrencyUpdateActivity(String oldCurrencyCode, String newCurrencyCode, ModuleBaseWithCustomFields record, Context context) {
        JSONObject info = new JSONObject();
        info.put(FacilioConstants.ContextNames.OLD_CURRENCY_CODE, oldCurrencyCode);
        info.put(FacilioConstants.ContextNames.NEW_CURRENCY_CODE, newCurrencyCode);
        CommonCommandUtil.addActivityToContext(record.getId(), -1L, MultiCurrencyActivityType.CHANGED_TO, info, (FacilioContext) context);
    }
}
