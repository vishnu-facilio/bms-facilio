package com.facilio.bmsconsole.TranslationImpl;

import com.facilio.bmsconsole.commands.translation.ModuleTranslationUtils;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class ModuleTranslationImpl implements TranslationIfc {
    private static final String MODULE = "module";
    private static final String FIELDS = "fields";
    private static final String DISPLAY_NAME = "displayName";
    private static final String MODULE_ID = "moduleId";
    private static final String FIELD_ID = "fieldId";

    @Override
    public JSONObject translate ( JSONObject json,Properties properties ) throws Exception {

        JSONObject object = (JSONObject)json.get("meta");
        object.keySet().forEach(obj -> {
            if(obj.equals(MODULE)) {
                JSONObject moduleObject = (JSONObject)object.get(MODULE);
                String moduleKey = ModuleTranslationUtils.getTranslationKey((Long)moduleObject.get(MODULE_ID));
                moduleObject.put(DISPLAY_NAME,getTranslation(properties,moduleKey,(String)moduleObject.get(DISPLAY_NAME)));
            }
            else if(obj.equals(FIELDS)) {
                JSONArray fieldJson = (JSONArray)object.get(FIELDS);
                for (int i = 0; i < fieldJson.size(); i++) {
                    JSONObject fieldObject = (JSONObject)fieldJson.get(i);
                    String fieldKey = ModuleTranslationUtils.getFieldTranslationKey((Long)fieldObject.get(FIELD_ID));
                    fieldObject.put(DISPLAY_NAME,getTranslation(properties,fieldKey,(String)fieldObject.get(DISPLAY_NAME)));
                }
            }
        });
        return json;
    }
}
