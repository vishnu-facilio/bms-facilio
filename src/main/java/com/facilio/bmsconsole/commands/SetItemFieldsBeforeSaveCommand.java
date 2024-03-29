package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.enums.CostType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.CurrencyUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetItemFieldsBeforeSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemContext> items = recordMap.get(moduleName);

        CurrencyContext baseCurrency = Constants.getBaseCurrency(context);
        Map<String, CurrencyContext> currencyMap = Constants.getCurrencyMap(context);

        if (CollectionUtils.isNotEmpty(items)) {
            for (V3ItemContext item : items) {
                if(item.getItemType()!=null){
                    V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,item.getItemType().getId(),V3ItemTypesContext.class);
                    if(item.getCostType()==null){
                        if(itemType.getCostType()!=null){
                            item.setCostType(itemType.getCostType());
                        }else{
                            item.setCostType(CostType.FIFO.getIndex());
                        }
                    }
                }
                List<V3PurchasedItemContext> purchasedItems = item.getPurchasedItems();
                if (CollectionUtils.isNotEmpty(purchasedItems)) {
                    V3PurchasedItemContext lastPurchasedItem = purchasedItems.stream()
                            .reduce((first, second) -> second).orElse(null);
                    if (lastPurchasedItem != null) {
                        String currencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(lastPurchasedItem), baseCurrency);
                        CurrencyUtil.setCurrencyCodeAndExchangeRateForWrite(item, baseCurrency, currencyMap, currencyCode, lastPurchasedItem.getExchangeRate());
                    }
                }
            }
        }

        context.put(Constants.RECORD_MAP, recordMap);
        return false;
    }
}
