package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderCostContext;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ServiceOrderPlannedServiceCostBeforeDelete  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        List<Long> plannedServiceIds = (List<Long>) context.get("recordIds");

        if (CollectionUtils.isNotEmpty(plannedServiceIds)) {
            List<ServiceOrderPlannedServicesContext> plannedServices = V3RecordAPI.getRecordsList(moduleName, plannedServiceIds, ServiceOrderPlannedServicesContext.class);
            List<ServiceOrderContext> serviceOrders = new ArrayList<>();
            for (ServiceOrderPlannedServicesContext plannedService : plannedServices) {
                serviceOrders.add(plannedService.getServiceOrder());
            }
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE, ServiceOrderCostContext.InventoryCostType.SERVICES);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.PLANS);
        }
        return false;

    }
}

