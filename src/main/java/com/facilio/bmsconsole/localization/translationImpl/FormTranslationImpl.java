package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FormTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONObject object = (JSONObject)json.get("result");
        JSONObject formObject = (JSONObject)object.get("form");
        if(formObject == null || formObject.isEmpty()){
            return json;
        }
        formObject.keySet().forEach(key->{
            if(key.equals(TranslationConstants.FIELDS)) {
                JSONArray fieldJson = (JSONArray)formObject.get(TranslationConstants.FIELDS);
                for (int i = 0; i < fieldJson.size(); i++) {
                    JSONObject fieldObject = (JSONObject)fieldJson.get(i);
                    String fieldKey = ModuleTranslationUtils.getFieldTranslationKey((String)fieldObject.get(TranslationConstants.NAME));
                    fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,fieldKey,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));
                }
            }
        });

        return json;
    }
}
