package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.Properties;

public class StateFlowTranslationImplV3 implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject json,@NonNull Properties translationFile ) throws Exception {

        JSONObject meta = (JSONObject)json.get("meta");
        JSONObject supplements = (JSONObject)meta.get("supplements");
        JSONObject module;
        if (supplements == null) {
            return json;
        } else if (supplements.containsKey("workorder")) {
            module = (JSONObject) supplements.get("workorder");
        } else if (supplements.containsKey("asset")) {
            module = (JSONObject) supplements.get("asset");
        } else {
            return json;
        }
        setDisplayName(translationFile, module);

        return json;
    }

    private void setDisplayName(Properties translationFile, JSONObject module) {

        JSONObject moduleState = (JSONObject)module.get("moduleState");
        if (moduleState != null && !module.isEmpty()){
            for (Object stateKey : moduleState.keySet()){
                JSONObject state = (JSONObject) moduleState.get(stateKey);
                String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE,String.valueOf(state.get("id")));
                state.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)state.get(TranslationConstants.DISPLAY_NAME)));
                state.put("primaryValue",getTranslation(translationFile,key,(String)state.get("primaryValue")));
                state.put("status",getTranslation(translationFile,key,(String)state.get("status")));
            }
        }

        JSONObject categories = (JSONObject) module.get("category");
        if (categories != null && !categories.isEmpty()){
            for (Object itr : categories.keySet()){
                JSONObject category = (JSONObject) categories.get(itr);
                String key = TranslationsUtil.getTranslationKey("category",String.valueOf(category.get("id")));
                category.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)category.get(TranslationConstants.DISPLAY_NAME)));
                category.put("primaryValue",getTranslation(translationFile,key,(String)category.get("primaryValue")));
            }
        }

        JSONObject priorities = (JSONObject) module.get("priority");

        if (priorities != null && !priorities.isEmpty()){
            for (Object itr : priorities.keySet()){
                JSONObject priority = (JSONObject) priorities.get(itr);
                String key = TranslationsUtil.getTranslationKey("priority",String.valueOf(priority.get("id")));
                priority.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)priority.get(TranslationConstants.DISPLAY_NAME)));
                priority.put("primaryValue",getTranslation(translationFile,key,(String)priority.get("primaryValue")));
            }
        }
    }
}
