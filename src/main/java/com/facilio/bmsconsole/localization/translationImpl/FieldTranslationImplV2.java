package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FieldTranslationImplV2 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties properties ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject object = (JSONObject)jsonObject.get("meta");
        String key = ModuleTranslationUtils.getTranslationKey((String)object.get("name"));
        object.put(TranslationConstants.DISPLAY_NAME,getTranslation(properties,key,(String)object.get(TranslationConstants.DISPLAY_NAME)));

        JSONObject moduleObject = (JSONObject)object.get("module");
        String moduleKey = ModuleTranslationUtils.getTranslationKey((String)moduleObject.get("name"));
        moduleObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(properties,moduleKey,(String)moduleObject.get(TranslationConstants.DISPLAY_NAME)));

        JSONArray fieldJson = (JSONArray)object.get("fields");
        for (int i = 0; i < fieldJson.size(); i++) {
            JSONObject fieldObject = (JSONObject)fieldJson.get(i);
            String fieldKey = ModuleTranslationUtils.getFieldTranslationKey((String)fieldObject.get("name"));
            fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(properties,fieldKey,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));

        }
        return json;
    }
}
