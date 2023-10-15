package com.facilio.fsm.commands.actuals;

import com.facilio.bmsconsoleV3.context.V3ServiceContext;
import com.facilio.bmsconsoleV3.util.V3InventoryUtil;
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

public class SetServiceOrderServiceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderServiceContext> serviceOrderServices = recordMap.get(moduleName);
        List<ServiceOrderContext> serviceOrders = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(serviceOrderServices)){
            for(ServiceOrderServiceContext serviceOrderService :serviceOrderServices){
                if(serviceOrderService.getServiceOrder()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Work Order cannot be empty");
                }
                if(serviceOrderService.getStartTime()!=null && serviceOrderService.getEndTime()!=null &&  serviceOrderService.getStartTime() > serviceOrderService.getEndTime()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Start time cannot be greater than end time");
                }
                if(serviceOrderService.getQuantity() <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
                if(serviceOrderService.getService()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service cannot be empty");
                }
                if(serviceOrderService.getServiceTask()!=null){
                    ServiceTaskContext serviceTask = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK,serviceOrderService.getServiceTask().getId(), ServiceTaskContext.class);
                    if(serviceTask.getServiceAppointment()!=null){
                        serviceOrderService.setServiceAppointment(serviceTask.getServiceAppointment());
                    }
                }
                serviceOrders.add(serviceOrderService.getServiceOrder());
                V3ServiceContext service = V3RecordAPI.getRecord(FacilioConstants.ContextNames.SERVICE,serviceOrderService.getService().getId(),V3ServiceContext.class);
                if(service.getSellingPrice()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Selling price cannot be empty for a service");
                }
                serviceOrderService.setUnitPrice(service.getSellingPrice());

                Double duration =null;
                if(serviceOrderService.getDuration()!=null){
                    duration = serviceOrderService.getDuration();
                }else{
                    duration = getDuration(serviceOrderService.getStartTime(),serviceOrderService.getEndTime());
                    if(duration!=null){
                        serviceOrderService.setDuration(duration);
                    }
                }
                if(duration !=null && duration > 0 && serviceOrderService.getStartTime() != null && serviceOrderService.getStartTime() >0 ) {
                    Long endTime = V3InventoryUtil.getReturnTimeFromDurationAndIssueTime(duration, serviceOrderService.getStartTime());
                    serviceOrderService.setEndTime(endTime);
                }
//                else if(duration !=null && duration > 0 && serviceOrderService.getEndTime() != null && serviceOrderService.getEndTime() >0 && (serviceOrderService.getStartTime() ==null || serviceOrderService.getStartTime() <= 0)){
//                    Long startTime = V3InventoryUtil.getIssueTimeFromDurationAndReturnTime(duration, serviceOrderService.getEndTime());
//                    serviceOrderService.setStartTime(startTime);
//                }
                if(service.getSellingPrice()!=null && serviceOrderService.getQuantity()!=null){
                    Double totalCost = service.getSellingPrice()* serviceOrderService.getQuantity();
                    if(service.getPaymentTypeEnum().equals(V3ServiceContext.PaymentType.DURATION_BASED) && duration!=null){
                        totalCost *= (duration / 3600);
                    }
                    serviceOrderService.setTotalCost(totalCost);
                }
            }
            recordMap.put(moduleName,serviceOrderServices);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER_LIST,serviceOrders);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_COST_TYPE,ServiceOrderCostContext.InventoryCostType.SERVICES);
            context.put(FacilioConstants.ContextNames.FieldServiceManagement.INVENTORY_SOURCE, ServiceOrderCostContext.InventorySource.ACTUALS);
        }
        return false;
    }
    private Double getDuration(Long startTime, Long endTime) {
        if (startTime != null && endTime != null) {
            return Double.valueOf((endTime - startTime)/1000);
        }
        return null;
    }
}
