package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetSurveySupportedModules extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> surveySupportedModules = new ArrayList<>();

        surveySupportedModules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            surveySupportedModules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, surveySupportedModules);

        return false;
    }
}
