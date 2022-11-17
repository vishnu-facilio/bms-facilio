package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedItemsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if(CollectionUtils.isNotEmpty(workOrderPlannedItems)){
            for(WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems){
                workOrderPlannedItem.setReservationType(ReservationType.SOFT.getIndex());
                if(workOrderPlannedItem.getDescription()==null){
                    V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,workOrderPlannedItem.getItemType().getId(),V3ItemTypesContext.class);
                    if(itemType.getDescription()!=null){
                        String description = itemType.getDescription();
                        workOrderPlannedItem.setDescription(description);
                    }
                }
                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && workOrderPlannedItem.getUnitPrice()==null){
                    V3ItemContext item = V3ItemsApi.getItem(workOrderPlannedItem.getItemType().getId(),workOrderPlannedItem.getStoreRoom().getId());
                    if (item == null) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not present in the given storeroom");
                    }
                    List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);
                    if (CollectionUtils.isNotEmpty(purchasedItems)){
                        workOrderPlannedItem.setUnitPrice(purchasedItems.get(0).getUnitcost());
                    }
                }
                //number fields validation
                if(workOrderPlannedItem.getQuantity()!=null && workOrderPlannedItem.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(workOrderPlannedItem.getUnitPrice()!=null && workOrderPlannedItem.getUnitPrice()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid unit price");
                }
                if(workOrderPlannedItem.getUnitPrice()!=null && workOrderPlannedItem.getQuantity()!=null){
                    //total cost computation
                    Double totalCost = workOrderPlannedItem.getUnitPrice() * workOrderPlannedItem.getQuantity();
                    workOrderPlannedItem.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
