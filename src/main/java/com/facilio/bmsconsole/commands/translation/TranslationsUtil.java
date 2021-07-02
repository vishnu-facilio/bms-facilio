package com.facilio.bmsconsole.commands.translation;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.lang.i18n.translation.TranslationConstants;
import com.facilio.util.FacilioUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TranslationsUtil {

    private static final String TRANSLATION_VALIDATION_FILE = "conf/translationvalidation.yml";

    public static long getTranslationFileId ( String langCode ) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("LANG_CODE",TranslationConstants.LANG_CODE,langCode,StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status","1",StringOperators.IS));
        return (long)select.fetchFirst().getOrDefault(TranslationConstants.FILE_ID,-1L);
    }

    public static boolean prefixSuffixValidation ( String prefix,String suffix ) throws IOException {
        Map<String, Object> tanslationConf = FacilioUtil.loadYaml(TRANSLATION_VALIDATION_FILE);
        List<Map<String, Object>> validation = (List<Map<String, Object>>)tanslationConf.get("translation");
        boolean case1 = false;
        boolean case2 = false;
        for (Map<String, Object> prop : validation) {
            if(prefix.equals(prop.get("prefix"))) {
                case1 = true;
            } else {
                continue;
            }
            List<String> siffixVal = (List<String>)prop.get("suffix");
            for (String suffix1 : siffixVal) {
                if(suffix.equals(suffix1)) {
                    case2 = true;
                    break;
                }
            }
        }
        return (case1 && case2) ? true : false;
    }
}
