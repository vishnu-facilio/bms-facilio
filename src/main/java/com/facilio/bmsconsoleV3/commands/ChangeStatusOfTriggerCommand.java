package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;

public class ChangeStatusOfTriggerCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        long triggerId = (long) context.get(FacilioConstants.ContextNames.ID);
        BaseTriggerContext trigger = TriggerUtil.getTrigger(triggerId);
        if (trigger == null) {
            throw new IllegalArgumentException("Invalid trigger");
        }

        Boolean status = (Boolean) context.get(FacilioConstants.ContextNames.STATUS);
        if (status == null) {
            return false;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(trigger.getModuleId());

        trigger.setStatus(status);
        context.put(TriggerUtil.TRIGGER_CONTEXT, trigger);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
        return false;
    }
}
