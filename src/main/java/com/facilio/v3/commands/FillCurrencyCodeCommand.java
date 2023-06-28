package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class FillCurrencyCodeCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.MULTI_CURRENCY)
                || MapUtils.isEmpty(recordMap)
                || CollectionUtils.isEmpty(recordMap.get(moduleName))){
            return false;
        }

        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        Map<String, Object> currencyInfo = CurrencyUtil.getCurrencyInfo();
        for (ModuleBaseWithCustomFields record : records) {
            String currencyCode = record.getCurrencyCode();
            if(StringUtils.isEmpty(currencyCode)) {
                currencyCode = (String) currencyInfo.get("currencyCode");
                record.setCurrencyCode(currencyCode);
            }
            if(record.getExchangeRate() <= 0){
                record.setExchangeRate(1);
            }
        }
        return false;
    }
}
