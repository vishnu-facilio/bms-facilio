package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ButtonTranslationImpl implements TranslationIfc {
    public static final String BUTTON="button";
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONArray  workflowRuleList= (JSONArray)jsonObject.get("workflowRuleList");
        if(workflowRuleList == null || workflowRuleList.isEmpty()){
            return json;
        }
        for (int i=0;i< workflowRuleList.size();i++){
            JSONObject  buttonObject= (JSONObject)workflowRuleList.get(i);
            String key = getTranslationKey((Long)buttonObject.get("id"));
            buttonObject.put(TranslationConstants.NAME,getTranslation(translationFile,key,(String)buttonObject.get(TranslationConstants.NAME)));
        }
        return json;
    }

    public static String getTranslationKey ( long id ) {
        return BUTTON+"."+id+"."+TranslationConstants.DISPLAY_NAME;
    }
}
