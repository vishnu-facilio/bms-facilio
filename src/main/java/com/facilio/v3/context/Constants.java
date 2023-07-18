package com.facilio.v3.context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bundle.bean.BundleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.commands.SaveOptions;
import com.facilio.workflowv2.bean.ScriptBean;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

public class Constants {
    public static final String SITE_LIST = "siteIds";
    public static final String FILTERS = "filters";
    public static final String QUICK_FILTER= "quickFilter";
    public static final String RECORD_MAP = "recordMap";
    public static final String EXCLUDE_PARENT_CRITERIA = "excludeParentCriteria";
    public static final String FILTER_CRITERIA = "filterCriteria";
    public static final String JOINS = "joins";
    public static final String BEFORE_FETCH_CRITERIA = "beforeFetchCriteria";
    public static final String WITH_COUNT = "withCount";
    public static final String WITHOUT_CUSTOMBUTTONS = "withoutCustomButtons";
    public static final String BEAN_CLASS = "beanClass";
    public static final String FROM_SCRIPT = "fromScript";
    public static final String ROWS_UPDATED = "rowsUpdated";
    public static final String PATCH_FIELDS = "patchFields";
    public static final String QUERY_PARAMS = "queryParams";
    public static final String STATE_FLOWS = "stateFlows";
    public static final String CUSTOM_BUTTONS = "customButtons";
    public static final String CUSTOM_BUTTONS_RECORDS = "customButtonsforRecords";
    public static final String SYSTEM_BUTTONS = "systemButtons";
    public static final String FETCH_CLASSIFICATION = "fetchClassification";
    public static final String FOR_EXPORT = "forExport";

    public static final String MODULE_VS_DELETEDIDS = "moduleVsDeletedIds";

    public static final String FILTER_BY = "filter_by";
    public static String getFilterBy(Context context) {
        return (String) context.get(FILTER_BY);
    }

    public static void setFilterBy(Context context, String filterBy) {
        context.put(FILTER_BY, filterBy);
    }


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

    private static final String BULK_RAW_INPUT = "bulkRawInput";
    public static Collection<Map<String, Object>> getBulkRawInput(Context context) {
        return (Collection<Map<String, Object>>) context.get(BULK_RAW_INPUT);
    }

