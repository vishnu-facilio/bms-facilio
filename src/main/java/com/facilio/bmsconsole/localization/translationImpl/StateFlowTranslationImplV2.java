package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateFlowTranslationImplV2 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONArray status = (JSONArray)jsonObject.get("status");

        if(status != null && !status.isEmpty()){

            for (int i=0;i< status.size();i++){
                JSONObject statusObject = (JSONObject)status.get(i);
                String currentStateKey = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(statusObject.get("id")));
                statusObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,currentStateKey,(String)statusObject.get(TranslationConstants.DISPLAY_NAME)));
            }
        }

        return json;
    }
}
