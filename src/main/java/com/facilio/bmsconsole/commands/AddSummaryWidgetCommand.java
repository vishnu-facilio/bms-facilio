package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SummaryWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import com.facilio.util.SummaryWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class AddSummaryWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SummaryWidget summaryWidget = (SummaryWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APP_ID);
        ApplicationContext app = appId != null ? ApplicationApi.getApplicationForId(appId) : null;

        if (summaryWidget != null) {
            if (app == null && summaryWidget.getAppId() <= 0) {
                app = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                appId = app.getId();
            } else if (app != null) {
                summaryWidget.setAppId(app.getId());
            }

            long moduleId;
            if (summaryWidget.getModuleId() <= 0) {
                String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                FacilioUtil.throwIllegalArgumentException(module == null, "Invalid moduleName");
                moduleId = module.getModuleId();
                summaryWidget.setModuleId(moduleId);
            } else {
                moduleId = summaryWidget.getModuleId();
            }

            Boolean isSystem = (Boolean) context.getOrDefault(FacilioConstants.CustomPage.IS_SYSTEM, false);
            String name = StringUtils.isNotEmpty(summaryWidget.getName()) ? summaryWidget.getName() :
                    StringUtils.isNotEmpty(summaryWidget.getDisplayName())? summaryWidget.getDisplayName(): "summaryWidget";
            List<String> existingNames = SummaryWidgetUtil.getExistingSummaryWidgetsNameOfModuleInApp(appId, moduleId);
            name = CustomPageAPI.generateUniqueName(name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", ""), existingNames, isSystem);
//            if((isSystem != null && isSystem) && StringUtils.isNotEmpty(summaryWidget.getName()) && !summaryWidget.getName().equalsIgnoreCase(name)) {
//                throw new IllegalArgumentException("linkName already exists, given linkName for summaryWidget is invalid");
//            }
            summaryWidget.setName(name);

            SummaryWidgetUtil.insertSummaryWidgetToDB(summaryWidget);

            context.put(FacilioConstants.SummaryWidget.SUMMARY_WIDGET_GROUPS, summaryWidget.getGroups());
            context.put(FacilioConstants.ContextNames.ID, summaryWidget.getId());
        }
        return false;
    }
}
