package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectedAppWidgetContext;
import com.facilio.bmsconsole.context.PageSectionWidgetContext;
import com.facilio.bmsconsole.context.PageTabContext;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.page.Page;
import com.facilio.bmsconsole.util.ConnectedAppAPI;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import lombok.SneakyThrows;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetWidgetsForModuleCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        PageTabContext.TabType tabType = (PageTabContext.TabType) context.get(FacilioConstants.CustomPage.TAB_TYPE);

        ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modbean.getModule(moduleName);

        if (module != null) {
            List<WidgetContext> widgets = new ArrayList<>();
            if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.CONNECTEDAPPS) && tabType == PageTabContext.TabType.CONNECTED_TAB) {
                List<Long> moduleIds = module.getExtendedModuleIds();
                String entityId = StringUtils.join(moduleIds, ",");
                List<ConnectedAppWidgetContext> summaryWidgets = ConnectedAppAPI.getConnectedAppWidgets(ConnectedAppWidgetContext.EntityType.SUMMARY_PAGE, entityId);
                if(CollectionUtils.isNotEmpty(summaryWidgets)) {
                for (ConnectedAppWidgetContext summaryWidget : summaryWidgets) {
                    PageSectionWidgetContext widget = new PageSectionWidgetContext();
                    widget.setDisplayName(summaryWidget.getWidgetName());
                    JSONObject summaryWidgetParams = new JSONObject();
                    summaryWidgetParams.put("widgetId", summaryWidget.getId());
                    summaryWidgetParams.put("name", summaryWidget.getWidgetName());
                    widget.setWidgetParams(summaryWidgetParams);
                }
                    context.put(FacilioConstants.Widget.WIDGETS, widgets);
                }
            } else {
                long moduleId = module.getModuleId();
                widgets = WidgetAPI.getWidgetsForModule(moduleId);
                if (CollectionUtils.isNotEmpty(widgets)) {
                    widgets.removeIf(f -> f.getWidgetType().getFeatureId() != -1 && !hasLicenseEnabled(f.getWidgetType().getFeatureId()));
                    context.put(FacilioConstants.Widget.WIDGETS, FieldUtil.getAsJSONArray(widgets, WidgetContext.class));
                }
            }
        }
        return false;
    }

    @SneakyThrows
    private boolean hasLicenseEnabled(int featureLicense) {
        AccountUtil.FeatureLicense license = AccountUtil.FeatureLicense.getFeatureLicense(featureLicense);
        boolean isEnabled = true;
        if (license != null) {
            isEnabled = AccountUtil.isFeatureEnabled(license);
        }
        return isEnabled;
    }
}
