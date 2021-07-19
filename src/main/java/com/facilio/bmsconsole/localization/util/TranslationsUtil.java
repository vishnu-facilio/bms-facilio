package com.facilio.bmsconsole.localization.util;

import com.facilio.bmsconsole.localization.translation.TranslationConfFile;
import com.facilio.collections.UniqueMap;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.service.FacilioServiceUtil;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j
public class TranslationsUtil {

    private static final String TRANSLATION_VALIDATION_FILE = "conf/translationvalidation.yml";
    private static final Map<String, TranslationConfFile> TRANSLATION_CONF_FILE_MAP = Collections.unmodifiableMap(initTranslationConfFile());

    @SneakyThrows
    private static Map<String, TranslationConfFile> initTranslationConfFile ()  {
        try {
            Map<String, Object> confFile = loadYaml();
            List<Map<String, Object>> list = (List<Map<String, Object>>)confFile.get("translation");
            Map<String, TranslationConfFile> translationsFile = new UniqueMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> translation : list) {
                    String prefix = (String) translation.get(TranslationConstants.PREFIX);
                    FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(prefix), "Prefix cannot be empty for translation config file");
                    List<String> suffix = (List<String>)translation.get(TranslationConstants.SUFFIX);
                    FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(suffix), "Suffix cannot be empty for translation config file");
                    TranslationConfFile confInstance = new TranslationConfFile();
                    confInstance.setPrefix(prefix);
                    confInstance.setSuffix(suffix);
                    translationsFile.put(prefix, confInstance);
                }
            }
            return translationsFile;
        }catch (Exception e){
            LOGGER.error("Exception occurrend while loading translation conf file",e);
            throw e;
        }
    }

    private static Map<String, Object> loadYaml () throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = FacilioServiceUtil.class.getClassLoader().getResourceAsStream(TRANSLATION_VALIDATION_FILE)) {
            return yaml.load(inputStream);
        }catch (Exception e){
            throw e;
        }
    }

    public static long getTranslationFileId ( String langCode ) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("LANG_CODE",TranslationConstants.LANG_CODE,langCode,StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status","1",StringOperators.IS));
        Map<String,Object> props = select.fetchFirst();
        long fileId = -1L;
        if(props !=null && !props.isEmpty()){
            fileId = (long)props.get(TranslationConstants.FILE_ID);
        }
        return fileId;
    }

    public static boolean prefixSuffixValidation ( String prefix,String suffix ) {
        return TRANSLATION_CONF_FILE_MAP.get(prefix).getSuffix().contains(suffix);
    }
}
