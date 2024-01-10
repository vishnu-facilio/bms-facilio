package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3ToolsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.xpath.operations.Bool;

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
                Long toolTypeId = invReqLineItem.getToolType() != null ? invReqLineItem.getToolType().getId() : null;

                Long storeRoomId = invReqLineItem.getStoreRoom() != null ? invReqLineItem.getStoreRoom().getId() : null;
                Double invReqLineItemQuantity = invReqLineItem.getQuantity();
                if(storeRoomId != null) {
                    Double availableItemQuantity = 0.00;
                    V3StoreRoomContext storeRoom = null;
                    Boolean availableInStoreRoom = false;

                    if(itemTypeId != null){
                        V3ItemContext tool = V3ItemsApi.getItem(itemTypeId,storeRoomId);
                        if(FacilioUtil.isEmptyOrNull(tool)) {
                            errorType = "Non-reservable";
                            errorMessage = "Item not available in Storeroom";
                        } else {
                            if(tool.getQuantity() != null){
                                availableItemQuantity = tool.getQuantity();
                            }
                            storeRoom = tool.getStoreRoom();
                            availableInStoreRoom = true;
                        }
                    } if(toolTypeId != null){
                        V3ToolContext tool = V3ToolsApi.getTool(toolTypeId,storeRoomId);
                        if(FacilioUtil.isEmptyOrNull(tool)) {
                            errorType = "Non-reservable";
                            errorMessage = "Tool not available in Storeroom";
                        } else {
                            if(tool.getQuantity() != null){
                                availableItemQuantity = tool.getQuantity();
                            }
                            storeRoom = tool.getStoreRoom();
                            availableInStoreRoom = true;
                        }
                    }

                    if(availableInStoreRoom) {
                        invReqLineItem.setDatum("availableQuantity", availableItemQuantity);
                        // checking if storeroom servers the work order's site
                        if(invReqLineItem.getInventoryRequestId().getWorkorder() != null) {
                            Long workOrderId = invReqLineItem.getInventoryRequestId().getWorkorder().getId();
                            List<Long> servingSiteIds = storeRoom.getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
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
