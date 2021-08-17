package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class DashboardTranslationImplV1 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONArray dashboardJson = (JSONArray)json.get("dashboardJson");

        for (int i = 0; i < dashboardJson.size(); i++) {
            JSONObject innerObject = (JSONObject)dashboardJson.get(i);
            JSONArray childJson = (JSONArray)innerObject.get("children");

            if(childJson != null && !childJson.isEmpty()) {

                for (int j = 0; j < childJson.size(); j++) {
                    JSONObject widgetJson = (JSONObject)childJson.get(j);
                    JSONObject widget = (JSONObject)widgetJson.get("widget");
                    String widgetId = String.valueOf(widget.get("id"));
                    if(widgetId != null){
                        String widgetKey = DashboardTranslationImpl.getDashboardWidgetKey(widgetId);
                        JSONObject headerObject = (JSONObject)widget.get("header");
                        String title = (String)headerObject.get("title");
                        if(title != null){
                            headerObject.put("title",getTranslation(translationFile,widgetKey,title));
                        }
                    }
                }
            }

            JSONArray subTabs = (JSONArray)innerObject.get("tabs");
            if(subTabs != null && !subTabs.isEmpty()){
                for (int k = 0; k < subTabs.size(); k++) {
                    JSONObject jsonObject = (JSONObject)subTabs.get(k);
                    String dashboardTabKey = DashboardTranslationImpl.getDashboardTabKey(String.valueOf(jsonObject.get("id")));
                    jsonObject.put("name",getTranslation(translationFile,dashboardTabKey,(String)jsonObject.get("name")));

                    JSONArray childTabs = (JSONArray)jsonObject.get("childTabs");

                    if(childTabs != null && !childTabs.isEmpty()) {
                        for (int l = 0; l < childTabs.size(); l++) {
                            JSONObject subTabObject = (JSONObject)childTabs.get(l);
                            String dashboardSubTabKey = DashboardTranslationImpl.getDashboardTabKey(String.valueOf(subTabObject.get("id")));
                            subTabObject.put("name",getTranslation(translationFile,dashboardSubTabKey,(String)subTabObject.get("name")));
                        }
                    }
                }
            }
        }

        return json;
    }
}
