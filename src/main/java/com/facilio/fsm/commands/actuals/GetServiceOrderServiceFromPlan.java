package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import com.facilio.fsm.context.ServiceOrderServiceContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import org.apache.commons.chain.Context;

public class GetServiceOrderServiceFromPlan extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long plannedServiceId = (Long) context.get(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICE_ID);
        if(plannedServiceId!=null && plannedServiceId>0){
            ServiceOrderPlannedServicesContext plannedService = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_PLANNED_SERVICES,plannedServiceId, ServiceOrderPlannedServicesContext.class);
            if(plannedService!=null){
                ServiceOrderServiceContext serviceOrderService = new ServiceOrderServiceContext();
                if(plannedService.getService()==null){
                    throw new FSMException(FSMErrorCode.SERVICE_REQUIRED);
                }
                serviceOrderService.setService(plannedService.getService());
                if(plannedService.getQuantity()!=null){
                    serviceOrderService.setQuantity(plannedService.getQuantity());
                }
                if(plannedService.getDuration()!=null){
                    serviceOrderService.setDuration(plannedService.getDuration());
                }
                context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_SERVICES,serviceOrderService);
            }
        }
        return false;
    }
}
