package com.facilio.fsm.commands.servicePlan;

import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePlanItemsContext;
import com.facilio.fsm.context.ServiceTaskTemplateContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SetServicePlanItemsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePlanItemsContext> servicePlanItems = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePlanItems)){
            for(ServicePlanItemsContext servicePlanItem : servicePlanItems){
                if(servicePlanItem.getItemType()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Item Type cannot be empty");
                }
                if(servicePlanItem.getQuantity()==null){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR,"Quantity cannot be empty");
                }
                if(servicePlanItem.getServiceTaskTemplate()!=null && servicePlanItem.getServiceTaskTemplate().getId()>0){
                    ServiceTaskTemplateContext serviceTaskTemplate = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_TASK_TEMPLATE,servicePlanItem.getServiceTaskTemplate().getId(), ServiceTaskTemplateContext.class);
                    if(serviceTaskTemplate.getServicePlan()!=null){
                        servicePlanItem.setServicePlan(serviceTaskTemplate.getServicePlan());
                    }
                }
            }
        }
        return false;
    }
}
