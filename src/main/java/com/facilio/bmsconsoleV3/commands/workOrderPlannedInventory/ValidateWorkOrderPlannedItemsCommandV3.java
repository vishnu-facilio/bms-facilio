package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestLineItemContext;
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

import java.util.List;
import java.util.Map;

public class ValidateWorkOrderPlannedItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        if (CollectionUtils.isNotEmpty(workOrderPlannedItems) && MapUtils.isNotEmpty(bodyParams) && (bodyParams.containsKey("reserve") || bodyParams.containsKey("unreserve"))) {
            for (WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems) {
                // Quantity Validation
                if (workOrderPlannedItem.getQuantity()==null || workOrderPlannedItem.getQuantity()==0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                //reservation type validation
                if(workOrderPlannedItem.getReservationType()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Reservation type cannot be null");
                }
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
                    List<V3ItemContext> items = selectRecordsBuilder.get();
                    if (items.size() < 1) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not present in the given storeroom");
                    } else {
                        if (items.get(0).getQuantity() < workOrderPlannedItem.getQuantity() && workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD)) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Available quantity in store is less than the requested quantity");
                        }
                    }
                }else{
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Storeroom cannot be empty");
                }
            }
        }
        return false;
    }
}
