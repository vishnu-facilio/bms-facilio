package com.facilio.fsm.actions;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;

import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

@Setter @Getter
@Log4j
public class ServiceOrderAction extends V3Action {
    private String moduleName;
    private Long orderId;
    private String status;
    private boolean skipValidation;

    public String fetchStatusActions() throws Exception{
        FacilioChain chain = FsmTransactionChainFactoryV3.getStatusBasedActions();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.STATUS, status);
        chain.execute();
        setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,FieldUtil.getAsJSON(context.get("buttons")));
        return V3Action.SUCCESS;
    }
    public String updateServiceOrders()throws Exception{
        if(orderId == null){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Service Order id can not be empty");
        }
        try {
            HashMap<String, String> successMsg = new HashMap<>();
            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);
            ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);
            ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);
            ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);
            ServiceOrderTicketStatusContext newStatus =  ServiceOrderAPI.getStatus(status);
            if(newStatus != null && newStatus.getId() == cancelledState.getId()){
                if(serviceOrderInfo.getStatus() ==  cancelledState){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order already in Cancelled state");
                }
                if(serviceOrderInfo.getStatus() == completedState){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be Cancelled as it is already Completed");
                }
                if(serviceOrderInfo.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED)){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be Cancelled as it is already Closed");
                }

                if(!skipValidation){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Cancelling Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                }

                serviceOrderInfo.setStatus(newStatus);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("msg","Service Order Cancelled Successfully");
            }

            if(newStatus != null && newStatus.getId() == completedState.getId()){

                if(!skipValidation){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Completing Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                }

                serviceOrderInfo.setStatus(newStatus);
                Long startDuration = serviceOrderInfo.getActualStartTime();
                Long endDuration = System.currentTimeMillis();
                serviceOrderInfo.setActualEndTime(endDuration);
                serviceOrderInfo.setActualDuration(endDuration - startDuration);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("msg","Service Order Completed Successfully");
            }

            if(newStatus !=null && newStatus.getId() == closedState.getId()){
                if(serviceOrderInfo.getStatus() != null && serviceOrderInfo.getStatus().getId() != completedState.getId()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Only Completed Service Orders can be closed");
                }
                serviceOrderInfo.setStatus(closedState);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("msg","Service Order Closed Successfully");
            }


            setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);

        }catch(Exception e){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,e.getMessage());
        }
        return SUCCESS;
    }
}
