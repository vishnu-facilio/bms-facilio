package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomKioskModule extends SignUpData {
    @Override
    public void addData() throws Exception {
        FacilioModule customKiosk = addCustomKioskModule();
        FacilioChain addModuleChain = TransactionChainFactory.addSystemModuleChain();
        addModuleChain.getContext().put(FacilioConstants.ContextNames.MODULE_LIST, Collections.singletonList(customKiosk));
        addModuleChain.execute();
    }

    private FacilioModule addCustomKioskModule() throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        // TODO Auto-generated method stub
        FacilioModule module = new FacilioModule("customkiosk",
                "CustomKiosk",
                "Custom_Kiosk",
                FacilioModule.ModuleType.BASE_ENTITY,
                modBean.getModule(FacilioConstants.ModuleNames.DEVICES),
                true
        );
        return module;
    }
}