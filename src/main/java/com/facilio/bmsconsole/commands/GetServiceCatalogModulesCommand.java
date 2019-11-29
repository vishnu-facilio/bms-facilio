package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetServiceCatalogModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();

        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.ASSET));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORKPERMIT));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.VENDORS));
        modules.add(modBean.getModule(FacilioConstants.ContextNames.PURCHASE_CONTRACT));

        modules.add(modBean.getModule(FacilioConstants.ContextNames.VISITOR_LOGGING));

        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);
        return false;
    }
}
