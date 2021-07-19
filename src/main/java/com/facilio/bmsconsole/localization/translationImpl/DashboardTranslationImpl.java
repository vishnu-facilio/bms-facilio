package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class DashboardTranslationImpl implements TranslationIfc {
    private static final String DASHBOARD = "dashboard";

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONArray array = (JSONArray)json.get("dashboardFolders");
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = (JSONObject)array.get(i);
            String outerKey = getTranslationKey(String.valueOf(object.get("id")));
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

    private String getTranslationKey ( String key ) {
        return DASHBOARD + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }
}
