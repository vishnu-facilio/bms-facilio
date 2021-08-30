package com.facilio.bmsconsole.localization.translationImpl;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.translation.TranslationIfc;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Properties;

public class FormFieldTranslationImpl implements TranslationIfc {
    public static final String FORM_FIELD = "formField";
    public static final String FIELD_OPTION = "fieldOption";

    public static String getTranslationKey ( String value ) {
        return FORM_FIELD + "." + value + "." + TranslationConstants.DISPLAY_NAME;
    }

    public static String getFormFieldOptionsTranslationKey ( String value ) {
        return FIELD_OPTION + "." + value + "." + TranslationConstants.DISPLAY_NAME;
    }

    @Override
    public JSONObject translate ( JSONObject json,Properties translationFile ) throws Exception {
        JSONObject jsonObject = (JSONObject)json.get("result");
        JSONObject form = (JSONObject)jsonObject.get("form");
        JSONArray sections = (JSONArray)form.get("sections");
        if(sections == null || sections.isEmpty()) {
            return json;
        }

        String formKey = FormTranslationImpl.getTranslationKey((String)form.get(TranslationConstants.NAME));
        form.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,formKey,(String)form.get(TranslationConstants.DISPLAY_NAME)));

        for (int i = 0; i < sections.size(); i++) {
            JSONObject sectionObject = (JSONObject)sections.get(i);
            JSONArray fields = (JSONArray)sectionObject.get("fields");
            for (int j = 0; j < fields.size(); j++) {
                JSONObject fieldObject = (JSONObject)fields.get(j);
                String key = getTranslationKey((String)fieldObject.get(TranslationConstants.NAME));
                fieldObject.put(TranslationConstants.DISPLAY_NAME,getTranslation(translationFile,key,(String)fieldObject.get(TranslationConstants.DISPLAY_NAME)));

                JSONObject optionFields = (JSONObject)fieldObject.get("field");
                if(optionFields != null) {
                    if(optionFields.get("dataTypeEnum").equals("ENUM")) {
                        JSONArray values = (JSONArray)optionFields.get("values");

                        if(values != null && !values.isEmpty()) {
                            for (int k = 0; k < values.size(); k++) {
                                JSONObject valueObject = (JSONObject)values.get(k);
                                String fieldOptionKey = getFormFieldOptionsTranslationKey(String.valueOf(valueObject.get("id")));
                                valueObject.put(TranslationConstants.NAME,getTranslation(translationFile,fieldOptionKey,(String)valueObject.get(TranslationConstants.NAME)));
                            }
                        }
                    }
                }
            }
        }
        return json;
    }
}
