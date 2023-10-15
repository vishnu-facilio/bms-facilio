package com.facilio.fsm.commands.plans;


import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.*;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetServiceOrderPlannedToolsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = recordMap.get(moduleName);
        List<ServiceOrderContext> serviceOrders = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools)){
            for(ServiceOrderPlannedToolsContext serviceOrderPlannedTool : serviceOrderPlannedTools){
                if(serviceOrderPlannedTool.getServiceTask()!=null){
                    ServiceTaskContext serviceTask = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,serviceOrderPlannedTool.getServiceTask().getId(), ServiceTaskContext.class);
                    if(serviceTask.getServiceOrder()!=null){
                        serviceOrderPlannedTool.setServiceOrder(serviceTask.getServiceOrder());
                        serviceOrders.add(serviceTask.getServiceOrder());

                    }
                    if(serviceTask.getServiceAppointment()!=null){
                        serviceOrderPlannedTool.setServiceAppointment(serviceTask.getServiceAppointment());
                    }
                }
                if(serviceOrderPlannedTool.getServiceOrder()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Work Order cannot be empty");
                }
                if(serviceOrderPlannedTool.getQuantity()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                if(serviceOrderPlannedTool.getToolType()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Tool Type cannot be empty");
                }
                //number fields validation
                if(serviceOrderPlannedTool.getQuantity()!=null && serviceOrderPlannedTool.getQuantity()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid quantity");
                }
                if(serviceOrderPlannedTool.getRate()!=null && serviceOrderPlannedTool.getRate()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid rate");
                }
                if(serviceOrderPlannedTool.getDuration()!=null && serviceOrderPlannedTool.getDuration()<0){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Enter a valid duration");
                }
                if(serviceOrderPlannedTool.getQuantity()!=null){
                    Double rate = serviceOrderPlannedTool.getRate()!=null ? serviceOrderPlannedTool.getRate() : 0;
                    Double duration = serviceOrderPlannedTool.getDuration()!=null ? serviceOrderPlannedTool.getDuration() : 0;
                    //total cost computation
                    Double totalCost = rate * serviceOrderPlannedTool.getQuantity() * (duration / 3600);
                    serviceOrderPlannedTool.setTotalCost(totalCost);
                }
            }
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE,ServiceOrderCostContext.InventoryCostType.TOOLS);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.PLANS);

        }

        return false;
    }
}
