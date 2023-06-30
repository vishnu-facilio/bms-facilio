package com.facilio.fsm.commands.serviceTasks;

import com.facilio.command.FacilioCommand;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;

public class taskStatusUpdate extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long taskId = (Long) context.get(Constants.RECORD_ID);
        HashMap<String,Object> recordMap = (HashMap<String, Object>) context.get(Constants.RECORD_MAP);
        List<ServiceTaskContext> serviceTask = (List<ServiceTaskContext>) recordMap.get(context.get("moduleName"));
        for(ServiceTaskContext task : serviceTask) {
            task.setStatus(ServiceTaskContext.ServiceTaskStatus.NEW.getIndex());
        }
        recordMap.put((String) context.get("moduleName"),serviceTask);
        context.put(Constants.RECORD_MAP,recordMap);
        context.put("status", ServiceTaskContext.ServiceTaskStatus.NEW);
        return false;
    }
}
