package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.audit.AuditData;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetDashboardFilterModulesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getModuleList();
        FacilioContext moduleContext = chain.getContext();
        FacilioModule.ModuleType moduleType = FacilioModule.ModuleType.BASE_ENTITY;
        moduleContext.put(FacilioConstants.ContextNames.MODULE_TYPE, moduleType.getValue());
        moduleContext.put(FacilioConstants.ContextNames.FETCH_DEFAULT_MODULES, true);
        chain.execute();

        List<FacilioModule> moduleList = (List<FacilioModule>) moduleContext.get(FacilioConstants.ContextNames.MODULE_LIST);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        moduleList.add(modBean.getModule(FacilioConstants.ContextNames.ALARM_SEVERITY));
        moduleList.add(modBean.getModule(FacilioConstants.ContextNames.GROUPS));
        context.put(FacilioConstants.ContextNames.MODULE_LIST, moduleList);
        return false;
    }
}
