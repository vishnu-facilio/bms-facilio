package com.facilio.multiImport.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.multiImport.context.ImportFieldMappingContext;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.multiImport.context.LookupIdentifierEnum;
import com.facilio.multiImport.multiImportExceptions.ImportFieldValueMissingException;
import com.facilio.v3.context.Constants;
import lombok.*;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.MySQLCodec;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadLookupHelper {
    private static final Logger LOGGER = Logger.getLogger(LoadLookupHelper.class.getName());
    public static final String VALUES_SEPARATOR = "##";

    Context context;
    String moduleName;
    List<FacilioField> mappedFields ;
    Map<String, ImportFieldMappingContext> sheetColumnNameVsFieldMapping;
    Map<Long, String> fieldIdVsSheetColumnNameMap;
    Map<String, String> fieldNameVsSheetColumnNameMap;
    Map<BaseLookupField, Map<String, Map<String, Object>>> lookupMap;

    @Builder.Default
    Map<BaseLookupField, Set<String>> sameModuleRecordCacheLookupMap = new HashMap<>();
    @Builder.Default
    Set<String> skipLookupNotFoundExceptionFields = new HashSet<>();
    boolean isOneLevel;

    public void loadLookupMap(List<ImportRowContext> rows) throws Exception {
        lookupMap = new HashMap<>();
        for(FacilioField field: mappedFields){

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
            return ;
        }

        for(ImportRowContext rowContext : rows){
            Map<String, Object> rowVal = rowContext.getRawRecordMap();
            for(Map.Entry<BaseLookupField, Map<String, Map<String, Object>>> mapEntry : lookupMap.entrySet()){
                BaseLookupField lookupField = mapEntry.getKey();
                Map<String, Map<String, Object>> numberIdPairList = mapEntry.getValue();

                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,fieldNameVsSheetColumnNameMap,lookupField,isOneLevel);

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
    }
    public void refreshLookupMap() throws Exception {
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
    public String getLookUKeyValueFromSheet(BaseLookupField parentLookUpField, List<FacilioField> uniqueFields, ImportRowContext rowContext, boolean validate) throws ImportFieldValueMissingException {
        StringBuilder keyValue = new StringBuilder();
        Set<String> canBeEmptyFieldNames = getCanBeEmptyFieldNames(parentLookUpField);
        if(uniqueFields.size()==1){
            return keyValue.toString();
        }

        Map<String, Object> rowVal = rowContext.getRawRecordMap();

        for (int i = 1; i < uniqueFields.size(); i++) {
            FacilioField uniqueField = uniqueFields.get(i);
            String uniqueFieldName = uniqueField.getName();
            String uniqueFieldSheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,
                    fieldNameVsSheetColumnNameMap,uniqueField,isOneLevel);

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
            // TODO removed after parsing cell data based on facilio field type in sheet reader
            if(uniqueField.getDataTypeEnum()== FieldType.NUMBER && NumberUtils.isNumber(uniqueCellStringValue)){
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
                    set = encodeAndAppendSingleQuotesToString(set);
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

    public List<FacilioField> getImportLookupUniqueFields(BaseLookupField lookupField) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        String lookupFieldName = lookupField.getName();
        String lookupModuleName = lookupField.getLookupModule().getName();


        String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,fieldNameVsSheetColumnNameMap,lookupField,isOneLevel);
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
                break;
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
    private static Map<String, Object> getSpecialLookupProps(BaseLookupField lookupField, Object value, String moduleName) throws Exception {
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
    private Set<String> encodeAndAppendSingleQuotesToString(Set<String> set){
        if(CollectionUtils.isEmpty(set)){
            return set;
        }
        return set.stream().map(s->{
                   s = s.replace(StringOperators.DELIMITED_COMMA,",");
                   return "\'"+ ESAPI.encoder().encodeForSQL(new MySQLCodec(MySQLCodec.Mode.STANDARD), s)+ "\'";

                }
               ).collect(Collectors.toSet());
    }
    public String getLookupIdentifierData(BaseLookupField lookupField, String data) throws Exception{
        String lookupFieldName = lookupField.getName();
        String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,
                fieldNameVsSheetColumnNameMap,lookupField,isOneLevel);
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
    private void removeRecordFromSameModuleRecordCache(ImportRowContext rowContext) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for(BaseLookupField lookupField : sameModuleRecordCacheLookupMap.keySet()){
            List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
            try{
                FacilioField primaryField = uniqueFields.get(0);
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,
                        fieldNameVsSheetColumnNameMap,primaryField,isOneLevel);

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
    public void addRecordToSameModuleRecordCache(ImportRowContext rowContext) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for(BaseLookupField lookupField : sameModuleRecordCacheLookupMap.keySet()){
            List<FacilioField> uniqueFields = getImportLookupUniqueFields(lookupField);
            try{
                FacilioField firstField = uniqueFields.get(0);
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,
                        fieldNameVsSheetColumnNameMap,firstField,isOneLevel);
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
    public boolean skipIfSameModuleLookupRecordInSheet(ImportRowContext rowContext){
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return false;
        }

        Map<String, Object> rowVal = rowContext.getRawRecordMap(); // un processed prop
        for (BaseLookupField baseLookupField : sameModuleRecordCacheLookupMap.keySet()){
            try{
                String sheetColumnName = MultiImportApi.getSheetColumnNameFromFacilioField(fieldIdVsSheetColumnNameMap,
                        fieldNameVsSheetColumnNameMap,baseLookupField,isOneLevel);

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
    public void clearErrorRecordInCache(List<ImportRowContext> recordsToBeAdded) throws Exception {
        if(MapUtils.isEmpty(sameModuleRecordCacheLookupMap)){
            return;
        }
        for(ImportRowContext rowContext : recordsToBeAdded){
            removeRecordFromSameModuleRecordCache(rowContext);
        }
    }
    public static Map<Long, ? extends Object> getLookupProps(LookupField field, Collection<Long> ids) throws Exception {
        if(CollectionUtils.isNotEmpty(ids)) {
            if(LookupSpecialTypeUtil.isSpecialType(field.getSpecialType())) {
                return LookupSpecialTypeUtil.getRecordsAsMap(field.getSpecialType(), ids);
            }
            else {
                Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(field.getLookupModule(), false);
                if(moduleClass != null) {
                    FacilioModule module = field.getLookupModule();
                    SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>()
                            .module(module)
                            .beanClass(moduleClass)
                            .andCondition(CriteriaAPI.getIdCondition(ids, module))
                            .fetchDeleted()
                            .skipModuleCriteria()
                            .skipScopeCriteria()
                            .skipPermission();


                    List<FacilioField> selectFields = new ArrayList<>();
                    FacilioField primaryField = Constants.getModBean().getPrimaryField(field.getLookupModule().getName());
                    FacilioField idField = FieldFactory.getIdField(field.getLookupModule());
                    selectFields.add(primaryField);
                    selectFields.add(idField);

                    lookupBeanBuilder.select(selectFields);

                    return lookupBeanBuilder.getAsMap();

                }
                else {
                    throw new IllegalArgumentException("Unknown Module Name while fetching look props "+field.getLookupModule().getName());
                }
            }
        }
        return null;
    }

}
