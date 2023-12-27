package com.facilio.v3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;

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

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyCodeVsCurrency = Constants.getCurrencyMap(context);

        List<ModuleBaseWithCustomFields> newRecords = CurrencyUtil.addMultiCurrencyData(moduleName, multiCurrencyFields, records, beanClass, baseCurrency, currencyCodeVsCurrency);
        recordMap.put(moduleName, newRecords);

        Set<String> extendedModules = Constants.getExtendedModules(context);
        if (CollectionUtils.isNotEmpty(extendedModules)) {
            for (String extendedModule : extendedModules) {
                recordMap.put(extendedModule, newRecords);
            }
        }

        context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        return false;
    }

}
