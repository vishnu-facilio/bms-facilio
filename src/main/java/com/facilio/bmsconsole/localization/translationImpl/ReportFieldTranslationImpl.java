package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.translation.ModuleTranslationUtils;
import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ReportFieldTranslationImpl implements TranslationIfc {
    @Override
    public JSONObject translate ( @NonNull JSONObject jsonObject,@NonNull Properties properties ) throws Exception {

        JSONObject result = (JSONObject)jsonObject.get("result");
        if(result != null && !result.isEmpty()) {
            JSONObject meta = (JSONObject)result.get("meta");
            if(meta != null && !meta.isEmpty()) {
                JSONObject dimension = (JSONObject)meta.get("dimension");
                if(dimension != null && !dimension.isEmpty()) {
                    for (Object dimensionKey : dimension.keySet()) {
                        JSONArray jsonArray = (JSONArray)dimension.get(dimensionKey);
                        updateFieldTranslation(properties,jsonArray);
                    }
                }
                JSONArray metrics = (JSONArray)meta.get("metrics");
                updateFieldTranslation(properties,metrics);
            }
        }

        return jsonObject;
    }

    private void updateFieldTranslation ( @NonNull Properties properties,JSONArray jsonArray ) {
        if(jsonArray != null && !jsonArray.isEmpty()) {
            for (int j = 0; j < jsonArray.size(); j++) {
                JSONObject fieldObject = (JSONObject)jsonArray.get(j);
                String fieldKey = ModuleTranslationUtils.getFieldTranslationKey((String)fieldObject.get("name"));
                fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(properties,fieldKey,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));
            }
        }
    }
}
