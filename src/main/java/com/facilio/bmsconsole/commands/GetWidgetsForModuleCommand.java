package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class GetWidgetsForModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(moduleName);
        if(module != null ) {
            long moduleId = module.getModuleId();;
            List<Map<String, Object>> widgets = WidgetAPI.getWidgetsForModule(moduleId);
            context.put(FacilioConstants.Widget.WIDGETS, widgets);
        }
        return false;
    }
}
