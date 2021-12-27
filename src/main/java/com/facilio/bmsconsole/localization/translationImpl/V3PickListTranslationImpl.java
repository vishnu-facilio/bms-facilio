package com.facilio.bmsconsole.localization.translationImpl;

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

public class V3PickListTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate(@NonNull JSONObject json, @NonNull Properties translationFile) throws Exception {

        HttpParameters params = Objects.requireNonNull(ActionContext.getContext(), "Action context cannot be null here").getParameters();
        Parameter param = params == null ? null : params.get("moduleName");

        if (param != null && StringUtils.isNotEmpty(param.getValue())) {
            String moduleName = param.getValue();
            JSONObject data = (JSONObject) json.get("data");

            if (data != null && !data.isEmpty()) {
                JSONArray pickList = (JSONArray) data.get("pickList");

                if (pickList != null && !pickList.isEmpty()) {
                    for (int i = 0; i < pickList.size(); i++) {
                        JSONObject jsonObject = (JSONObject) pickList.get(i);

                        if (jsonObject != null && !jsonObject.isEmpty()) {
                            String key = TranslationsUtil.getTranslationKey(moduleName, String.valueOf(jsonObject.get("value")));
                            jsonObject.put("label", getTranslation(translationFile, key, (String) jsonObject.get("label")));
                        }

                    }
                }
            }

        }
        return json;
    }
}
