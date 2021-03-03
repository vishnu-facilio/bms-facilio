package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTransactionRuleModulesCommand extends FacilioCommand{
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List<FacilioModule>> subModules = new HashMap<>();
        List<FacilioModule> modules = new ArrayList<>();
        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        List<FacilioModule> woSubModules = new ArrayList<>();
        woSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_ITEMS));
        woSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_TOOLS));
        woSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WO_LABOUR));
        woSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WO_SERVICE));
        woSubModules.add(modBean.getModule(FacilioConstants.ContextNames.WORKORDER_COST));
        woSubModules.addAll(modBean.getSubModules(FacilioConstants.ContextNames.WORK_ORDER));

        subModules.put(FacilioConstants.ContextNames.WORK_ORDER, woSubModules);

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVENTORY)) {
            List<FacilioModule> prSubModules = new ArrayList<>();
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            prSubModules.addAll(modBean.getSubModules(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            subModules.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, prSubModules);

            List<FacilioModule> poSubModules = new ArrayList<>();
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
            poSubModules.addAll(modBean.getSubModules(FacilioConstants.ContextNames.PURCHASE_ORDER));
            subModules.put(FacilioConstants.ContextNames.PURCHASE_ORDER, poSubModules);

        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            List<FacilioModule> quoteSubModules = new ArrayList<>();
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
            quoteSubModules.addAll(modBean.getSubModules(FacilioConstants.ContextNames.QUOTE));
            subModules.put(FacilioConstants.ContextNames.QUOTE, quoteSubModules);
        }

        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
