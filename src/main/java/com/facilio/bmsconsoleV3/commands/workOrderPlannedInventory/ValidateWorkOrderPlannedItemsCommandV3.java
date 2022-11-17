package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;


import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.*;
import java.util.stream.Collectors;


public class ValidateWorkOrderPlannedItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<Long, V3WorkOrderContext> workOrderMap = new HashMap<>();
        V3WorkOrderContext wo = new V3WorkOrderContext();
        if (CollectionUtils.isNotEmpty(workOrderPlannedItems)) {
                for (WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems) {
                    if(workOrderPlannedItem.getIsReserved()){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot update a reserved item");
                    }
                    if (MapUtils.isNotEmpty(bodyParams) && (bodyParams.containsKey("reserve") || bodyParams.containsKey("unreserve"))) {
                        // Quantity Validation
                        if (workOrderPlannedItem.getQuantity() == null || workOrderPlannedItem.getQuantity() == 0) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                        }
                        //reservation type validation
                        if (workOrderPlannedItem.getReservationType() == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Reservation type cannot be null");
                        }
                        // Item validation
                        Long itemTypeId = workOrderPlannedItem.getItemType().getId();
                        Long storeRoomId = workOrderPlannedItem.getStoreRoom() != null ? workOrderPlannedItem.getStoreRoom().getId() : null;
                        if (storeRoomId != null) {
                            V3ItemContext item = V3InventoryUtil.getItemWithStoreroomServingSites(itemTypeId, storeRoomId);

                            if (item == null) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not present in the given storeroom");
                            } else {
                                // checking if storeroom servers the work order's site
                                if (workOrderPlannedItem.getWorkOrder() != null) {
                                    Long workOrderId = workOrderPlannedItem.getWorkOrder().getId();
                                    List<Long> servingSiteIds = item.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
                                    if (workOrderMap.get(workOrderId) == null) {
                                        wo = V3InventoryUtil.getWorkOrder(workOrderId);
                                        workOrderMap.put(workOrderId, wo);
                                    } else {
                                        wo = workOrderMap.get(workOrderId);
                                    }
                                    if (wo.getSiteId() > 0 && !servingSiteIds.contains(wo.getSiteId())) {
                                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom does not serve the selected work order's site");
                                    }
                                }
                                if (item.getQuantity() < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD)) {
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Available quantity in store is less than the requested quantity");
                                }
                            }
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be empty");
                        }
                    }
                }
        }
        return false;
    }
}
