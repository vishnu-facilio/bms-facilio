package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetReservedInventoryRequestLineItems extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = recordMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        Map<Long, V3WorkOrderContext> workOrderMap = new HashMap<>();
        V3WorkOrderContext workOrder = new V3WorkOrderContext();
        String errorType = "";
        String errorMessage = "";
        // Reservation Validations
        if(CollectionUtils.isNotEmpty(inventoryRequestLineItems) && MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("reserve")){
            for(V3InventoryRequestLineItemContext invReqLineItem : inventoryRequestLineItems){
                Long itemTypeId = invReqLineItem.getItemType() != null ? invReqLineItem.getItemType().getId() : null;
                Long storeRoomId = invReqLineItem.getStoreRoom() != null ? invReqLineItem.getStoreRoom().getId() : null;
                Double invReqLineItemQuantity = invReqLineItem.getQuantity();
                if(itemTypeId != null && storeRoomId != null) {
                    V3ItemContext item = V3ItemsApi.getItem(itemTypeId,storeRoomId);
                    if(FacilioUtil.isEmptyOrNull(item)) {
                        errorType = "Non-reservable";
                        errorMessage = "Item not available in Storeroom";
                    }
                    else {
                        Double availableItemQuantity = item.getQuantity();
                        invReqLineItem.setDatum("availableQuantity", availableItemQuantity);
                        // checking if storeroom servers the work order's site
                        if(invReqLineItem.getInventoryRequestId().getWorkorder() != null) {
                            Long workOrderId = invReqLineItem.getInventoryRequestId().getWorkorder().getId();
                            List<Long> servingSiteIds = item.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
                            if(workOrderMap.get(workOrderId)==null) {
                                workOrder = V3InventoryUtil.getWorkOrder(workOrderId);
                                workOrderMap.put(workOrderId,workOrder);
                            }
                            else {
                                workOrder = workOrderMap.get(workOrderId);
                            }
                            if(workOrder.getSiteId()>0 && !servingSiteIds.contains(workOrder.getSiteId())) {
                                errorType = "Non-reservable";
                                errorMessage = "Storeroom does not serve the selected work order's site";
                            }
                        }
                        else if(invReqLineItemQuantity == null || invReqLineItemQuantity <= 0) {
                            errorType = "Non-reservable";
                            errorMessage = "Reserve Quantity is Empty";
                        }
                        else {
                            if(availableItemQuantity < invReqLineItemQuantity && invReqLineItem.getReservationTypeEnum() != null && invReqLineItem.getReservationTypeEnum().equals(ReservationType.HARD)){
                                errorType = "Non-reservable";
                                errorMessage = "Hard Reserve quantity is more than available quantity";
                            }
                            else if(availableItemQuantity < invReqLineItemQuantity && invReqLineItem.getReservationTypeEnum() != null && invReqLineItem.getReservationTypeEnum().equals(ReservationType.SOFT)){
                                errorType = "Reservable";
                                errorMessage = "Reserved quantity is greater than Available quantity\n";
                            }
                        }
                    }
                }
                if(storeRoomId==null) {
                    errorType = "Non-reservable";
                    errorMessage = "Storeroom is not defined";
                    invReqLineItem.setDatum("availableQuantity", null);
                }
                invReqLineItem.setDatum("errorType", errorType);
                invReqLineItem.setDatum("errorMessage", errorMessage);
                errorType = "";
                errorMessage = "";
            }
        }

        return false;
    }
}
