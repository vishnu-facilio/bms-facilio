package com.facilio.bmsconsole.localization.translation;

import com.facilio.bmsconsole.localization.util.TranslationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Properties;

@Log4j
public class ModuleTranslationUtils {

    public static final String PREFIX_MODULE = "module";
    private static final String SUFFIX = "displayName";
    public static final String PREFIX_FIELD ="field";
    public static final String PREFIX_FIELD_OPTIONS= "fieldOption";

    public static String getTranslationKey(String linkName){
        return PREFIX_MODULE+"."+linkName.trim()+"."+SUFFIX;
    }
    public static String getFieldTranslationKey ( String linkName ) {
        return PREFIX_FIELD+"."+linkName.trim()+"."+SUFFIX;
    }

    public static String getFieldOptionsTranslationKey ( String linkName ) {
        return PREFIX_FIELD_OPTIONS+"."+linkName.trim()+"."+SUFFIX;
    }

    static JSONObject constructJSONObject ( List<FacilioField> fields,FacilioModule module,Properties translationFile ) {
        JSONObject prop = new JSONObject();
        if(translationFile != null) {
            try  {
                translationFile.forEach(( k,v ) -> translationFile.put(k.toString().trim(),v.toString().trim()));
                JSONObject moduleProps = new JSONObject();
                moduleProps.put(TranslationConstants.LABEL,module.getDisplayName());
                moduleProps.put(TranslationConstants.PREFIX,PREFIX_MODULE);
                moduleProps.put(TranslationConstants.SUFFIX,SUFFIX);
                moduleProps.put(TranslationConstants.VALUE,translationFile.getProperty(getTranslationKey(module.getName()),""));
                JSONArray array = new JSONArray();
                fields.forEach(field -> {
                    JSONObject fieldProps = new JSONObject();
                    fieldProps.put(TranslationConstants.LABEL,field.getDisplayName());
                    fieldProps.put(TranslationConstants.PREFIX,PREFIX_FIELD);
                    fieldProps.put(TranslationConstants.SUFFIX,SUFFIX);
                    fieldProps.put(TranslationConstants.VALUE,translationFile.getProperty(getFieldTranslationKey(field.getName()),""));
                    array.add(fieldProps);
                });
                prop.put("module",moduleProps);
                prop.put("fields",array);
                System.out.println(prop);
                return prop;
            } catch (Exception e) {
                LOGGER.error("Exception occurred while fetching fileId in Translation",e);
            }
        } else {
            throw new RuntimeException("File is not available");
        }
        return prop;
    }
}
