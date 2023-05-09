package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedServicesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetWorkOrderPlannedServicesCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedServicesContext> workOrderPlannedServices = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(workOrderPlannedServices)){
            for(WorkOrderPlannedServicesContext workOrderPlannedService : workOrderPlannedServices){
                V3ServiceContext service = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SERVICE,workOrderPlannedService.getService().getId(),V3ServiceContext.class);
                if(workOrderPlannedService.getDescription()==null && service.getDescription()!=null){
                    workOrderPlannedService.setDescription(service.getDescription());
                }
                //number fields validation
                if(workOrderPlannedService.getQuantity()!=null && workOrderPlannedService.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(workOrderPlannedService.getUnitPrice()!=null && workOrderPlannedService.getUnitPrice()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid unit price");
                }
                if(workOrderPlannedService.getUnitPrice()!=null && workOrderPlannedService.getQuantity()!=null){
                    //total cost computation
                    Double totalCost = workOrderPlannedService.getUnitPrice() * workOrderPlannedService.getQuantity();
                    if(service.getPaymentTypeEnum().equals(V3ServiceContext.PaymentType.FIXED)){
                        workOrderPlannedService.setTotalCost(totalCost);
                    }else if(service.getPaymentTypeEnum().equals(V3ServiceContext.PaymentType.DURATION_BASED) && workOrderPlannedService.getDuration()!=null){
                        workOrderPlannedService.setTotalCost(totalCost * (workOrderPlannedService.getDuration()/3600));
                    }
                }
            }
        }

        return false;
    }
}
