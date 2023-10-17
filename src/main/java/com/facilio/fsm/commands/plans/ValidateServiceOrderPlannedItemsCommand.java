package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ValidateServiceOrderPlannedItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedItemsContext> serviceOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<Long, ServiceOrderContext> serviceOrderMap = new HashMap<>();
        ServiceOrderContext so = new ServiceOrderContext();
        if (CollectionUtils.isNotEmpty(serviceOrderPlannedItems)) {
            for (ServiceOrderPlannedItemsContext serviceOrderPlannedItem : serviceOrderPlannedItems) {
//                if(serviceOrderPlannedItem.getIsReserved()){
//                    throw new FSMException(FSMErrorCode.ALREADY_RESERVED);
//                }
                if (MapUtils.isNotEmpty(bodyParams) && (bodyParams.containsKey("reserve"))) {
                    // Quantity Validation
                    if (serviceOrderPlannedItem.getQuantity() == null || serviceOrderPlannedItem.getQuantity() == 0) {
                        throw new FSMException(FSMErrorCode.QUANTITY_REQUIRED);

                    }
                    //reservation type validation
                    if (serviceOrderPlannedItem.getReservationType() == null) {
                        throw new FSMException(FSMErrorCode.RESERVATION_TYPE_REQUIRED);
                    }
                    // Item validation
                    Long itemTypeId = serviceOrderPlannedItem.getItemType().getId();
                    Long storeRoomId = serviceOrderPlannedItem.getStoreRoom() != null ? serviceOrderPlannedItem.getStoreRoom().getId() : null;
                    if (storeRoomId != null) {
                        V3ItemContext item = V3InventoryUtil.getItemWithStoreroomServingSites(itemTypeId, storeRoomId);

                        if (item == null) {
                            throw new FSMException(FSMErrorCode.ITEM_NOT_PRESENT);
                        } else {
                            // checking if storeroom servers the service order's site
                            if (serviceOrderPlannedItem.getServiceOrder() != null) {
                                Long serviceOrderId = serviceOrderPlannedItem.getServiceOrder().getId();
                                List<Long> servingSiteIds = item.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
                                if (serviceOrderMap.get(serviceOrderId) == null) {
                                    so = ServiceOrderAPI.getServiceOrder(serviceOrderId);
                                    serviceOrderMap.put(serviceOrderId, so);
                                } else {
                                    so = serviceOrderMap.get(serviceOrderId);
                                }
                                if (so.getSite()!=null && so.getSite().getId() > 0 && !servingSiteIds.contains(so.getSite().getId())) {
                                    throw new FSMException(FSMErrorCode.STORE_DOES_NOT_SERVE_SITE);
                                }
                            }
                            if (item.getQuantity()==null || (item.getQuantity() < serviceOrderPlannedItem.getQuantity() && serviceOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD))) {
                                throw new FSMException(FSMErrorCode.INSUFFICIENT_QUANTITY);
                            }
                        }
                    } else {
                        throw new FSMException(FSMErrorCode.STOREROOM_REQUIRED);                    }
                }
            }
        }
        return false;
    }
}
