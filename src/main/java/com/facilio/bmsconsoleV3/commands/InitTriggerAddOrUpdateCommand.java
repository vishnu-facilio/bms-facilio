package com.facilio.bmsconsoleV3.commands;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerChainUtil;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;


public class InitTriggerAddOrUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseTriggerContext trigger = (BaseTriggerContext)context.get(TriggerUtil.TRIGGER_CONTEXT);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        if (trigger == null){
            return false ;
        }

        if (trigger.getId() > 0){
            FacilioChain triggerChain = TriggerChainUtil.getTriggerUpdateChain(trigger.getEventTypeEnum());
            FacilioContext triggerContext = triggerChain.getContext();
            triggerContext.put(TriggerUtil.TRIGGER_CONTEXT, trigger);
            triggerContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            triggerChain.execute();

            context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext.get(TriggerUtil.TRIGGER_CONTEXT));
        }else{

            FacilioChain triggerChain = TriggerChainUtil.getTriggerCreateChain(trigger.getEventTypeEnum());
            FacilioContext triggerContext = triggerChain.getContext();
            triggerContext.put(TriggerUtil.TRIGGER_CONTEXT, trigger);
            triggerContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            triggerChain.execute();

            context.put(TriggerUtil.TRIGGER_CONTEXT, triggerContext.get(TriggerUtil.TRIGGER_CONTEXT));
        }

        return false;
    }
}
