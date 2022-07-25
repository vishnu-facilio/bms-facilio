package com.facilio.bmsconsole.localization.util;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.localization.translation.TranslationConfFile;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.collections.UniqueMap;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.service.FacilioServiceUtil;
import com.facilio.util.FacilioUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;

@Log4j
public class TranslationsUtil {

    private static final String TRANSLATION_VALIDATION_FILE = "conf/translationvalidation.yml";
    private static final Map<String, TranslationConfFile> TRANSLATION_CONF_FILE_MAP = Collections.unmodifiableMap(initTranslationConfFile());
    public static final Map<String,Map<String, String>> COLUMN_VS_TRANSLATION_TYPE = getColumnVsType();

    @SneakyThrows
    private static Map<String, TranslationConfFile> initTranslationConfFile () {
        try {
            Map<String, Object> confFile = loadYaml();
            List<Map<String, Object>> list = (List<Map<String, Object>>)confFile.get("translation");
            Map<String, TranslationConfFile> translationsFile = new UniqueMap<>();
            if(CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> translation : list) {
                    String prefix = (String)translation.get(TranslationConstants.PREFIX);
                    FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(prefix),"Prefix cannot be empty for translation config file");
                    List<String> suffix = (List<String>)translation.get(TranslationConstants.SUFFIX);
                    FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(suffix),"Suffix cannot be empty for translation config file");
                    TranslationConfFile confInstance = new TranslationConfFile();
                    confInstance.setPrefix(prefix);
                    confInstance.setSuffix(suffix);
                    translationsFile.put(prefix,confInstance);
                }
            }
            return translationsFile;
        } catch (Exception e) {
            LOGGER.error("Exception occurrend while loading translation conf file",e);
            throw e;
        }
    }

    private static Map<String, Object> loadYaml () throws IOException {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = FacilioServiceUtil.class.getClassLoader().getResourceAsStream(TRANSLATION_VALIDATION_FILE)) {
            return yaml.load(inputStream);
        } catch (Exception e) {
            throw e;
        }
    }

    public static long getTranslationFileId ( String langCode ) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
                .table(TranslationConstants.getTranslationModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("LANG_CODE",TranslationConstants.LANG_CODE,langCode,StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("STATUS","status","1",StringOperators.IS));
        Map<String, Object> props = select.fetchFirst();
        long fileId = -1L;
        if(props != null && !props.isEmpty()) {
            fileId = (long)props.get(TranslationConstants.FILE_ID);
        }
        return fileId;
    }

    public static boolean prefixSuffixValidation ( String prefix,String suffix ) {
        return TRANSLATION_CONF_FILE_MAP.get(prefix).getSuffix().contains(suffix);
    }

    public static JSONObject constructJSON ( String label,String prefix,String suffix,String key,String value,Properties properties ) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TranslationConstants.LABEL,label);
        jsonObject.put(TranslationConstants.PREFIX,prefix);
        jsonObject.put(TranslationConstants.SUFFIX,suffix);
        jsonObject.put(TranslationConstants.KEY,key);
        jsonObject.put(TranslationConstants.VALUE,properties.getProperty(value,null));
        return jsonObject;
    }

    private static Map<String, Map<String, String>> getColumnVsType () {
        Map<String, Map<String, String>> columnVsType = new HashMap<>();
        Map<String, String> moduleTab = new HashMap<>();
        moduleTab.put("DETAILS","DETAILS");
        moduleTab.put("FIELDS","FIELDS");
        moduleTab.put("FORMS","FORMS");
        moduleTab.put("VIEWS","VIEWS");
        moduleTab.put("VIEW_FOLDER","VIEW FOLDER");
        moduleTab.put("STATES","STATES");
        moduleTab.put("STATE_TRANSITION","STATE TRANSITION");
        moduleTab.put("BUTTONS","BUTTONS");

        columnVsType.put("moduleTab",moduleTab);

        Map<String, String> dashboardTab = new HashMap<>();
        dashboardTab.put("DASHBOARD_FOLDER","DASHBOARD FOLDER");
        dashboardTab.put("DASHBOARD","DASHBOARD & WIDGETS");
        columnVsType.put("dashboardTab",dashboardTab);

        Map<String, String> reportTab = new HashMap<>();
        reportTab.put("REPORT_FOLDER","REPORT FOLDER");
        reportTab.put("REPORT","REPORT");
        columnVsType.put("reportTab",reportTab);

        return columnVsType;
    }

    public static void updateStatus ( String lang,boolean status ) throws SQLException {
        update(lang,status);
    }

    private static void update ( String lang,boolean status ) throws SQLException {
        Map<String, Object> prop = new HashMap<>();
        prop.put("status",status);
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(TranslationConstants.getTranslationModule().getTableName())
                .fields(TranslationConstants.getTranslationFields())
                .andCondition(CriteriaAPI.getCondition("LANG_CODE","langCode",lang,StringOperators.IS));
        builder.update(prop);
    }

    public static String getTranslationKey ( String prefix , String key ) {
        return prefix + "." + key + "." + TranslationConstants.DISPLAY_NAME;
    }


    public static List<String> SPECIAL_HANDLING_MODULES = Collections.unmodifiableList(initModuleMap());

    private static List<String> initModuleMap() {
        return Arrays.asList(
                FacilioConstants.ContextNames.TICKET_CATEGORY,
                FacilioConstants.ContextNames.TICKET_PRIORITY,
                FacilioConstants.ContextNames.TICKET_TYPE,
                FacilioConstants.ContextNames.ASSET_CATEGORY,
                FacilioConstants.ContextNames.ASSET_TYPE,
                FacilioConstants.ContextNames.ASSET_DEPARTMENT);
    }

    public static  Map<String,String> WORKORDER_FIELDS_MAP = Collections.unmodifiableMap(initWorkOrderFieldsMap());
    private static Map<String,String> initWorkOrderFieldsMap() {
        Map<String,String> workOrderMap = new HashMap<>();
        workOrderMap.put("CATEGORY","ticketcategory");
        workOrderMap.put("PRIORITY","ticketpriority");
        workOrderMap.put("TYPE","tickettype");
        return workOrderMap;
    }

    public static  Map<String,String> ASSET_FIELDS_MAP = Collections.unmodifiableMap(initAssetFieldsMap());
    private static Map<String,String> initAssetFieldsMap() {
        Map<String,String> assetFieldsMap = new HashMap<>();
        assetFieldsMap.put("CATEGORY","assetcategory");
        assetFieldsMap.put("DEPARTMENT","assetdepartment");
        assetFieldsMap.put("TYPE","assettype");
        return assetFieldsMap;
    }

	public static Map<String,Object> getTranslation(long id) throws Exception{
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder().select(TranslationConstants.getTranslationFields())
													.table(TranslationConstants.getTranslationModule().getTableName())
													.andCondition(CriteriaAPI.getIdCondition(id,TranslationConstants.getTranslationModule()))
													.andCondition(CriteriaAPI.getCondition("STATUS","status","1",StringOperators.IS));
		return select.fetchFirst();
	}
}
