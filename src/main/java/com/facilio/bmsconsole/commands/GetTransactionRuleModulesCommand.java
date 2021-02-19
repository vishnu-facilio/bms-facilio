package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetTransactionRuleModulesCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
