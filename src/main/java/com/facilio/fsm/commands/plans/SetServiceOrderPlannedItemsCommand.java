package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3PurchasedItemContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class SetServiceOrderPlannedItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedItems)){
            for(ServiceOrderPlannedItemsContext serviceOrderPlannedItem : serviceOrderPlannedItems){
                if(serviceOrderPlannedItem.getQuantity()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                if(serviceOrderPlannedItem.getItemType()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item Type cannot be empty");
                }
                serviceOrderPlannedItem.setReservationType(ReservationType.SOFT.getIndex());

                if(MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && (boolean) bodyParams.get("reserve")){
                    serviceOrderPlannedItem.setIsReserved(true);
                    if( serviceOrderPlannedItem.getUnitPrice()==null) {
                        V3ItemContext item = V3ItemsApi.getItem(serviceOrderPlannedItem.getItemType().getId(), serviceOrderPlannedItem.getStoreRoom().getId());
                        if (item == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not present in the given storeroom");
                        }
                        List<V3PurchasedItemContext> purchasedItems = V3InventoryUtil.getPurchasedItemsBasedOnCostType(item);
                        if (CollectionUtils.isNotEmpty(purchasedItems)) {
                            serviceOrderPlannedItem.setUnitPrice(purchasedItems.get(0).getUnitcost());
                        }
                    }
                }
                //number fields validation
                if(serviceOrderPlannedItem.getQuantity()!=null && serviceOrderPlannedItem.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(serviceOrderPlannedItem.getUnitPrice()!=null && serviceOrderPlannedItem.getUnitPrice()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid unit price");
                }
                if(serviceOrderPlannedItem.getQuantity()!=null){
                    Double unitPrice = serviceOrderPlannedItem.getUnitPrice()!=null ? serviceOrderPlannedItem.getUnitPrice() : 0;
                    //total cost computation
                    Double totalCost = unitPrice * serviceOrderPlannedItem.getQuantity();
                    serviceOrderPlannedItem.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
