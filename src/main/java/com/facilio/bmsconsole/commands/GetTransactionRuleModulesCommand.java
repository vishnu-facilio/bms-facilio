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
        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
        }

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
