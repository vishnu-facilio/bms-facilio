package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddMultiCurrencyDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                || MapUtils.isEmpty(recordMap)
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

        CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();
        List<ModuleBaseWithCustomFields> newRecords = new ArrayList<>();
        Map<String, CurrencyContext> currencyCodeVsCurrency = CurrencyUtil.getCurrencyMap();

        for (ModuleBaseWithCustomFields record : records) {
            String currencyCode = record.getCurrencyCode();
            CurrencyContext currency = StringUtils.isNotEmpty(currencyCode) ? MapUtils.isNotEmpty(currencyCodeVsCurrency) ? currencyCodeVsCurrency.get(currencyCode) : null : null;

            if (currency == null || baseCurrency == null || StringUtils.isEmpty(currencyCode) || currencyCodeVsCurrency.size() == 1) {
                record.setCurrencyCode(null);
                record.setExchangeRate(1);
            } else {
                double exchangeRate = currency.getExchangeRate();
                record.setExchangeRate(exchangeRate);
            }

            Map<String, Object> props = FieldUtil.getAsProperties(record);
            CurrencyUtil.setBaseCurrencyValueForRecord(props, multiCurrencyFields);

            newRecords.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass));
        }

        recordMap.put(moduleName, newRecords);
        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }
}
