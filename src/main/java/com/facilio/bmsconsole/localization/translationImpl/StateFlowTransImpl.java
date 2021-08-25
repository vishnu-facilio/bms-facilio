package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateFlowTransImpl implements TranslationIfc {

    public static String STATE_FLOW = "stateFlow";

    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject result = (JSONObject)json.get("result");
        JSONArray stateFlows = (JSONArray)result.get("stateFlows");

        if(stateFlows != null && !stateFlows.isEmpty()) {
            for (int i = 0; i < stateFlows.size(); i++) {
                JSONObject statusObject = (JSONObject)stateFlows.get(i);
                String currentStateKey = StateFlowTranslationImpl.getTranslationKey(STATE_FLOW,String.valueOf(statusObject.get("id")));
                statusObject.put(TranslationConstants.NAME,getTranslation(translationFile,currentStateKey,(String)statusObject.get(TranslationConstants.NAME)));
            }
        }
        return json;
    }
}
