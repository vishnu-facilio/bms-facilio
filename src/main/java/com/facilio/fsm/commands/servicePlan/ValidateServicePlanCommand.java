package com.facilio.fsm.commands.servicePlan;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServicePlanContext;
import com.facilio.fsm.context.ServiceTaskTemplateContext;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class ValidateServicePlanCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServicePlanContext> servicePlans = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(servicePlans)){
            for(ServicePlanContext servicePlan : servicePlans){
                List<ServiceTaskTemplateContext> serviceTaskTemplates = getServiceTaskTemplates(servicePlan);
                if(CollectionUtils.isNotEmpty(serviceTaskTemplates)){
                    Set<String> taskNames = new HashSet<>();
                    for(ServiceTaskTemplateContext serviceTaskTemplate : serviceTaskTemplates){
                        if(serviceTaskTemplate.getName()==null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Task Name is required");
                        }
                        if(!taskNames.add(serviceTaskTemplate.getName())){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR,"Names of the tasks are required to be unique within the service plan");
                        }
                    }
                }
            }
        }
        return false;
    }
    private List<ServiceTaskTemplateContext> getServiceTaskTemplates(ServicePlanContext servicePlan){
        List<ServiceTaskTemplateContext> serviceTaskTemplates = new ArrayList<>();
        if(servicePlan.getRelations() != null &&
                servicePlan.getRelations().get("serviceTaskTemplate") != null &&
                servicePlan.getRelations().get("serviceTaskTemplate").get(0) != null &&
                servicePlan.getRelations().get("serviceTaskTemplate").get(0).getData() != null){
            List<V3Context> serviceTaskTemplateContexts = servicePlan.getRelations().get("serviceTaskTemplate").get(0).getData();
            for(V3Context serviceTaskTemplate : serviceTaskTemplateContexts){
                serviceTaskTemplates.add(FieldUtil.getAsBeanFromMap(serviceTaskTemplate.getData(), ServiceTaskTemplateContext.class));
            }
        }
        return serviceTaskTemplates;
    }
}
