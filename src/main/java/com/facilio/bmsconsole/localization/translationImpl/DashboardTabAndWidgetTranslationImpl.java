package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class DashboardTabAndWidgetTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("dashboardTabContext");
        if(jsonObject != null) {
            JSONArray clientJsonWidget = (JSONArray)jsonObject.get("clientWidgetJson");

            if(clientJsonWidget != null && !clientJsonWidget.isEmpty()) {
                for (int i = 0; i < clientJsonWidget.size(); i++) {
                    JSONObject widgetJson = (JSONObject)clientJsonWidget.get(i);
                    JSONObject widget = (JSONObject)widgetJson.get("widget");
                    String widgetId = String.valueOf(widget.get("id"));
                    if(widgetId != null) {
                        String widgetKey = DashboardTranslationImpl.getDashboardWidgetKey(widgetId);
                        JSONObject headerObject = (JSONObject)widget.get("header");
                        String title = (String)headerObject.get("title");
                        if(title != null) {
                            headerObject.put("title",getTranslation(translationFile,widgetKey,title));
                        }
                    }
                }
            }
        }
        return json;
    }
}
