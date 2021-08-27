package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.DashboardTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetDashboardTabAndWidgetTransFields implements TranslationTypeInterface {
    @Override
    public JSONArray constructTranslationObject ( @NonNull WebTabContext context,Map<String,String> filters,Properties properties ) throws Exception {

        Long dashboardTabId = Long.parseLong(filters.get("dashboardTabId"));
        DashboardTabContext dashboardTabContexts = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
        DashboardContext dashboardContext = DashboardUtil.getDashboard(dashboardTabContexts.getDashboardId());
        JSONArray jsonArray = new JSONArray();

        if(dashboardTabContexts != null) {
            String dashboardTabKey = DashboardTranslationImpl.getDashboardTabKey(String.valueOf(dashboardTabId));
            jsonArray.add(TranslationsUtil.constructJSON(dashboardTabContexts.getName(),DashboardTranslationImpl.DASHBOARD_TAB,TranslationConstants.DISPLAY_NAME,String.valueOf(dashboardTabId),dashboardTabKey,properties));

            List<DashboardWidgetContext> dashboardWidgets = dashboardTabContexts.getDashboardWidgets();

            if(CollectionUtils.isNotEmpty(dashboardWidgets)) {
                for (DashboardWidgetContext dashboardWidget : dashboardWidgets) {
                    String widgetId = String.valueOf(dashboardWidget.getId());
                    String dashboardWidgetKey = DashboardTranslationImpl.getDashboardWidgetKey(widgetId);
                    jsonArray.add(TranslationsUtil.constructJSON(dashboardWidget.getHeaderText(),DashboardTranslationImpl.DASHBOARD_WIDGET,TranslationConstants.DISPLAY_NAME,widgetId,dashboardWidgetKey,properties));
                }
            }
        }

        if(dashboardContext != null) {
            String dashboardKey = DashboardTranslationImpl.getTranslationKey(dashboardContext.getLinkName());
            jsonArray.add(TranslationsUtil.constructJSON(dashboardContext.getLinkName(),DashboardTranslationImpl.DASHBOARD,TranslationConstants.DISPLAY_NAME,dashboardContext.getLinkName(),dashboardKey,properties));
        }


        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);

        return sectionArray;
    }
}
