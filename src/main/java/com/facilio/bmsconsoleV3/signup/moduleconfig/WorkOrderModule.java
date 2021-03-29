package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class WorkOrderModule extends BaseModuleConfig {

    public WorkOrderModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.WORK_ORDER);
    }

    @Override
    public void addData() throws Exception {
        addTriggers();
    }

    @Override
    public void migration() throws Exception {
        addTriggers();
    }
}
