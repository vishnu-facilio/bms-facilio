package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;
import java.util.Set;

public class FieldOptionTranslationImpl implements TranslationIfc {

    private static final String FIELD_OPTION = "fieldOption";
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONArray jsonArray = (JSONArray)jsonObject.get("fields");
        for (int i=0;i<jsonArray.size();i++){
            JSONObject fieldObject = (JSONObject)jsonArray.get(i);
            String commonKey = (String)fieldObject.get(TranslationConstants.NAME);
            String outerKey = getTranslationKey(commonKey);
            fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,outerKey,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));
            if(fieldObject.containsKey("enumMap")){
                JSONObject enumMap = (JSONObject)fieldObject.get("enumMap");
                Set<String> keySet = enumMap.keySet();
                for ( String key : keySet){
                	 String innerKey = getTranslationKey("pickList."+commonKey+"."+key);
                    enumMap.put(key,getTranslation(translationFile,innerKey,(String)enumMap.get(key)));
                }
            }
        }
        return json;
    }

    private String getTranslationKey ( String key ) {
        return FIELD_OPTION+"."+key+"."+TranslationConstants.DISPLAY_NAME;
    }
}
