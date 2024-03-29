package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;


public class ValidatePlannedItemsBeforeDeleteCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedItemIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedItemIds)) {
            List<WorkOrderPlannedItemsContext> plannedItems = V3RecordAPI.getRecordsList(moduleName, plannedItemIds, WorkOrderPlannedItemsContext.class);
            for (WorkOrderPlannedItemsContext plannedItem : plannedItems) {
                if (plannedItem.getIsReserved()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delete a reserved Item");
                }
            }
        }
        return false;

    }
}
