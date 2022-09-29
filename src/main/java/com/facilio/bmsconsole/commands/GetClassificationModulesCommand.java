package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetClassificationModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioModule> modules = new ArrayList<>();
        List<FacilioModule> customModules = new ArrayList<>();

        customModules.addAll(modBean.getModuleList(FacilioModule.ModuleType.BASE_ENTITY, true));
        modules.addAll(customModules);
        context.put(FacilioConstants.ContextNames.MODULE_LIST, modules);

        return false;
    }
}
