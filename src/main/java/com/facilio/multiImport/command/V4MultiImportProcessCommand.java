package com.facilio.multiImport.command;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.context.LookupIdentifierEnum;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO check unique field handling behaviour for lookup record fetch
public  class V4MultiImportProcessCommand extends BaseMultiImportProcessCommand {
    private static final Logger LOGGER = Logger.getLogger(V4MultiImportProcessCommand.class.getName());

    int pointer=0;
    boolean ignoreProgressSendingToClient;
    int one_percentage_records;
    private static final float ratio = 10f/7;
    List<ImportRowContext> recordsToBeAdded = new ArrayList<>();
    List<ImportRowContext> recordsSkipped = new ArrayList<>();
    int insertRecordsCount,updateRecordsCount,skipRecordsCount;
    private static final int SIZE_FOR_REL_RECORD_LOADING = 500;   //500 * 10  we can select 5000 mapped record ids at a time
    Map<ImportFieldMappingContext, RelationMappingContext> fieldMapVsRelationMapping = new HashMap<>();
    Map<RelationMappingContext, ImportFieldMappingContext> relationMapVsFieldMap = new HashMap<>();
    Map<RelationMappingContext, Map<String,Long>> relationMappingVsMappedRecordIdsMap = new HashMap<>();
    private int fromIndex = 0;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("V4MultiImportProcessCommand started time:"+System.currentTimeMillis());

        init(context);
        processRecords(batchRows);
        importRecords();
        processSkippedRecords();
        fillImportResults();

        LOGGER.info("V4MultiImportProcessCommand completed time:"+System.currentTimeMillis());

