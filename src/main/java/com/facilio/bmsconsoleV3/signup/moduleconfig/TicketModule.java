package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.context.Constants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TicketModule extends BaseModuleConfig{

    public TicketModule() throws Exception{
        setModuleName(FacilioConstants.ContextNames.TICKET);
    }

    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="Tickets_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.TICKET), tableName);
    }
}
