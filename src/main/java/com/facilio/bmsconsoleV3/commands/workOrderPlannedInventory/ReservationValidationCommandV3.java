package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        Map<Long, V3WorkOrderContext> workOrderMap = new HashMap<>();
        V3WorkOrderContext wo = new V3WorkOrderContext();
        if (CollectionUtils.isNotEmpty(workOrderPlannedItems) && MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("reserveValidation")) {
            Map<Long,Double> itemMap = new HashMap<>();
            for (WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems) {
                // Item validation

                Long itemTypeId = workOrderPlannedItem.getItemType().getId();
                Long storeRoomId = workOrderPlannedItem.getStoreRoom() !=null ? workOrderPlannedItem.getStoreRoom().getId() :null;
                if(storeRoomId!=null) {
                    V3ItemContext item = V3InventoryUtil.getItemWithStoreroomServingSites(itemTypeId,storeRoomId);

                    if (item == null) {
                        workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                        workOrderPlannedItem.setDatum("errorMessage", "Item not available in Storeroom");
                        workOrderPlannedItem.setDatum("availableQuantity", null);
                    } else {
                        // checking if storeroom servers the work order's site
                        if(workOrderPlannedItem.getWorkOrder()!=null){
                            Long workOrderId = workOrderPlannedItem.getWorkOrder().getId();
                            List<Long> servingSiteIds = item.getStoreRoom().getServingsites().stream().map(servingSite -> servingSite.getId()).collect(Collectors.toList());
                            if(workOrderMap.get(workOrderId)==null){
                                wo = V3InventoryUtil.getWorkOrder(workOrderId);
                                workOrderMap.put(workOrderId,wo);
                            }else{
                                wo = workOrderMap.get(workOrderId);
                            }
                            if(wo.getSiteId()>0 && !servingSiteIds.contains(wo.getSiteId())){
                                workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                                workOrderPlannedItem.setDatum("errorMessage", "Storeroom does not serve the selected work order's site");
                            }
                        }
                        Long itemId = item.getId();
                        Double availableQuantity = item.getQuantity() != null ? item.getQuantity() : 0.0;
                        if(itemMap.containsKey(itemId)){
                            availableQuantity = itemMap.get(itemId);
                        }
                        workOrderPlannedItem.setDatum("availableQuantity", availableQuantity);
                        // Quantity Validation
                        if (workOrderPlannedItem.getQuantity() == null || workOrderPlannedItem.getQuantity() == 0) {
                            workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                            workOrderPlannedItem.setDatum("errorMessage", "Reserve Quantity is empty");
                        }
                        else{
                            itemMap.put(itemId,availableQuantity - workOrderPlannedItem.getQuantity());
                            if (availableQuantity < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD)) {
                                workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                                workOrderPlannedItem.setDatum("errorMessage", "Hard Reserve quantity is more than available quantity");
                            } else if (availableQuantity < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.SOFT)) {
                                workOrderPlannedItem.setDatum("errorType", "Reservable");
                                workOrderPlannedItem.setDatum("errorMessage", "Reserved quantity is greater than Available quantity");
                            }
                        }

                    }
                    //reservation type validation
                    if(workOrderPlannedItem.getReservationTypeEnum()==null || workOrderPlannedItem.getReservationType()<=0){
                        workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                        workOrderPlannedItem.setDatum("errorMessage", "Reservation Type is null");
                    }
                }
                else{
                    workOrderPlannedItem.setDatum("errorType", "Non-reservable");
                    workOrderPlannedItem.setDatum("errorMessage", "Storeroom is not defined");
                    workOrderPlannedItem.setDatum("availableQuantity", null);
                }
            }
        }

            return false;
    }
}
