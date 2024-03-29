package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.exception.FSMErrorCode;
import com.facilio.fsm.exception.FSMException;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerifySOStatusUpdate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> dataList = (List<ServiceOrderContext>) recordMap.get(moduleName);
        Map<String, Map<Long,ServiceOrderContext>> oldrecordMap = (Map<String, Map<Long,ServiceOrderContext>>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long,ServiceOrderContext> oldMap = oldrecordMap.get(moduleName);
        for(ServiceOrderContext order : dataList) {
//            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,order.getId());
            ServiceOrderContext oldOrder = oldMap.get(order.getId());

            if (oldOrder.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED) || oldOrder.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED) || (oldOrder.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.COMPLETED) && order.getStatus() != ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED))){
                throw new FSMException(FSMErrorCode.SO_CANCEL_EDIT_FAILED);
            }
//            if (oldOrder.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CANCELLED)){
//                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Unable to update Service Order in Cancelled State");
//            } else if (oldOrder.getStatus() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED)){
//                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Unable to update Service Order in Closed State");
//            }
//            if(oldOrder.getStatus() != null && oldOrder.getStatus().getId() == ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.CLOSED).getId()){
//                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be edited in Closed State");
//            }
            if(order.getSite() != null && oldOrder.getSite().getId() != order.getSite().getId()){
                throw new FSMException(FSMErrorCode.SO_SITE_PREVENT_CHANGE);
            }

            if(!oldOrder.getName().equals(order.getName())){
                throw new FSMException(FSMErrorCode.SO_NAME_PREVENT_CHANGE);
            }
            if(oldOrder.getTerritory() != null){
                if(order.getTerritory()!=null && oldOrder.getTerritory().getId() != order.getTerritory().getId()){
                    throw new FSMException(FSMErrorCode.SO_TERRITORY_PREVENT_CHANGE);
                }
            } else {
                if(order.getTerritory()!=null){
                    throw new FSMException(FSMErrorCode.SO_TERRITORY_PREVENT_CHANGE);
                }
            }

            if(oldOrder.isAutoCreateSa() != order.isAutoCreateSa()){
                throw new FSMException(FSMErrorCode.SO_AUTOCREATE_PREVENT_CHANGE);
            }
        }
        return false;
    }
}
