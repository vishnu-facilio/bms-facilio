package com.facilio.trigger.command;

import com.facilio.bmsconsole.util.ScheduledRuleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

public class ValidateScheduleTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
        EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);

        if (eventType.equals(EventType.TIMESERIES_COMPLETE)){
            return false;
        }

        BaseTriggerContext oldtrigger = TriggerUtil.getTrigger(trigger.getId(),eventType);
        if (oldtrigger.getEventTypeEnum().equals(EventType.SCHEDULED) && !(oldtrigger.getEventTypeEnum().equals(trigger.getEventTypeEnum()))){
            ScheduledRuleAPI.deleteScheduledTriggerJob(oldtrigger.getId());
        }
        return false;
    }
}
