package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateTriggerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        BaseTriggerContext trigger = (BaseTriggerContext) context.get(TriggerUtil.TRIGGER_CONTEXT);
        EventType eventType = trigger.getEventTypeEnum();

        if (eventType.equals(EventType.TIMESERIES_COMPLETE)){
            return false;
        }

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);

        List<BaseTriggerContext> oldTriggers = TriggerUtil.getTriggersForEventTypeAndModule(module.getModuleId(), trigger.getEventTypeEnum(), BaseTriggerContext.class);

        if (CollectionUtils.isNotEmpty(oldTriggers)) {
            for (BaseTriggerContext oldTrigger : oldTriggers) {
                if (oldTrigger.equals(trigger)) {
                    context.put(TriggerUtil.TRIGGER_CONTEXT, oldTrigger);
                    break;
                }
            }
        }


        return false;
    }
}
