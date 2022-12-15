package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3ItemsApi;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;

import java.util.*;
import java.util.stream.Collectors;

public class WorkOrderItemUnsavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> itemIds = (List<Long>) context.get(FacilioConstants.ContextNames.ITEM);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);

        if(itemIds != null && workOrderId != null) {
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Collection<SupplementRecord> supplementFields = new ArrayList<>();
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.ITEM);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            supplementFields.add((LookupField) fieldsMap.get("itemType"));
            supplementFields.add((LookupField) fieldsMap.get("storeRoom"));

            List<V3ItemContext> items = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.ITEM,itemIds, V3ItemContext.class, supplementFields);
            List<V3WorkorderItemContext> workorderItems = new ArrayList<>();
            for(V3ItemContext item : items){
                V3WorkorderItemContext workorderItem = new V3WorkorderItemContext();
                workorderItem.setItem(item);
                workorderItem.setWorkorder(workOrder);
                workorderItem.setStoreRoom(item.getStoreRoom());
                workorderItem.setQuantity(1);
                workorderItem.setUnitPrice(item.getLastPurchasedPrice());
                Double cost = null;
                if(item.getLastPurchasedPrice() != null && item.getLastPurchasedPrice() > 0) {
                    cost = workorderItem.getQuantity() * workorderItem.getUnitPrice();
                }
                workorderItem.setCost(cost);
                workorderItems.add(workorderItem);
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItems);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WORKORDER_ITEMS);
            context.put(FacilioConstants.ContextNames.WORKORDER_ITEMS,workorderItems);

        }
        return false;
    }
}
