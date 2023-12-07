package com.facilio.trigger.command;

import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

public class GetTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
        if(id <= 0){
            return false;
        }
        if (!(eventType.equals(EventType.CREATE) || eventType.equals(EventType.EDIT) ||
                eventType.equals(EventType.TIMESERIES_COMPLETE))){
            return false;
        }
        BaseTriggerContext trigger = TriggerUtil.getTrigger(id, true);
        context.put(FacilioConstants.ContextNames.TRIGGER,trigger);
        return false;
    }
}
