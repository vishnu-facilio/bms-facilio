package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedItemsCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(workOrderPlannedItems)){
            for(WorkOrderPlannedItemsContext workOrderPlannedItem : workOrderPlannedItems){
                if(workOrderPlannedItem.getDescription()==null){
                    V3ItemTypesContext itemType = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ITEM_TYPES,workOrderPlannedItem.getItemType().getId(),V3ItemTypesContext.class);
                    if(itemType.getDescription()!=null){
                        String description = itemType.getDescription();
                        workOrderPlannedItem.setDescription(description);
                    }
                }
                if(workOrderPlannedItem.getUnitPrice()!=null && workOrderPlannedItem.getQuantity()!=null){
                    Double totalCost = workOrderPlannedItem.getUnitPrice() * workOrderPlannedItem.getQuantity();
                    workOrderPlannedItem.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
