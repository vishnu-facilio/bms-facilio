package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.bmsconsole.localization.util.TranslationsUtil;
import com.facilio.translation.TranslationIfc;
import com.opensymphony.xwork2.ActionContext;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.HttpParameters;
import org.apache.struts2.dispatcher.Parameter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Objects;
import java.util.Properties;

public class V2ListApiTranslationIml implements TranslationIfc {
    @Override
    public JSONObject translate(@NonNull JSONObject json, @NonNull Properties translationFile) throws Exception {

        HttpParameters params = Objects.requireNonNull(ActionContext.getContext(), "Action context cannot be null here").getParameters();
        Parameter param = params == null ? null : params.get("moduleName");

        if (param != null && StringUtils.isNotEmpty(param.getValue()) && TranslationsUtil.SPECIAL_HANDLING_MODULES.contains(param.getValue())) {
            String moduleName= param.getName();
            JSONObject result = (JSONObject) json.get("result");
            if (result != null && !result.isEmpty()) {
                JSONArray jsonArray = (JSONArray) result.get("moduleDatas");
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    for (int i=0;i< json.size();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        String key = TranslationsUtil.getTranslationKey(moduleName, String.valueOf(jsonObject.get("id")));
                        String displayName = (String) jsonObject.getOrDefault(TranslationConstants.DISPLAY_NAME,null);
                        if (displayName != null){
                            jsonObject.put(TranslationConstants.DISPLAY_NAME, getTranslation(translationFile, key, (String) jsonObject.get(TranslationConstants.DISPLAY_NAME)));
                        }else {
                            jsonObject.put(TranslationConstants.NAME, getTranslation(translationFile, key, (String) jsonObject.get(TranslationConstants.NAME)));
                        }
                    }
                }
            }
        }

        return json;
    }
}
