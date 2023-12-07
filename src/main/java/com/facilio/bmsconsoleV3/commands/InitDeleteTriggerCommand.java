package com.facilio.bmsconsoleV3.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerChainUtil;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class InitDeleteTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(FacilioConstants.ContextNames.ID);
        BaseTriggerContext trigger = TriggerUtil.getTrigger(id);

        FacilioChain triggerChain = TriggerChainUtil.getTriggerDeleteChain(trigger.getEventTypeEnum());
        FacilioContext triggerContext = triggerChain.getContext();
        triggerContext.put(FacilioConstants.ContextNames.ID, id);
        triggerChain.execute();

        return false;
    }
}
