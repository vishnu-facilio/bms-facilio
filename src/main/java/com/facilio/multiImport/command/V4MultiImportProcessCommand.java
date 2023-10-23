package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.annotations.RowFunction;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.enums.FieldTypeImportRowProcessor;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.LoadLookupHelper;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO check unique field handling behaviour for lookup record fetch
public  class V4MultiImportProcessCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(V4MultiImportProcessCommand.class.getName());
    Long importId = null;
    ImportDataDetails importDataDetails = null;
    ImportFileSheetsContext importSheet = null;
    Long importSheetId = null;

    Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping = null;
    Map<Long, String> fieldIdVsSheetColumnNameMap = null;
    Map<String, String> fieldNameVsSheetColumnNameMap = null;
    List<FacilioField> fields = null;
    Map<String, FacilioField> fieldNameVsFacilioFieldMap = null;
    Map<Long, FacilioField> fieldIdVsFacilioFieldMap = null;
    ArrayList<FacilioField> requiredFields = null;
    Map<String, SiteContext> sitesMap = null;
    StateFlowRuleContext defaultStateFlow =null;
    Map<Long,StateFlowRuleContext> stateFlowIdVsStateFlowContext = null;


    String moduleName = null;
    FacilioModule module = null;
    RowFunction beforeProcessRowFunction = null;
    RowFunction afterProcessRowFunction = null;

    Context context = null;

    List<ImportRowContext> batchRows = null;
    Map<String,Object> batchCollectMap = null;
    int pointer=0;
    boolean ignoreProgressSendingToClient;
    int one_percentage_records;
    ModuleBean moduleBean = null;
    private static final float ratio = 10f/7;
    List<ImportRowContext> recordsToBeAdded = new ArrayList<>();
    List<ImportRowContext> recordsSkipped = new ArrayList<>();
    int insertRecordsCount,updateRecordsCount,skipRecordsCount;
    private static final int SIZE_FOR_REL_RECORD_LOADING = 500;   //500 * 10  we can select 5000 mapped record ids at a time
    Map<ImportFieldMappingContext, RelationMappingContext> fieldMapVsRelationMapping = new HashMap<>();
    Map<RelationMappingContext, ImportFieldMappingContext> relationMapVsFieldMap = new HashMap<>();
    Map<RelationMappingContext, Map<String,Long>> relationMappingVsMappedRecordIdsMap = new HashMap<>();
    Map<ImportFieldMappingType,List<ImportFieldMappingContext>> typeVsFieldMappings = new HashMap<>();
    private int fromIndex = 0;

    LoadLookupHelper lookupHelper = null;
    List<FacilioField> mappedFields = new ArrayList<>();

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("V4MultiImportProcessCommand started time:"+System.currentTimeMillis());

        init(context);
        processRecords(batchRows);
        fillImportResults();

        LOGGER.info("V4MultiImportProcessCommand completed time:"+System.currentTimeMillis());

        return false;
    }

    private void init(Context context) throws Exception {
        this.context = context;
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importDataDetails = (ImportDataDetails)context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);

        moduleName = importSheet.getModuleName();
        module = Constants.getModBean().getModule(moduleName);
        importSheetId = importSheet.getId();

        //type vs import field mapping
        typeVsFieldMappings = importSheet.getTypeVsFieldMappings();

        //importSheetFields info
        sheetColumnNameVsFieldMapping = importSheet.getSheetColumnNameVsFieldMapping();
        fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();

        //FacilioFields info
        fields = MultiImportApi.getImportFields(context, importSheet.getModuleName());
        ImportConstants.setImportFields(context,fields);
        fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(fields);
        fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(fields);
        mappedFields = MultiImportApi.getMappedFields(typeVsFieldMappings.get(ImportFieldMappingType.NORMAL),
                fieldIdVsFacilioFieldMap,fieldNameVsFacilioFieldMap);

        beforeProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.BEFORE_PROCESS_ROW_FUNCTION);
        afterProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.AFTER_PROCESS_ROW_FUNCTION);

        batchRows = (List<ImportRowContext>) context.get(MultiImportApi.ImportProcessConstants.BATCH_ROWS);
        batchCollectMap = (Map<String,Object>) context.getOrDefault(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_MAP,new HashMap<>());
        Set<String> skipLookupNotFoundExceptionFields  = (Set<String >) context.get(MultiImportApi.ImportProcessConstants.SKIP_LOOKUP_NOT_FOUND_EXCEPTION);
        if(skipLookupNotFoundExceptionFields == null){
            skipLookupNotFoundExceptionFields = new HashSet<>();
        }
        requiredFields = (ArrayList<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = MultiImportApi.getRequiredFields(moduleName);
        }

        sitesMap = getSiteMap();
        stateFlowIdVsStateFlowContext = getStateFlows();

        lookupHelper = LoadLookupHelper.builder()
                .context(context)
                .moduleName(moduleName)
                .mappedFields(mappedFields)
                .sheetColumnNameVsFieldMapping(sheetColumnNameVsFieldMapping)
                .fieldIdVsSheetColumnNameMap(fieldIdVsSheetColumnNameMap)
                .fieldNameVsSheetColumnNameMap(fieldNameVsSheetColumnNameMap)
                .skipLookupNotFoundExceptionFields(skipLookupNotFoundExceptionFields)
                .build();
        lookupHelper.loadLookupMap(batchRows);

        one_percentage_records = MultiImportApi.getOnePercentageRecordsCount(importDataDetails);
        if(module!=null){
            defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(module);
        }
        moduleBean = Constants.getModBean();
        loadRelationShipMap();
        ImportConstants.setRelationshipFieldMapping(context, fieldMapVsRelationMapping);
    }
    private void processRecords(List<ImportRowContext> rows) throws Exception {
        int curIndex = 0;
        for (ImportRowContext rowContext : rows) {
            checkAndRefreshRelationshipData(curIndex++, rows);

            validateRow(rowContext);

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
        importRecords();
        processSkippedRecords();
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

    private void validateRow(ImportRowContext rowContext) throws Exception {
        if (MultiImportApi.isInsertImportSheet(importSheet)) {
            MultiImportApi.checkMandatoryFieldsValueExistsOrNot(requiredFields, importSheet, rowContext);
        }

        if (importSheet.getImportSetting() != MultiImportSetting.INSERT.getValue()) {
            MultiImportApi.checkImportSettingFieldValueExistOrNot(importSheet,rowContext);
        }
    }
    private void fillImportResults(){
        ImportConstants.setRowContextList(context, batchRows);
        context.put(ImportConstants.INSERT_RECORDS_COUNT,insertRecordsCount);
        context.put(ImportConstants.UPDATE_RECORDS_COUNT,updateRecordsCount);
        context.put(ImportConstants.SKIP_RECORDS_COUNT,skipRecordsCount);
    }

    private HashMap<String, Object> getProcessedRawRecordMap(ImportRowContext rowContext) throws Exception {

        HashMap<String, Object> props = new LinkedHashMap<>();   // processed prop
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        long rowNo = rowContext.getRowNumber();


        // adding source_type and source_id in the props
        props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
        props.put(FacilioConstants.ContextNames.SOURCE_ID, importId);

        // add formId in props if field mapping present for formId
        setFormIdInProps(props, rowVal);

        // add site only for insert
        setSiteIdInPropsOnlyForInsertImport(props, rowVal);
        setDefaultStateFlowIdAndModuleState(props);


        String sheetColumnName = null;

        try {
            if (beforeProcessRowFunction != null) {
                beforeProcessRowFunction.apply(rowContext, rowVal, props, context);
            }

            List<ImportFieldMappingContext> mappedImportFields = typeVsFieldMappings.getOrDefault(ImportFieldMappingType.NORMAL,Collections.EMPTY_LIST);

            for(ImportFieldMappingContext mappingContext : mappedImportFields){

                FacilioField facilioField = MultiImportApi.getFacilioField(mappingContext,fieldIdVsFacilioFieldMap,fieldNameVsFacilioFieldMap);

                sheetColumnName = mappingContext.getSheetColumnName();
                Object cellValue = rowVal.get(sheetColumnName);
                if (MultiImportApi.isEmpty(cellValue)) {
                    // The value of row is empty. Set it as null. // todo check this flow
                    props.put(facilioField.getName(), null);
                    continue;
                }

                if (facilioField.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
                    String cellValueString = cellValue.toString();
                    if (StringUtils.isNotEmpty(cellValueString)) {
                        SiteContext site = sitesMap.get(cellValueString);
                        FacilioUtil.throwIllegalArgumentException(site == null || site.getId() < 0, "Site named as " + cellValueString + " not found under column " + sheetColumnName);
                        props.put(facilioField.getName(), site.getId());
                    }
                    continue;
                }
                if (facilioField.getName().equals(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
                    String cellValueString = cellValue.toString();
                    long stateFlowId = (long) Double.parseDouble(cellValueString);
                    StateFlowRuleContext stateFlowRuleContext = stateFlowIdVsStateFlowContext.get(stateFlowId);
                    FacilioUtil.throwIllegalArgumentException(stateFlowRuleContext == null, "In valid State flow id");
                    props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, stateFlowRuleContext.getId());
                    Map<String, Object> moduleState = new HashMap<>();
                    moduleState.put("id", stateFlowRuleContext.getDefaultStateId());
                    props.put(FacilioConstants.ContextNames.MODULE_STATE, moduleState);

                    continue;
                }

                FieldTypeImportRowProcessor importRowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(facilioField.getDataTypeEnum().name());

                importRowProcessor.process(mappingContext,rowContext,facilioField,cellValue,props,lookupHelper);

            }

            //Relationship Mapped record ids validation (mapped record id should present in DB)
            if (MapUtils.isNotEmpty(fieldMapVsRelationMapping)) {
                for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : fieldMapVsRelationMapping.entrySet()) {
                    ImportFieldMappingContext fieldMappingContext = entry.getKey();
                    RelationMappingContext relationMappingContext = entry.getValue();
                    sheetColumnName = fieldMappingContext.getSheetColumnName();
                    Object cellValue = rowVal.get(sheetColumnName);
                    if (MultiImportApi.isEmpty(cellValue)) {
                        continue;
                    }
                    validateMappedRelRecord(relationMappingContext, cellValue, props);
                }
            }


            if (afterProcessRowFunction != null) {
                afterProcessRowFunction.apply(rowContext, rowVal, props, context);
            }
        } catch (Exception e) {
            LOGGER.severe("Process Import Exception -- Row No --" + rowNo + " Fields Mapping --" + sheetColumnName);
            String errorMessage = null;

            ImportParseException parseException = new ImportParseException(sheetColumnName, e);
            errorMessage = parseException.getClientMessage();

            rowContext.setErrorOccurredRow(true);
            rowContext.setErrorMessage(errorMessage);
            return props;  // if any sheet column data processing throws error means just mark entire row as error record

        }

        rowContext.setProcessedRawRecordMap(props);

        return props;
    }

    private Map<String, SiteContext> getSiteMap() throws Exception {
        Set<String> siteNames = (Set<String>) batchCollectMap.get("siteId");

        if (CollectionUtils.isEmpty(siteNames)) {
            return Collections.EMPTY_MAP;
        }

        List<SiteContext> sites = SpaceAPI.getSitesByNameWithoutScoping(siteNames);

        Map<String, SiteContext> sitesMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity(), (a, b) -> a));
        }
        return sitesMap;
    }
    private  Map<Long,StateFlowRuleContext> getStateFlows() throws Exception {
        Map<Long,StateFlowRuleContext> stateFlowIdVsStateFlowContext = null;
        Set<Long> stateFlowIds = (Set<Long>) batchCollectMap.get("stateFlowId");

        if(CollectionUtils.isEmpty(stateFlowIds)){
            return Collections.EMPTY_MAP;
        }

        List<StateFlowRuleContext> stateFlowRuleContextList = StateFlowRulesAPI.getStateFlowBaseDetails(new ArrayList<>(stateFlowIds));
        stateFlowIdVsStateFlowContext = stateFlowRuleContextList.stream().collect(Collectors.toMap(StateFlowRuleContext::getId,Function.identity()));

        return stateFlowIdVsStateFlowContext;
    }

    private void setSiteIdInPropsOnlyForInsertImport(HashMap<String, Object> props, Map<String, Object> rowVal){
        if (!MultiImportApi.isInsertImportSheet(importSheet)) {
            return;
        }
        if (MapUtils.isEmpty(sitesMap)) {
            return;
        }
        String sheetColumnName = fieldNameVsSheetColumnNameMap.get("siteId");
        if (rowVal.get(sheetColumnName)!=null) {
            Object cellValue = rowVal.get(sheetColumnName);
            String siteName = cellValue.toString();
            SiteContext siteContext = sitesMap.get(siteName.trim());
            if (!MultiImportApi.isEmpty(cellValue)&&siteContext!=null) {
                props.put("siteId", siteContext.getId());
            }
        }
    }

    private void setFormIdInProps(HashMap<String, Object> props, Map<String, Object> rowVal) throws Exception {
        FacilioField formField = Constants.getModBean().getField("formId", moduleName);


        if (formField != null && MultiImportApi.isFieldMappingPresent(importSheet, formField)) {
            props.put("formId", rowVal.get(MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, formField)));
        }
    }
    private void setDefaultStateFlowIdAndModuleState(HashMap<String, Object> props){
        if (defaultStateFlow != null) {
            props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, defaultStateFlow.getId());
            Map<String,Object> moduleState = new HashMap<>();
            moduleState.put("id", defaultStateFlow.getDefaultStateId());
            props.put(FacilioConstants.ContextNames.MODULE_STATE, moduleState);
        }
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
