package com.facilio.fsm.commands.serviceTasks;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ServiceTaskStatusCheck extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<String, Map<Long, ServiceTaskContext>> oldrecordMap = (Map<String, Map<Long, ServiceTaskContext>>) context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP);
        Map<Long, ServiceTaskContext> oldMap = oldrecordMap.get(moduleName);
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(serviceTasks)) {
            for (ServiceTaskContext serviceTask : serviceTasks) {
                if (serviceTask != null) {
                    ServiceTaskContext oldTask = oldMap.get(serviceTask.getId());
                    if (oldTask.getStatusEnum().equals(ServiceTaskContext.ServiceTaskStatus.CANCELLED)) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Unable to update Service Task in Cancelled State");
                    }
                    if (oldTask.getStatusEnum().equals(ServiceTaskContext.ServiceTaskStatus.COMPLETED) && !serviceTask.getStatusEnum().equals(ServiceTaskContext.ServiceTaskStatus.IN_PROGRESS)) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "Unable to update Service Task in Completed State");
                    }
                }
            }
        }
        return false;
    }
}
