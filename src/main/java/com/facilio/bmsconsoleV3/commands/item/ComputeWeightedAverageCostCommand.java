package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
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
                V3ItemContext itemRecord = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM,item.getId(),V3ItemContext.class);
                if(itemRecord.getItemType()!=null && itemRecord.getItemType().getId()>0 && !isRotating(itemRecord.getItemType().getId())){
                    Double weightedAverageCost = itemRecord.getWeightedAverageCost()!=null ? itemRecord.getWeightedAverageCost() : 0;
                    Double availableQuantity = itemRecord.getQuantity()!=null ? itemRecord.getQuantity() : 0;
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
                    weightedAverageCost = Math.floor(weightedAverageCost * 100)/ 100;
                    item.setWeightedAverageCost(weightedAverageCost);
                    V3RecordAPI.updateRecord(item,modBean.getModule(FacilioConstants.ContextNames.ITEM),modBean.getAllFields(FacilioConstants.ContextNames.ITEM));
                }
            }
        }
        return false;
    }
    private Boolean isRotating(Long itemTypeId)throws Exception{
        V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,itemTypeId, V3ItemTypesContext.class);
        return itemType.isRotating();
    }
}
