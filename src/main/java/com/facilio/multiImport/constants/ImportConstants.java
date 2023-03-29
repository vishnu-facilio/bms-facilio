package com.facilio.multiImport.constants;

import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ImportConstants{
    public static final String INSERT_RECORDS = "insertRecords";
    public static final String INSERT_RECORD_MAP = "insertRecordMap";
    public static final String LOGID_VS_ROW_CONTEXT_MAP = "logIdVsRowContextMap";
    public static final String ROW_CONTEXT_LIST = "rowContextList";
    public static  final String IMPORT_SHEET = "importSheet";

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
    public static ImportDataDetails getImportDataDetails(Context context){
        return (ImportDataDetails) context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
    }
}
