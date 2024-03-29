package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateFlowTranslationImpl implements TranslationIfc {

    public static final String STATE = "state";
    public static final String STATE_TRANSITION = "stateTransition";
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject currentState = (JSONObject)jsonObject.get("currentState");
        if(currentState != null && !currentState.isEmpty()) {
            String currentStateKey = getTranslationKey(STATE,String.valueOf(currentState.get("id")));
            currentState.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,currentStateKey,(String)currentState.get(TranslationConstants.DISPLAY_NAME)));
        }

        JSONArray states = (JSONArray)jsonObject.get("states");
        if(states != null && !states.isEmpty()) {
            for (int i = 0; i < states.size(); i++) {
                JSONObject state = (JSONObject)states.get(i);
                String key = getTranslationKey(STATE_TRANSITION,String.valueOf(state.get("id")));
                state.put(TranslationConstants.NAME,getTranslation(translationFile,key,(String)state.get(TranslationConstants.NAME)));
            }
        }
        return json;
    }

    public static String getTranslationKey ( String prefix , String key ) {
        return prefix + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }

}
