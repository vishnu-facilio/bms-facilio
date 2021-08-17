package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class DashboardTranslationImpl implements TranslationIfc {

    public static final String DASHBOARD = "dashboard";
    public static final String DASHBOARD_FOLDER="dashboardFolder";
    public static final String DASHBOARD_TAB="dashboardTab";
    public static final String DASHBOARD_WIDGET="dashboardWidget";

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONArray array = (JSONArray)json.get("dashboardFolders");
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject)array.get(i);
            String outerKey = getDashboardFolderKey(String.valueOf(object.get("id")));
            object.put(TranslationConstants.NAME,getTranslation(translationFile,outerKey,(String)object.get(TranslationConstants.NAME)));
            JSONArray innerArray = (JSONArray)object.get("dashboards");
            if(innerArray != null && !innerArray.isEmpty()) {
                for (int j = 0; j < innerArray.size(); j++)  {
                    JSONObject innerObject = (JSONObject)innerArray.get(j);
                    String innerKey = getTranslationKey((String)innerObject.get("linkName"));
                    innerObject.put("dashboardName",getTranslation(translationFile,innerKey,(String)innerObject.get("dashboardName")));
                }
            }
        }
        return json;
    }

    public static String getTranslationKey ( String key ) {
        return DASHBOARD + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }

    public static String getDashboardFolderKey ( String key ) {
        return DASHBOARD_FOLDER + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }

    public static String getDashboardTabKey ( String key ) {
        return DASHBOARD_TAB + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }

    public static String getDashboardWidgetKey ( String key ) {
        return DASHBOARD_WIDGET + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }
}
