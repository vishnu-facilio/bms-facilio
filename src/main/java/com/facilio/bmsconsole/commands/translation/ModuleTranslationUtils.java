package com.facilio.bmsconsole.commands.translation;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.lang.i18n.translation.TranslationBean;
import com.facilio.lang.i18n.translation.TranslationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Log4j
public class ModuleTranslationUtils {

    private static final String PREFIX_MODULE = "module";
    private static final String SUFFIX = "displayName";
    private static final String PREFIX_FIELD ="field";

    public static String getTranslationKey(long moduleId){
        return PREFIX_MODULE+"."+moduleId+"."+SUFFIX;
    }
    public static String getFieldTranslationKey ( long fieldId ) {
        return PREFIX_FIELD+"."+fieldId+"."+SUFFIX;
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
                moduleProps.put(TranslationConstants.VALUE,translationFile.getProperty(getTranslationKey(module.getModuleId()),""));
                JSONArray array = new JSONArray();
                fields.forEach(field -> {
                    JSONObject fieldProps = new JSONObject();
                    fieldProps.put(TranslationConstants.LABEL,field.getDisplayName());
                    fieldProps.put(TranslationConstants.PREFIX,PREFIX_FIELD);
                    fieldProps.put(TranslationConstants.SUFFIX,SUFFIX);
                    fieldProps.put(TranslationConstants.VALUE,translationFile.getProperty(getFieldTranslationKey(field.getFieldId()),""));
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
