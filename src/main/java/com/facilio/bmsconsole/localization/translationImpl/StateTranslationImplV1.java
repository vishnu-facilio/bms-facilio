package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateTranslationImplV1 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject workOrder = (JSONObject)json.get("workorder");
        JSONObject state = (JSONObject)workOrder.get("moduleState");

        String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(state.get("id")));
        state.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)state.get(TranslationConstants.DISPLAY_NAME)));
        state.put("primaryValue",getTranslation(translationFile,key,(String)state.get("primaryValue")));
        state.put("status",getTranslation(translationFile,key,(String)state.get("status")));

        return json;
    }
}
