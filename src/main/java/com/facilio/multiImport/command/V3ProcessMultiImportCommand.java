package com.facilio.multiImport.command;


import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.*;
import com.facilio.multiImport.annotations.RowFunction;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportFileSheetsContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.enums.MultiImportSetting;
import com.facilio.multiImport.multiImportExceptions.ImportFieldValueMissingException;
import com.facilio.multiImport.multiImportExceptions.ImportLookupModuleValueNotFoundException;
import com.facilio.multiImport.multiImportExceptions.ImportMandatoryFieldsException;
import com.facilio.multiImport.multiImportExceptions.ImportParseException;
import com.facilio.multiImport.util.MultiImportApi;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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
    private static final String VALUES_SEPERATOR = "##";
    int chunkLimit = 0;
    Long importId = null;
    ImportFileSheetsContext importSheet = null;
    Long importSheetId = null;
    Long lastRowIdTaken = null;

    Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping = null;
    Map<Long, String> fieldIdVsSheetColumnNameMap = null;
    Map<String, String> fieldNameVsSheetColumnNameMap = null;
    List<FacilioField> fields = null;
    Map<String, FacilioField> fieldNameVsFacilioFieldMap = null;
    Map<Long, FacilioField> fieldIdVsFacilioFieldMap = null;
    ArrayList<FacilioField> requiredFields = null;
    Map<String, SiteContext> sitesMap = null;

    String moduleName = null;
    Map<BaseLookupField, Map<String, Object>> lookupMap = null;
    RowFunction beforeProcessRowFunction = null;
    RowFunction afterProcessRowFunction = null;

    Context context = null;

    List<ImportRowContext> batchRows = null;
    @Override
    public boolean executeCommand(Context context) throws Exception {

        init(context);

        for (ImportRowContext rowContext : batchRows) {

            validateRow(rowContext);

            HashMap<String, Object> processedProps = getProcessedRawRecordMap(rowContext);

            rowContext.setProcessedRawRecordMap(processedProps);
        }

        ImportConstants.setRowContextList(context, batchRows);
        return false;
    }

    private void init(Context context) throws Exception {
        this.context = context;
        importId = (Long) context.get(FacilioConstants.ContextNames.IMPORT_ID);
        importSheet = (ImportFileSheetsContext) context.get(FacilioConstants.ContextNames.IMPORT_SHEET);

        moduleName = importSheet.getModuleName();
        importSheetId = importSheet.getId();
        lastRowIdTaken = importSheet.getLastRowIdTaken();

        //importSheetFields info
        sheetColumnNameVsFieldMapping = importSheet.getSheetColumnNameVsFieldMapping();
        fieldIdVsSheetColumnNameMap = importSheet.getFieldIdVsSheetColumnNameMap();
        fieldNameVsSheetColumnNameMap = importSheet.getFieldNameVsSheetColumnNameMap();

        //FacilioFields info
        fields = getFields(context, importSheet.getModuleName());
        fieldNameVsFacilioFieldMap = FieldFactory.getAsMap(fields);
        fieldIdVsFacilioFieldMap = FieldFactory.getAsIdMap(fields);

        beforeProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.BEFORE_PROCESS_ROW_FUNCTION);
        afterProcessRowFunction = (RowFunction) context.get(MultiImportApi.ImportProcessConstants.AFTER_PROCESS_ROW_FUNCTION);

        chunkLimit = (int) context.get(FacilioConstants.ContextNames.CHUNK_LIMIT);
        batchRows = MultiImportApi.getRowsByBatch(importSheetId, lastRowIdTaken, chunkLimit);

        requiredFields = (ArrayList<FacilioField>) context.get(ImportAPI.ImportProcessConstants.REQUIRED_FIELDS);
        if (CollectionUtils.isEmpty(requiredFields)) {
            requiredFields = getRequiredFields(moduleName);
        }

        if (isSiteIdFieldPresent()) {
            sitesMap = getSiteMap();
        }
        lookupMap = loadLookupMap(batchRows, context, moduleName);
    }

    private void validateRow(ImportRowContext rowContext) throws Exception {
        Map<String, Object> rowVal = rowContext.getRawRecordMap();
        long rowNo = rowContext.getRowNumber();

        if (MultiImportApi.isInsertImportSheet(importSheet)) {
            checkMandatoryFieldsValueExistsOrNot(requiredFields, importSheet, rowVal, rowNo, rowContext);
        }

        if (importSheet.getImportSetting() != MultiImportSetting.INSERT.getValue()) {
            checkImportSettingFieldValueExistOrNot(importSheet, rowVal, sheetColumnNameVsFieldMapping, rowNo, rowContext);
        }
    }


    private HashMap<String, Object> getProcessedRawRecordMap(ImportRowContext rowContext) throws Exception {

        HashMap<String, Object> props = new LinkedHashMap<>();   // processed prop
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        long rowNo = rowContext.getRowNumber();

        LOGGER.info("row -- " + rowNo + " rowVal --- " + rowVal);

        // adding source_type and source_id in the props
        props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
        props.put(FacilioConstants.ContextNames.SOURCE_ID, importId);

        // add formId in props if field mapping present for formId
        setFormIdInProps(props, rowVal);

        // add site only for insert
        setSiteIdInPropsOnlyForInsertImport(props, rowVal);

        if (beforeProcessRowFunction != null) {
            beforeProcessRowFunction.apply(rowNo, rowVal, props, context);
        }


        for (FacilioField field : fields) {

            if (!MultiImportApi.isFieldMappingPresent(importSheet, field)) { // there is no mapping for this field. Don't do anything
                continue;
            }

            String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, field);

            Object cellValue = rowVal.get(sheetColumnName);
            if (isEmpty(cellValue)) {
                // The value of row is empty. Set it as null. // todo check this flow
                props.put(field.getName(), null);
                continue;
            }

            try {
                if (field.getName().equals(FacilioConstants.ContextNames.SITE_ID)) {
                    String cellValueString = cellValue.toString();
                    if (StringUtils.isNotEmpty(cellValueString) && MapUtils.isNotEmpty(sitesMap)) {
                        SiteContext site = sitesMap.get(cellValueString);
                        if (site != null && site.getId() > 0) {
                            props.put(field.getName(), site.getId());
                        }
                    }
                    continue;
                }

                formatCellValueBasedOnFieldType(field,rowNo,sheetColumnName,cellValue,props,rowContext);

            } catch (Exception ex) {
                LOGGER.severe("Process Import Exception -- Row No --" + rowNo + " Fields Mapping --" + sheetColumnName);
                String errorMessage =null;

                if( ex instanceof ImportLookupModuleValueNotFoundException){
                    errorMessage = ((ImportLookupModuleValueNotFoundException)ex).getClientMessage();
                }
                else{
                    ImportParseException parseException = new ImportParseException((int) rowNo, sheetColumnName, ex);
                    errorMessage = parseException.getClientMessage();
                }

                rowContext.setErrorOccuredRow(true);
                rowContext.setErrorMessage(errorMessage);
                LOGGER.severe("Reason for failed :" + errorMessage);
            }

        }

        if (afterProcessRowFunction != null) {
            afterProcessRowFunction.apply(rowNo, rowVal, props, context);
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
                        throw new ImportParseException((int) rowNo, sheetColumnName, new Exception("date format cannot be null"));
                    }
                    if (dateFormat.equals(MultiImportApi.ImportProcessConstants.TIME_STAMP_STRING)) {
                        millis = Long.parseLong(cellValue.toString());
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
                String multiLookupValue = cellValue.toString();
                String[] split = multiLookupValue.split(",");
                List<Object> values = new ArrayList<>();
                String lookUpModuleName = ((BaseLookupField) field).getLookupModule().getName();
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(Constants.getModBean(), context, lookUpModuleName);
                Map<String, Object> nameVsIds = lookupMap.get(field);
                for (String s : split) {
                    String name = s.trim();
                    String lookUpValueKey = getLookUKeyValueFromSheet(uniqueFields, fieldIdVsSheetColumnNameMap, fieldNameVsSheetColumnNameMap, rowContext, true);

                    if (!lookUpValueKey.isEmpty()) {
                        name = name + VALUES_SEPERATOR + lookUpValueKey;
                    }
                    Object value = nameVsIds.get(name);

                    if(value == null){
                        throw new ImportLookupModuleValueNotFoundException(lookUpModuleName,(int)rowNo,sheetColumnName,null);
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
                Map<String, Object> nameVsIds = lookupMap.get(field);
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(Constants.getModBean(), context, ((BaseLookupField) field).getLookupModule().getName());
                String lookUpValueKey = getLookUKeyValueFromSheet(uniqueFields, fieldIdVsSheetColumnNameMap, fieldNameVsSheetColumnNameMap, rowContext, true);
                String name = cellValue.toString().trim();
                if (!lookUpValueKey.isEmpty()) {
                    name = name + VALUES_SEPERATOR + lookUpValueKey;
                }
                Object value = nameVsIds.get(name);

                if(value == null){
                    throw new ImportLookupModuleValueNotFoundException(lookUpModuleName,(int)rowNo,sheetColumnName,null);
                }

                props.put(field.getName(), value);
                break;
            }
            case NUMBER:
            case DECIMAL: {
                String cellValueString = cellValue.toString();
                if (cellValueString.contains(",")) {
                    cellValueString = cellValueString.replaceAll(",", "");
                }

                Double cellDoubleValue = Double.parseDouble(cellValueString);
                if (!props.containsKey(field.getName())) {
                    props.put(field.getName(), cellDoubleValue);
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
    private String getLookUKeyValueFromSheet(List<FacilioField> uniqueFields, Map<Long, String> fieldIdVsSheetColumnNameMap, Map<String, String> fieldNameVsSheetColumnNameMap, ImportRowContext rowContext, boolean validate) throws ImportFieldValueMissingException {
        StringBuilder keyValue = new StringBuilder();

        if(uniqueFields.size()==1){
            return keyValue.toString();
        }

        Map<String, Object> rowVal = rowContext.getRawRecordMap();

        for (int i = 1; i < uniqueFields.size(); i++) {
            FacilioField uniqueField = uniqueFields.get(i);
            Long uniqueFieldId = uniqueField.getFieldId();
            String uniqueFieldName = uniqueField.getName();
            String uniqueFieldSheetColumnName = uniqueFieldId != -1l ? fieldIdVsSheetColumnNameMap.get(uniqueFieldId) : fieldNameVsSheetColumnNameMap.get(uniqueFieldName);
            Object uniqueCellValue = rowVal.get(uniqueFieldSheetColumnName);
            if (isEmpty(uniqueCellValue)) {
                uniqueCellValue = "null";
                if (validate) {
                    throw new ImportFieldValueMissingException((int) rowContext.getRowNumber(), uniqueFieldSheetColumnName, new Exception());
                }
            }
            String uniqueCellStringValue = uniqueCellValue.toString();

            keyValue.append(uniqueCellStringValue.trim());
            if (i == uniqueFields.size() - 1) {
                break;
            }
            keyValue.append(VALUES_SEPERATOR);
        }
        return keyValue.toString();
    }

    private Map<BaseLookupField, Map<String, Object>> loadLookupMap(List<ImportRowContext> allRows, Context context, String moduleName) throws Exception {
        Map<BaseLookupField, Map<String, Object>> lookupMap = new HashMap<>();

        List<FacilioField> fields = getFields(context, moduleName);
        for(FacilioField field: fields){
            Long fieldId = field.getFieldId();
            String fieldName = field.getName();
            if (!fieldIdVsSheetColumnNameMap.containsKey(fieldId) && !fieldNameVsSheetColumnNameMap.containsKey(fieldName)) {
                // there is no mapping for this field. Don't do anything
                continue;
            }
            if (field.getDataTypeEnum() == FieldType.LOOKUP || field.getDataTypeEnum() == FieldType.MULTI_LOOKUP) {
                BaseLookupField lookupField = (BaseLookupField) field;
                Map<String, Object> numberIdPairList = new HashMap<>();
                lookupMap.put(lookupField, numberIdPairList);
            }
        }

        if(MapUtils.isEmpty(lookupMap)){
            return lookupMap;
        }

        for(ImportRowContext rowContext : allRows){
            Map<String, Object> rowVal = rowContext.getRawRecordMap();
            for(Map.Entry<BaseLookupField,Map<String,Object>> mapEntry : lookupMap.entrySet()){
                BaseLookupField lookupField = mapEntry.getKey();
                Map<String, Object> numberIdPairList = mapEntry.getValue();

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

                List<FacilioField> uniqueFields = getImportLookupUniqueFields(Constants.getModBean(), context, lookupField.getLookupModule().getName());
                String keyValue = getLookUKeyValueFromSheet(uniqueFields, fieldIdVsSheetColumnNameMap, fieldNameVsSheetColumnNameMap, rowContext, false);

                lookupMap.put(lookupField, numberIdPairList);

                for (String value : values) {
                    if (!keyValue.isEmpty()) {
                        value = value + VALUES_SEPERATOR + keyValue;
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
            Map<String, Object> nameIdMap = lookupMap.get(lookupField);
            if (LookupSpecialTypeUtil.isSpecialType(lookupModuleName)) {
                for (String name : nameIdMap.keySet()) {
                    Map<String, Object> propValue = getSpecialLookupProps(lookupField, name, lookupModuleName);
                    nameIdMap.put(name, propValue);
                }
            } else {
                List<FacilioField> uniqueFields = getImportLookupUniqueFields(Constants.getModBean(), context, lookupField.getLookupModule().getName());
                SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = getSelectBuilderForLoadLookUpMap(lookupField, nameIdMap, uniqueFields);

                if(selectBuilder == null){
                    return lookupMap;
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

    private String getUniqueKeyForLookUp(List<FacilioField> uniqueFields, Map<String, Object> prop) {
        StringBuilder uniqueKey = new StringBuilder();
        for (int i = 0; i < uniqueFields.size(); i++) {
            FacilioField field = uniqueFields.get(i);
            String fieldName = field.getName();
            String value = prop.get(fieldName).toString();
            if (FacilioUtil.isNumeric(value)) {
                value = ((Double) FacilioUtil.parseDouble(value)).toString();
            }
            uniqueKey.append(value);
            if (i == uniqueFields.size() - 1) {
                break;
            }
            uniqueKey.append(VALUES_SEPERATOR);

        }
        return uniqueKey.toString();
    }

    private SelectRecordsBuilder<ModuleBaseWithCustomFields> getSelectBuilderForLoadLookUpMap(BaseLookupField lookupField, Map<String, Object> nameIdMap, List<FacilioField> uniqueFields) throws Exception {
        List<FacilioField> fieldsList = new ArrayList<>();
        fieldsList.add(FieldFactory.getIdField(lookupField.getLookupModule()));
        fieldsList.addAll(uniqueFields);

        FacilioField primaryField = uniqueFields.get(0);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
                .module(lookupField.getLookupModule())
                .select(fieldsList);

        if (lookupField.getName().equals("moduleState")) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(lookupField.getModule().getModuleId()), NumberOperators.EQUALS));
        } else if (lookupField.getName().equals("approvalStatus")) {
            selectBuilder.andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", null, CommonOperators.IS_EMPTY));
        }

        Set<String> set = new HashSet<>();

        for (String uniqueValues : nameIdMap.keySet()) {
            String name = uniqueValues.split(VALUES_SEPERATOR)[0];
            set.add(name.replace(",", StringOperators.DELIMITED_COMMA));
        }

        if(CollectionUtils.isEmpty(set)){
           return null;
        }

        selectBuilder.andCondition(CriteriaAPI.getCondition(primaryField, StringUtils.join(set, ","), StringOperators.IS));

        Criteria uniqueFieldsCriteria = getCriteriaForLoadLookUpMap(uniqueFields, nameIdMap);
        if (!uniqueFieldsCriteria.isEmpty()) {
            selectBuilder.andCriteria(uniqueFieldsCriteria);
        }
        return selectBuilder;
    }

    private Criteria getCriteriaForLoadLookUpMap(List<FacilioField> uniqueFields, Map<String, Object> nameIdMap) {

        Criteria criteria = new Criteria();
        for (int i = 1; i < uniqueFields.size(); i++) {

            Condition condition = new Condition();
            Set<String> set = new HashSet<>();

            FacilioField field = uniqueFields.get(i);
            condition.setField(field);
            condition.setOperator(StringOperators.IS);


            for (String uniqueValues : nameIdMap.keySet()) {
                String uniqueValue = uniqueValues.split(VALUES_SEPERATOR)[i];

                if (uniqueValue.equals("null")) {
                    continue;
                }
                set.add(uniqueValue);
            }
            condition.setValue(StringUtils.join(set, ","));
            criteria.addAndCondition(condition);

        }
        return criteria;
    }

    private List<FacilioField> getImportLookupUniqueFields(ModuleBean modBean, Context context, String moduleName) throws Exception {
        Map<String, List<String>> lookupMainFieldMap = (Map<String, List<String>>) context.get(MultiImportApi.ImportProcessConstants.LOOKUP_UNIQUE_FIELDS_MAP);
        List<FacilioField> uniqueFields = new ArrayList<>();
        if (MapUtils.isNotEmpty(lookupMainFieldMap) && lookupMainFieldMap.containsKey(moduleName)) {
            List<String> fieldNames = lookupMainFieldMap.get(moduleName);
            for (String fieldName : fieldNames) {
                FacilioField uniqueField = modBean.getField(fieldName, moduleName);
                uniqueFields.add(uniqueField);
            }
        }
        if (CollectionUtils.isEmpty(uniqueFields)) {   //if uniqueFields not configured by module owners ,take lookup module's primary field as a unique field
            FacilioField primaryField = modBean.getPrimaryField(moduleName);
            uniqueFields.add(primaryField);
        }
        return uniqueFields;
    }

    private boolean isEmpty(Object cellValue) {
        if (cellValue == null || cellValue.toString().equals("") || (cellValue.toString().equals("n/a"))) {
            return true;
        }
        return false;
    }

    private List<FacilioField> getFields(Context context, String moduleName) throws Exception {

        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        if (CollectionUtils.isEmpty(fields)) {
            fields = Constants.getModBean().getAllFields(moduleName);
        }
        if (CollectionUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("Fields not found for module " + moduleName);
        }
        return fields;
    }

    private ArrayList<FacilioField> getRequiredFields(String moduleName) throws Exception {
        ArrayList<FacilioField> fields = new ArrayList<FacilioField>();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        for (FacilioField field : allFields) {
            if (field.isRequired()) {
                fields.add(field);
            }
        }
        return fields;
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

    private void checkMandatoryFieldsValueExistsOrNot(List<FacilioField> mandatoryFields, ImportFileSheetsContext importSheet, Map<String, Object> rowVal, long rowNo, ImportRowContext rowContext) throws ImportMandatoryFieldsException {
        if (CollectionUtils.isNotEmpty(mandatoryFields)) {
            ArrayList<String> valueMissingColumns = new ArrayList<>();
            for (FacilioField field : mandatoryFields) {

                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet,field);

                if (sheetColumnName != null && Objects.isNull(rowVal.get(sheetColumnName))) {  //if object is null means,mark isErrorOccuredRow as a true and save error message in ImportRow context
                    valueMissingColumns.add(sheetColumnName);
                }
            }

            if (CollectionUtils.isNotEmpty(valueMissingColumns)) {
                ImportFieldValueMissingException exception = new ImportFieldValueMissingException((int) rowNo, valueMissingColumns.toString(), new Exception());
                String errorMessage = exception.getClientMessage();
                rowContext.setErrorOccuredRow(true);
                rowContext.setErrorMessage(errorMessage);
                LOGGER.severe(errorMessage);
            }

        }
    }

    private void checkImportSettingFieldValueExistOrNot(ImportFileSheetsContext importSheet, Map<String, Object> rowVal, Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping, long rowNo, ImportRowContext rowContext) throws Exception {
        List<String> sheetColumnNames = null;
        if (importSheet.getImportSetting() == MultiImportSetting.INSERT_SKIP.getValue()) {
            sheetColumnNames = importSheet.getInsertByFieldsList();

        } else {
            sheetColumnNames = importSheet.getUpdateByFieldsList();
        }

        if (CollectionUtils.isNotEmpty(sheetColumnNames)) {
            for (String columnName : sheetColumnNames) {
                if (Objects.isNull(rowVal.get(columnName))) {  //if object is null means,mark isErrorOccuredRow as a true and save error message in ImportRow context
                    ImportFieldValueMissingException exception = new ImportFieldValueMissingException((int) rowNo, columnName, new Exception());
                    String errorMessage = exception.getClientMessage();
                    rowContext.setErrorOccuredRow(true);
                    rowContext.setErrorMessage(errorMessage);
                    LOGGER.severe(errorMessage);
                }
            }
        }
    }

    private Map<String, SiteContext> getSiteMap() throws Exception {
        Set<String> siteNames = getAllRecordSiteNames();

        if (CollectionUtils.isEmpty(siteNames)) {
            return Collections.EMPTY_MAP;
        }

        List<SiteContext> sites = SpaceAPI.getSitesByNameWithoutScoping(siteNames);

        Map<String, SiteContext> sitesMap = null;
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity(), (a, b) -> a));
        }
        return sitesMap;
    }

    public boolean isSiteIdFieldPresent() throws Exception {
        FacilioField siteIdField = Constants.getModBean().getField("siteId", moduleName);

        if (siteIdField == null) {
            return false;
        }

        String siteSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(importSheet, siteIdField);

        if (siteSheetColumnName == null) {
            return false;
        }

        return true;
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

    private void setSiteIdInPropsOnlyForInsertImport(HashMap<String, Object> props, Map<String, Object> rowVal) {
        if (!MultiImportApi.isInsertImportSheet(importSheet)) {
            return;
        }
        if (MapUtils.isEmpty(sitesMap)) {
            return;
        }
        if (rowVal.containsKey(fieldNameVsSheetColumnNameMap.get("siteId"))) {
            Object cellValue = rowVal.get(fieldNameVsSheetColumnNameMap.get("siteId"));
            if (!isEmpty(cellValue)) {
                String siteName = cellValue.toString();
                SiteContext siteContext = sitesMap.get(siteName.trim());
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

}

