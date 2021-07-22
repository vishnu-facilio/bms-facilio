package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class WebTabTranslationImpl implements TranslationIfc {

    public static final String WEB_TAB = "webTab";
    public static final String WEB_TAB_GROUP = "webTabGroup";

    public static String getTranslationKey ( String prefix,String value ) {
        return prefix + "." + value + "." + TranslationConstants.DISPLAY_NAME;
    }

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject application = (JSONObject)jsonObject.get("application");
        JSONArray jsonArray = (JSONArray)application.get("layouts");

        for (int i = 0; i < jsonArray.size(); i++) {

            JSONObject webTabGroups = (JSONObject)jsonArray.get(i);
            JSONArray webTabGroupList = (JSONArray)webTabGroups.get("webTabGroupList");

            for (int k = 0; k < webTabGroupList.size(); k++) {

                JSONObject webTabGroupObject = (JSONObject)webTabGroupList.get(k);
                String webTabGroupkey = getTranslationKey(WEB_TAB_GROUP,(String)webTabGroupObject.get("route"));
                webTabGroupObject.put(TranslationConstants.NAME,getTranslation(translationFile,webTabGroupkey,(String)webTabGroupObject.get(TranslationConstants.NAME)));

                JSONArray webTabArray = (JSONArray)webTabGroupObject.get("webTabs");

                for (int j = 0; j < webTabArray.size(); j++) {
                    JSONObject webTabObject = (JSONObject)webTabArray.get(j);
                    String key = getTranslationKey(WEB_TAB,(String)webTabObject.get("route"));
                    webTabObject.put(TranslationConstants.NAME,getTranslation(translationFile,key,(String)webTabObject.get(TranslationConstants.NAME)));
                }
            }
        }

        return json;
    }
}
