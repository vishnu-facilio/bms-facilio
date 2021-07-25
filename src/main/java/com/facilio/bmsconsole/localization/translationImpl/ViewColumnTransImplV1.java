package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ViewColumnTransImplV1 implements TranslationIfc {

    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONArray jsonArray = (JSONArray)json.get("fields");

        if(jsonArray != null && !jsonArray.isEmpty()) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject obj = (JSONObject)jsonArray.get(i);
                long id = (long)obj.get("id");
                String viewKey = ViewColumnTranslationImpl.getTranslationKey(String.valueOf(id));
                obj.put("columnDisplayName",getTranslation(translationFile,viewKey,(String)obj.get("columnDisplayName")));
            }
        }

        return json;
    }
}
