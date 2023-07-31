package com.facilio.fsm.commands.serviceOrders;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SOStatusChangeCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = String.valueOf(context.get("moduleName"));
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);

        List<ServiceOrderContext> dataList = (List<ServiceOrderContext>) recordMap.get(moduleName);
        List<ServiceOrderContext> serviceOrdersNew = new ArrayList<>();
        for(ServiceOrderContext order : dataList) {
            if(order.getStatus() ==null || order.getStatus().getTypeCode() < 0){
                if(order.isAutoCreateSa()){
                    if(CollectionUtils.isEmpty(order.getServiceTask())){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Atleast One Task must be present when AutoCreate SA is enabled");
                    }
                    if(order.getPreferredStartTime() == null || order.getPreferredEndTime() == null || order.getPreferredStartTime() < 0 || order.getPreferredEndTime() < 0){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR,"Scheduled Start and End time are mandatory when AutoCreate SA is enabled");
                    }
                    order.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.SCHEDULED));
                }
                else {
                    order.setStatus(ServiceOrderAPI.getStatus(FacilioConstants.ServiceOrder.NEW));
                }
            }
            serviceOrdersNew.add(order);
        }
        recordMap.put(moduleName,serviceOrdersNew);
        context.put(Constants.RECORD_MAP,recordMap);
        return false;
    }
}
