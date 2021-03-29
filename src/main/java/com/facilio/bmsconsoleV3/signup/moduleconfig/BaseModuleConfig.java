package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.trigger.context.BaseTriggerContext;
import com.facilio.trigger.context.TriggerType;
import com.facilio.trigger.util.TriggerUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>As a framework, there are lot of functionalities are added, and that will affect all the modules. For
 * the all functionality to be work properly we need to add quite a lot of configuration for every modules. This
 * method will add all and upcoming configurations in the single place.</p> <br />
 *
 * <p>These are the list of configurations are added,</p>
 * <b>Triggers</b> - Default triggers types are added.
 */
public abstract class BaseModuleConfig extends SignUpData {

    private FacilioModule module;
    private String moduleName;

    protected void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    protected FacilioModule getModule() throws Exception {
        if (module == null) {
            if (StringUtils.isEmpty(moduleName)) {
                throw new IllegalArgumentException("Module not found");
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            module = modBean.getModule(moduleName);
            if (module == null) {
                throw new IllegalArgumentException("Module not found");
            }
        }
        return module;
    }

    protected void addTriggers() throws Exception {
        addTrigger("Create", EventType.CREATE);
        addTrigger("Update", EventType.EDIT);
        addTrigger("Delete", EventType.DELETE);
    }

    private BaseTriggerContext addTrigger(String name, EventType eventType) throws Exception {
        BaseTriggerContext trigger = new BaseTriggerContext();
        trigger.setName(name);
        trigger.setDefault(true);
        trigger.setStatus(true);
        trigger.setEventType(eventType);
        trigger.setType(TriggerType.MODULE_TRIGGER);

        FacilioChain triggerAddOrUpdateChain = TransactionChainFactoryV3.getTriggerAddOrUpdateChain();
        FacilioContext context = triggerAddOrUpdateChain.getContext();
        context.put(TriggerUtil.TRIGGER_CONTEXT, trigger);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModule().getName());
        triggerAddOrUpdateChain.execute();

        return trigger;
    }

    public abstract void migration() throws Exception;
}
