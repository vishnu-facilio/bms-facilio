package com.facilio.fsm.commands.plans;


import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetServiceOrderPlannedToolsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderPlannedToolsContext> serviceOrderPlannedTools = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrderPlannedTools)){
            for(ServiceOrderPlannedToolsContext serviceOrderPlannedTool : serviceOrderPlannedTools){
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
                if(serviceOrderPlannedTool.getRate()!=null && serviceOrderPlannedTool.getQuantity()!=null && serviceOrderPlannedTool.getDuration()!=null){
                    //total cost computation
                    Double totalCost = serviceOrderPlannedTool.getRate() * serviceOrderPlannedTool.getQuantity() * (serviceOrderPlannedTool.getDuration() / 3600);
                    serviceOrderPlannedTool.setTotalCost(totalCost);
                }
            }
        }

        return false;
    }
}
