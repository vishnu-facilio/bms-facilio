package com.facilio.bmsconsole.commands.translation;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.lang.i18n.translation.TranslationConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

@Log4j
public class TranslationUtils {

    static JSONObject constructJSONObject ( List<FacilioField> fields,FacilioModule module,String langCode ) throws Exception {
        long fileId = getFileId(langCode);
        FileStore fs = FacilioFactory.getFileStore();
        String name = fs.getFileInfo(fileId).getFilePath();
        JSONObject prop = new JSONObject();
        Properties properties = new Properties();
        if(fileId > -1L) {
            try (InputStream stream = new FileInputStream(name)) {
                properties.load(stream);
                properties.forEach(( k,v ) -> properties.put(k.toString().trim(),v.toString().trim()));
                JSONObject moduleProps = new JSONObject();
                moduleProps.put("label",module.getDisplayName());
                String moduleKey = module.getModuleId()+module.getDisplayName();
                moduleProps.put("key",moduleKey);
                moduleProps.put("value",properties.getProperty(moduleKey,""));
                JSONArray array = new JSONArray();
                fields.forEach(field -> {
                    JSONObject fieldProps = new JSONObject();
                    fieldProps.put("label",field.getDisplayName());
                    String fieldKey = field.getFieldId() + field.getDisplayName();
                    fieldProps.put("key",fieldKey);
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
}
