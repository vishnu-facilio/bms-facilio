package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FieldTranslationImplV3 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject data = (JSONObject)json.get("data");
        JSONArray fields = (JSONArray)data.get("fields");
        for (int i=0;i<fields.size();i++){
            JSONObject fieldObject = (JSONObject)fields.get(i);
            String fieldKey = ModuleTranslationUtils.getFieldTranslationKey((String)fieldObject.get("name"));
            fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,fieldKey,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));
        }
        return json;
    }
}
