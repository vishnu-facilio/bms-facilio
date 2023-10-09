package com.facilio.fsm.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServiceTaskDurationUpdateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long,ServiceTaskContext> oldServiceTaskRecordMap = (Map<Long,ServiceTaskContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.OLD_RECORD_MAP)).get("serviceTask"));
        List<ServiceTaskContext> serviceTasks = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(serviceTasks)){
            String inProgress = FacilioConstants.ContextNames.ServiceTaskStatus.IN_PROGRESS;
            String completed = FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED;

            for(ServiceTaskContext serviceTask : serviceTasks){
                if(serviceTask!=null){
                    ServiceTaskContext oldServiceTask = oldServiceTaskRecordMap.get(serviceTask.getId());
                    if(oldServiceTask != null && !Objects.equals(serviceTask.getStatus(), oldServiceTask.getStatus())){
                        if(serviceTask.getStatus()!=null && serviceTask.getStatus().getName().equals(inProgress)){
                            serviceTask.setActualStartTime(System.currentTimeMillis());
                        }
                        if(serviceTask.getStatus()!=null && serviceTask.getStatus().getName().equals(completed)){
                            serviceTask.setActualEndTime(System.currentTimeMillis());
                            if(serviceTask.getActualStartTime()!=null && serviceTask.getActualEndTime()!=null) {
                                serviceTask.setActualDuration(getDuration(serviceTask.getActualStartTime(), serviceTask.getActualEndTime()));
                            }
                        }
                    }

                }
            }
        }
        return false;
    }
    private Double getDuration(Long startTime,Long endTime){
        return (endTime - startTime)/1000.0;
    }
}
