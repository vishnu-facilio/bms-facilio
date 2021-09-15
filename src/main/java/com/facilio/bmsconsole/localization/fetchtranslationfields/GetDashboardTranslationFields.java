package com.facilio.bmsconsole.localization.fetchtranslationfields;

import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.context.WebTabContext;
import com.facilio.bmsconsole.localization.translationImpl.DashboardTranslationImpl;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.util.FacilioUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GetDashboardTranslationFields {

    static JSONArray constructTranslationObject (Long dashboardId, List<DashboardTabContext> dashboardTabContextList ,Properties properties ) throws Exception {

        DashboardContext dashboardContext = DashboardUtil.getDashboard(dashboardId);

        JSONArray jsonArray = new JSONArray();
        if(dashboardContext != null) {
            String dashboardKey = DashboardTranslationImpl.getTranslationKey(dashboardContext.getLinkName());
            jsonArray.add(TranslationsUtil.constructJSON(dashboardContext.getLinkName(),DashboardTranslationImpl.DASHBOARD,TranslationConstants.DISPLAY_NAME,dashboardContext.getLinkName(),dashboardKey,properties));
        }

        if(CollectionUtils.isNotEmpty(dashboardTabContextList)) {
            for (DashboardTabContext dashboardTabContext : dashboardTabContextList){
                String dashboardTabKey = DashboardTranslationImpl.getDashboardTabKey(String.valueOf(dashboardTabContext.getId()));
                jsonArray.add(TranslationsUtil.constructJSON(dashboardTabContext.getName(),DashboardTranslationImpl.DASHBOARD_TAB,TranslationConstants.DISPLAY_NAME,String.valueOf(dashboardTabContext.getId()),dashboardTabKey,properties));

                List<DashboardWidgetContext> dashboardWidgets = dashboardTabContext.getDashboardWidgets();

                if(CollectionUtils.isNotEmpty(dashboardWidgets)) {
                    for (DashboardWidgetContext dashboardWidget : dashboardWidgets) {
                        String widgetId = String.valueOf(dashboardWidget.getId());
                        String dashboardWidgetKey = DashboardTranslationImpl.getDashboardWidgetKey(widgetId);
                        jsonArray.add(TranslationsUtil.constructJSON(dashboardWidget.getHeaderText(),DashboardTranslationImpl.DASHBOARD_WIDGET,TranslationConstants.DISPLAY_NAME,widgetId,dashboardWidgetKey,properties));
                    }
                }
            }
        }

        JSONObject fieldObject = new JSONObject();
        fieldObject.put("fields",jsonArray);
        fieldObject.put("label","");

        JSONArray sectionArray = new JSONArray();
        sectionArray.add(fieldObject);
        return sectionArray;
    }
}
