package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONObject;

import java.util.Properties;

public class WebTabTranslationImpl implements TranslationIfc {

    private static final String WEB_TAB = "webTab";

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject webTabObject = (JSONObject)jsonObject.get("webTabs");
        webTabObject.keySet().forEach(webTab -> {
            String key = getTranslationKey((String)webTabObject.get("route"));
            webTabObject.put(TranslationConstants.NAME,getTranslation(translationFile,key,(String)webTabObject.get(TranslationConstants.NAME)));
        });
        return json;
    }

    private String getTranslationKey ( String value ) {
        return WEB_TAB + "." + value + "." + TranslationConstants.DISPLAY_NAME;
    }
}
