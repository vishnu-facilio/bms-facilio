package com.facilio.fsm.commands.serviceOrders;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

public class VerifySOStatusUpdate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> dataList = (List<ServiceOrderContext>) recordMap.get(moduleName);
        for(ServiceOrderContext order : dataList) {
            ServiceOrderContext serviceOrderInfo = V3RecordAPI.getRecord(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER,order.getId());
            if(serviceOrderInfo.getStatus() == ServiceOrderContext.ServiceOrderStatus.CLOSED.getIndex()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order cannot be edited in Closed State");
            }
            if(order.getSite() != null && serviceOrderInfo.getSite().getId() != order.getSite().getId()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Site Cannot be modified for Service Order");
            }

            if(!serviceOrderInfo.getName().equals(order.getName())){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Service Order Name Cannot be modified");
            }
            if(serviceOrderInfo.getTerritory() != null){
                if(order.getTerritory()!=null && serviceOrderInfo.getTerritory().getId() != order.getTerritory().getId()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Territory Cannot be modified for Service Order");
                }
            } else {
                if(order.getTerritory()!=null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Territory Cannot be modified for Service Order");
                }
            }

            if(serviceOrderInfo.isAutoCreateSa() != order.isAutoCreateSa()){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Auto-Create SA Cannot be modified for Service Order");
            }
        }
        return false;
    }
}
