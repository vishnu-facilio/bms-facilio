package com.facilio.v3.context;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Constants {
    public static final String FILTERS = "filters";
    public static final String RECORD_MAP = "recordMap";
    public static final String EXCLUDE_PARENT_CRITERIA = "excludeParentCriteria";
    public static final String FILTER_CRITERIA = "filterCriteria";
    public static final String BEFORE_FETCH_CRITERIA = "beforeFetchCriteria";
    public static final String MODULE_BEAN = "moduleBean";
    public static final String RECORD_ID = "recordId";
    public static final String WITH_COUNT = "withCount";
    public static final String COUNT = "count";
    public static final String BEAN_CLASS = "beanClass";
    public static final String ROWS_UPDATED = "rowsUpdated";
    public static final String PATCH_FIELDS = "patchFields";
    public static final String QUERY_PARAMS = "queryParams";
    public static final String STATE_FLOWS = "stateFlows";

    private static final String MODULE_NAME = "moduleName";
    public static String getModuleName(Context context) {
        return (String) context.get(MODULE_NAME);
    }

    public static void setModuleName(Context context, String moduleName) {
        context.put(MODULE_NAME, moduleName);
    }

    private static final String FETCH_SUPPLEMENTS = "fetchSupplement";
    public static List<LookupField> getFetchSupplements(Context context) {
       return (List<LookupField>) context.get(FETCH_SUPPLEMENTS);
    }

    public static void setFetchSupplements(Context context, List<LookupField> supplements) {
        context.put(FETCH_SUPPLEMENTS, supplements);
    }

    private static final String RAW_INPUT = "rawInput";
    public static Map<String, Object> getRawInput(Context context) {
        return (Map<String, Object>) context.get(RAW_INPUT);
    }

    public static void setRawInput(Context context, Map<String, Object> jsonObject) {
        context.put(RAW_INPUT, jsonObject);
    }

    private static final String BODY_PARAMS = "bodyParams";
    public static Map<String, Object> getBodyParams(Context context) {
        return (JSONObject) context.get(BODY_PARAMS);
    }

    public static void setBodyParams(Context context, Map<String, Object> jsonObject) {
        context.put(BODY_PARAMS, jsonObject);
    }

    private static final String SUPPLEMENT_MAP = "supplementMap";
    public static void setSupplementMap(Context context, Map<String, Map<String, Object>> supplementMap) {
        context.put(SUPPLEMENT_MAP, supplementMap);
    }

    public static Map<String, Map<String, Object>> getSupplementMap(Context context) {
        return (Map<String, Map<String, Object>>) context.get(SUPPLEMENT_MAP);
    }

    private static final String JSON_RECORD_MAP = "jsonRecordMap";
    public static void setJsonRecordMap(Context context, JSONObject jsonObject) {
        context.put(JSON_RECORD_MAP, jsonObject);
    }

    public static JSONObject getJsonRecordMap(Context context) {
        return (JSONObject) context.get(JSON_RECORD_MAP);
    }

    public static void setAttachmentFileList(Context context, List<File> files) {
        context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST, files);
    }

    public static List<File> getAttachmentFileList(Context context) {
        return (List<File>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_LIST);
    }

    public static void setAttachmentFileNames(Context context, List<String> fileNames) {
        context.put(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME, fileNames);
    }

    public static List<String> getAttachmentFileNames(Context context) {
        return (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_FILE_NAME);
    }

    public static void setAttachmentContentTypes(Context context, List<String> contentTypes) {
        context.put(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE, contentTypes);
    }

    public static List<String> getAttachmentContentTypes(Context context) {
        return (List<String>) context.get(FacilioConstants.ContextNames.ATTACHMENT_CONTENT_TYPE);
    }

    private static final String ATTACHMENT_ID_MAP = "attachmentIdMap";
    public static Map<String, Long> getAttachmentNameVsId(Context context) {
        return (Map<String, Long>) context.get(ATTACHMENT_ID_MAP);
    }

    public static void setAttachmentNameVsId(Context context, Map<String, Long> attachmentVsId) {
        context.put(ATTACHMENT_ID_MAP, attachmentVsId);
    }


    public static Map<String, List<ModuleBaseWithCustomFields>> getRecordMap(Context context) {
        return (Map<String, List<ModuleBaseWithCustomFields>>) context.get(RECORD_MAP);
    }

    public static void setRecordMap(Context context, Map<String, List<ModuleBaseWithCustomFields>> recordMap) {
        context.put(RECORD_MAP, recordMap);
    }

    public static Map getQueryParams(Context context) {
        return (Map) context.get(QUERY_PARAMS);
    }

}
