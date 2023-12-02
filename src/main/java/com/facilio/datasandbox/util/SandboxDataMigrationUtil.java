package com.facilio.datasandbox.util;

import com.facilio.beans.ModuleBean;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;
import com.facilio.v3.context.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class SandboxDataMigrationUtil {
    private static void moduleSpecialHandlingDuringExport(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        if (module.getName().equals(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
            FacilioField parentCommentModuleNameField = FieldFactory.getStringField("parentCommentModuleName", null, module);
            fields.add(parentCommentModuleNameField);

            List<Long> commentModuleIdList = propsList.stream().filter(prop -> prop.containsKey("commentModuleId")).map(prop -> (long) prop.get("commentModuleId")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(commentModuleIdList)) {
                Criteria moduleIdCriteria = new Criteria();
                FacilioField moduleIdField = FieldFactory.getModuleIdField(ModuleFactory.getModuleModule());
                moduleIdCriteria.addAndCondition(CriteriaAPI.getCondition(moduleIdField, commentModuleIdList, NumberOperators.EQUALS));

                List<FacilioModule> moduleList = modBean.getModuleList(moduleIdCriteria);
                Map<Long, String> moduleIdVsModuleName = moduleList.stream().collect(Collectors.toMap(FacilioModule::getModuleId, FacilioModule::getName));

                for (Map<String, Object> prop : propsList) {
                    long commentModuleId = prop.containsKey("commentModuleId") ? (long) prop.get("commentModuleId") : -1;
                    String commentModuleName = moduleIdVsModuleName.get(commentModuleId);

                    prop.put("parentCommentModuleName", commentModuleName);
                }
            }
        }
    }

    public static File exportDataAsCSVFile(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList,
                                           List<String> fileFieldNames, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                           Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookups) throws Exception {

        if (org.apache.commons.collections4.CollectionUtils.isEmpty(fields) || org.apache.commons.collections4.CollectionUtils.isEmpty(propsList)) {
            return null;
        }

        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        File csvFile = DataPackageFileUtil.createTempFileForModule(module.getName());
        String filePath = csvFile.getPath();

        // add system fields
        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.SITE_ID)) {
            FacilioField siteIdField = FieldFactory.getSiteIdField(module);
            fields.add(0, siteIdField);
        }

        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.FORM_ID)) {
            FacilioField formIdField = FieldFactory.getNumberField(FacilioConstants.ContextNames.FORM_ID, null, module);
            fields.add(0, formIdField);
        }

        if (!fieldsMap.containsKey(FacilioConstants.ContextNames.STATE_FLOW_ID)) {
            FacilioField stateFlowIdFields = FieldFactory.getNumberField(FacilioConstants.ContextNames.STATE_FLOW_ID, null, module);
            fields.add(0, stateFlowIdFields);
        }

        fields.add(0, FieldFactory.getIdField(module));

        // special handling
        moduleSpecialHandlingDuringExport(module, fields, propsList);

        try (FileWriter writer = new FileWriter(filePath, false)) {
            // add header row
            StringBuilder str = new StringBuilder();
            for (FacilioField field : fields) {
                String sheetColumnName = field.getName();
                str.append(PackageFileUtil.escapeCsv(sheetColumnName));
                str.append(',');
            }
            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');

            // add record data
            addRecordData(writer, fields, propsList, fileFieldNames, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookups);
        }

        return csvFile;
    }

    public static void updateDataInCSVFile(File moduleCsvFile, List<FacilioField> fields, List<Map<String, Object>> propsList,
                                           List<String> fileFieldNames, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                           Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookups) throws Exception {

        if (org.apache.commons.collections.CollectionUtils.isEmpty(fields) || org.apache.commons.collections.CollectionUtils.isEmpty(propsList)) {
            return;
        }

        String filePath = moduleCsvFile.getPath();

        try (FileWriter writer = new FileWriter(filePath, true)) {
            // add record data
            addRecordData(writer, fields, propsList, fileFieldNames, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookups);
        }
    }

    public static void addRecordData(FileWriter writer, List<FacilioField> fields, List<Map<String, Object>> propsList, List<String> fileFieldNames,
                                     boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                     Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookupDetails) throws Exception {

        ModuleBean modBean = Constants.getModBean();
        for (Map<String, Object> record : propsList) {
            StringBuilder str = new StringBuilder();
            for (FacilioField field : fields) {
                String fieldName = field.getName();
                // "Id" is appended with fileFields fieldName
                fieldName = (field instanceof FileField) ? fieldName + "Id" : fieldName;
                Object value = record.get(fieldName);

                if (value != null) {
                    if (field instanceof NumberField && Objects.equals(field.getName(), "siteId")) {
                        if (getDependantModuleData) {
                            Long recordId = (Long) value;
                            FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
                            handleFetchedAndTobeFetchedRecords(siteModule, Collections.singletonList(recordId), fetchedRecords, toBeFetchRecords);
                        }
                    } else if (numberLookupDetails.containsKey(fieldName)) {
                        if (getDependantModuleData) {
                            Map<String, Object> fieldLookupModuleDetails = numberLookupDetails.get(fieldName);
                            String lookupModuleName = (String) fieldLookupModuleDetails.get("lookupModuleName");

                            Long recordId = (Long) value;
                            FacilioModule lookupModule = modBean.getModule(lookupModuleName);
                            handleFetchedAndTobeFetchedRecords(lookupModule, Collections.singletonList(recordId), fetchedRecords, toBeFetchRecords);
                        }
                    } else if (field instanceof FileField || (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fileFieldNames) && fileFieldNames.contains(fieldName))) {
                        long fileId = (long) value;
                        long recordId = (long) record.get("id");
                        value = DataPackageFileUtil.exportFileFieldData(recordId, fileId, null);
                    } else {
                        value = getDataForField(field, value, getDependantModuleData, fetchedRecords, toBeFetchRecords);
                    }

                    if (value != null) {
                        str.append(PackageFileUtil.escapeCsv(value.toString()));
                    }
                }
                str.append(',');
            }
            writer.append(StringUtils.stripEnd(str.toString(), ","));
            writer.append('\n');
        }
        writer.flush();
        writer.close();
    }

    private static String getDataForField(FacilioField field, Object value, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords, Map<String, List<Long>> toBeFetchRecords) {
        Object result = null;
        FacilioModule lookupModule;
        switch (field.getDataTypeEnum()) {
            case LOOKUP:
                Map<String, Object> fieldValue = (Map<String, Object>) value;
                result = fieldValue.get("id");

                if (getDependantModuleData) {
                    long recordId = Long.parseLong(String.valueOf(result));
                    lookupModule = ((LookupField) field).getLookupModule();
                    handleFetchedAndTobeFetchedRecords(lookupModule, Collections.singletonList(recordId), fetchedRecords, toBeFetchRecords);
                }
                break;

            case MULTI_LOOKUP:
                List<Long> recordIdsList = new ArrayList<>();
                StringJoiner multiLookupBuilder = new StringJoiner(",");
                List<Map<String, Object>> multiLookupValues = (List<Map<String, Object>>) value;
                for (Map<String, Object> multiLookupValue : multiLookupValues) {
                    Long lookupValue = (Long) multiLookupValue.get("id");
                    multiLookupBuilder.add(String.valueOf(lookupValue));
                    recordIdsList.add(lookupValue);
                }
                result = multiLookupBuilder.toString();

                if (getDependantModuleData) {
                    lookupModule = ((MultiLookupField) field).getLookupModule();
                    handleFetchedAndTobeFetchedRecords(lookupModule, recordIdsList, fetchedRecords, toBeFetchRecords);
                }
                break;

            case MULTI_ENUM:
                StringJoiner multiEnumBuilder = new StringJoiner(",");
                List<Integer> multiEnumValues = (List<Integer>) value;
                for (Integer multiEnumValue : multiEnumValues) {
                    multiEnumBuilder.add(multiEnumValue.toString());
                }
                result = multiEnumBuilder.toString();
                break;

            case URL_FIELD:
                StringJoiner urlBuilder = new StringJoiner(",");
                Map<String, Object> urlValue = (Map<String, Object>) value;
                urlBuilder.add(urlValue.get("href").toString());
                if (urlValue.get("name") != null && urlValue.get("target") != null) {
                    urlBuilder.add(urlValue.get("target").toString());
                    urlBuilder.add(urlValue.get("name").toString());
                }
                result = urlBuilder.toString();
                break;

            default:
                result = value.toString();
                break;
        }
        return result.toString();
    }

    private static void handleFetchedAndTobeFetchedRecords(FacilioModule lookupModule, List<Long> recordIds, Map<String, List<Long>> fetchedRecords, Map<String, List<Long>> toBeFetchRecords) {
        String lookupModuleName = lookupModule.getName();
        if (PackageUtil.nameVsComponentType.containsKey(lookupModuleName)) {
            return;
        }

        List<Long> fetchedRecordIds = fetchedRecords.computeIfAbsent(lookupModuleName, k -> new ArrayList<>());
        List<Long> toBeFetchedRecordIds = toBeFetchRecords.computeIfAbsent(lookupModuleName, k -> new ArrayList<>());

        for (long recordId : recordIds) {
            if (!fetchedRecordIds.contains(recordId)) {
                if (!toBeFetchedRecordIds.contains(recordId)) {
                    toBeFetchedRecordIds.add(recordId);
                }
            }
        }
    }

    public static List<Map<String, Object>> getDataFromCSV(String moduleName, String moduleFileName, Map<String, FacilioField> fieldsMap, int offset, int limit) throws Exception {
        List<Map<String, String>> fieldNameVsValueList = parseCSVAndGetData(moduleName, moduleFileName, offset, limit);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fieldNameVsValueList)) {
            List<Map<String, Object>> propsList = convertCSVDataToProps(fieldsMap, fieldNameVsValueList);
            // special handling
            moduleSpecialHandlingDuringImport(Constants.getModBean().getModule(moduleName), new ArrayList<>(fieldsMap.values()), propsList);
            return propsList;
        }
        return null;
    }

    private static List<Map<String, String>> parseCSVAndGetData(String moduleName, String moduleFileName, int offset, int limit) throws Exception {
        InputStream moduleCSVStream = DataPackageFileUtil.getModuleCSVStream(moduleFileName);
        if (moduleCSVStream == null) {
            LOGGER.info("Data Migration - Insert - CSV InputStream is null for ModuleName - " + moduleName);
            return null;
        }

        List<Map<String, String>> fieldNameVsValueList = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(moduleCSVStream, StandardCharsets.UTF_8))) {
            List<String[]> csvData = csvReader.readAll();
            String[] fieldNames = csvData.get(0);
            for (int i = offset + 1; i < limit + 1; i++) {
                String[] fieldValues = csvData.get(i);

                Map<String, String> fieldNameVsValueMap = getFieldNameVsValueMap(fieldNames, fieldValues);
                fieldNameVsValueList.add(fieldNameVsValueMap);
            }
        } catch (Exception ex) {
            LOGGER.info("Data Migration - Insert - Error while parsing CSV for ModuleName - " + moduleName + "\nException - " + ex);
            throw new Exception(ex);
        }

        moduleCSVStream.close();
        return fieldNameVsValueList;
    }

    private static Map<String, String> getFieldNameVsValueMap(String[] fieldNames, String[] values) {
        Map<String, String> fieldNameVsValueMap = new HashMap<>();
        for (int i = 0; i < fieldNames.length; i++) {
            String fieldName = fieldNames[i];
            String value = values[i];

            fieldNameVsValueMap.put(fieldName, value);
        }
        return fieldNameVsValueMap;
    }

    private static List<Map<String, Object>> convertCSVDataToProps(Map<String, FacilioField> fieldsMap, List<Map<String, String>> fieldNameVsValueList) throws Exception {
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(fieldNameVsValueList)) {
            return null;
        }

        List<Map<String, Object>> propsList = new ArrayList<>();

        for (Map<String, String> fieldNameVsValue : fieldNameVsValueList) {
            Map<String, Object> dataProp = new HashMap<>();
            for (Map.Entry<String, String> entry : fieldNameVsValue.entrySet()) {
                String fieldName = entry.getKey();
                String value = entry.getValue();

                FacilioField field = fieldsMap.get(fieldName);

                if (field == null || org.apache.commons.lang3.StringUtils.isEmpty(value)) {
                    dataProp.put(fieldName, value);
                } else {
                    switch (field.getDataTypeEnum()) {
                        case ID:
                        case DATE:
                        case NUMBER:
                        case DATE_TIME:
                            if (field.getDisplayType() == FacilioField.FieldDisplayType.DECIMAL || field.getDisplayType() == FacilioField.FieldDisplayType.TEXTBOX) {
                                Double fieldValue = Double.parseDouble(value);
                                dataProp.put(fieldName, fieldValue);
                            } else {
                                long fieldValue;
                                // TODO - Value "-1" Cannot be inserted to DB, but "-1" is present in some NonNullable Fields
                                if (Objects.equals(value, "-1.0") || Objects.equals(value, "-1")) {
                                    fieldValue = -2;
                                } else {
                                    fieldValue = Long.parseLong(value);
                                }
                                dataProp.put(fieldName, fieldValue);
                            }
                            break;
                        case DECIMAL:
                            dataProp.put(fieldName, Double.parseDouble(value));
                            break;
                        case URL_FIELD:
                            String[] valueArray = value.split(",");
                            String href = valueArray[0];
                            String target = valueArray[1];
                            UrlField.UrlTarget targetEnum = UrlField.UrlTarget.valueOf(target);

                            Map<String, Object> prop = new HashMap<>();
                            prop.put("href", href);
                            prop.put("target", targetEnum);
                            if (valueArray.length > 2) {
                                String name = valueArray[2];
                                prop.put("name", name);
                            }
                            dataProp.put(fieldName, prop);
                            break;
                        case BOOLEAN:
                            Boolean booleanValue = Boolean.valueOf(value);
                            dataProp.put(fieldName, booleanValue);
                            break;
                        case LOOKUP:
                            Map<String, Object> lookupValue = new HashMap<>();
                            lookupValue.put("id", Long.parseLong(value));
                            dataProp.put(fieldName, lookupValue);
                            break;
                        case MULTI_LOOKUP:
                            String[] multiLookupValues = value.split(",");
                            List<Map<String, Object>> multiLookupValuesList = new ArrayList<>();
                            for (String multiLookupValue : multiLookupValues) {
                                Map<String, Object> multiLookupValueMap = new HashMap<>();
                                multiLookupValueMap.put("id", Long.parseLong(multiLookupValue));

                                multiLookupValuesList.add(multiLookupValueMap);
                            }
                            dataProp.put(fieldName, multiLookupValuesList);
                            break;
                        case MULTI_ENUM:
                            List<Integer> multiEnumValuesList = new ArrayList<>();
                            String[] multiEnumValues = value.split(",");
                            for (String multiEnumValue : multiEnumValues) {
                                multiEnumValuesList.add(Integer.parseInt(multiEnumValue));
                            }
                            dataProp.put(fieldName, multiEnumValuesList);
                            break;
                        case ENUM:
                        case SYSTEM_ENUM:
                            dataProp.put(fieldName, Integer.parseInt(value));
                            break;
                        case FILE:
                        case CURRENCY_FIELD:
                            ObjectMapper objectMapper = new ObjectMapper();
                            Map<String, Object> dataObjProp = objectMapper.readValue(value, Map.class);
                            dataProp.put(fieldName, dataObjProp);
                            break;
                        default:
                            dataProp.put(fieldName, value);
                            break;
                    }
                }
            }
            propsList.add(dataProp);
        }
        return propsList;
    }

    private static void moduleSpecialHandlingDuringImport(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList) throws Exception {
        ModuleBean modBean = Constants.getModBean();

        if (module.getName().equals(FacilioConstants.ContextNames.COMMENT_ATTACHMENTS)) {
            List<String> commentModuleNameList = propsList.stream().filter(prop -> prop.containsKey("parentCommentModuleName")).map(prop -> (String) prop.get("parentCommentModuleName")).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(commentModuleNameList)) {
                List<FacilioModule> moduleList = modBean.getModuleList(commentModuleNameList);
                Map<String, Long> moduleNameVsModuleId = moduleList.stream().collect(Collectors.toMap(FacilioModule::getName, FacilioModule::getModuleId));

                for (Map<String, Object> prop : propsList) {
                    String commentModuleName = prop.containsKey("parentCommentModuleName") ? (String) prop.get("parentCommentModuleName") : null;
                    long commentModuleId = StringUtils.isNotEmpty(commentModuleName) ? moduleNameVsModuleId.get(commentModuleName) : -1;

                    prop.put("commentModuleId", commentModuleId);
                }
            }
        }
    }

    public static Map<Long, List<Long>> getModuleIdVsOldIds(long moduleId, List<Map<String, Object>> propsList, Map<String, FacilioField> fieldsMap,
                                                            Map<String, Map<String, Object>> numberLookUps, List<String> fieldNamesToParse) {
        // Parse all LookUp fields
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(fieldNamesToParse)) {
            fieldNamesToParse = new ArrayList<>(fieldsMap.keySet());
        }

        Map<Long, List<Long>> moduleIdVsOldRecordIds = new HashMap<>();
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fieldNamesToParse)) {
            for (Map<String, Object> prop : propsList) {
                for (Map.Entry<String, Object> entry : prop.entrySet()) {
                    Object value = entry.getValue();
                    String fieldName = entry.getKey();
                    FacilioField fieldObj = fieldsMap.get(fieldName);

                    if (fieldName.equals("id")) {
                        moduleIdVsOldRecordIds.computeIfAbsent(moduleId, k -> new ArrayList<>());
                        moduleIdVsOldRecordIds.get(moduleId).add((long) value);
                    }

                    if (fieldObj == null || value == null || !fieldNamesToParse.contains(fieldName)) {
                        continue;
                    }

                    switch (fieldObj.getDataTypeEnum()) {
                        case LOOKUP:
                            FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                            String lookupModuleName = lookupModule.getName();
                            long lookupModuleModuleId = lookupModule.getModuleId();
                            if (!PackageUtil.nameVsComponentType.containsKey(lookupModuleName) && lookupModuleModuleId > 0 && org.apache.commons.collections4.MapUtils.isNotEmpty(((Map<String, Object>) value))) {
                                Long lookupDataId = (Long) ((Map<String, Object>) value).get("id");
                                moduleIdVsOldRecordIds.computeIfAbsent(lookupModuleModuleId, k -> new ArrayList<>());
                                moduleIdVsOldRecordIds.get(lookupModuleModuleId).add(lookupDataId);
                            }
                            break;

                        case MULTI_LOOKUP:
                            FacilioModule multiLookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                            String multiLookupModuleName = multiLookupModule.getName();
                            long multiLookupModuleModuleId = multiLookupModule.getModuleId();
                            if (!PackageUtil.nameVsComponentType.containsKey(multiLookupModuleName) && multiLookupModuleModuleId > 0 && org.apache.commons.collections4.CollectionUtils.isNotEmpty((List<Map<String, Object>>) value)) {
                                for (Map<String, Object> lookupData : (List<Map<String, Object>>) value) {
                                    Long lookupDataId = (Long) (lookupData).get("id");

                                    moduleIdVsOldRecordIds.computeIfAbsent(multiLookupModuleModuleId, k -> new ArrayList<>());
                                    moduleIdVsOldRecordIds.get(multiLookupModuleModuleId).add(lookupDataId);
                                }
                            }
                            break;

                        case NUMBER:
                            if (org.apache.commons.collections4.MapUtils.isNotEmpty(numberLookUps) && numberLookUps.containsKey(fieldName)) {
                                Long lookupDataId = (Long) value;
                                if (lookupDataId > 0) {
                                    Long parentModuleId = (Long) numberLookUps.get(fieldName).get("lookupModuleId");
                                    String numberLookupModuleName = (String) numberLookUps.get(fieldName).get("lookupModuleName");

                                    if (!PackageUtil.nameVsComponentType.containsKey(numberLookupModuleName)) {
                                        moduleIdVsOldRecordIds.computeIfAbsent(parentModuleId, k -> new ArrayList<>());
                                        moduleIdVsOldRecordIds.get(parentModuleId).add(lookupDataId);
                                    }
                                }
                            }
                            break;

                        default:
                            break;
                    }
                }
            }
        }
        return moduleIdVsOldRecordIds;
    }

    public static void updateLookupData(FacilioField fieldObj, Object fieldValue, Map<String, Object> dataProp, Map<String, Map<String, Object>> numberLookups,
                                        Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping, Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId) {
        String fieldName = fieldObj.getName();
        switch (fieldObj.getDataTypeEnum()) {
            case LOOKUP:
                FacilioModule lookupModule = ((LookupField) fieldObj).getLookupModule();
                if (org.apache.commons.collections4.MapUtils.isNotEmpty(((Map<String, Object>) fieldValue))) {
                    Long lookupDataId = (Long) ((Map<String, Object>) fieldValue).get("id");
                    if (PackageUtil.nameVsComponentType.containsKey(lookupModule.getName())) {
                        Long newId = getMetaConfNewId(lookupModule.getName(), lookupDataId, componentTypeVsOldVsNewId);
                        dataProp.put("id", newId);
                    } else {
                        dataProp.put("id", moduleIdVsOldNewIdMapping.get(lookupModule.getModuleId()).get(lookupDataId));
                    }
                }
                break;

            case MULTI_LOOKUP:
                FacilioModule multiLookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                if (multiLookupModule != null && CollectionUtils.isNotEmpty((List<Map<String, Object>>) fieldValue)) {
                    List<Map<String, Object>> updatedMultiLookupData = new ArrayList<>();
                    for (Map<String, Object> lookupData : (List<Map<String, Object>>) fieldValue) {
                        Long lookupDataId = (Long) (lookupData).get("id");
                        if (PackageUtil.nameVsComponentType.containsKey(multiLookupModule.getName())) {
                            Long newId = getMetaConfNewId(multiLookupModule.getName(), lookupDataId, componentTypeVsOldVsNewId);
                            lookupData.put("id", newId);
                        } else {
                            lookupData.put("id", moduleIdVsOldNewIdMapping.get(multiLookupModule.getModuleId()).get(lookupDataId));
                        }
                        updatedMultiLookupData.add(lookupData);
                    }
                } else {
                    dataProp.put(fieldName, fieldValue);
                }
                break;

            case NUMBER:
                if (org.apache.commons.collections4.MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName) && org.apache.commons.collections4.MapUtils.isNotEmpty(moduleIdVsOldNewIdMapping)) {
                    Long lookupDataId = (Long) fieldValue;
                    if (lookupDataId != null && lookupDataId > 0) {
                        Long parentModuleId = (Long) numberLookups.get(fieldName).get("lookupModuleId");
                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                        if (StringUtils.isNotEmpty(lookupModuleName) && PackageUtil.nameVsComponentType.containsKey(lookupModuleName)) {
                            Long newId = getMetaConfNewId(lookupModuleName, lookupDataId, componentTypeVsOldVsNewId);
                            dataProp.put(fieldName, newId);
                        } else if (moduleIdVsOldNewIdMapping.containsKey(parentModuleId) && moduleIdVsOldNewIdMapping.get(parentModuleId).containsKey(lookupDataId)) {
                            dataProp.put(fieldName, moduleIdVsOldNewIdMapping.get(parentModuleId).get(lookupDataId));
                        }
                    }
                } else {
                    dataProp.put(fieldName, fieldValue);
                }
                break;

            default:
                dataProp.put(fieldName, fieldValue);
                break;
        }
    }

    public static Long getMetaConfNewId(String compName, Long oldId, Map<ComponentType, Map<Long, Long>> compTypeVsOldVsNewIdMap) {
        ComponentType componentType = PackageUtil.nameVsComponentType.get(compName);
        if (org.apache.commons.collections4.MapUtils.isEmpty(compTypeVsOldVsNewIdMap) || oldId == null || componentType == null || !compTypeVsOldVsNewIdMap.containsKey(componentType)) {
            return -1L;
        }

        Map<Long, Long> oldIdVsNewId = compTypeVsOldVsNewIdMap.get(componentType);
        return oldIdVsNewId.getOrDefault(oldId, -1L);
    }

    public static Map<Long, Map<Long, Long>> getOldIdVsNewIdMapping(DataMigrationBean migrationBean, long dataMigrationId, Map<Long, List<Long>> moduleIdVsOldRecordIds) throws Exception {
        Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping = new HashMap<>();
        if (org.apache.commons.collections4.MapUtils.isNotEmpty(moduleIdVsOldRecordIds)) {
            for (Map.Entry<Long, List<Long>> moduleVsIds : moduleIdVsOldRecordIds.entrySet()) {
                Map<Long, Long> idMappings = migrationBean.getOldVsNewId(dataMigrationId, moduleVsIds.getKey(), moduleVsIds.getValue());
                if (MapUtils.isNotEmpty(idMappings)) {
                    if (moduleIdVsOldNewIdMapping.containsKey(moduleVsIds.getKey())) {
                        moduleIdVsOldNewIdMapping.get(moduleVsIds.getKey()).putAll(idMappings);
                    } else {
                        moduleIdVsOldNewIdMapping.put(moduleVsIds.getKey(), idMappings);
                    }
                }
            }
        }
        return moduleIdVsOldNewIdMapping;
    }
}
