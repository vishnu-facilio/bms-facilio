package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetAutomationModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.VISITOR)) {
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));
        }
       if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONTRACT)) {
        	modules.add(modBean.getModule(FacilioConstants.ContextNames.CONTRACTS));
        }

        if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.NEW_ALARMS)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.NEW_READING_ALARM));
        }
        else {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ALARM));
        }

        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.CUSTOM));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
