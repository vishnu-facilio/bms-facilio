package com.facilio.trigger.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.Iterator;
import java.util.List;

public class GetAllTriggersCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module name");
        }

        List<BaseTriggerContext> triggers = TriggerUtil.getTriggers(module, null, null, false, true, null);
        if (CollectionUtils.isNotEmpty(triggers)) {
            Iterator<BaseTriggerContext> iterator = triggers.iterator();
            while (iterator.hasNext()) {
                BaseTriggerContext next = iterator.next();
                if (next.isInternal()) {
                    iterator.remove();
                }
            }
        }
        context.put(TriggerUtil.TRIGGERS_LIST, triggers);
        return false;
    }
}
