package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnReserveItemsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = (List<WorkOrderPlannedItemsContext>) context.get(FacilioConstants.ContextNames.WO_PLANNED_ITEMS);


        if(CollectionUtils.isNotEmpty(workOrderPlannedItems)) {
            for (WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems) {
                Long itemTypeId = workOrderPlannedItem.getItemType().getId();
                Long storeRoomId = workOrderPlannedItem.getStoreRoom().getId();

                V3ItemContext item = V3ItemsApi.getItem(itemTypeId,storeRoomId);
                if (item == null) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item is not present in the given storeroom");
                }
                Long itemId = item.getId();
                if(item.getReservedQuantity()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot Unreserve Item, Reserved Quantity is Empty");
                }
                else{
                    Double itemReservedQuantity = item.getReservedQuantity();
                    Double plannedItemQuantity = workOrderPlannedItem.getQuantity();

                    Double newReservedQuantity = itemReservedQuantity - plannedItemQuantity;

                    ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    String itemModuleName = FacilioConstants.ContextNames.ITEM;
                    FacilioModule module = modBean.getModule(itemModuleName);
                    List<FacilioField> fields = modBean.getAllFields(itemModuleName);

                    List<FacilioField> updatedFields = new ArrayList<>();
                    Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
                    updatedFields.add(fieldsMap.get("reservedQuantity"));

                    Map<String, Object> map = new HashMap<>();
                    map.put("reservedQuantity", newReservedQuantity);

                    if(workOrderPlannedItem.getReservationTypeEnum().equals(ReservationType.HARD)){
                        // to update available quantity in item module
                        Double availableQuantity = item.getQuantity()==null ? 0 : item.getQuantity();
                        Double newAvailableQuantity = availableQuantity + plannedItemQuantity;

                        updatedFields.add(fieldsMap.get("quantity"));
                        map.put("quantity", newAvailableQuantity);

                        // updating available quantity in item type

                        updateAvailableQuantityInItemType(workOrderPlannedItem.getQuantity(),itemTypeId);
                    }

                    UpdateRecordBuilder<V3ItemContext> updateBuilder = new UpdateRecordBuilder<V3ItemContext>()
                            .module(module).fields(updatedFields)
                            .andCondition(CriteriaAPI.getIdCondition(itemId, module));
                    updateBuilder.updateViaMap(map);

                }



            }
        }


        return false;
    }
    private void updateAvailableQuantityInItemType(Double plannedItemQuantity,Long itemTypeId) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = FacilioConstants.ContextNames.ITEM_TYPES;
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder<V3ItemTypesContext> selectRecordsBuilder = new SelectRecordsBuilder<V3ItemTypesContext>()
                .module(module)
                .beanClass(V3ItemTypesContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(itemTypeId,module));

        List<V3ItemTypesContext> itemTypes = selectRecordsBuilder.get();

        Double availableQuantity = itemTypes.get(0).getQuantity();

        Double newAvailableQuantity = availableQuantity + plannedItemQuantity;

        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("quantity"));
        Map<String, Object> map = new HashMap<>();
        map.put("quantity", newAvailableQuantity);

        UpdateRecordBuilder<V3ItemTypesContext> updateBuilder = new UpdateRecordBuilder<V3ItemTypesContext>()
                .module(module).fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(itemTypeId, module));
        updateBuilder.updateViaMap(map);
    }
}
