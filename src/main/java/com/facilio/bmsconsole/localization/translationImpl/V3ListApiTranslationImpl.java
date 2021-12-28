package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONObject;

import java.util.Properties;

public class V3ListApiTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate(@NonNull JSONObject json, @NonNull Properties translationFile) throws Exception {

        JSONObject meta = (JSONObject) json.get("meta");
        if (meta != null && !meta.isEmpty()) {
            JSONObject supplements = (JSONObject) meta.get("supplements");
            if (supplements != null && !supplements.isEmpty()) {
                if (supplements.containsKey("workorder")) {
                    setDisplayName(translationFile, (JSONObject) supplements.get("workorder"), FacilioConstants.ContextNames.TICKET_CATEGORY, FacilioConstants.ContextNames.TICKET_PRIORITY, FacilioConstants.ContextNames.TICKET_TYPE, null);
                } else if (supplements.containsKey("asset")) {
                    setDisplayName(translationFile, (JSONObject) supplements.get("asset"), FacilioConstants.ContextNames.ASSET_CATEGORY, null, FacilioConstants.ContextNames.ASSET_TYPE, FacilioConstants.ContextNames.ASSET_DEPARTMENT);
                }
            }
        }
        return json;
    }

    private void setDisplayName(Properties translationFile, JSONObject module, String category, String priority, String type, String department) {

        JSONObject moduleState = (JSONObject) module.get("moduleState");

        if (moduleState != null && !module.isEmpty()) {
            for (Object stateKey : moduleState.keySet()) {
                JSONObject state = (JSONObject) moduleState.get(stateKey);
                String key = StateFlowTranslationImpl.getTranslationKey(StateFlowTranslationImpl.STATE, String.valueOf(state.get("id")));
                state.put(TranslationConstants.DISPLAY_NAME, getTranslation(translationFile, key, (String) state.get(TranslationConstants.DISPLAY_NAME)));
                state.put("primaryValue", getTranslation(translationFile, key, (String) state.get("primaryValue")));
                state.put("status", getTranslation(translationFile, key, (String) state.get("status")));
            }
        }

        JSONObject categories = (JSONObject) module.get("category");

        if (categories != null && !categories.isEmpty()) {
            for (Object itr : categories.keySet()) {
                JSONObject c = (JSONObject) categories.get(itr);
                String key = TranslationsUtil.getTranslationKey(category, String.valueOf(c.get("id")));
                c.put(TranslationConstants.DISPLAY_NAME, getTranslation(translationFile, key, (String) c.get(TranslationConstants.DISPLAY_NAME)));
                c.put("primaryValue", getTranslation(translationFile, key, (String) c.get("primaryValue")));
            }
        }

        JSONObject priorities = (JSONObject) module.get("priority");

        if (priorities != null && !priorities.isEmpty()) {
            for (Object itr : priorities.keySet()) {
                JSONObject jsonObject = (JSONObject) priorities.get(itr);
                String key = TranslationsUtil.getTranslationKey(priority, String.valueOf(jsonObject.get("id")));
                jsonObject.put(TranslationConstants.DISPLAY_NAME, getTranslation(translationFile, key, (String) jsonObject.get(TranslationConstants.DISPLAY_NAME)));
                jsonObject.put("primaryValue", getTranslation(translationFile, key, (String) jsonObject.get("primaryValue")));
            }
        }
    }
}
