package com.facilio.fsm.commands.serviceTasks;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

public class TaskStatusUpdate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTask = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        for(ServiceTaskContext task : serviceTask) {
            //defaulting the status of all tasks to new when created
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.NEW.getIndex());
        }
        recordMap.put((String) context.get("moduleName"),serviceTask);
        context.put(Constants.RECORD_MAP,recordMap);
        return false;
    }
}
