package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ViewColumnTranslationImpl implements TranslationIfc {

    private static final String VIEWS_COLUMNS ="viewColumn";

    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject viewDetail = (JSONObject)jsonObject.get("viewDetail");
        String outerKey = getTranslationKey((String)viewDetail.get(TranslationConstants.NAME));
        viewDetail.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,outerKey,(String)viewDetail.get(TranslationConstants.DISPLAY_NAME)));
        JSONArray jsonArray = (JSONArray)viewDetail.get("fields");
        for (int i=0;i<jsonArray.size();i++){
            JSONObject obj = (JSONObject)jsonArray.get(i);
            String fieldName = (String)obj.get("fieldName");
            if(StringUtils.isEmpty(fieldName)){
                fieldName = (String)obj.get("name");
            }
            if(StringUtils.isNotEmpty(fieldName)){
                String viewKey = ModuleTranslationUtils.getFieldTranslationKey(fieldName);
                obj.put("columnDisplayName",getTranslation(translationFile,viewKey,(String)obj.get("columnDisplayName")));
            }
        }

        return json;
    }

    private String getTranslationKey ( String key ) {
        return VIEWS_COLUMNS+"."+key+"."+TranslationConstants.DISPLAY_NAME;
    }
}
