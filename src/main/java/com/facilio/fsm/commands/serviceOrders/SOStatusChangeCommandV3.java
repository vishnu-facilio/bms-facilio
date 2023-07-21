package com.facilio.fsm.commands.serviceOrders;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

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
            if(order.getStatus() < 0){
                if(order.isAutoCreateSa()){
                    order.setStatus(ServiceOrderContext.ServiceOrderStatus.SCHEDULED);
                }
                else {
                    order.setStatus(ServiceOrderContext.ServiceOrderStatus.NEW.getIndex());
                }
            }
            serviceOrdersNew.add(order);
        }
        recordMap.put(moduleName,serviceOrdersNew);
        context.put(Constants.RECORD_MAP,recordMap);
        return false;
    }
}
