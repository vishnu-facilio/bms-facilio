package com.facilio.trigger.command;

import com.facilio.bmsconsole.util.ScheduledRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

public class DeleteScheduleTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long triggerId = (long) context.get(FacilioConstants.ContextNames.ID);
        if (triggerId < -1){
            return false;
        }
        ScheduledRuleAPI.deleteScheduledTriggerJob(triggerId);
        return false;
    }
}
