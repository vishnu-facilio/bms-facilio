package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FormFieldTranslationImpl implements TranslationIfc {
    public static final String FORM_FIELD = "formField";
    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject fieldObject = (JSONObject)jsonObject.get("fields");
        if(fieldObject == null || fieldObject.isEmpty()){
            return json;
        }
        fieldObject.keySet().forEach(field ->{
            String key = getTranslationKey((String)fieldObject.get(TranslationConstants.NAME));
            fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));
        });
        return json;
    }

    public static String getTranslationKey ( String value ) {
        return  FORM_FIELD+"."+value+"."+TranslationConstants.DISPLAY_NAME;
    }
}
