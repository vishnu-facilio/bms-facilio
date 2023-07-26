package com.facilio.fsm.actions;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

@Setter @Getter
@Log4j
public class ServiceOrderAction extends V3Action {
    private String moduleName;
    private Long orderId;
    private int status;
    private boolean skipValidation;

    public String updateServiceOrders()throws Exception{
        if(orderId == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order id can not be empty");
        }
        try {
            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);

            ServiceOrderContext.ServiceOrderStatus newStatus =  ServiceOrderContext.ServiceOrderStatus.valueOf(status);
            if(newStatus == ServiceOrderContext.ServiceOrderStatus.CANCELLED){
                if(serviceOrderInfo.getStatus() == ServiceOrderContext.ServiceOrderStatus.CANCELLED.getIndex()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order already in Cancelled state");
                }
                if(serviceOrderInfo.getStatus() == ServiceOrderContext.ServiceOrderStatus.COMPLETED.getIndex()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be Cancelled as it is already Completed");
                }
                if(serviceOrderInfo.getStatus() == ServiceOrderContext.ServiceOrderStatus.CLOSED.getIndex()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be Cancelled as it is already Closed");
                }

                if(!skipValidation){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Cancelling Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                }

                serviceOrderInfo.setStatus(newStatus);
                updateServiceOrder(serviceOrderInfo);
            }

            if(newStatus == ServiceOrderContext.ServiceOrderStatus.COMPLETED){

                if(!skipValidation){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Completing Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                }

                serviceOrderInfo.setStatus(newStatus);
                Long startDuration = serviceOrderInfo.getActualStartTime();
                Long endDuration = System.currentTimeMillis();
                serviceOrderInfo.setActualEndTime(endDuration);
                serviceOrderInfo.setActualDuration(endDuration - startDuration);
                updateServiceOrder(serviceOrderInfo);
            }

        }catch(Exception e){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,e.getMessage());
        }
        return SUCCESS;
    };
}
