package com.facilio.multiImport.command;


import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.multiImport.annotations.RowFunction;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.*;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.multiImportExceptions.ImportFieldValueMissingException;
import com.facilio.multiImport.multiImportExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.multiImport.util.MultiImportChainUtil;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


// TODO skip records if any of the value cannot be resolved.. like lookup name cannot be resolved to id
// TODO check unique field handling behaviour for lookup record fetch
public class V3ProcessMultiImportCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(V3ProcessMultiImportCommand.class.getName());
    private static final String VALUES_SEPARATOR = "##";
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
    Map<BaseLookupField, Map<String, Map<String, Object>>> lookupMap = null;
    Map<BaseLookupField, Set<String>> sameModuleRecordCacheLookupMap = new HashMap<>();
    RowFunction beforeProcessRowFunction = null;
    RowFunction afterProcessRowFunction = null;

    Context context = null;

    List<ImportRowContext> batchRows = null;
    Map<String,Object> batchCollectMap = null;
    Set<String> skipLookupNotFoundExceptionFields = null;
    int pointer=0;
    boolean ignoreProgressSendingToClient;
    int one_percentage_records;
    ModuleBean moduleBean = null;
    Class beanClass = null;
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
        LOGGER.info("V3ProcessMultiImportCommand started time:"+System.currentTimeMillis());

        init(context);
        processRecords(batchRows);
        fillImportResults();

        LOGGER.info("V3ProcessMultiImportCommand completed time:"+System.currentTimeMillis());

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

        //importSheetFields info
        sheetColumnNameVsFieldMapping = importSheet.getSheetColumnNameVsFieldMapping();
        fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();

        //FacilioFields info
        fields = MultiImportApi.getImportFields(context, importSheet.getModuleName());
        ImportConstants.setImportFields(context,fields);
        fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(fields);
        fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(fields);

        beforeProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.BEFORE_PROCESS_ROW_FUNCTION);
        afterProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.AFTER_PROCESS_ROW_FUNCTION);

        batchRows = (List<ImportRowContext>) context.get(MultiImportApi.ImportProcessConstants.BATCH_ROWS);
        batchCollectMap = (Map<String,Object>) context.getOrDefault(MultiImportApi.ImportProcessConstants.BATCH_COLLECT_MAP,new HashMap<>());
        skipLookupNotFoundExceptionFields = (Set<String >) context.get(MultiImportApi.ImportProcessConstants.SKIP_LOOKUP_NOT_FOUND_EXCEPTION);
        if(skipLookupNotFoundExceptionFields == null){
            skipLookupNotFoundExceptionFields = new HashSet<>();
        }
        requiredFields = (ArrayList<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = MultiImportApi.getRequiredFields(moduleName);
        }

        sitesMap = getSiteMap();
        stateFlowIdVsStateFlowContext = getStateFlows();

        lookupMap = loadLookupMap(batchRows);
        one_percentage_records = MultiImportApi.getOnePercentageRecordsCount(importDataDetails);
        if(module!=null){
            defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(module);
        }
        moduleBean = Constants.getModBean();
        beanClass = (Class) context.get(Constants.BEAN_CLASS);
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

            addRecordToSameModuleRecordCache(rowContext);

            if(skipIfSameModuleLookupRecordInSheet(rowContext)){
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
            refreshLookupMap();  //refresh lookup map
            checkAndRefreshRelationshipData(curIndex, recordsSkipped); // refresh relationship data
            Iterator<ImportRowContext> iterator = recordsSkipped.listIterator();
            List<ImportRowContext> nexRecordsSkippedList = new ArrayList<>();

            while (iterator.hasNext()) {
                ImportRowContext rowContext = iterator.next();
                if(skipIfSameModuleLookupRecordInSheet(rowContext)){
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

        clearErrorRecordInCache(recordsToBeAdded);
        recordsToBeAdded.clear();
    }
    private void clearErrorRecordInCache(List<ImportRowContext> recordsToBeAdded) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        for(ImportRowContext rowContext : recordsToBeAdded){
            removeRecordFromSameModuleRecordCache(rowContext);
        }
    }

    private void validateRow(ImportRowContext rowContext) throws Exception {
        if (MultiImportApi.isInsertImportSheet(importSheet)) {
            MultiImportApi.checkMandatoryFieldsValueExistsOrNot(requiredFields, importSheet, rowContext);
        }

        if (importSheet.getImportSetting() != MultiImportSetting.INSERT.getValue()) {
            MultiImportApi.checkImportSettingFieldValueExistOrNot(importSheet,rowContext);
        }
    }
    private void removeRecordFromSameModuleRecordCache(ImportRowContext rowContext) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for(BaseLookupField lookupField : sameModuleRecordCacheLookupMap.keySet()){
            List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
            try{
                FacilioField primaryField = uniqueFields.get(0);
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, primaryField);
                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    continue;
                }
                String name = cellValue.toString().trim();

                String lookUpValueKey = getLookUKeyValueFromSheet(lookupField,uniqueFields,rowContext, true);

                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPARATOR + lookUpValueKey;
                }
                sameModuleRecordCacheLookupMap.get(lookupField).remove(name);
            }catch (ImportFieldValueMissingException e){

            }
        }

    }
    private void addRecordToSameModuleRecordCache(ImportRowContext rowContext) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for(BaseLookupField lookupField : sameModuleRecordCacheLookupMap.keySet()){
            List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
            try{
                FacilioField firstField = uniqueFields.get(0);
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, firstField);
                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    continue;
                }
                String name = cellValue.toString().trim();

                String lookUpValueKey = getLookUKeyValueFromSheet(lookupField,uniqueFields,rowContext, true);

                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPARATOR + lookUpValueKey;
                }
                sameModuleRecordCacheLookupMap.get(lookupField).add(name);
            }catch (ImportFieldValueMissingException e){

            }
        }
    }
    private boolean skipIfSameModuleLookupRecordInSheet(ImportRowContext rowContext){
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return false;
        }

        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for (BaseLookupField baseLookupField : sameModuleRecordCacheLookupMap.keySet()){
            try{
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, baseLookupField);

                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    continue;
                }
                String name = cellValue.toString().trim();

                Map<String, Map<String, Object>> nameVsIds = lookupMap.get(baseLookupField);
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(baseLookupField);
                String lookUpValueKey = getLookUKeyValueFromSheet(baseLookupField,uniqueFields,rowContext, true);

                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPARATOR + lookUpValueKey;
                }
                Object value = nameVsIds.get(name); //DP record

                if(value == null && containsInSameModuleCache(baseLookupField,name)){
                    return true;
                }
            }catch (Exception e){
                rowContext.setErrorOccurredRow(true);
                rowContext.setErrorMessage(e.getMessage());
            }
        }

        return false;
    }
    private boolean containsInSameModuleCache(BaseLookupField lookupField,String uniqueRecordKey){
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return false;
        }
        return sameModuleRecordCacheLookupMap.get(lookupField).contains(uniqueRecordKey);
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


            for (FacilioField field : fields) {

                if (!MultiImportApi.isFieldMappingPresent(importSheet, field)) { // there is no mapping for this field. Don't do anything
                    continue;
                }

                sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, field);

                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    // The value of row is empty. Set it as null. // todo check this flow
                    props.put(field.getName(), null);
                    continue;
                }


                if (field.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
                    String cellValueString = cellValue.toString();
                    if (StringUtils.isNotEmpty(cellValueString)) {
                        SiteContext site = sitesMap.get(cellValueString);
                        FacilioUtil.throwIllegalArgumentException(site == null || site.getId() < 0, "Site named as " + cellValueString + " not found under column " + sheetColumnName);
                        props.put(field.getName(), site.getId());
                    }
                    continue;
                }
                if (field.getName().equals(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
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

                formatCellValueBasedOnFieldType(field, rowNo, sheetColumnName, cellValue, props, rowContext);

            }

            //Relationship Mapped record ids validation (mapped record id should present in DB)
            if (MapUtils.isNotEmpty(fieldMapVsRelationMapping)) {
                for (Map.Entry<ImportFieldMappingContext, RelationMappingContext> entry : fieldMapVsRelationMapping.entrySet()) {
                    ImportFieldMappingContext fieldMappingContext = entry.getKey();
                    RelationMappingContext relationMappingContext = entry.getValue();
                    sheetColumnName = fieldMappingContext.getSheetColumnName();
                    Object cellValue = rowVal.get(sheetColumnName);
                    if (isEmpty(cellValue)) {
                        continue;
                    }
                    validateMappedRelRecord(relationMappingContext, sheetColumnName, cellValue, props, rowContext);
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

    private void formatCellValueBasedOnFieldType(FacilioField field,long rowNo, String sheetColumnName,
                                                 Object cellValue,
                                                 HashMap<String, Object> props,ImportRowContext rowContext) throws Exception{

        switch (field.getDataTypeEnum()) {
            case DATE:
            case DATE_TIME: {
                long millis;
                if (!(cellValue instanceof Number)) {
                    ImportFieldMappingContext importFieldContext = sheetColumnNameVsFieldMapping.get(sheetColumnName);
                    String dateFormat = importFieldContext.getDateFormat();
                    if (dateFormat == null) {
                        throw new IllegalArgumentException("date format cannot be null");
                    }
                    if (dateFormat.equals(MultiImportApi.ImportProcessConstants.TIME_STAMP_STRING)) {
                        millis = new Double(cellValue.toString()).longValue();
                    } else {
                        Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormat, cellValue.toString());
                        millis = dateInstant.toEpochMilli();
                    }
                } else {
                    millis = (long) Double.parseDouble(cellValue.toString());
                }
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), millis);
                }
                break;
            }
            case SYSTEM_ENUM: {
                SystemEnumField enumField = (SystemEnumField) field;
                String enumString = (String) cellValue;
                int enumIndex = enumField.getIndex(enumString);

                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), enumIndex);
                }
                break;
            }
            case ENUM: {
                EnumField enumField = (EnumField) field;
                String enumString = (String) cellValue;
                int enumIndex = enumField.getIndex(enumString);

                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), enumIndex);
                }
                break;
            }
            case MULTI_ENUM: {
                MultiEnumField multiEnumField = (MultiEnumField) field;
                String enumString = (String) cellValue;
                ArrayList enumIndices = new ArrayList();
                if (StringUtils.isNotEmpty(enumString)) {
                    for (String string : FacilioUtil.splitByComma(enumString)) {
                        int enumIndex = multiEnumField.getIndex(string);
                        if (enumIndex > 0) {
                            enumIndices.add(enumIndex);
                        }
                    }
                }
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), enumIndices);
                }
                break;
            }
            case MULTI_LOOKUP: {
                String multiLookupValue = getLookupIdentifierData((BaseLookupField) field,cellValue.toString().trim());
                String[] split = multiLookupValue.split(",");
                List<Object> values = new ArrayList<>();

                String lookUpModuleDisplayName = ((BaseLookupField) field).getLookupModule().getDisplayName();
                List<FacilioField> uniqueFields = getImportLookupUniqueFields((BaseLookupField) field);
                Map<String, Map<String, Object>> nameVsIds = lookupMap.get(field);
                for (String s : split) {
                    String name = s.trim();
                    String lookUpValueKey = getLookUKeyValueFromSheet((BaseLookupField) field,uniqueFields,rowContext, true);

                    if (!lookUpValueKey.isEmpty()) {
                        name = name + VALUES_SEPARATOR + lookUpValueKey;
                    }
                    Object value = nameVsIds.get(name);

                    if(value == null && !skipLookupNotFoundExceptionFields.contains(field.getName())){
                        throw new ImportLookupModuleValueNotFoundException(lookUpModuleDisplayName,sheetColumnName,null);
                    }

                    if (nameVsIds.containsKey(name) && nameVsIds.get(name) != null) {
                        values.add(nameVsIds.get(name));
                    }
                }
                props.put(field.getName(), values);
                break;
            }
            case LOOKUP: {
                String lookUpModuleName = ((BaseLookupField) field).getLookupModule().getName();
                Map<String, Map<String, Object>> nameVsIds = lookupMap.get(field);
                List<FacilioField> uniqueFields = getImportLookupUniqueFields((BaseLookupField) field);
                String lookUpValueKey = getLookUKeyValueFromSheet((BaseLookupField) field,uniqueFields,rowContext, true);
                String name = getLookupIdentifierData((BaseLookupField) field,cellValue.toString().trim());
                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPARATOR + lookUpValueKey;
                }
                Object value = nameVsIds.get(name);

                if(value == null && !skipLookupNotFoundExceptionFields.contains(field.getName())){ //lookup record not in DP and not in Sheet means mark error
                    throw new ImportLookupModuleValueNotFoundException(lookUpModuleName,sheetColumnName,null);
                }

                props.put(field.getName(), value);
                break;
            }
            case NUMBER:{
                String cellValueString = cellValue.toString();
                if (cellValueString.contains(",")) {
                    cellValueString = cellValueString.replaceAll(",", "");
                }

                long cellLongValue =(long)Double.parseDouble(cellValueString);
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), cellLongValue);
                }
                NumberField numberField = (NumberField) field;
                ImportFieldMappingContext importFieldMapping = sheetColumnNameVsFieldMapping.get(sheetColumnName);
                if(numberField.getMetric()!=-1 && numberField.getUnitId()!=-1 && importFieldMapping.getUnitId()!=-1){
                    props.put(field.getName()+"Unit",importFieldMapping.getUnitId());
                }
                break;
            }
            case DECIMAL: {
                String cellValueString = cellValue.toString();
                if (cellValueString.contains(",")) {
                    cellValueString = cellValueString.replaceAll(",", "");
                }

                Double cellDoubleValue = Double.parseDouble(cellValueString);
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), cellDoubleValue);
                }
                NumberField decimalField = (NumberField) field;
                ImportFieldMappingContext importFieldMapping = sheetColumnNameVsFieldMapping.get(sheetColumnName);
                if(decimalField.getMetric()!=-1 && decimalField.getUnitId()!=-1 && importFieldMapping.getUnitId()!=-1){
                    props.put(field.getName()+"Unit",importFieldMapping.getUnitId());
                }
                break;
            }
            case BOOLEAN: {
                String cellValueString = cellValue.toString();
                boolean booleanValue = FacilioUtil.parseBoolean(cellValueString);
                props.put(field.getName(), booleanValue);
                break;
            }
            case ID: {
                String cellValueString = cellValue.toString();
                long id = (long) Double.parseDouble(cellValueString);
                props.put(field.getName(), id);
                break;
            }
            case STRING:{

                if(field.getDisplayType()== FacilioField.FieldDisplayType.EMAIL){

                    String cellValueString = cellValue.toString();

                    String regex = FacilioConstants.FormContextNames.EMAIL_REGEX;
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(cellValueString);

                    FacilioUtil.throwIllegalArgumentException(!matcher.matches(), "Invalid Email");

                }

                if(cellValue instanceof Double){
                    String cellValueString = cellValue.toString();
                    Long number = (long) Double.parseDouble(cellValueString);
                    props.put(field.getName(), number.toString());
                }else {
                    props.put(field.getName(), cellValue);
                }
                break;
            }
            default: {
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), cellValue);
                }
            }
        }
    }

    // TODO check unique field handling behaviour for lookup record fetch
    private String getLookUKeyValueFromSheet(BaseLookupField parentLookUpField,List<FacilioField> uniqueFields,ImportRowContext rowContext, boolean validate) throws ImportFieldValueMissingException {
        StringBuilder keyValue = new StringBuilder();
        Set<String> canBeEmptyFieldNames = getCanBeEmptyFieldNames(parentLookUpField);
        if(uniqueFields.size()==1){
            return keyValue.toString();
        }

        Map<String, Object> rowVal = rowContext.getRawRecordMap();

        for (int i = 1; i < uniqueFields.size(); i++) {
            FacilioField uniqueField = uniqueFields.get(i);
            Long uniqueFieldId = uniqueField.getFieldId();
            String uniqueFieldName = uniqueField.getName();
            String uniqueFieldSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet,uniqueField);
            if(uniqueFieldSheetColumnName == null && uniqueFieldName!=null && uniqueFieldName.equals("site")){
                uniqueFieldSheetColumnName = fieldNameVsSheetColumnNameMap.get("siteId");
            }
            Object uniqueCellValue = rowVal.get(uniqueFieldSheetColumnName);
            if (isEmpty(uniqueCellValue)) {
                uniqueCellValue = "null";
                if (validate && !canBeEmptyFieldNames.contains(uniqueFieldName)) {
                    throw new ImportFieldValueMissingException(uniqueFieldSheetColumnName, new Exception());
                }
            }
            String uniqueCellStringValue = uniqueCellValue.toString();
            /* to be removed after parsing cell data based on facilio field type in sheet reader */
            if(uniqueField.getDataTypeEnum()==FieldType.NUMBER && NumberUtils.isNumber(uniqueCellStringValue)){
                  Long numberValue = (long) Double.parseDouble(uniqueCellStringValue);
                  uniqueCellStringValue =numberValue.toString();
            }

            keyValue.append(uniqueCellStringValue.trim());
            if (i == uniqueFields.size() - 1) {
                break;
            }
            keyValue.append(VALUES_SEPARATOR);
        }
        return keyValue.toString();
    }

    private Map<BaseLookupField, Map<String, Map<String, Object>>> loadLookupMap(List<ImportRowContext> allRows) throws Exception {
        Map<BaseLookupField, Map<String, Map<String, Object>>> lookupMap = new HashMap<>();
        List<FacilioField> fields = MultiImportApi.getImportFields(context, moduleName);
        for(FacilioField field: fields){
            Long fieldId = field.getFieldId();
            String fieldName = field.getName();
            if (!fieldIdVsSheetColumnNameMap.containsKey(fieldId) && !fieldNameVsSheetColumnNameMap.containsKey(fieldName)) {
                // there is no mapping for this field. Don't do anything
                continue;
            }
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                BaseLookupField lookupField = (BaseLookupField) field;
                Map<String, Map<String, Object>> numberIdPairList = new HashMap<>();
                lookupMap.put(lookupField, numberIdPairList);

                if(lookupField.getLookupModule().getName().equals(moduleName)){
                    sameModuleRecordCacheLookupMap.put(lookupField,new HashSet<>());
                }
            }
        }

        if(MapUtils.isEmpty(lookupMap)){
            return lookupMap;
        }

        for(ImportRowContext rowContext : allRows){
            Map<String, Object> rowVal = rowContext.getRawRecordMap();
            for(Map.Entry<BaseLookupField, Map<String, Map<String, Object>>> mapEntry : lookupMap.entrySet()){
                BaseLookupField lookupField = mapEntry.getKey();
                Map<String, Map<String, Object>> numberIdPairList = mapEntry.getValue();

                Long fieldId = lookupField.getFieldId();
                String fieldName = lookupField.getName();
                String sheetColumnName = fieldId != -1l ? fieldIdVsSheetColumnNameMap.get(fieldId) : fieldNameVsSheetColumnNameMap.get(fieldName);

                Object cellValue = rowVal.get(sheetColumnName);
                if (isEmpty(cellValue)) {
                    continue;
                }

                String[] values;
                if (lookupField.getDataTypeEnum() == FieldType.LOOKUP) {
                    values = new String[]{cellValue.toString()};
                } else if (lookupField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    values = cellValue.toString().split(",");
                } else {
                    continue;
                }

                List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
                String keyValue = getLookUKeyValueFromSheet(lookupField,uniqueFields,rowContext, false);

                lookupMap.put(lookupField, numberIdPairList);

                for (String value : values) {
                    if (!keyValue.isEmpty()) {
                        value = value + VALUES_SEPARATOR + keyValue;
                    }
                    numberIdPairList.put(value.trim(), null);
                }
            }

        }

        for (BaseLookupField lookupField : lookupMap.keySet()) {
            String lookupModuleName = "";
            if (lookupField.getLookupModule() == null && lookupField.getSpecialType() != null) {
                lookupModuleName = lookupField.getSpecialType();
            } else {
                lookupModuleName = lookupField.getLookupModule().getName();
            }
            Map<String, Map<String, Object>> nameIdMap = lookupMap.get(lookupField);
            if (LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
                for (String name : nameIdMap.keySet()) {
                    Map<String, Object> propValue = getSpecialLookupProps(lookupField, name, lookupModuleName);
                    nameIdMap.put(name, propValue);
                }
            } else {
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = getSelectBuilderForLoadLookUpMap(lookupField, nameIdMap, uniqueFields);

                if(selectBuilder == null){
                    continue;
                }

                List<Map<String, Object>> props = selectBuilder.getAsProps();
                if (CollectionUtils.isNotEmpty(props)) {
                    Map<String, Map<String, Object>> propMap = props.stream().collect(Collectors.toMap(p -> {
                        String uniqueKey = getUniqueKeyForLookUp(uniqueFields, p);
                        return uniqueKey;
                    }, Function.identity(), (a, b) -> a));

                    for (String name : propMap.keySet()) {
                        nameIdMap.put(name, propMap.get(name));
                    }
                }
            }

        }
        return lookupMap;
    }
    private void refreshLookupMap() throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        for(BaseLookupField baseLookupField : sameModuleRecordCacheLookupMap.keySet()){
            Map<String, Map<String, Object>> nameIdMap = lookupMap.get(baseLookupField);
            String lookupModuleName = "";
            if (baseLookupField.getLookupModule() == null && baseLookupField.getSpecialType() != null) {
                lookupModuleName = baseLookupField.getSpecialType();
            } else {
                lookupModuleName = baseLookupField.getLookupModule().getName();
            }
            if (LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
                for (String name : nameIdMap.keySet()) {
                    Map<String, Object> propValue = getSpecialLookupProps(baseLookupField, name, lookupModuleName);
                    nameIdMap.put(name, propValue);
                }
            } else {
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(baseLookupField);

                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = getSelectBuilderForLoadLookUpMap(baseLookupField, nameIdMap, uniqueFields);

                if(selectBuilder == null){
                    return;
                }

                List<Map<String, Object>> props = selectBuilder.getAsProps();
                if (CollectionUtils.isNotEmpty(props)) {
                    Map<String, Map<String, Object>> propMap = props.stream().collect(Collectors.toMap(p -> {
                        String uniqueKey = getUniqueKeyForLookUp(uniqueFields, p);
                        return uniqueKey;
                    }, Function.identity(), (a, b) -> a));

                    for (String name : propMap.keySet()) {
                        nameIdMap.put(name, propMap.get(name));
                    }
                }
            }
        }
    }

    private String getUniqueKeyForLookUp(List<FacilioField> uniqueFields, Map<String, Object> prop) {
        StringBuilder uniqueKey = new StringBuilder();
        for (int i = 0; i < uniqueFields.size(); i++) {
            FacilioField field = uniqueFields.get(i);
            String fieldName = field.getName();
            Object value = prop.get(fieldName);
            if(value==null){
                value="null";
            }
            if(field instanceof SupplementRecord && value instanceof Map){
                try {
                    FacilioField primaryField = Constants.getModBean().getPrimaryField(((BaseLookupField)field).getLookupModule().getName());
                    if(primaryField!=null){
                        value = ((Map)value).get(primaryField.getName());
                    }
                }catch (Exception ignored){
                    value=null;
                }
            }
            uniqueKey.append(value);
            if (i == uniqueFields.size() - 1) {
                break;
            }
            uniqueKey.append(VALUES_SEPARATOR);

        }
        return uniqueKey.toString();
    }

    private SelectRecordsBuilder<ModuleBaseWithCustomFields> getSelectBuilderForLoadLookUpMap(BaseLookupField lookupField, Map<String, Map<String, Object>> nameIdMap, List<FacilioField> uniqueFields) throws Exception {

        if(MapUtils.isEmpty(nameIdMap)){
            return null;
        }

        List<FacilioField> fieldsList = new ArrayList<>();
        List<FacilioField> extraSelectFields = getLoadLookUpExtraSelectFields(lookupField.getLookupModule().getName());
        if(CollectionUtils.isNotEmpty(extraSelectFields)){
            fieldsList.addAll(extraSelectFields);
        }
        FacilioModule importModule = Constants.getModBean().getModule(moduleName);
        fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
        fieldsList.addAll(uniqueFields);


        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                .module(lookupField.getLookupModule())
                .select(fieldsList);

        if (lookupField.getName().equals("moduleState")) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(importModule.getModuleId()), NumberOperators.EQUALS));
        } else if (lookupField.getName().equals("approvalStatus")) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", null, CommonOperators.IS_EMPTY));
        }

        Criteria uniqueFieldsCriteria = getCriteriaForLoadLookUpMap(uniqueFields, nameIdMap,selectBuilder,lookupField);
        if (!uniqueFieldsCriteria.isEmpty()) {
            selectBuilder.andCriteria(uniqueFieldsCriteria);
        }
        return selectBuilder;
    }

    private Criteria getCriteriaForLoadLookUpMap(List<FacilioField> uniqueFields, Map<String, Map<String, Object>> nameIdMap,SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder,BaseLookupField parentLookUpField) throws Exception {
        Criteria criteria = new Criteria();
        List<SupplementRecord> supplementRecords= new ArrayList<>();
        Set<String> canBeEmptyFieldNames = getCanBeEmptyFieldNames(parentLookUpField);
        for (int i = 0; i < uniqueFields.size(); i++) {
            Condition condition = new Condition();
            Set<String> set = new HashSet<>();

            FacilioField field = uniqueFields.get(i);
            condition.setField(field);
            condition.setOperator(StringOperators.IS);

            StringBuilder tableAlias = new StringBuilder(parentLookUpField.getName());
            tableAlias.append("_").append(field.getName());

            for (String uniqueValues : nameIdMap.keySet()) {
                String uniqueValue = uniqueValues.split(VALUES_SEPARATOR)[i];

                if (uniqueValue.equals("null")) {
                    continue;
                }
                set.add(uniqueValue.replace(",", StringOperators.DELIMITED_COMMA));
            }
            if(CollectionUtils.isEmpty(set)){
                continue;
            }
            if(field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum()==FieldType.MULTI_LOOKUP){
                BaseLookupField childLookupField = (BaseLookupField)field;
                FacilioField primaryField = Constants.getModBean().getPrimaryField(childLookupField.getLookupModule().getName());
                FacilioField idField = FieldFactory.getIdField(childLookupField.getLookupModule());
                if(canBeEmptyFieldNames.contains(field.getName())){
                    String primaryValue = StringUtils.join(set, ",");
                    selectBuilder.leftJoin(primaryField.getTableName()).alias(tableAlias.toString())
                            .on(tableAlias+".ID="+childLookupField.getCompleteColumnName());
                    Criteria canBeEmptyCriteria = new Criteria();
                    canBeEmptyCriteria.addAndCondition(CriteriaAPI.getCondition(tableAlias+".ID", idField.getName(),"", CommonOperators.IS_EMPTY));
                    canBeEmptyCriteria.addOrCondition(CriteriaAPI.getCondition(tableAlias+"."+primaryField.getName(),primaryField.getName(),primaryValue,StringOperators.IS));
                    selectBuilder.andCriteria(canBeEmptyCriteria);
                }else{
                    set = appendSingleQuotesTString(set);
                    String primaryValue = StringUtils.join(set, ",");
                    selectBuilder.innerJoin(primaryField.getTableName()).alias(tableAlias.toString())
                            .on(tableAlias+".ID="+childLookupField.getCompleteColumnName()+" AND "+tableAlias+"."+primaryField.getName()+" IN ("+primaryValue+")");
                }
                supplementRecords.add((SupplementRecord) field);

            }else{
                condition.setValue(StringUtils.join(set, ","));
                criteria.addAndCondition(condition);
            }
        }
        if(CollectionUtils.isNotEmpty(supplementRecords)){
            selectBuilder.fetchSupplements(supplementRecords);
        }
        return criteria;
    }

    private List<FacilioField> getImportLookupUniqueFields(BaseLookupField lookupField) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String lookupFieldName = lookupField.getName();
        String lookupModuleName = lookupField.getLookupModule().getName();

        String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet,lookupField);
        ImportFieldMappingContext fieldMappingContext = sheetColumnNameVsFieldMapping.get(sheetColumnName);
        LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();

        List<FacilioField> uniqueFields = new ArrayList<>();
        switch (lookupIdentifier){
            case ID:  //look up data loading using id
                FacilioField idField = FieldFactory.getIdField(lookupField.getLookupModule());
                uniqueFields.add(idField);
                break;
            case PRIMARY_FIELD:
                Map<String, List<String>> lookupMainFieldMap = (Map<String, List<String>>) context.get(MultiImportApi.ImportProcessConstants.LOOKUP_UNIQUE_FIELDS_MAP);
                if (MapUtils.isNotEmpty(lookupMainFieldMap) && lookupMainFieldMap.containsKey(lookupFieldName)) {
                    List<String> fieldNames = lookupMainFieldMap.get(lookupFieldName);
                    for (String fieldName : fieldNames) {
                        if(fieldName.startsWith("*")){
                            fieldName = fieldName.substring(1,fieldName.length());
                        }
                        FacilioField uniqueField = modBean.getField(fieldName, lookupModuleName);
                        uniqueFields.add(uniqueField);
                    }
                }
                if (CollectionUtils.isEmpty(uniqueFields)) {   //if uniqueFields not configured by module owners ,take lookup module's primary field as a unique field
                    FacilioField primaryField = modBean.getPrimaryField(lookupModuleName);
                    uniqueFields.add(primaryField);
                }
        }

        return uniqueFields;
    }
    private Set<String> getCanBeEmptyFieldNames(BaseLookupField lookupField){
        Map<String, List<String>> lookupMainFieldMap = (Map<String, List<String>>) context.get(MultiImportApi.ImportProcessConstants.LOOKUP_UNIQUE_FIELDS_MAP);
        Set<String> canBeEmptyFieldNames = new HashSet<>();
        String lookupFieldName = lookupField.getName();

        if (MapUtils.isNotEmpty(lookupMainFieldMap) && lookupMainFieldMap.containsKey(lookupFieldName)) {
            List<String> fieldNames = lookupMainFieldMap.get(lookupFieldName);
            for (String fieldName : fieldNames) {
                if(fieldName.startsWith("*")){
                    canBeEmptyFieldNames.add(fieldName.substring(1,fieldName.length()));
                }
            }
        }

        return canBeEmptyFieldNames;

    }

    private boolean isEmpty(Object cellValue) {
        if (cellValue == null || cellValue.toString().equals("") || (cellValue.toString().equals("n/a"))) {
            return true;
        }
        return false;
    }



    public static Map<String, Object> getSpecialLookupProps(BaseLookupField lookupField, Object value, String moduleName) throws Exception {
        String key = lookupField.getModule().getName() + "__" + lookupField.getName();
        try {
            if (value == null || value.toString().isEmpty()) {
                if (!lookupField.isRequired()) {
                    return null;
                } else {
                    throw new Exception("Field value missing under column " + key + ".");
                }
            }

            switch (moduleName) {
                case "users": {
                    long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);

                    String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
                    Pattern pattern = Pattern.compile(emailRegex);
                    Matcher matcher = pattern.matcher(value.toString());

                    if (matcher.matches()) {
                        User user = AccountUtil.getUserBean().getUserFromEmail(value.toString(), appDomainObj != null ? appDomainObj.getIdentifier() : null, AccountUtil.getCurrentOrg().getOrgId());
                        if (user != null) {
                            return FieldUtil.getAsProperties(user);
                        }

                    }
                }
                case "role": {
                    long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    Role role = AccountUtil.getRoleBean(AccountUtil.getCurrentOrg().getOrgId()).getRole(AccountUtil.getCurrentOrg().getOrgId(), value.toString());
                    if (role != null) {
                        return FieldUtil.getAsProperties(role);
                    }

                }

            }

        } catch (Exception e) {
            LOGGER.severe("Exception occurred for special lookup: " + e.toString());
            throw e;
        }
        return null;
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

    public Set<String> getAllRecordSiteNames() throws Exception {
        FacilioField siteIdField = Constants.getModBean().getField("siteId", moduleName);
        String siteSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, siteIdField);
        Set<String> siteNames = new HashSet<>();
        for (ImportRowContext importRowContext : batchRows) {
            Map<String, Object> rowVal = importRowContext.getRawRecordMap();
            Object cellValue = rowVal.get(siteSheetColumnName);
            if (!isEmpty(cellValue)) {
                String siteName = cellValue.toString();
                siteNames.add(siteName);
            }
        }
        return siteNames;
    }
    public Set<Long> getAllRecordStateFlowIds() throws Exception {
        FacilioField stateFlowIdField = Constants.getModBean().getField("stateFlowId", moduleName);
        String siteSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, stateFlowIdField);
        Set<Long> stateFlowIds = new HashSet<>();
        for (ImportRowContext importRowContext : batchRows) {
            Map<String, Object> rowVal = importRowContext.getRawRecordMap();
            Object cellValue = rowVal.get(siteSheetColumnName);
            if (!isEmpty(cellValue)) {
                String stateFlowIdString = cellValue.toString();
                if(NumberUtils.isNumber(stateFlowIdString)){
                    stateFlowIds.add((long)Double.parseDouble(stateFlowIdString));
                }
            }
        }
        return stateFlowIds;
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
            if (!isEmpty(cellValue)&&siteContext!=null) {
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
    private List<FacilioField> getLoadLookUpExtraSelectFields(String lookUpModuleName) throws Exception {
        List<FacilioField> loadLookUpExtraSelectFields = new ArrayList<>();
        Map<String, List<String>> loadLookUpExtraSelectFieldsMap = (Map<String, List<String>>)context.get(MultiImportApi.ImportProcessConstants.LOAD_LOOK_UP_EXTRA_SELECT_FIELDS_MAP);
        if(MapUtils.isEmpty(loadLookUpExtraSelectFieldsMap) || CollectionUtils.isEmpty(loadLookUpExtraSelectFieldsMap.get(lookUpModuleName))){
            return loadLookUpExtraSelectFields;
        }
        List<String> fieldNames = loadLookUpExtraSelectFieldsMap.get(lookUpModuleName);
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(lookUpModuleName));
        for(String fieldName:fieldNames){
            if(fieldMap.containsKey(fieldName)){
                loadLookUpExtraSelectFields.add(fieldMap.get(fieldName));
            }
        }

        return loadLookUpExtraSelectFields;
    }
    private Set<String> appendSingleQuotesTString(Set<String> set){
        if(CollectionUtils.isEmpty(set)){
            return set;
        }
        return set.stream().map(s->"\'"+s+"\'").collect(Collectors.toSet());
    }
    private String getLookupIdentifierData(BaseLookupField lookupField,String data) throws Exception{
        String lookupFieldName = lookupField.getName();
        String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet,lookupField);
        ImportFieldMappingContext fieldMappingContext = sheetColumnNameVsFieldMapping.get(sheetColumnName);
        LookupIdentifierEnum lookupIdentifier = fieldMappingContext.getLookupIdentifierEnum();

        switch (lookupIdentifier){
            case ID: {
                if (lookupField.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                    StringBuilder stringBuilder = new StringBuilder();
                    String values[] = data.split(",");
                    int n = values.length;
                    for (int i = 0; i < n; i++) {
                        String val = values[i];
                        if (!NumberUtils.isNumber(val)) {
                            throw new IllegalArgumentException(lookupFieldName + " field value:" + val + " is not number");
                        }
                        Long numberData = (long) Double.parseDouble(data);
                        stringBuilder.append(numberData.toString());
                        if (i != n - 1) {
                            stringBuilder.append(",");
                        }
                    }
                    return stringBuilder.toString();
                } else if (lookupField.getDataTypeEnum() == FieldType.LOOKUP) {
                    if (!NumberUtils.isNumber(data)) {
                        throw new IllegalArgumentException(lookupFieldName + " field value:" + data + " is not number");
                    }
                    Long numberData = (long) Double.parseDouble(data);
                    data = numberData.toString();
                    return data;
                }
            }
            break;
        }
        return data;
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
                    if (isEmpty(cellValue)) {
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
        List<ImportFieldMappingContext> fieldMappingContextList = importSheet != null ? importSheet.getRelationFieldMapping() : Collections.EMPTY_LIST;
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
    private void validateMappedRelRecord(RelationMappingContext relationMappingContext, String sheetColumnName,
                                         Object cellValue,
                                         HashMap<String, Object> props, ImportRowContext rowContext) throws Exception {
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
