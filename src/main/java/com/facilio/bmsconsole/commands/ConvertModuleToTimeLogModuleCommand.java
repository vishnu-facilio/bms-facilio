package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

public class ConvertModuleToTimeLogModuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE_NAME);
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        if (module == null){
            throw new IllegalArgumentException("Module cannot be empty");
        }
        FacilioModule timeLogModule = StateFlowRulesAPI.getTimeLogModule(module);
        if (timeLogModule == null){
            throw new IllegalArgumentException("Time Log module is not found");
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, timeLogModule.getName());
        return false;
    }
}
