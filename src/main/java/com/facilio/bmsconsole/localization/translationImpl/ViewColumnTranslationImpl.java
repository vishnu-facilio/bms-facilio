package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ViewColumnTranslationImpl implements TranslationIfc {

    public static final String VIEWS_COLUMNS = "viewColumn";

    public static String getTranslationKey ( String key ) {
        return VIEWS_COLUMNS + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }

    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject viewDetail = (JSONObject)jsonObject.get("viewDetail");
        String outerKey = getTranslationKey((String)viewDetail.get(TranslationConstants.NAME));
        viewDetail.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,outerKey,(String)viewDetail.get(TranslationConstants.DISPLAY_NAME)));
        JSONArray jsonArray = (JSONArray)viewDetail.get("fields");

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject obj = (JSONObject)jsonArray.get(i);
            String name = (String)obj.get("name");
            if(name == null) {
                name = (String)obj.get("fieldName");
            } 
            if(name == null) {
                continue;
            }

            String viewKey = getTranslationKey(name);
            obj.put("columnDisplayName",getTranslation(translationFile,viewKey,(String)obj.get("columnDisplayName")));
        }

        return json;
    }
}
