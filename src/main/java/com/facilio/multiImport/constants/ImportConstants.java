package com.facilio.multiImport.constants;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ImportConstants{
    public static final String INSERT_RECORDS = "insertRecords";
    public static final String INSERT_RECORD_MAP = "insertRecordMap";
    public static final String UPDATE_RECORDS = "updateRecords";
    public static final String UPDATE_RECORD_MAP = "updateRecordMap";
    public static final String OLD_RECORDS_MAP = "oldRecordsMap";
    public static final String LOGID_VS_ROW_CONTEXT_MAP = "logIdVsRowContextMap";
    public static final String ROW_CONTEXT_LIST = "rowContextList";
    public static  final String IMPORT_SHEET = "importSheet";
    public static final String INSERT_RECORDS_COUNT = "insertRecordsCount";
    public static final String UPDATE_RECORDS_COUNT = "updateRecordsCount";
    public static final String SKIP_RECORDS_COUNT = "skipRecordsCount";
    public static final String IMPORT_FIELDS="importFields";

    public static Collection<Pair<Long,Map<String,Object>>> getInsertRecords(Context context) {
        return (Collection<Pair<Long,Map<String,Object>>>) context.get(INSERT_RECORDS);
    }

    public static void setInsertRecords(Context context, Collection<Pair<Long,Map<String,Object>>> jsonObjects) {
        context.put(INSERT_RECORDS, jsonObjects);
    }
    public static void setInsertRecordMap(Context context, Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> recordMap) {
        context.put(INSERT_RECORD_MAP, recordMap);
    }
    public static Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> getInsertRecordMap(Context context){
        return (Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>>)context.get(INSERT_RECORD_MAP);
    }
    public static void setUpdateRecords(Context context, Collection<Pair<Long,Map<String,Object>>> updateRecordsPair) {
        context.put(UPDATE_RECORDS, updateRecordsPair);
    }
    public static void setUpdateRecordMap(Context context,Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> updateRecordMap) {
        context.put(UPDATE_RECORD_MAP, updateRecordMap);
    }
    public static Collection<Pair<Long,Map<String,Object>>> getUpdateRecords(Context context) {
        return (Collection<Pair<Long,Map<String,Object>>>) context.get(UPDATE_RECORDS);
    }
    public static Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>> getUpdateRecordMap(Context context){
        return (Map<String, List<Pair<Long,ModuleBaseWithCustomFields>>>)context.get(UPDATE_RECORD_MAP);
    }
    public static void setOldRecordsMap(Context context, Map<Long, V3Context> oldRecordsMap) {
        context.put(OLD_RECORDS_MAP, oldRecordsMap);
    }
    public static Map<Long, V3Context> getOldRecordsMap(Context context) {
       return  (Map<Long, V3Context>)context.get(OLD_RECORDS_MAP);
    }
    public static void setLogIdVsRowContextMap(Context context,Map<Long, ImportRowContext> logIdVsRowContext){
        context.put(LOGID_VS_ROW_CONTEXT_MAP,logIdVsRowContext);
    }
    public static Map<Long, ImportRowContext> getLogIdVsRowContextMap(Context context){
        return (Map<Long, ImportRowContext>)context.get(LOGID_VS_ROW_CONTEXT_MAP);
    }
    public static void setRowContextList(Context context,List<ImportRowContext> allRows){
        context.put(ROW_CONTEXT_LIST,allRows);
    }
    public static List<ImportRowContext> getRowContextList(Context context){
        return (List<ImportRowContext>) context.get(ROW_CONTEXT_LIST);
    }
    public static ImportFileSheetsContext getImportSheet(Context context){
        return (ImportFileSheetsContext)  context.get(IMPORT_SHEET);
    }
    public static void setImportSheet(Context context,ImportFileSheetsContext importSheet){
        context.put(IMPORT_SHEET,importSheet);
    }
    public static ImportDataDetails getImportDataDetails(Context context){
        return (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
    }
    public static void setImportDataDetails(Context context,ImportDataDetails importDataDetails){
        context.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS,importDataDetails);
    }
    public static void setImportFields(Context context, List<FacilioField> fields){
        context.put(IMPORT_FIELDS,fields);
    }
    public static List<FacilioField> getImportFields(Context context){
        return (List<FacilioField>) context.get(IMPORT_FIELDS);
    }
}
