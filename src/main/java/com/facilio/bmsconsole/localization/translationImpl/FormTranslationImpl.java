package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FormTranslationImpl implements TranslationIfc {
    public static final String FORM = "form";

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONObject object = (JSONObject)json.get("result");
        JSONArray jsonArray = (JSONArray)object.get("forms");
        if(jsonArray == null || jsonArray.isEmpty()) {
            return json;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject formObject = (JSONObject)jsonArray.get(i);
            String fieldKey = getTranslationKey((String)formObject.get(TranslationConstants.NAME));
            formObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,fieldKey,(String)formObject.get(TranslationConstants.DISPLAY_NAME)));
        }
        return json;
    }

    public static String getTranslationKey ( String key ) {
        return FORM + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }
}
