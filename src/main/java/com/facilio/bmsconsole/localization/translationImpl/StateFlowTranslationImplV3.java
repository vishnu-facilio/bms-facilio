package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateFlowTranslationImplV3 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject meta = (JSONObject)json.get("meta");
        JSONObject supplements = (JSONObject)meta.get("supplements");
        JSONObject workOrder = (JSONObject)supplements.get("workorder");
        JSONObject moduleState = (JSONObject)workOrder.get("moduleState");

        for (Object stateKey : moduleState.keySet()){
            JSONObject state = (JSONObject)moduleState.get(stateKey);
            String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(state.get("id")));
            state.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)state.get(TranslationConstants.DISPLAY_NAME)));
        }
        return json;
    }
}
