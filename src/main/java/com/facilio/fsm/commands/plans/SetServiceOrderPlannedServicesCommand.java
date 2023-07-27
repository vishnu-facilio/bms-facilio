package com.facilio.fsm.commands.plans;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderPlannedServicesContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetServiceOrderPlannedServicesCommand  extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedServicesContext> serviceOrderPlannedServices = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedServices)){
            for(ServiceOrderPlannedServicesContext serviceOrderPlannedService : serviceOrderPlannedServices){
                V3ServiceContext service = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SERVICE,serviceOrderPlannedService.getService().getId(),V3ServiceContext.class);
                //number fields validation
                if(serviceOrderPlannedService.getQuantity()!=null && serviceOrderPlannedService.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(serviceOrderPlannedService.getUnitPrice()!=null && serviceOrderPlannedService.getUnitPrice()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid unit price");
                }
                if(serviceOrderPlannedService.getUnitPrice()!=null && serviceOrderPlannedService.getQuantity()!=null){
                    //total cost computation
                    Double totalCost = serviceOrderPlannedService.getUnitPrice() * serviceOrderPlannedService.getQuantity();
                    if(service.getPaymentTypeEnum().equals(V3ServiceContext.PaymentType.FIXED)){
                        serviceOrderPlannedService.setTotalCost(totalCost);
                    }else if(service.getPaymentTypeEnum().equals(V3ServiceContext.PaymentType.DURATION_BASED) && serviceOrderPlannedService.getDuration()!=null){
                        serviceOrderPlannedService.setTotalCost(totalCost * (serviceOrderPlannedService.getDuration()/3600));
                    }
                }
            }
        }

        return false;
    }
}
