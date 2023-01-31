package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;

public class GetExtendedModulesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
        JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(moduleName);
        FacilioUtil.throwIllegalArgumentException(parentModule == null, "Invalid module while getting extended modules");

        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
        }

        List<FacilioModule> extendedModules = modBean.getChildModules(parentModule, pagination, searchString, false);

        context.put(FacilioConstants.ContextNames.MODULE_LIST, extendedModules);

        return false;
    }
}
