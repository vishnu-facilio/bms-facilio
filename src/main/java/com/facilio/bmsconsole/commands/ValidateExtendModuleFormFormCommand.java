package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.List;

public class ValidateExtendModuleFormFormCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String extendModuleName = (String) context.get(FacilioConstants.ContextNames.EXTENDED_MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule facilioModule = modBean.getModule(moduleName);
        FacilioModule extendedModule = modBean.getModule(extendModuleName);

        FacilioUtil.throwIllegalArgumentException(facilioModule == null || extendedModule == null, "modules cannot be empty");

        List<Long> extendedModuleIds = extendedModule.getExtendedModuleIds();

        FacilioUtil.throwIllegalArgumentException(!(extendedModuleIds.contains(facilioModule.getModuleId())), moduleName + " is not a extend module for " + extendModuleName);

        context.put(FacilioConstants.ContextNames.MODULE_NAME, extendModuleName);

        return false;
    }
}
