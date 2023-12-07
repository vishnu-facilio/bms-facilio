package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ComputeWeightedAverageCostCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        List<V3ItemContext> items = (List<V3ItemContext>) context.get(FacilioConstants.ContextNames.ITEMS);
        if(CollectionUtils.isNotEmpty(items)){
            for(V3ItemContext item : items){
                Double weightedAverageCost = item.getWeightedAverageCost()!=null ? item.getWeightedAverageCost() : 0;
                Double availableQuantity = item.getQuantity()!=null ? item.getQuantity() : 0;
                Double weightedAverage = weightedAverageCost * availableQuantity;
                Double totalQuantity= availableQuantity;
                if(CollectionUtils.isNotEmpty(item.getPurchasedItems())){
                    for(V3PurchasedItemContext purchasedItem : item.getPurchasedItems()){
                        if(purchasedItem.getQuantity()!=null && purchasedItem.getUnitcost()!=null){
                            totalQuantity += purchasedItem.getQuantity();
                            weightedAverage += purchasedItem.getQuantity() * purchasedItem.getUnitcost();
                        }
                    }
                }
                weightedAverageCost = weightedAverage/totalQuantity;
                item.setWeightedAverageCost(weightedAverageCost);
            }
            V3Util.processAndUpdateBulkRecords(modBean.getModule(FacilioConstants.ContextNames.ITEM), (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.ITEMS),  FieldUtil.getAsMapList(items, V3ItemContext.class)  ,null,null,null,null,null,null,null,null,false,false,null);
        }
        return false;
    }
}
