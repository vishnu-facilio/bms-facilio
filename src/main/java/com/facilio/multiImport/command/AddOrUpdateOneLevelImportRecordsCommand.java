package com.facilio.multiImport.command;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.multiImport.config.ImportConfig;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddOrUpdateOneLevelImportRecordsCommand extends FacilioCommand {
    private Long importId = null;
    private  ImportDataDetails importDataDetails = null;
    private ImportFileSheetsContext importSheet;

    private String parentModuleName;

    private Map<Long, List<ImportFieldMappingContext>> oneLevelMap;
    private Map<ImportFieldMappingType,List<ImportFieldMappingContext>> typeVsFieldMappings;

    private List<ImportRowContext> rowContextList;
    private Map<Long, ImportRowContext> logIdVsImportRows;
    private boolean isUpdateMode;
    public AddOrUpdateOneLevelImportRecordsCommand(boolean isUpdateMode){
        this.isUpdateMode = isUpdateMode;
    }
    @Override
    public boolean executeCommand(Context context) throws Exception {
        init(context);
        loadOneLevelMap();

        if(MapUtils.isEmpty(oneLevelMap) || CollectionUtils.isEmpty(rowContextList)){
            return false;
        }

        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> recordMap = isUpdateMode ?
                ImportConstants.getUpdateRecordMap(context) : ImportConstants.getInsertRecordMap(context);

        List<Pair<Long, ModuleBaseWithCustomFields>> recordList = recordMap.get(parentModuleName);

        if(CollectionUtils.isEmpty(recordList)){
            return false;
        }

        Map<Long, ModuleBaseWithCustomFields> logIdVsParentRecord = recordList.stream()
                .collect(Collectors.toMap(
                        pair -> pair.getKey(),
                        pair -> pair.getValue()
                ));

        for(Map.Entry<Long,List<ImportFieldMappingContext>> entry:oneLevelMap.entrySet()) {
            Long parentLookupFieldId = entry.getKey();
            List<ImportFieldMappingContext> childFieldMappings = entry.getValue();

            BaseLookupField parentLookupField = (BaseLookupField) Constants.getModBean().getField(parentLookupFieldId);
            String lookupModuleName = parentLookupField.getLookupModule().getName();
            FacilioModule lookupModule = Constants.getModBean().getModule(lookupModuleName);

            FacilioChain chain = MultiImportChainUtil.getOneLevelImportProcessChain(lookupModuleName,childFieldMappings);
            ImportConfig importConfig = MultiImportChainUtil.getMultiImportConfig(lookupModuleName);
            Class beanClass = MultiImportChainUtil.getBeanClass(importConfig, lookupModule);

            FacilioContext oneLevelContext = chain.getContext();
            oneLevelContext.put(FacilioConstants.ContextNames.MODULE_NAME, lookupModuleName);
            oneLevelContext.put(FacilioConstants.ContextNames.IMPORT_ID, importId);
            oneLevelContext.put(FacilioConstants.ContextNames.IMPORT_SHEET, importSheet);
            oneLevelContext.put(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS, importDataDetails);
            oneLevelContext.put(MultiImportApi.ImportProcessConstants.BATCH_ROWS, rowContextList);
            oneLevelContext.put(Constants.BEAN_CLASS, beanClass);

            if(isUpdateMode){
                Map<Long,ModuleBaseWithCustomFields> logIdVsOldLookupRecord = getLogIdVsOldLookupRecordMap(parentLookupField,logIdVsParentRecord);
                oneLevelContext.put(MultiImportApi.ImportProcessConstants.LOG_ID_VS_OLD_RECORD_MAP,logIdVsOldLookupRecord);
            }

            chain.execute();

            Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> oneLevelInsertRecordMap = ImportConstants.getInsertRecordMap(oneLevelContext);

            if(MapUtils.isEmpty(oneLevelInsertRecordMap)){
                continue;
            }
            List<Pair<Long,ModuleBaseWithCustomFields>> oneLevelInsertRecords = oneLevelInsertRecordMap.get(lookupModuleName);

            for(Pair<Long,ModuleBaseWithCustomFields> pair:oneLevelInsertRecords){ //only set insert one level record context
                long logId = pair.getKey();

                ImportRowContext rowContext = logIdVsImportRows.get(logId);
                if(rowContext.isErrorOccurredRow()){
                    continue;
                }
                ModuleBaseWithCustomFields oneLevelRecord = pair.getValue();

                ModuleBaseWithCustomFields parentRecord = logIdVsParentRecord.get(logId);

                FieldUtil.setValue(parentRecord,parentLookupField,oneLevelRecord);
            }

        }


        return false;
    }
    private void init(Context context) throws Exception{
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importDataDetails = (ImportDataDetails)context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);
        parentModuleName = importSheet.getModuleName();
        typeVsFieldMappings = importSheet.getTypeVsFieldMappings();

        rowContextList = ImportConstants.getRowContextList(context).stream()
                                        .filter(row ->!row.isErrorOccurredRow()).collect(Collectors.toList());
        logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);
    }
    private void loadOneLevelMap() {
        List<ImportFieldMappingContext> oneLevelFieldMappings = typeVsFieldMappings.get(ImportFieldMappingType.ONE_LEVEL);
        if(CollectionUtils.isEmpty(oneLevelFieldMappings)){
            return;
        }
        oneLevelMap = oneLevelFieldMappings.stream().collect(Collectors.groupingBy(ImportFieldMappingContext::getParentLookupFieldId));
    }

    private Map<Long,ModuleBaseWithCustomFields> getLogIdVsOldLookupRecordMap(BaseLookupField lookupField,Map<Long, ModuleBaseWithCustomFields> logIdVsParentRecord) throws Exception{
        Map<Long,ModuleBaseWithCustomFields> logIdVsLookupRecordIdMap = new HashMap<>();
        for(Map.Entry<Long,ModuleBaseWithCustomFields> entry : logIdVsParentRecord.entrySet()){
            Long logId = entry.getKey();
            V3Context record = (V3Context) entry.getValue();

            ModuleBaseWithCustomFields lookupRecord = (ModuleBaseWithCustomFields) FieldUtil.getValue(record,lookupField);
            if(lookupRecord!=null){
                logIdVsLookupRecordIdMap.put(logId,lookupRecord);
            }
        }
        return logIdVsLookupRecordIdMap;
    }
}
