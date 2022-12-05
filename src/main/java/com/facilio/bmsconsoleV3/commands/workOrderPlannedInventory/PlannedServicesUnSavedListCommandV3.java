package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PlannedServicesUnSavedListCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String services = (String) context.get(FacilioConstants.ContextNames.SERVICE);
        Long workOrderId = (Long) context.get(FacilioConstants.ContextNames.WORK_ORDER);
        if(services!=null && workOrderId!=null){
            V3WorkOrderContext workOrder = new V3WorkOrderContext();
            workOrder.setId(workOrderId);
            List<Long> serviceIdsArray = Arrays.asList(services.split(",")).stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());

            List<WorkOrderPlannedServicesContext> workOrderPlannedServices = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(serviceIdsArray)) {
                List<V3ServiceContext> serviceRecords = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.SERVICE,serviceIdsArray,V3ServiceContext.class);
                for (V3ServiceContext serviceRecord : serviceRecords) {
                    WorkOrderPlannedServicesContext workOrderPlannedService = new WorkOrderPlannedServicesContext();
                    V3ServiceContext service = new V3ServiceContext();
                    service.setId(serviceRecord.getId());
                    service.setName(serviceRecord.getName());
                    workOrderPlannedService.setService(service);
                    if (serviceRecord.getDescription() != null) {
                        workOrderPlannedService.setDescription(serviceRecord.getDescription());
                    }
                    workOrderPlannedService.setWorkOrder(workOrder);
                    workOrderPlannedService.setQuantity(1.0);
                    workOrderPlannedServices.add(workOrderPlannedService);
                }
            }
            Map<String, List> recordMap = new HashMap<>();
            recordMap.put(FacilioConstants.ContextNames.WO_PLANNED_SERVICES,workOrderPlannedServices);
            context.put(FacilioConstants.ContextNames.WO_PLANNED_SERVICES, workOrderPlannedServices);
            context.put(FacilioConstants.ContextNames.RECORD_MAP,recordMap);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,FacilioConstants.ContextNames.WO_PLANNED_SERVICES);
        }
        return false;
    }
}
