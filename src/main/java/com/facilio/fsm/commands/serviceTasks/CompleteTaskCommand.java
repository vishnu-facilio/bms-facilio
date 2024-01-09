package com.facilio.fsm.commands.serviceTasks;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.util.ServiceTaskUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class CompleteTaskCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        boolean validate = (boolean) context.getOrDefault(FacilioConstants.ContextNames.DO_VALIDTION,false);
        JSONObject bodyParams = new JSONObject();
        bodyParams.put("completeTask", true);
        ServiceTaskUtil.moveToCloseState(recordId, FacilioConstants.ContextNames.ServiceTaskStatus.COMPLETED,validate,bodyParams);
        return false;
    }
}