    public static void setBulkRawInput(Context context, Collection<Map<String, Object>> jsonObjects) {
        context.put(BULK_RAW_INPUT, jsonObjects);
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

    public static Map<String, List<Object>> getQueryParems(Context context) {
        return (Map<String, List<Object>>) context.get(QUERY_PARAMS);
    }

    private static final String OLD_RECORD_MAP = "oldRecordMap";
    public static <T extends ModuleBaseWithCustomFields> void addToOldRecordMap (Context context, String moduleName, T record) {
        addToOldRecordMap(context, moduleName, Collections.singletonList(record));
    }

    public static <T extends ModuleBaseWithCustomFields> void addToOldRecordMap (Context context, String moduleName, List<T> records) {
        if (CollectionUtils.isEmpty(records)) {
            return;
        }

        Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>> oldRecordsMap = (Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>>) context.computeIfAbsent(OLD_RECORD_MAP, k -> new HashMap<>());
        Map<Long, T> oldRecords = (Map<Long, T>) oldRecordsMap.computeIfAbsent(moduleName, k -> new HashMap<>());
        for (T record : records) {
            if (record == null) {
                continue;
            }
            oldRecords.put(record.getId(), record);
        }
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getOldRecordMap (Context context) {
        String moduleName = Constants.getModuleName(context);
        return getOldRecordMap(context, moduleName);
    }

    public static <T extends ModuleBaseWithCustomFields> Map<Long, T> getOldRecordMap (Context context, String moduleName) {
        Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>> oldRecordsMap = (Map<String, Map<Long, ? extends ModuleBaseWithCustomFields>>) context.get(OLD_RECORD_MAP);
        return oldRecordsMap == null ? null : (Map<Long, T>) oldRecordsMap.get(moduleName);
    }
    public static <T extends ModuleBaseWithCustomFields> T getOldRecord (Context context, String moduleName, long id) {
        Map<Long, T> oldRecords = getOldRecordMap(context, moduleName);
        return oldRecords == null ? null : oldRecords.get(id);
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

    public static <T extends ModuleBaseWithCustomFields> void addRecordList (Map<String, List<ModuleBaseWithCustomFields>> recordMap, String moduleName, List<T> recordList) {
        recordMap.put(moduleName, (List<ModuleBaseWithCustomFields>) recordList);
    }

    public static <E extends ModuleBaseWithCustomFields> List<E> getRecordListFromMap(Map<String, List<ModuleBaseWithCustomFields>> recordMap, String moduleName) {
        return (List<E>) recordMap.get(moduleName);
    }

    public static <E extends ModuleBaseWithCustomFields> List<E> getRecordListFromContext(FacilioContext context, String moduleName) {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        return recordMap == null ? null : getRecordListFromMap(recordMap, moduleName);
    }

    public static <E extends ModuleBaseWithCustomFields> List<E> getRecordList (FacilioContext context) {
        String moduleName = Constants.getModuleName(context);
        return getRecordListFromContext(context, moduleName);
    }

    public static <E extends ModuleBaseWithCustomFields> List<E> getRecordsFromContext (FacilioContext context) {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = getRecordMap(context);
        List<ModuleBaseWithCustomFields> records = null;
        if (recordMap == null) {
            records = (List<ModuleBaseWithCustomFields>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            if (CollectionUtils.isEmpty(records)) {
                ModuleBaseWithCustomFields record = (ModuleBaseWithCustomFields) context.get(FacilioConstants.ContextNames.RECORD);
                if (record != null) {
                    records = Collections.singletonList(record);
                } else return null;
            }
        } else {
            records = recordMap.get(moduleName);
        }
        return (List<E>) records;
    }

    public static <E extends ModuleBaseWithCustomFields> void setRecordList (FacilioContext context, List<E> records) {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        if (recordMap == null) {
            recordMap = new HashMap<>();
            setRecordMap(context, recordMap);
        }
        addRecordList(recordMap, moduleName, records);
    }

    public static ModuleBaseWithCustomFields getRecord(FacilioContext context, long id) {
        return getRecord(context, getModuleName(context), id);
    }

    public static ModuleBaseWithCustomFields getRecord(FacilioContext context, String moduleName, long id) {
        List<ModuleBaseWithCustomFields> records = getRecordMap(context).get(moduleName);
        if (CollectionUtils.isNotEmpty(records)) {
            Optional<ModuleBaseWithCustomFields> first = records.stream().filter(record -> record.getId() == id).findFirst();
            if (first.isPresent()) {
                return first.get();
            }
        }
        return null;
    }

    private static final String EXTENDED_MODULES = "extendedModules";
    public static Set<String> getExtendedModules (Context context) {
        return (Set<String>) context.get(EXTENDED_MODULES);
    }
    public static void setExtendedModules (Context context, Set<String> extendedModules) {
        context.put(EXTENDED_MODULES, extendedModules);
    }

    private static final String EXTENDED_MODULE_SAVE_OPTIONS = "extendedModuleSaveOptions";
    public static Map<String, SaveOptions> getExtendedSaveOptions(Context context) {
        return (Map<String, SaveOptions>) context.get(EXTENDED_MODULE_SAVE_OPTIONS);
    }
    public static SaveOptions getExtendedSaveOption(Context context, String moduleName) {
        Map<String, SaveOptions> saveOptionsMap = getExtendedSaveOptions(context);
        return saveOptionsMap == null ? null : saveOptionsMap.get(moduleName);
    }
    public static void addExtendedSaveOption (Context context, String moduleName, SaveOptions options) {
        Map<String, SaveOptions> saveOptionsMap = (Map<String, SaveOptions>) context.computeIfAbsent(EXTENDED_MODULE_SAVE_OPTIONS, (k) -> new HashMap<>());
        saveOptionsMap.put(moduleName, options);
    }


    public static Map getQueryParams(Context context) {
        return (Map) context.get(QUERY_PARAMS);
    }

    public static List<Object> getQueryParamList (Context context, String paramName) {
        Map<String, List<Object>> bodyParams = getQueryParams(context);
        return MapUtils.isEmpty(bodyParams) ? null : bodyParams.get(paramName);
    }

    public static Object getQueryParam (Context context, String paramName) {
        List<Object> values = getQueryParamList(context, paramName);
        return CollectionUtils.isEmpty(values) ? null : values.get(0);
    }

    public static boolean containsQueryParam(Context context, String paramName) {
        return getQueryParam(context, paramName) != null;
    }

    public static Object getQueryParamOrThrow(Context context, String paramName) {
        Object paramValue = getQueryParam(context, paramName);
        if (paramValue == null) {
            throw new IllegalArgumentException(paramName + " is required");
        }
        return paramValue;
    }

    @Deprecated
    public static final String RECORD_ID = "recordId";
    @Deprecated
    public static long getRecordId(Context context) {
        return (long) context.get(RECORD_ID);
    }
    @Deprecated
    public static void setRecordId(Context context, long recordId) {
        context.put(RECORD_ID, recordId);
    }

    public static final String RECORD_ID_LIST = FacilioConstants.ContextNames.RECORD_ID_LIST;
    public static List<Long> getRecordIds(Context context) {
        return (List<Long>) context.get(RECORD_ID_LIST);
    }

    public static void setRecordIds(Context context, List<Long> recordIds) {
        context.put(RECORD_ID_LIST, recordIds);
    }

    public static void setRowsUpdated(Context context, int no) {
        context.put(ROWS_UPDATED, no);
    }

    public static Integer getRowsUpdated(Context context) {
        return (Integer) context.get(ROWS_UPDATED);
    }

    public static final String COUNT_MAP = "countMap";
    public static Map<String, Integer> getCountMap(Context context) {
        return (Map<String, Integer>) context.get(COUNT_MAP);
    }

    public static void setCountMap(Context context, Map<String, Integer> countMap) {
        context.put(COUNT_MAP, countMap);
    }

    private static final String CHANGE_SET = FacilioConstants.ContextNames.CHANGE_SET;

    public static Map<Long, List<UpdateChangeSet>> getChangeSet(Context context) {
        return (Map<Long, List<UpdateChangeSet>>) context.get(CHANGE_SET);
    }

    public static void setChangeSet(Context context, Map<Long, List<UpdateChangeSet>> changeSet) {
        context.put(CHANGE_SET, changeSet);
    }

    private static final String EVENT_TYPE = FacilioConstants.ContextNames.EVENT_TYPE;

    @Deprecated
    /**
     * use {@link CommonCommandUtil#getEventTypes(Context)}
     */
    public static EventType getEventType(Context context) {
        return (EventType) context.get(EVENT_TYPE);
    }

    @Deprecated
    /**
     * use {@link CommonCommandUtil#addEventType(EventType, FacilioContext)}
     */
    public static void setEventType(Context context, EventType eventType) {
        context.put(EVENT_TYPE, eventType);
    }

    public static List<UpdateChangeSet> getRecordChangeSets(Context context, Long recordId) {
        Map<Long,List<UpdateChangeSet>> moduleChangeSet = getModuleChangeSets(context);
        if(MapUtils.isNotEmpty(moduleChangeSet) && moduleChangeSet.containsKey(recordId)){
            return moduleChangeSet.get(recordId);
        }
        return new ArrayList<>();
    }

    private static final String MODULE_MAP = FacilioConstants.ContextNames.MODULE_MAP;

    public static void setModuleMap(Context context, Map<Long, List<FacilioModule>> moduleMap) {
        context.put(MODULE_MAP, moduleMap);
    }

    public static Map<Long, List<FacilioModule>> getModuleMap(Context context) {
        return (Map<Long, List<FacilioModule>>) context.get(MODULE_MAP);
    }

    public static Map<Long, List<UpdateChangeSet>> getModuleChangeSets(Context context) {
        String moduleName = Constants.getModuleName(context);
        Map<String, Map<Long,List<UpdateChangeSet>>> allChangeSet = CommonCommandUtil.getChangeSetMap((FacilioContext)context);
        if(MapUtils.isNotEmpty(allChangeSet) && allChangeSet.containsKey(moduleName)) {
            return allChangeSet.get(moduleName);
        }
        return new HashMap<>();
    }

    private static final String DELETE_RECORD_ID_MAP = "deleteRecordIdMap";
    public static Map<String, List<Long>> getDeleteRecordIdMap(Context context) {
        return (Map<String, List<Long>>) context.get(DELETE_RECORD_ID_MAP);
    }
    public static void setDeleteRecordIdMap(Context context, Map<String, List<Long>> deleteRecordIdMap) {
        context.put(DELETE_RECORD_ID_MAP, deleteRecordIdMap);
    }

    private static final String DELETED_RECORDS = "deletedRecords";
    public static <T extends ModuleBaseWithCustomFields> List<T> getDeletedRecords(Context context) {
        return (List<T>) context.get(DELETED_RECORDS);
    }
    public static <T extends ModuleBaseWithCustomFields> void setDeletedRecords(Context context, List<T> deletedRecords) {
        context.put(DELETED_RECORDS, deletedRecords);
    }

    private static final String V3CONFIG = "v3Config";
    public static V3Config getV3Config(Context context) {
        return (V3Config) context.get(V3CONFIG);
    }

    public static void setV3config(Context context, V3Config v3Config) {
        context.put(V3CONFIG, v3Config);
    }

    private static final String ONLY_COUNT = "onlyCount";
    public static boolean getOnlyCount(Context context) {
        if (context.get(ONLY_COUNT) == null) {
            return false;
        }
        return (Boolean) context.get(ONLY_COUNT);
    }

    public static void setOnlyCount(Context context, boolean onlyCount) {
        context.put(ONLY_COUNT, onlyCount);
    }

    public static final String COUNT = "count";
    public static Long getCount(Context context) {
        return (Long) context.get(COUNT);
    }

    public static void setCount(Context context, long count) {
        context.put(COUNT, count);
    }

    private static final String LIST_RELATION_CRITERIA = "listRelationCriteria";
    public static Map<String, List<String>> getListRelationCriteria(Context context) {
        return (Map<String, List<String>>) context.get(LIST_RELATION_CRITERIA);
    }

    public static void setListRelationCriteria(Context context, Map<String, List<String>> lookupFieldCriteriaMap) {
        context.put(LIST_RELATION_CRITERIA, lookupFieldCriteriaMap);
    }

    private static final String LIST_RELATION_RECORDS = "listRelationRecords";
    public static Map<String, Map<String, Object>> getListRelationRecords(Context context) {
        return (Map<String, Map<String, Object>>) context.get(LIST_RELATION_RECORDS);
    }

    public static void setListRelationRecords(Context context, Map<String, Map<String, Object>> relationRecords) {
        context.put(LIST_RELATION_RECORDS, relationRecords);
    }

    public static void setFilterCriteria(Context context, Criteria criteria) {
        context.put(FILTER_CRITERIA, criteria);
    }

    public static Criteria getFilterCriteria(Context context) {
        return (Criteria) context.get(FILTER_CRITERIA);
    }

    public static ModuleBean getModBean() throws Exception {
        return (ModuleBean) BeanFactory.lookup("ModuleBean");
    }
    public static ScriptBean getScriptBean() throws Exception {
        return (ScriptBean) BeanFactory.lookup("ScriptBean");
    }

    public static NamespaceBean getNsBean() throws Exception {
        return (NamespaceBean) BeanFactory.lookup("NamespaceBean");
    }

    public static BundleBean getBundleBean(long orgid) throws Exception {
        return (BundleBean) BeanFactory.lookup("BundleBean",orgid);
    }

    private static final String IS_V4 = "isV4";
    public static void setIsV4(Context context, boolean isV4) {
        context.put(IS_V4, isV4);
    }

    public static boolean isV4(Context context) {
        Boolean isV4 = (Boolean) context.get(IS_V4);
        return isV4 != null && isV4;
    }

    private static final String RESULT = "result";
    public static void setResult(Context context, Object result) {
        context.put(RESULT, result);
    }

    public static Object getResult(Context context) {
        return context.get(RESULT);
    }

    private static final String __TAMPERING = "__tampering";
    public static Map<String, Boolean> getTamperingContext(Context context) {
        return (Map<String, Boolean>) context.getOrDefault(__TAMPERING, new HashMap<String, Boolean>());
    }

    public static void setTamperingContext(Context context, Map<String, Boolean> tamperingContext) {
        context.put(__TAMPERING, tamperingContext);
    }

    private static  final String HAS_MORE_RECORDS = "hasMoreRecords";
    public static boolean hasMoreRecords(Context context) {
        Boolean hasMoreRecords = (Boolean) context.get(HAS_MORE_RECORDS);
        return hasMoreRecords != null && hasMoreRecords;
    }

    public static void setHasMoreRecords(Context context, boolean hasMoreRecords) {
        context.put(HAS_MORE_RECORDS, hasMoreRecords);
    }

    @Deprecated
    private static final String RECORD = FacilioConstants.ContextNames.RECORD;
    @Deprecated
    public static ModuleBaseWithCustomFields getRecord(Context context) {
        return (ModuleBaseWithCustomFields) context.get(RECORD);
    }
    @Deprecated
    public static void setRecord(Context context, ModuleBaseWithCustomFields record) {
        context.put(RECORD, record);
    }

    public static void setActivityContext(Context context, String activityContext) {
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, activityContext);
    }
    public static String getActivityContext(Context context) {
        return (String) context.get(FacilioConstants.ContextNames.CURRENT_ACTIVITY);
    }
}
