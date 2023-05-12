package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.fw.BeanFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import static com.facilio.util.SummaryWidgetUtil.getSummaryWidget;

public class GetCustomPageWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String widgetName = (String) context.get(FacilioConstants.ContextNames.WIDGET_NAME);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long appId = (long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1L);

        if (StringUtils.isEmpty(moduleName)){
            throw new IllegalArgumentException("ModuleName cannot be null");
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        long moduleId = moduleBean.getModule(moduleName).getModuleId();

        SummaryWidget pageWidgets = getSummaryWidget(appId, -1, widgetName, moduleId);

        context.put(FacilioConstants.ContextNames.CUSTOM_PAGE_WIDGET, pageWidgets);

        return false;
    }
}
