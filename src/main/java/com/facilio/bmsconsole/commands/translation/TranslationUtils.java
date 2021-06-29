package com.facilio.bmsconsole.commands.translation;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
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
public class TranslationUtils {
    private static final String TRANSLATION_VALIDATION_FILE = "conf/translationvalidation.yml";
    static JSONObject constructJSONObject ( List<FacilioField> fields,FacilioModule module,InputStream stream ) {
        JSONObject prop = new JSONObject();
        Properties properties = new Properties();
        if(stream != null) {
            try  {
                properties.load(stream);
                properties.forEach(( k,v ) -> properties.put(k.toString().trim(),v.toString().trim()));
                JSONObject moduleProps = new JSONObject();
                moduleProps.put("label",module.getDisplayName());
                moduleProps.put("prefix","module");
                moduleProps.put("suffix","displayName");
                moduleProps.put("key",module.getModuleId());
                String moduleKey = "module"+module.getModuleId()+"displayName";
                moduleProps.put("value",properties.getProperty(moduleKey,""));
                JSONArray array = new JSONArray();
                fields.forEach(field -> {
                    JSONObject fieldProps = new JSONObject();
                    fieldProps.put("label",field.getDisplayName());
                    fieldProps.put("prefix","field");
                    fieldProps.put("suffix","displayName");
                    String fieldKey = "field"+field.getFieldId()+"displayName";
                    fieldProps.put("key",field.getFieldId());
                    fieldProps.put("value",properties.getProperty(fieldKey,""));
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

    static long getFileId ( String langCode ) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("LANG_CODE",TranslationConstants.LANG_CODE,langCode,StringOperators.IS));
        return (long)select.fetchFirst().getOrDefault(TranslationConstants.FILE_ID,-1L);
    }

    static boolean validatePrefixSuffix ( String prefix,String suffix ) throws IOException {
//        Map<String, Object> namespaceConf = FacilioUtil.loadYaml(TRANSLATION_VALIDATION_FILE);
//        List<Map<String, Object>> validation = (List<Map<String, Object>>) namespaceConf.get("validation");
//        for (Map<String,Object> prop : validation){
//
//        }

        return (prefix.equals("module") && suffix.equals("displayName")) ? true : false;
    }
}
