package com.facilio.multiImport.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.annotations.RowFunction;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportDataDetails;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.FieldTypeImportRowProcessor;
import com.facilio.multiImport.enums.ImportFieldMappingType;
import com.facilio.multiImport.multiImportExceptions.ImportMandatoryFieldsException;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.LoadLookupHelper;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// TODO check unique field handling behaviour for lookup record fetch
public abstract class BaseMultiImportProcessCommand extends FacilioCommand {
    private static final Logger LOGGER = Logger.getLogger(BaseMultiImportProcessCommand.class.getName());
    Long importId = null;
    ImportDataDetails importDataDetails = null;
    ImportFileSheetsContext importSheet = null;
    Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping = null;
    Map<Long, String> fieldIdVsSheetColumnNameMap = null;
    Map<String, String> fieldNameVsSheetColumnNameMap = null;
    Map<ImportFieldMappingType,List<ImportFieldMappingContext>> typeVsFieldMappings = null;
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
    ModuleBean moduleBean = null;
    LoadLookupHelper lookupHelper = null;
    List<FacilioField> mappedFields = new ArrayList<>();
    List<ImportFieldMappingContext> currentModuleFieldMappings;
    protected abstract List<ImportFieldMappingContext> getCurrentModuleFieldMappings();
    protected abstract String getModuleName() throws Exception;
    Map<String,Object> batchCollectMap = null;
    protected void init(Context context) throws Exception {
        this.context = context;
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importDataDetails = (ImportDataDetails)context.get(FacilioConstants.ContextNames.IMPORT_DATA_DETAILS);
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);

        moduleName = getModuleName();
        module = Constants.getModBean().getModule(moduleName);

        //importSheetFields info
        sheetColumnNameVsFieldMapping = importSheet.getSheetColumnNameVsFieldMapping();
        fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();
        typeVsFieldMappings = importSheet.getTypeVsFieldMappings();

        //FacilioFields info
        fields = MultiImportApi.getImportFields(context, moduleName);
        ImportConstants.setImportFields(context,fields);
        fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(fields);
        fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(fields);

        currentModuleFieldMappings = getCurrentModuleFieldMappings();
        mappedFields = MultiImportApi.getMappedFields(currentModuleFieldMappings,
                fieldIdVsFacilioFieldMap,fieldNameVsFacilioFieldMap);
        ImportConstants.setMappedFields(context,mappedFields);

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
                .isOneLevel(isOneLevel())
                .context(context)
                .moduleName(moduleName)
                .mappedFields(mappedFields)
                .sheetColumnNameVsFieldMapping(sheetColumnNameVsFieldMapping)
                .fieldIdVsSheetColumnNameMap(fieldIdVsSheetColumnNameMap)
                .fieldNameVsSheetColumnNameMap(fieldNameVsSheetColumnNameMap)
                .skipLookupNotFoundExceptionFields(skipLookupNotFoundExceptionFields)
                .build();
        lookupHelper.loadLookupMap(batchRows);

        if(module!=null){
            defaultStateFlow = StateFlowRulesAPI.getDefaultStateFlow(module);
        }
        moduleBean = Constants.getModBean();
    }
    protected HashMap<String, Object> getProcessedRawRecordMap(ImportRowContext rowContext) throws Exception {

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

            for(ImportFieldMappingContext mappingContext : currentModuleFieldMappings){

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

                FieldTypeImportRowProcessor importRowProcessor = FieldTypeImportRowProcessor.getFieldTypeImportRowProcessor(facilioField.getDataTypeEnum());

                importRowProcessor.process(mappingContext,rowContext,facilioField,cellValue,props,lookupHelper);

            }

            if (afterProcessRowFunction != null) {
                afterProcessRowFunction.apply(rowContext, rowVal, props, context);
            }

            processRelationshipData(sheetColumnName,rowVal,props);

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
    protected abstract boolean isOneLevel();
    protected abstract boolean isInsertImport();
    protected Map<String, SiteContext> getSiteMap() throws Exception {
        Set<String> siteNames = (Set<String>) batchCollectMap.get("siteId");

        if (CollectionUtils.isEmpty(siteNames)) {
            return Collections.EMPTY_MAP;
        }
        siteNames = siteNames.stream().map(siteName -> siteName.replace(",", StringOperators.DELIMITED_COMMA))
                .collect(Collectors.toSet());
        List<SiteContext> sites = SpaceAPI.getSitesByNameWithoutScoping(siteNames);

        Map<String, SiteContext> sitesMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity(), (a, b) -> a));
        }
        return sitesMap;
    }
    protected   Map<Long, StateFlowRuleContext> getStateFlows() throws Exception {
        Map<Long,StateFlowRuleContext> stateFlowIdVsStateFlowContext = null;
        Set<Long> stateFlowIds = (Set<Long>) batchCollectMap.get("stateFlowId");

        if(CollectionUtils.isEmpty(stateFlowIds)){
            return Collections.EMPTY_MAP;
        }

        List<StateFlowRuleContext> stateFlowRuleContextList = StateFlowRulesAPI.getStateFlowBaseDetails(new ArrayList<>(stateFlowIds));
        stateFlowIdVsStateFlowContext = stateFlowRuleContextList.stream().collect(Collectors.toMap(StateFlowRuleContext::getId,Function.identity()));

        return stateFlowIdVsStateFlowContext;
    }

    protected void setSiteIdInPropsOnlyForInsertImport(HashMap<String, Object> props, Map<String, Object> rowVal){
        if (!isInsertImport()) {
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

    protected void setFormIdInProps(HashMap<String, Object> props, Map<String, Object> rowVal) throws Exception {
        FacilioField formField = Constants.getModBean().getField("formId", moduleName);


        if (formField != null && MultiImportApi.isFieldMappingPresent(importSheet, formField,isOneLevel())) {
            props.put("formId", rowVal.get(MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, formField,isOneLevel())));
        }
    }
    protected void setDefaultStateFlowIdAndModuleState(HashMap<String, Object> props){
        if (defaultStateFlow != null) {
            props.put(FacilioConstants.ContextNames.STATE_FLOW_ID, defaultStateFlow.getId());
            Map<String,Object> moduleState = new HashMap<>();
            moduleState.put("id", defaultStateFlow.getDefaultStateId());
            props.put(FacilioConstants.ContextNames.MODULE_STATE, moduleState);
        }
    }
    protected  void checkMandatoryFieldsValueExistsOrNot(ImportRowContext rowContext){
        if (CollectionUtils.isEmpty(requiredFields)) {
            return;
        }
        Map<String,Object> rowVal = rowContext.getRawRecordMap();
        ArrayList<String> valueMissingColumns = new ArrayList<>();
        for (FacilioField field : requiredFields) {

            String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap
                    ,fieldNameVsSheetColumnNameMap,field,isOneLevel());

            if (sheetColumnName != null && Objects.isNull(rowVal.get(sheetColumnName))) {  //if object is null means,mark isErrorOccurredRow as a true and save error message in ImportRow context
                valueMissingColumns.add(sheetColumnName);
            }
        }

        if (CollectionUtils.isNotEmpty(valueMissingColumns)) {
            ImportMandatoryFieldsException exception = new ImportMandatoryFieldsException(valueMissingColumns, new Exception());
            String errorMessage = exception.getClientMessage();
            rowContext.setErrorOccurredRow(true);
            rowContext.setErrorMessage(errorMessage);
        }

    }

    // To handle relationship
    protected void processRelationshipData(String sheetColumnName,Map<String,Object> rowVal,
                                           HashMap<String,Object> props) throws Exception{

    }
}
