package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.List;

public class AddDefaultSystemFields extends SignUpData {
    @Override
    public void addData () throws Exception {
        if(!FacilioProperties.isProduction()){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioModule> modules = new ArrayList<>();
            modules.add(modBean.getModule(FacilioConstants.ContextNames.ATTENDANCE));
            FacilioChain addModuleChain = TransactionChainFactory.addDefaultSystemFields();
            addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, modules);
            addModuleChain.getContext().put(FacilioConstants.Module.SYS_FIELDS_NEEDED, true);
            addModuleChain.execute();
        }
    }
}
