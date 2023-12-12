package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetTransactionRuleModulesCommand extends FacilioCommand {
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

        List<FacilioModule> otherSubMod = modBean.getSubModules(FacilioConstants.ContextNames.WORK_ORDER);
        if(CollectionUtils.isNotEmpty(otherSubMod)) {
            woSubModules.addAll(otherSubMod);
        }

        subModules.put(FacilioConstants.ContextNames.WORK_ORDER, woSubModules);

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PURCHASE)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_REQUEST));
            List<FacilioModule> otherPrSubMod = modBean.getSubModules(FacilioConstants.ContextNames.PURCHASE_REQUEST);
            if(CollectionUtils.isNotEmpty(otherPrSubMod)) {
                subModules.put(FacilioConstants.ContextNames.PURCHASE_REQUEST, otherPrSubMod);

            }
            modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_ORDER));
            List<FacilioModule> otherPoSubMod = modBean.getSubModules(FacilioConstants.ContextNames.PURCHASE_ORDER);
            if(CollectionUtils.isNotEmpty(otherPoSubMod)) {
                subModules.put(FacilioConstants.ContextNames.PURCHASE_ORDER, otherPoSubMod);
            }
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.QUOTATION)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.QUOTE));
            List<FacilioModule> otherQuoteSubMod = modBean.getSubModules(FacilioConstants.ContextNames.QUOTE);
            if(CollectionUtils.isNotEmpty(otherQuoteSubMod)) {
                subModules.put(FacilioConstants.ContextNames.QUOTE, otherQuoteSubMod);
            }

        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INVOICE)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.INVOICE));
            List<FacilioModule> otherInvoiceSubMod = modBean.getSubModules(FacilioConstants.ContextNames.INVOICE);
            if(CollectionUtils.isNotEmpty(otherInvoiceSubMod)) {
                subModules.put(FacilioConstants.ContextNames.INVOICE, otherInvoiceSubMod);
            }

        }

        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        context.put(FacilioConstants.ContextNames.SUB_MODULES, subModules);

        return false;
    }
}
