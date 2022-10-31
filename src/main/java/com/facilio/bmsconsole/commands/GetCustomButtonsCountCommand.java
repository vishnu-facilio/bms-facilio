package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

public class GetCustomButtonsCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (StringUtils.isEmpty(moduleName)) {
            throw new Exception("Module cannot be empty");
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new Exception("Module cannot be empty");
        }

        long count = WorkflowRuleAPI.getCustomButtonsCount(module, searchString);

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }
}