        return false;
    }

    @Override
    protected List<ImportFieldMappingContext> getCurrentModuleFieldMappings() {
        List<ImportFieldMappingContext> fieldMappings = new ArrayList<>();
        fieldMappings.addAll(typeVsFieldMappings.get(ImportFieldMappingType.NORMAL));
        fieldMappings.addAll(typeVsFieldMappings.get(ImportFieldMappingType.GROUPED_FIELD));
        return fieldMappings;
    }

    @Override
    protected String getModuleName() throws Exception {
        return importSheet.getModuleName();
    }
    @Override
    protected void processRelationshipData(Map<String,Object> rowVal,
                                           HashMap<String,Object> props) throws Exception{
        //Relationship Mapped record ids validation (mapped record id should present in DB)
        if (MapUtils.isNotEmpty(fieldMapVsRelationMapping)) {
            for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : fieldMapVsRelationMapping.entrySet()) {
                ImportFieldMappingContext fieldMappingContext = entry.getKey();
                RelationMappingContext relationMappingContext = entry.getValue();
                String sheetColumnName = fieldMappingContext.getSheetColumnName();
                Object cellValue = rowVal.get(sheetColumnName);
                if (MultiImportApi.isEmpty(cellValue)) {
                    continue;
                }
                validateMappedRelRecord(relationMappingContext, cellValue, props);
            }
        }

    }
    @Override
    protected boolean isOneLevel() {
        return false;
    }

    @Override
    protected boolean isInsertImport() {
        return MultiImportApi.isInsertImportSheet(importSheet);
    }

    protected void init(Context context) throws Exception {
        super.init(context);
        one_percentage_records = MultiImportApi.getOnePercentageRecordsCount(importDataDetails);
        loadRelationShipMap();
        ImportConstants.setRelationshipFieldMapping(context, fieldMapVsRelationMapping);
    }
    private void processRecords(List<ImportRowContext> rows) throws Exception {
        int curIndex = 0;
        for (ImportRowContext rowContext : rows) {
            checkAndRefreshRelationshipData(curIndex++, rows);

            if(rowContext.isErrorOccurredRow()){ //skip if is row is not valid
                sendImportProgressToClient();
                continue;
            }

            lookupHelper.addRecordToSameModuleRecordCache(rowContext);

            if(lookupHelper.skipIfSameModuleLookupRecordInSheet(rowContext)){
                recordsSkipped.add(rowContext);
                continue;
            }
            HashMap<String, Object> processedProps = getProcessedRawRecordMap(rowContext);

            rowContext.setProcessedRawRecordMap(processedProps);

            recordsToBeAdded.add(rowContext);

            sendImportProgressToClient();
        }
    }
    private void checkAndRefreshRelationshipData(int curIndex, List<ImportRowContext> rows) throws Exception{
        if(MapUtils.isEmpty(fieldMapVsRelationMapping)){
            return;
        }
        if(curIndex == fromIndex){
            int toIndex = Math.min(fromIndex + SIZE_FOR_REL_RECORD_LOADING,rows.size());
            refreshRelationMappingVsMappedRecordIdsMap(rows.subList(fromIndex,toIndex));
            fromIndex = toIndex;
        }
    }
    private void processSkippedRecords() throws Exception {
        int curIndex = 0;
        fromIndex = 0; // re-initialize fromIndex
        while (CollectionUtils.isNotEmpty(recordsSkipped)){
            curIndex++;
            lookupHelper.refreshLookupMap();  //refresh lookup map
            checkAndRefreshRelationshipData(curIndex, recordsSkipped); // refresh relationship data
            Iterator<ImportRowContext> iterator = recordsSkipped.listIterator();
            List<ImportRowContext> nexRecordsSkippedList = new ArrayList<>();

            while (iterator.hasNext()) {
                ImportRowContext rowContext = iterator.next();
                if(lookupHelper.skipIfSameModuleLookupRecordInSheet(rowContext)){
                    nexRecordsSkippedList.add(rowContext);
                    continue;
                }
                HashMap<String, Object> processedProps = getProcessedRawRecordMap(rowContext);

                rowContext.setProcessedRawRecordMap(processedProps);

                recordsToBeAdded.add(rowContext);

                sendImportProgressToClient();
            }

            importRecords();
            recordsSkipped = nexRecordsSkippedList;
        }
    }

    private void importRecords() throws Exception {
        if (CollectionUtils.isEmpty(recordsToBeAdded)){
            return;
        }

        FacilioChain importChain =  MultiImportChainUtil.getImportChain(moduleName,importSheet.getImportSettingEnum());

        FacilioContext importContext = importChain.getContext();
        importContext.putAll(context);

        ImportConstants.setRowContextList(importContext, recordsToBeAdded);

        importChain.execute();

        insertRecordsCount += (Integer) importContext.getOrDefault(ImportConstants.INSERT_RECORDS_COUNT,0);
        updateRecordsCount += (Integer) importContext.getOrDefault(ImportConstants.UPDATE_RECORDS_COUNT,0);
        skipRecordsCount += (Integer)   importContext.getOrDefault(ImportConstants.SKIP_RECORDS_COUNT,0);

        lookupHelper.clearErrorRecordInCache(recordsToBeAdded);
        recordsToBeAdded.clear();
    }

    private void fillImportResults(){
        ImportConstants.setRowContextList(context, batchRows);
        context.put(ImportConstants.INSERT_RECORDS_COUNT,insertRecordsCount);
        context.put(ImportConstants.UPDATE_RECORDS_COUNT,updateRecordsCount);
        context.put(ImportConstants.SKIP_RECORDS_COUNT,skipRecordsCount);
    }

    private void sendImportProgressToClient() throws Exception {//send until 50 percentage to client in this command
        importDataDetails.setProcessedRecordsCount(importDataDetails.getProcessedRecordsCount()+1);
        if(ignoreProgressSendingToClient){
            return;
        }
        float currentPercentage = Math.round(MultiImportApi.getImportCompletePercentage(importDataDetails));
        float percentage_var = Math.round(currentPercentage) / (ratio)+30;

        if(percentage_var>99){
            percentage_var = 99;
        }

        if(++pointer % one_percentage_records ==0  || pointer== batchRows.size()){
            HashMap<String,Object> clientJson =  new HashMap<>();
            clientJson.put("percentage",percentage_var);
            MultiImportApi.sendMultiImportProgressToClient(importDataDetails,clientJson);
        }
        if(percentage_var>=99){
            ignoreProgressSendingToClient =true;
        }
    }

    private Map<RelationMappingContext, Map<String,Long>> loadRelationMappingVsMappedRecordIdsMap(List<ImportRowContext> allRows) throws Exception {
        Map<RelationMappingContext, Map<String,Long>> relationMappingVsRecordIdsMap = new HashMap<>();
        fieldMapVsRelationMapping.values().forEach(value -> relationMappingVsRecordIdsMap.put(value, new HashMap<>()));
        for (ImportRowContext rowContext : allRows) {
            Map<String, Object> rowVal = rowContext.getRawRecordMap();
            String sheetColumnName = null;
            try {
                for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : fieldMapVsRelationMapping.entrySet()) {
                    ImportFieldMappingContext fieldMappingContext = entry.getKey();
                    RelationMappingContext relationMappingContext = entry.getValue();
                    Map<String,Long>  globalRelationMappingVsRecordIdsMap = null;
                    if(MapUtils.isNotEmpty(this.relationMappingVsMappedRecordIdsMap)){
                        globalRelationMappingVsRecordIdsMap = this.relationMappingVsMappedRecordIdsMap.get(relationMappingContext);
                    }

                    sheetColumnName = fieldMappingContext.getSheetColumnName();
                    Object cellValue = rowVal.get(sheetColumnName);
                    if (MultiImportApi.isEmpty(cellValue)) {
                        continue;
                    }
                    String[] values;
                    String realCellValue = getRelationshipIdentifierData(fieldMappingContext,cellValue.toString());
                    if (realCellValue.contains(",")) {
                        values = realCellValue.split(",");
                    } else {
                        values = new String[]{realCellValue};
                    }
                    if (values.length > 10) {
                        throw new IllegalArgumentException("Mapped Related record should be less than or equal to 10");
                    }
                    MultiImportApi.duplicateRecordsCheckAndThrowError(Arrays.stream(values).collect(Collectors.toList()));
                    HashMap<String,Long> recordIdentifierVsRecordId = new HashMap<>();
                    for(String value : values){
                        if(globalRelationMappingVsRecordIdsMap!=null && globalRelationMappingVsRecordIdsMap.containsKey(value)){
                            continue;
                        }
                        recordIdentifierVsRecordId.put(value,-1l);
                    }

                    relationMappingVsRecordIdsMap.get(relationMappingContext).putAll(recordIdentifierVsRecordId);
                }

            } catch (Exception e) {
                LOGGER.severe("Exception while relationMappingVsRecordIds loading -- Row No --" + rowContext.getRowNumber() + " Fields Mapping --" + sheetColumnName);
                rowContext.setErrorOccurredRow(true);
                ImportParseException parseException = new ImportParseException(sheetColumnName, e);
                String errorMessage = parseException.getClientMessage();
                rowContext.setErrorMessage(errorMessage);
                LOGGER.severe("Reason for failed:" + errorMessage);
            }


        }

        for (Map.Entry<RelationMappingContext, Map<String,Long>> entry : relationMappingVsRecordIdsMap.entrySet()) {
            RelationMappingContext relationMappingContext = entry.getKey();
            Map<String,Long> uniqueIdentifierVsRecordIdMap = entry.getValue();
            ImportFieldMappingContext  fieldMappingContext = relationMapVsFieldMap.get(relationMappingContext);
            if(MapUtils.isEmpty(uniqueIdentifierVsRecordIdMap)){
                continue;
            }

            FacilioModule toModule = relationMappingContext.getToModule();
            FacilioField uniqueField = getUniqueFieldForRelationMapping(fieldMappingContext,toModule);
            String uniqueFieldName = uniqueField.getName();

            List<FacilioField> selectableFields = new ArrayList<>();
            selectableFields.add(FieldFactory.getIdField(toModule));
            selectableFields.add(uniqueField);

            Criteria criteria = getCriteriaForRelationshipRecordFetch(fieldMappingContext,toModule,uniqueIdentifierVsRecordIdMap.keySet());
            SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                    .module(toModule)
                    .select(selectableFields)
                    .andCriteria(criteria);

            List<Map<String, Object>> props = selectBuilder.getAsProps();
            if (CollectionUtils.isNotEmpty(props)) {
                for(Map<String, Object> prop : props){
                    String uniqueIdentifier =  prop.get(uniqueFieldName).toString();
                    Long recordId = (Long) prop.get("id");
                    uniqueIdentifierVsRecordIdMap.put(uniqueIdentifier,recordId);
                }
                relationMappingVsRecordIdsMap.put(relationMappingContext, uniqueIdentifierVsRecordIdMap);
            }

        }

        return relationMappingVsRecordIdsMap;
    }
    private Criteria getCriteriaForRelationshipRecordFetch(ImportFieldMappingContext fieldMappingContext,FacilioModule module,Set<String> recordIdentifiers) throws Exception {
        Criteria criteria = new Criteria();
        LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();
        switch (lookupIdentifier){
            case ID:
                Set<Long> ids = recordIdentifiers.stream().map(Long::parseLong).collect(Collectors.toSet());
                criteria.addAndCondition(CriteriaAPI.getIdCondition(ids,module));
                break;
            case PRIMARY_FIELD:
                FacilioField primaryField = Constants.getModBean().getPrimaryField(module.getName());
                criteria.addAndCondition(CriteriaAPI.getCondition(primaryField,StringUtils.join(recordIdentifiers,","),StringOperators.IS));
        }
        return criteria;
    }
    private FacilioField  getUniqueFieldForRelationMapping(ImportFieldMappingContext fieldMappingContext,FacilioModule module) throws Exception {
        LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();
        switch (lookupIdentifier){
            case ID:
                return FieldFactory.getIdField(module);
            default:
                FacilioField primaryField = Constants.getModBean().getPrimaryField(module.getName());
                return primaryField;
        }
    }

    private void loadRelationShipMap() throws Exception {
        List<ImportFieldMappingContext> fieldMappingContextList = typeVsFieldMappings.getOrDefault(ImportFieldMappingType.RELATIONSHIP,Collections.EMPTY_LIST);
        for (ImportFieldMappingContext fieldMapping : fieldMappingContextList) {
            if (fieldMapping.getRelMappingId() == -1l) {
                continue;
            }
            RelationMappingContext relationMappingContext = RelationUtil.getRelationMapping(fieldMapping.getRelMappingId());
            fieldMapVsRelationMapping.put(fieldMapping, relationMappingContext);
            relationMapVsFieldMap.put(relationMappingContext,fieldMapping);
            relationMappingVsMappedRecordIdsMap.put(relationMappingContext, new HashMap<>());
        }
    }
    private void validateMappedRelRecord(RelationMappingContext relationMappingContext,
                                         Object cellValue,
                                         HashMap<String, Object> props) throws Exception {
        String[] values;
        ImportFieldMappingContext fieldMappingContext = relationMapVsFieldMap.get(relationMappingContext);
        String realCellValue = getRelationshipIdentifierData(fieldMappingContext,cellValue.toString());
        if (realCellValue.contains(",")) {
            values = realCellValue.split(",");
        } else {
            values = new String[]{realCellValue};
        }

        List<String> relMappingRecordInSheet = Arrays.stream(values).collect(Collectors.toList()); //data -:primary value or id in string

        Map<String,Long> dbNameVsRecordIdMap = relationMappingVsMappedRecordIdsMap.get(relationMappingContext);

        if(MapUtils.isEmpty(dbNameVsRecordIdMap)){
            throw new IllegalArgumentException("Mapped related record ids not in Facilio");
        }
        else{
            StringBuilder stringBuilder = new StringBuilder();
            List<Long> mappedRelRecordIds = new ArrayList<>();
            for(String uniqueIdentifier : relMappingRecordInSheet){
                if(!dbNameVsRecordIdMap.containsKey(uniqueIdentifier) ||  dbNameVsRecordIdMap.get(uniqueIdentifier)==-1L){
                    stringBuilder.append(uniqueIdentifier).append(",");
                }
                mappedRelRecordIds.add(dbNameVsRecordIdMap.get(uniqueIdentifier));
            }
            if(StringUtils.isNotEmpty(stringBuilder.toString())){
                String str = stringBuilder.toString();
                str = str.substring(0, str.lastIndexOf(","));

                if(str.split(",").length==1){
                    str = str + " related record is not in Facilio";
                }else{
                    str = str + " related records are not in Facilio";
                }
                throw new IllegalArgumentException(str);
            }
            props.put(relationMappingContext.getMappingLinkName(),mappedRelRecordIds);
        }
    }
    private void refreshRelationMappingVsMappedRecordIdsMap(List<ImportRowContext> subList) throws Exception{
        Map<RelationMappingContext, Map<String,Long>> relationMappingVsRecordIdsMap  = loadRelationMappingVsMappedRecordIdsMap(subList);
        for(RelationMappingContext relationMappingContext : this.relationMappingVsMappedRecordIdsMap.keySet()){
            Map<String,Long> newRecordIdsFromDB = relationMappingVsRecordIdsMap.get(relationMappingContext);
            this.relationMappingVsMappedRecordIdsMap.get(relationMappingContext).putAll(newRecordIdsFromDB);
        }
    }
    private String getRelationshipIdentifierData(ImportFieldMappingContext fieldMappingContext,String data) throws Exception{
        LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();
        switch (lookupIdentifier){
            case ID:
                StringBuilder stringBuilder = new StringBuilder();
                String[] values = data.split(",");
                int n = values.length;
                for(int i=0;i<n;i++){
                    String val = values[i];
                    if(!NumberUtils.isNumber(val)){
                        throw new IllegalArgumentException(fieldMappingContext.getSheetColumnName()+" column value:"+val+" is not number");
                    }
                    long numberData = (long)Double.parseDouble(val);
                    stringBuilder.append(numberData);
                    if(i!=n-1){
                        stringBuilder.append(",");
                    }
                }
                return stringBuilder.toString();
        }
        return data;
    }
}
