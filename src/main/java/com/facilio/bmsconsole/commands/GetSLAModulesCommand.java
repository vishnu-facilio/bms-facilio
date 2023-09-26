package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetSLAModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();

        modules.add(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER));

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SERVICE_REQUEST)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.SERVICE_REQUEST));
        }

        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.INSPECTION)) {
            modules.add(modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE));
        }
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.FSM)) {
            modules.add(modBean.getModule(FacilioConstants.ContextNames.FieldServiceManagement.SERVICE_ORDER));
            modules.add(modBean.getModule(FacilioConstants.ServiceAppointment.SERVICE_APPOINTMENT));
        }

        modules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY,true));

        context.put(FacilioConstants.ContextNames.MODULE_LIST,modules);
        return false;
    }
}
