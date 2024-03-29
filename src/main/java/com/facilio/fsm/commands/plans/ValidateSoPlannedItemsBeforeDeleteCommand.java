package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderCostContext;
import com.facilio.fsm.context.ServiceOrderPlannedItemsContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidateSoPlannedItemsBeforeDeleteCommand  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedItemIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedItemIds)) {
            List<ServiceOrderPlannedItemsContext> plannedItems = V3RecordAPI.getRecordsList(moduleName, plannedItemIds, ServiceOrderPlannedItemsContext.class);
            List<ServiceOrderContext> serviceOrders = new ArrayList<>();
            for (ServiceOrderPlannedItemsContext plannedItem : plannedItems) {
                if (plannedItem.getIsReserved()) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot delete a reserved Item");
                }
                serviceOrders.add(plannedItem.getServiceOrder());
            }
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE, ServiceOrderCostContext.InventoryCostType.ITEMS);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.PLANS);
        }
        return false;

    }
}
