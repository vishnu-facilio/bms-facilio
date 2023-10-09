package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderCostContext;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderPlannedToolsCostBeforeDelete  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedToolIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedToolIds)) {
            List<ServiceOrderPlannedToolsContext> plannedTools = V3RecordAPI.getRecordsList(moduleName, plannedToolIds, ServiceOrderPlannedToolsContext.class);
            List<ServiceOrderContext> serviceOrders = new ArrayList<>();
            for (ServiceOrderPlannedToolsContext plannedTool : plannedTools) {
                serviceOrders.add(plannedTool.getServiceOrder());
            }
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE, ServiceOrderCostContext.InventoryCostType.TOOLS);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.PLANS);
        }
        return false;

    }
}
