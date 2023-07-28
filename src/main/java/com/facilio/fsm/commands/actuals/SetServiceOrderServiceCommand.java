package com.facilio.fsm.commands.actuals;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderServiceContext;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetServiceOrderServiceCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderServiceContext> serviceOrderServices = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderServices)){
            for(ServiceOrderServiceContext serviceOrderService :serviceOrderServices){
                if(serviceOrderService.getStartTime()!=null && serviceOrderService.getEndTime()!=null &&  serviceOrderService.getStartTime() > serviceOrderService.getEndTime()){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Start time cannot be greater than end time");
                }
                if(serviceOrderService.getQuantity() <= 0) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Quantity cannot be empty");
                }
//                if()
//                serviceOrderService.setTotalCost();

            }
        }
        return false;
    }
}
