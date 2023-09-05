package com.facilio.bmsconsoleV3.commands.controlActions;

import com.facilio.bmsconsoleV3.context.controlActions.V3CommandsContext;
import com.facilio.bmsconsoleV3.context.controlActions.V3ControlActionContext;
import com.facilio.bmsconsoleV3.util.ControlActionAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Context;

import java.util.List;

public class CreateOneTimeJobForCommandExecutionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        V3ControlActionContext v3ControlActionContext = (V3ControlActionContext) context.get(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME);
        if(v3ControlActionContext == null){
            return false;
        }
        FacilioTimer.deleteJob(v3ControlActionContext.getId(), "PickCommandsToBeExecutedScheduledAction");
        FacilioTimer.deleteJob(v3ControlActionContext.getId(),"PickCommandsToBeExecutedRevertAction");
        FacilioTimer.scheduleOneTimeJobWithTimestampInSec(v3ControlActionContext.getId(),"PickCommandsToBeExecutedScheduledAction",v3ControlActionContext.getScheduledActionDateTime()/1000,"priority");
        if(v3ControlActionContext.getRevertActionDateTime() != null){
            FacilioTimer.scheduleOneTimeJobWithTimestampInSec(v3ControlActionContext.getId(),"PickCommandsToBeExecutedRevertAction",v3ControlActionContext.getRevertActionDateTime()/1000,"priority");
        }
        return false;
    }
}
