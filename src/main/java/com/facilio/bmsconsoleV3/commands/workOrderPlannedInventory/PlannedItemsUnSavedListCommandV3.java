package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlannedItemsUnSavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String itemTypes = (String) context.get(FacilioConstants.ContextNames.ITEM_TYPES);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(itemTypes!=null && workOrderId!=null){
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);

            List<Long> itemTypeIdsArray = Arrays.asList(itemTypes.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

            List<WorkOrderPlannedItemsContext> workOrderPlannedItems = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(itemTypeIdsArray)){
                List<V3ItemTypesContext> itemTypeRecords = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.ITEM_TYPES,itemTypeIdsArray,V3ItemTypesContext.class);
                for(V3ItemTypesContext itemTypeRecord : itemTypeRecords){
                    WorkOrderPlannedItemsContext workOrderPlannedItem = new WorkOrderPlannedItemsContext();

                    V3ItemTypesContext itemType = new V3ItemTypesContext();
                    itemType.setId(itemTypeRecord.getId());
                    itemType.setName(itemTypeRecord.getName());

                    workOrderPlannedItem.setItemType(itemType);

                    if(itemTypeRecord.getDescription()!=null){
                        workOrderPlannedItem.setDescription(itemTypeRecord.getDescription());
                    }
                    workOrderPlannedItem.setWorkOrder(workOrder);
                    workOrderPlannedItem.setQuantity(1.0);
                    workOrderPlannedItems.add(workOrderPlannedItem);
                }
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WO_PLANNED_ITEMS,workOrderPlannedItems);
            context.put(FacilioConstants.ContextNames.WO_PLANNED_ITEMS,workOrderPlannedItems);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WO_PLANNED_ITEMS);
        }
        return false;
    }
}
