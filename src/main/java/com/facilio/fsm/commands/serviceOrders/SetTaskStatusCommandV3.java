package com.facilio.fsm.commands.serviceOrders;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceOrderContext;
import com.facilio.fsm.context.ServiceOrderPlannedToolsContext;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.fsm.context.ServiceTaskStatusContext;
import com.facilio.fsm.util.ServiceOrderAPI;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetTaskStatusCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<ServiceOrderContext> serviceOrders = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceOrders)){
            for(ServiceOrderContext serviceOrder : serviceOrders){
                Map<String, List<Map<String, Object>>> subForm = new HashMap<>();
                if(serviceOrder!=null && CollectionUtils.isNotEmpty(serviceOrder.getServiceTask())){
                    List<ServiceTaskContext> serviceTasks = serviceOrder.getServiceTask();
                    for(ServiceTaskContext serviceTask : serviceTasks){
                        if(serviceTask.getStatus()==null){
                            ServiceTaskStatusContext taskStatus = ServiceOrderAPI.getTaskStatus(FacilioConstants.ContextNames.ServiceTaskStatus.NEW);
                            serviceTask.setStatus(taskStatus);
                        }
                    }
                    subForm.put(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_TASK, FieldUtil.getAsMapList(serviceTasks, ServiceTaskContext.class));
                    serviceOrder.setSubForm(subForm);
                }
            }
            recordMap.put(moduleName,serviceOrders);
            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
        }
        return false;
    }

}
