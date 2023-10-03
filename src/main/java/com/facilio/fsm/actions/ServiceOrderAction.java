package com.facilio.fsm.actions;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.commands.FsmTransactionChainFactoryV3;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderTicketStatusContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.HashMap;
import java.util.Map;

import static com.facilio.fsm.util.ServiceOrderAPI.updateServiceOrder;

@Setter @Getter
@Log4j
public class ServiceOrderAction extends V3Action {
    private String moduleName;
    private Long orderId;
    private String status;
    private String identifier;
    private String buttonAction;
    private boolean validate;
    private static Map<String,String> statusMap = new HashMap<String, String>() {{
        put("cancelSO", FacilioConstants.ServiceOrder.CANCELLED);
        put("cloneSO", "Clone");
        put("associateSOSP", "Associate");
        put("completeWork", FacilioConstants.ServiceOrder.COMPLETED);
        put("closeSO", FacilioConstants.ServiceOrder.CLOSED);
    }};

    public String systemActions() throws Exception{
        return updateServiceOrders();
    }

    public String updateServiceOrders()throws Exception{
        if(orderId == null){
            throw new FSMException(FSMErrorCode.SO_ID_NOT_EMPTY);
        }
        if(statusMap.get(identifier).equals("Clone") || statusMap.get(identifier).equals("Associate") ){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, statusMap.get(identifier) +" feature is not enabled");
        }
        try {
            HashMap<String, String> successMsg = new HashMap<>();
            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,orderId);
            ServiceOrderTicketStatusContext inProgressState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.IN_PROGRESS);
            ServiceOrderTicketStatusContext cancelledState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED);
            ServiceOrderTicketStatusContext completedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED);
            ServiceOrderTicketStatusContext closedState = ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED);
            String statusData = statusMap.get(identifier);
            ServiceOrderTicketStatusContext newStatus =  ServiceOrderAPI.getStatus(statusData);
            if(newStatus != null && newStatus.getId() == cancelledState.getId()){
                if(serviceOrderInfo.getStatus().getId() ==  cancelledState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(serviceOrderInfo.getStatus().getId() == completedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(serviceOrderInfo.getStatus().getId() == closedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CANCEL_FAILED);
                }
                if(validate){
                    successMsg.put("title","Cancel Service Order");
                    successMsg.put("message","Cancelling Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                    setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);
                    return V3Action.SUCCESS;
                }
                serviceOrderInfo.setStatus(newStatus);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Service Order Cancelled Successfully");
            }

            if(newStatus != null && newStatus.getId() == completedState.getId()){
                if(serviceOrderInfo.getStatus().getId() != inProgressState.getId()){
                    throw new FSMException(FSMErrorCode.SO_COMPLETE_WARNING);
                }
                if(validate){
                    successMsg.put("title","Complete Service Order");
                    successMsg.put("message","Completing Service Order will affect all its Appointments and Tasks, Do you still want to proceed?");
                    setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);
                    return V3Action.SUCCESS;
                }

                serviceOrderInfo.setStatus(newStatus);
                Long startDuration = serviceOrderInfo.getActualStartTime();
                Long endDuration = System.currentTimeMillis();
                serviceOrderInfo.setActualEndTime(endDuration);
                serviceOrderInfo.setActualDuration(endDuration - startDuration);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Service Order Completed Successfully");
            }

            if(newStatus !=null && newStatus.getId() == closedState.getId()){
                if(serviceOrderInfo.getStatus() != null && serviceOrderInfo.getStatus().getId() != completedState.getId()){
                    throw new FSMException(FSMErrorCode.SO_CLOSE_WARNING);
                }
                serviceOrderInfo.setStatus(closedState);
                updateServiceOrder(serviceOrderInfo);
                successMsg.put("message","Service Order Closed Successfully");
            }
            setData(FacilioConstants.ServiceOrder.SERVICE_ORDER_STATUS_ACTIONS,successMsg);

        }catch(Exception e){
            if(e instanceof FSMException){
                throw e;
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR,e.getMessage());
            }
        }
        return SUCCESS;
    }
}
