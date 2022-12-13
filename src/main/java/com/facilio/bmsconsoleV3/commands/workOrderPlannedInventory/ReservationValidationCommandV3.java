package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
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

public class ReservationValidationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> queryParams = Constants.getQueryParams(context);
        if (CollectionUtils.isNotEmpty(workOrderPlannedItems) && MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("reserveValidation")) {
            Map<Long,Double> itemMap = new HashMap<>();
            for (WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems) {
                // Item validation

                Long itemTypeId = workOrderPlannedItem.getItemType().getId();
                Long storeRoomId = workOrderPlannedItem.getStoreRoom() !=null ? workOrderPlannedItem.getStoreRoom().getId() :null;
                if(storeRoomId!=null) {
                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String itemModuleName = FacilioConstants.ContextNames.ITEM;
                    FacilioModule module = modBean.getModule(itemModuleName);
                    List<FacilioField> fields = modBean.getAllFields(itemModuleName);
                    SelectRecordsBuilder<V3ItemContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemContext>()
                            .module(module)
                            .beanClass(V3ItemContext.class)
                            .select(fields)
                            .andCondition(CriteriaAPI.getCondition("ITEM_TYPES_ID", "itemType", String.valueOf(itemTypeId), NumberOperators.EQUALS))
                            .andCondition(CriteriaAPI.getCondition("STORE_ROOM_ID", "storeRoom", String.valueOf(storeRoomId), NumberOperators.EQUALS));

                    V3ItemContext item = selectRecordsBuilder.fetchFirst();

                    if (item == null) {
                        workOrderPlannedItem.setDatum("errorType", "Error Note");
                        workOrderPlannedItem.setDatum("errorMessage", "Item not present in storeroom");
                        workOrderPlannedItem.setDatum("availableQuantity", null);
                    } else {
                        Long itemId = item.getId();
                        Double availableQuantity = item.getQuantity() != null ? item.getQuantity() : 0.0;
                        if(itemMap.containsKey(itemId)){
                            availableQuantity = itemMap.get(itemId);
                        }
                        workOrderPlannedItem.setDatum("availableQuantity", availableQuantity);
                        // Quantity Validation
                        if (workOrderPlannedItem.getQuantity() == null || workOrderPlannedItem.getQuantity() == 0) {
                            workOrderPlannedItem.setDatum("errorType", "Error Note");
                            workOrderPlannedItem.setDatum("errorMessage", "Reserve Quantity is null");
                        }
                        else{
                            itemMap.put(itemId,availableQuantity - workOrderPlannedItem.getQuantity());
                            if (availableQuantity < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD)) {
                                workOrderPlannedItem.setDatum("errorType", "Error Note");
                                workOrderPlannedItem.setDatum("errorMessage", "Hard Reserve quantity is more than available quantity");
                            } else if (availableQuantity < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.SOFT)) {
                                workOrderPlannedItem.setDatum("errorType", "Warning Note");
                                workOrderPlannedItem.setDatum("errorMessage", "Soft Reserve quantity is more than available quantity");
                            }
                        }

                    }
                    //reservation type validation
                    if(workOrderPlannedItem.getReservationTypeEnum()==null || workOrderPlannedItem.getReservationType()<=0){
                        workOrderPlannedItem.setDatum("errorType", "Error Note");
                        workOrderPlannedItem.setDatum("errorMessage", "Reservation Type is null");
                    }
                }
                else{
                    workOrderPlannedItem.setDatum("errorType", "Error Note");
                    workOrderPlannedItem.setDatum("errorMessage", "Storeroom cannot be null");
                    workOrderPlannedItem.setDatum("availableQuantity", null);
                }
            }
        }

            return false;
    }
}
