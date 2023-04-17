package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3StoreRoomContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestLineItemContext;
import com.facilio.bmsconsoleV3.enums.ReservationType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetStoreRoomAndReservationTypeForInventoryRequestLineItems extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequests = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(inventoryRequests)){
            for(V3InventoryRequestContext inventoryRequest : inventoryRequests) {
                V3StoreRoomContext inventoryRequestStoreRoom = inventoryRequest.getStoreRoom();
                List<V3InventoryRequestLineItemContext> inventoryRequestLineItems = recordMap.get(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
                if (CollectionUtils.isNotEmpty(inventoryRequestLineItems)) {
                    for (V3InventoryRequestLineItemContext inventoryRequestLineItem : inventoryRequestLineItems) {
                        Long inventoryRequestLineItemId = inventoryRequestLineItem.getId();
                        // setting storeroom for inventory request line item from inventory request
                        if (inventoryRequestStoreRoom != null) {
                            setStoreRoomForLineItem(inventoryRequestLineItemId, inventoryRequestStoreRoom);
                        }
                        // setting inventory request line item reservation type default to soft temporarily..
                        setLineItemReservationType(inventoryRequestLineItemId);
                    }
                }
            }
        }

        return false;
    }
    public void setStoreRoomForLineItem(Long inventoryRequestLineItemId, V3StoreRoomContext storeRoom) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestLineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("storeRoom"));
        Map<String, Object> map = new HashMap<>();
        map.put("storeRoom", FieldUtil.getAsProperties(storeRoom));
        UpdateRecordBuilder<V3InventoryRequestLineItemContext> updateRecordBuilder = new UpdateRecordBuilder<V3InventoryRequestLineItemContext>()
                .module(inventoryRequestLineItemModule)
                .fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(String.valueOf(inventoryRequestLineItemId), inventoryRequestLineItemModule));
        updateRecordBuilder.updateViaMap(map);
    }

    public void setLineItemReservationType(Long inventoryRequestLineItemId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule inventoryRequestLineItemModule = modBean.getModule(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.INVENTORY_REQUEST_LINE_ITEMS);
        List<FacilioField> updatedFields = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        updatedFields.add(fieldsMap.get("reservationType"));
        Map<String, Object> map = new HashMap<>();
        map.put("reservationType", ReservationType.SOFT.getIndex());
        UpdateRecordBuilder<V3InventoryRequestLineItemContext> updateRecordBuilder = new UpdateRecordBuilder<V3InventoryRequestLineItemContext>()
                .module(inventoryRequestLineItemModule)
                .fields(updatedFields)
                .andCondition(CriteriaAPI.getIdCondition(String.valueOf(inventoryRequestLineItemId), inventoryRequestLineItemModule));
        updateRecordBuilder.updateViaMap(map);
    }

}
