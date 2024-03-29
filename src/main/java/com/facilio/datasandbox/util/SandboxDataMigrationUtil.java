package com.facilio.datasandbox.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.utils.PackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.util.DataMigrationUtil;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.xml.builder.XMLBuilder;
import com.opencsv.CSVReader;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j
public class SandboxDataMigrationUtil {
    public static void updateDataMigrationContext(DataMigrationStatusContext dataMigrationContext, DataMigrationStatusContext.DataMigrationStatus status, Long moduleId, String moduleFileName, int count) throws Exception {
        updateDataMigrationContext(dataMigrationContext, status, moduleId, null, moduleFileName, count);
    }

    public static void updateDataMigrationContextWithModuleName(DataMigrationStatusContext dataMigrationContext, DataMigrationStatusContext.DataMigrationStatus status, String moduleName, String moduleFileName, int count) throws Exception {
        updateDataMigrationContext(dataMigrationContext, status, null, moduleName, moduleFileName, count);
    }

    private static void updateDataMigrationContext(DataMigrationStatusContext dataMigrationContext, DataMigrationStatusContext.DataMigrationStatus status, Long moduleId, String moduleName, String moduleFileName, int count) throws Exception {
        moduleId = (moduleId == null || moduleId < 0) ? -99 : moduleId;
        moduleName = (StringUtils.isNotEmpty(moduleName)) ? moduleName : "";
        status = status != null ? status : dataMigrationContext.getStatusEnum();
        moduleFileName = (StringUtils.isNotEmpty(moduleFileName)) ? moduleFileName : "";

        dataMigrationContext.setStatus(status);
        dataMigrationContext.setLastModuleId(moduleId);
        dataMigrationContext.setLastModuleName(moduleName);
        dataMigrationContext.setModuleFileName(moduleFileName);
        dataMigrationContext.setSysModifiedTime(System.currentTimeMillis());
        if(count >= 0) {
            dataMigrationContext.setMigratedCount(count);
        }

        DataMigrationBean dataMigrationBean = DataMigrationConstants.getDataMigrationBean(dataMigrationContext.getOrgId());
        dataMigrationBean.updateDataMigrationContext(dataMigrationContext);
    }

    public static void constructResponse(DataMigrationStatusContext dataMigrationObj, Map<String, Map<String, Object>> migrationModuleNameVsDetails, Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext) throws Exception {
        if (MapUtils.isNotEmpty(moduleNameVsCsvFileContext)) {
            // dataConfig.xml
            XMLBuilder dataConfigXML = XMLBuilder.create(PackageConstants.DATA_FOLDER_NAME);
            XMLBuilder modulesXMLBuilder = dataConfigXML.element(PackageConstants.MODULES);

            for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
                String moduleName = moduleNameVsDetails.getKey();
                Stack<ModuleCSVFileContext> moduleCSVFileContexts = moduleNameVsCsvFileContext.get(moduleName);

                if (CollectionUtils.isNotEmpty(moduleCSVFileContexts)) {
                    for (ModuleCSVFileContext csvFileContext : moduleCSVFileContexts) {
                        XMLBuilder dataModuleNameConfigXML = modulesXMLBuilder.element(PackageConstants.MODULE);
                        dataModuleNameConfigXML.attr(PackageConstants.NAME, moduleName);
                        dataModuleNameConfigXML.attr(PackageConstants.SEQUENCE, String.valueOf(csvFileContext.getOrder()));
                        dataModuleNameConfigXML.attr(PackageConstants.RECORDS_COUNT, String.valueOf(csvFileContext.getRecordCount()));
                        dataModuleNameConfigXML.text(csvFileContext.getCsvFileName());
                    }
                }
            }

            DataPackageFileUtil.addDataConfFile(dataConfigXML.getAsXMLString());
        }

        String url = DataPackageFileUtil.getFileUrl(PackageUtil.getRootFolderPath());
        LOGGER.info("####Data Migration - DataPackageUrl - " + url);

        // clean temp folder
        FileUtils.deleteDirectory(DataPackageFileUtil.getTempFolderRoot());

        // remove threadlocal
        dataMigrationObj.setPackageFilePath(url);
        PackageUtil.removeRootFolderPath();

        // update status context
        DataMigrationConstants.getDataMigrationBean(dataMigrationObj.getOrgId()).updateDataMigrationContext(dataMigrationObj);
    }

    public static int getMigratedRecordCount(Stack<ModuleCSVFileContext> moduleCSVFileContexts) {
        int migratedCount = 0;
        if (CollectionUtils.isNotEmpty(moduleCSVFileContexts)) {
            migratedCount = moduleCSVFileContexts.stream().mapToInt(ModuleCSVFileContext::getRecordCount).sum();
        }
        return migratedCount;
    }

    public static String addDataPropsToCSVFile(Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileName, FacilioModule module, List<FacilioField> allFields, List<Map<String, Object>> propsForCsv,
                                               List<String> fileFieldNamesWithId, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                               Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookUps, Context context) throws Exception {

        if (CollectionUtils.isEmpty(allFields) || CollectionUtils.isEmpty(propsForCsv)) {
            return null;
        }

        ModuleCSVFileContext fileContextForModule = DataPackageFileUtil.getFileContextForModule(module.getName(), moduleNameVsCsvFileName);
        String fileName = fileContextForModule.getCsvFileName();
        String moduleName = module.getName();
        int migratedCount;

        // module data csv creation
        LOGGER.info("####Data Migration - CSV Creation started for ModuleName - " + moduleName);

        if (fileContextForModule.getRecordCount() == 0) {
            // create a new file
            File moduleCsvFile = DataPackageFileUtil.createTempFileForModule(fileName);

            migratedCount = exportDataAsCSVFile(module, allFields, propsForCsv, moduleCsvFile.getPath(), fileFieldNamesWithId,
                    getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps, context);
            DataPackageFileUtil.addModuleCSVFile(fileName, moduleCsvFile);
            LOGGER.info("####Data Migration - New CSV File created for ModuleName - " + moduleName);
        } else {
            // update records in old file
            String sourceFileURL = getCSVFileURL(fileName);
            File tempCsvFile = DataPackageFileUtil.addFileToTempFolder(sourceFileURL, fileName);

            migratedCount = updateDataInCSVFile(tempCsvFile, module, allFields, propsForCsv, fileFieldNamesWithId, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookUps, context);
            DataPackageFileUtil.addModuleCSVFile(fileName, tempCsvFile);
            LOGGER.info("####Data Migration - Old CSV File updated for ModuleName - " + moduleName);
        }

        int recordsSize = fileContextForModule.getRecordCount() + migratedCount;
        fileContextForModule.setRecordCount(recordsSize);

        LOGGER.info("####Data Migration - CSV Creation completed for ModuleName - " + moduleName);

        return fileName;
    }

    private static String getCSVFileURL(String fileName) {
        String absolutePath = PackageUtil.getRootFolderPath() + File.separator + PackageConstants.DATA_FOLDER_NAME + File.separator + fileName;
        return DataPackageFileUtil.getFileUrl(absolutePath);
    }

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
        } else if (module.getName().equals(FacilioConstants.ContextNames.TASK) || module.getName().equals(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
            Set<Long> readingFieldIds = new HashSet<>();
            for (Map<String, Object> prop : propsList) {
                Long readingFieldId = (Long) prop.get("readingFieldId");
                if (readingFieldId != null && readingFieldId > 0) {
                    readingFieldIds.add(readingFieldId);
                }
            }

            if (CollectionUtils.isEmpty(readingFieldIds)) {
                return;
            }

            List<FacilioField> readingFieldsList = modBean.getFields(readingFieldIds);
            if (CollectionUtils.isNotEmpty(readingFieldsList)) {
                FacilioField readingFieldNameField = FieldFactory.getStringField("readingFieldName", null, module);
                FacilioField readingFieldModuleNameNameField = FieldFactory.getStringField("readingFieldModuleNameName", null, module);
                fields.add(readingFieldNameField);
                fields.add(readingFieldModuleNameNameField);

                Map<Long, FacilioField> fieldIdVsFieldObj = readingFieldsList.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity()));

                for (Map<String, Object> prop : propsList) {
                    Long readingFieldId = (Long) prop.get("readingFieldId");
                    if (readingFieldId != null && readingFieldId > 0 && fieldIdVsFieldObj.containsKey(readingFieldId)) {
                        FacilioField readingField = fieldIdVsFieldObj.get(readingFieldId);
                        String readingFieldModuleNameName = readingField.getModule() != null ? readingField.getModule().getName() : null;

                        prop.put("readingFieldName", readingField.getName());
                        prop.put("readingFieldModuleNameName", readingFieldModuleNameName);
                    }
                }
            }
        } else if (module.getName().equals(FacilioConstants.ContextNames.READING_DATA_META)) {
            Set<Long> fieldIds = new HashSet<>();
            for (Map<String, Object> prop : propsList) {
                Long fieldId = (Long) prop.get("fieldId");
                fieldIds.add(fieldId);
            }

            if (CollectionUtils.isEmpty(fieldIds)) {
                return;
            }

            List<FacilioField> fieldsList = modBean.getFields(fieldIds);
            if (CollectionUtils.isNotEmpty(fieldsList)) {
                FacilioField fieldNameField = FieldFactory.getStringField("fieldName", null, module);
                FacilioField fieldModuleNameNameField = FieldFactory.getStringField("fieldModuleNameName", null, module);
                fields.add(fieldNameField);
                fields.add(fieldModuleNameNameField);

                Map<Long, FacilioField> fieldIdVsFieldObj = fieldsList.stream().collect(Collectors.toMap(FacilioField::getFieldId, Function.identity()));

                List<Long> fieldIdsToRemove = new ArrayList<>();
                for (Map<String, Object> prop : propsList) {
                    Long fieldId = (Long) prop.get("fieldId");
                    FacilioField fieldObj = fieldIdVsFieldObj.get(fieldId);
                    if (fieldObj == null) {
                        fieldIdsToRemove.add(fieldId);
                        continue;
                    }
                    String fieldModuleName = fieldObj.getModule() != null ? fieldObj.getModule().getName() : null;

                    prop.put("fieldName", fieldObj.getName());
                    prop.put("fieldModuleNameName", fieldModuleName);
                }

                // Remove fieldId without fieldObj
                Iterator<Map<String, Object>> iterator = propsList.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> prop = iterator.next();
                    Long fieldId = (Long) prop.get("fieldId");
                    if (fieldIdsToRemove.contains(fieldId)) {
                        iterator.remove();
                    }
                }

            }
        }
    }

    public static int exportDataAsCSVFile(FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList, String filePath,
                                           List<String> fileFieldNames, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                           Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookups, Context context) throws Exception {

        // add system fields
        SandboxModuleConfigUtil.addSystemFieldsOnPackageCreation(module, fields);

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
            return addRecordData(writer, fields, propsList, fileFieldNames, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookups, context);
        }
    }

    public static int updateDataInCSVFile(File moduleCsvFile, FacilioModule module, List<FacilioField> fields, List<Map<String, Object>> propsList,
                                           List<String> fileFieldNames, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                           Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookups, Context context) throws Exception {

        // add system fields
        SandboxModuleConfigUtil.addSystemFieldsOnPackageCreation(module, fields);

        // special handling
        moduleSpecialHandlingDuringExport(module, fields, propsList);

        String filePath = moduleCsvFile.getPath();

        try (FileWriter writer = new FileWriter(filePath, true)) {
            // add record data
            return addRecordData(writer, fields, propsList, fileFieldNames, getDependantModuleData, fetchedRecords, toBeFetchRecords, numberLookups, context);
        }
    }

    public static int addRecordData(FileWriter writer, List<FacilioField> fields, List<Map<String, Object>> propsList, List<String> fileFieldNames,
                                     boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords,
                                     Map<String, List<Long>> toBeFetchRecords, Map<String, Map<String, Object>> numberLookupDetails, Context context) throws Exception {

        int migratedCount = 0;
        long transactionStartTime = DataMigrationConstants.getTransactionStartTime(context);
        long transactionTimeOut = (long) context.get(DataMigrationConstants.TRANSACTION_TIME_OUT);

        ModuleBean modBean = Constants.getModBean();
        for (Map<String, Object> record : propsList) {
            migratedCount++;
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

            if (DataMigrationConstants.isTransactionTimeOutReached(transactionStartTime, transactionTimeOut)) {
                break;
            }
        }
        writer.flush();
        writer.close();

        return migratedCount;
    }

    private static String getDataForField(FacilioField field, Object value, boolean getDependantModuleData, Map<String, List<Long>> fetchedRecords, Map<String, List<Long>> toBeFetchRecords) {
        Object result = null;
        FacilioModule lookupModule;
        String moduleName = field.getModule() != null ? field.getModule().getName() : "";
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
                urlBuilder.add(urlValue.get("target").toString());
                if (urlValue.get("name") != null && urlValue.get("target") != null) {
                    urlBuilder.add(urlValue.get("name").toString());
                }
                result = urlBuilder.toString();
                break;

            case STRING:
            case BIG_STRING:
            case LARGE_TEXT:
                if (DataMigrationUtil.getJsonFieldNamesForModule(moduleName).contains(field.getName())) {
                    result = value.toString();
                } else {
                    result = value.toString().replace(",", StringOperators.DELIMITED_COMMA);
                }
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

    public static List<Map<String, Object>> getDataFromCSV(FacilioModule module, String moduleFileName, Map<String, FacilioField> fieldsMap, List<String> numberFileFields, int offset, int limit) throws Exception {
        List<Map<String, String>> fieldNameVsValueList = parseCSVAndGetData(module.getName(), moduleFileName, offset, limit);
        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(fieldNameVsValueList)) {
            List<Map<String, Object>> propsList = convertCSVDataToProps(module.getName(), fieldsMap, fieldNameVsValueList, numberFileFields);
            // special handling
            moduleSpecialHandlingDuringImport(module, new ArrayList<>(fieldsMap.values()), propsList);
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
            int count = 0;
            String[] fieldValues;
            String[] fieldNames = csvReader.readNext();

            // Skip the rows till offset is reached
            while (count < offset && csvReader.readNext() != null) {
                count++;
            }

            // Read and process rows starting from the specified offset
            while ((fieldValues = csvReader.readNext()) != null && count < offset + limit) {
                Map<String, String> fieldNameVsValueMap = getFieldNameVsValueMap(fieldNames, fieldValues);
                fieldNameVsValueList.add(fieldNameVsValueMap);

                count++;
            }
        } catch (Exception ex) {
            LOGGER.info("Data Migration - Insert - Error while parsing CSV for ModuleName - " + moduleName + "\nException - " + ex);
            throw new Exception(ex);
        }

        moduleCSVStream.close();
        return fieldNameVsValueList;
    }

    private static Map<String, String> getFieldNameVsValueMap(String[] fieldNames, String[] values) {
        // Size of fieldNames & values may not be same (if consecutive columns are empty, they were not parsed)
        Map<String, String> fieldNameVsValueMap = new HashMap<>();
        int length = Math.min(fieldNames.length, values.length);
        for (int i = 0; i < length; i++) {
            String fieldName = fieldNames[i];
            String value = values[i];

            fieldNameVsValueMap.put(fieldName, value);
        }
        return fieldNameVsValueMap;
    }

    private static List<Map<String, Object>> convertCSVDataToProps(String moduleName, Map<String, FacilioField> fieldsMap, List<Map<String, String>> fieldNameVsValueList, List<String> numberFileFields) throws Exception {
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

                if (StringUtils.isEmpty(value)) {
                    continue;
                }

                if (field == null) {
                    dataProp.put(fieldName, value);
                } else if (CollectionUtils.isNotEmpty(numberFileFields) && numberFileFields.contains(fieldName)) {
                    // NumberFileFields has similar parsing as like FileFields
                    Map<String, Object> dataObjProp = parseStringToMap(value);
                    dataProp.put(fieldName, dataObjProp);
                } else {
                    switch (field.getDataTypeEnum()) {
                        case ID:
                        case DATE:
                        case NUMBER:
                        case DATE_TIME:
                            if (field.getDisplayType() == FacilioField.FieldDisplayType.DECIMAL || field.getDisplayType() == FacilioField.FieldDisplayType.TEXTBOX || value.contains(".")) {
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
                        case STRING:
                        case BIG_STRING:
                        case LARGE_TEXT:
                            String parsedValue;
                            if (DataMigrationUtil.getJsonFieldNamesForModule(moduleName).contains(fieldName)) {
                                parsedValue = StringUtils.isNotEmpty(value) ? FacilioUtil.parseJson(value).toJSONString() : null;
                            } else {
                                parsedValue = value.replace(StringOperators.DELIMITED_COMMA, ",");
                            }
                            dataProp.put(fieldName, parsedValue);
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
                            Map<String, Object> dataObjProp = parseStringToMap(value);
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

    private static Map<String, Object> parseStringToMap(String input) {
        Map<String, Object> resultMap = new HashMap<>();

        // Remove curly braces at the beginning and end
        String cleanedInput = input.substring(1, input.length() - 1);

        // Split the string into key-value pairs
        String[] pairs = cleanedInput.split(", ");

        for (String pair : pairs) {
            int indexOfSeparator = pair.indexOf('=');
            String key = pair.substring(0, indexOfSeparator);
            String value = pair.substring(indexOfSeparator + 1);

            // Add key-value pair to the map
            resultMap.put(key, (Object) value);
        }

        return resultMap;
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
        } else if (module.getName().equals(FacilioConstants.ContextNames.TASK) || module.getName().equals(FacilioConstants.ContextNames.JOB_PLAN_TASK)) {
            Map<String, Set<String>> moduleNameVsFieldsNames = new HashMap<>();
            for (Map<String, Object> prop : propsList) {
                Long readingFieldId = prop.containsKey("readingFieldId") ? (Long) prop.get("readingFieldId") : null;
                if (readingFieldId != null && readingFieldId > 0) {
                    String readingFieldName = prop.containsKey("readingFieldName") ? (String) prop.get("readingFieldName") : null;
                    String readingFieldModuleNameName = prop.containsKey("readingFieldModuleNameName") ? (String) prop.get("readingFieldModuleNameName") : null;

                    if (StringUtils.isNotEmpty(readingFieldName) && StringUtils.isNotEmpty(readingFieldModuleNameName)) {
                        moduleNameVsFieldsNames.computeIfAbsent(readingFieldModuleNameName, k -> new HashSet<>());
                        moduleNameVsFieldsNames.get(readingFieldModuleNameName).add(readingFieldName);
                    }
                }
            }

            if (MapUtils.isNotEmpty(moduleNameVsFieldsNames)) {
                Map<String, FacilioModule> moduleNameVsModuleObj = new HashMap<>();
                Map<String, Map<String, FacilioField>> moduleVsFieldsMap = new HashMap<>();
                for (Map.Entry<String, Set<String>> entry : moduleNameVsFieldsNames.entrySet()) {
                    String moduleName = entry.getKey();
                    Set<String> fieldNames = entry.getValue();
                    Map<String, FacilioField> moduleFieldsMap = SandboxModuleConfigUtil.getModuleFields(moduleName, new ArrayList<>(fieldNames));
                    if (MapUtils.isNotEmpty(moduleFieldsMap)) {
                        if (!moduleNameVsModuleObj.containsKey(moduleName)) {
                            FacilioModule currModule = modBean.getModule(moduleName);
                            moduleNameVsModuleObj.put(moduleName, currModule);
                        }
                        moduleVsFieldsMap.put(moduleName, moduleFieldsMap);
                    }
                }

                for (Map<String, Object> prop : propsList) {
                    Long readingFieldId = prop.containsKey("readingFieldId") ? (Long) prop.get("readingFieldId") : null;
                    if (readingFieldId != null && readingFieldId > 0) {
                        String readingFieldName = prop.containsKey("readingFieldName") ? (String) prop.get("readingFieldName") : null;
                        String readingFieldModuleNameName = prop.containsKey("readingFieldModuleNameName") ? (String) prop.get("readingFieldModuleNameName") : null;

                        if (moduleVsFieldsMap.containsKey(readingFieldModuleNameName) && moduleVsFieldsMap.get(readingFieldModuleNameName).containsKey(readingFieldName)) {
                            FacilioModule readingModule = moduleNameVsModuleObj.get(readingFieldModuleNameName);
                            FacilioField readingField = moduleVsFieldsMap.get(readingFieldModuleNameName).get(readingFieldName);

                            prop.put("readingFieldId", readingField.getFieldId());
                            prop.put("##ReadingFieldModule##", readingModule);
                        }
                    }
                }
            }
        } else if (module.getName().equals(FacilioConstants.ContextNames.READING_DATA_META)) {
            Map<String, Set<String>> moduleNameVsFieldsNames = new HashMap<>();
            for (Map<String, Object> prop : propsList) {
                String fieldName = (String) prop.get("fieldName");
                String fieldModuleNameName = (String) prop.get("fieldModuleNameName");

                if (StringUtils.isNotEmpty(fieldName) && StringUtils.isNotEmpty(fieldModuleNameName)) {
                    moduleNameVsFieldsNames.computeIfAbsent(fieldModuleNameName, k -> new HashSet<>());
                    moduleNameVsFieldsNames.get(fieldModuleNameName).add(fieldName);
                }
            }

            if (MapUtils.isNotEmpty(moduleNameVsFieldsNames)) {
                Map<String, FacilioModule> moduleNameVsModuleObj = new HashMap<>();
                Map<String, Map<String, FacilioField>> moduleVsFieldsMap = new HashMap<>();
                for (Map.Entry<String, Set<String>> entry : moduleNameVsFieldsNames.entrySet()) {
                    String moduleName = entry.getKey();
                    Set<String> fieldNames = entry.getValue();
                    Map<String, FacilioField> moduleFieldsMap = SandboxModuleConfigUtil.getModuleFields(moduleName, new ArrayList<>(fieldNames));
                    if (MapUtils.isNotEmpty(moduleFieldsMap)) {
                        if (!moduleNameVsModuleObj.containsKey(moduleName)) {
                            FacilioModule currModule = modBean.getModule(moduleName);
                            moduleNameVsModuleObj.put(moduleName, currModule);
                        }
                        moduleVsFieldsMap.put(moduleName, moduleFieldsMap);
                    }
                }

                for (Map<String, Object> prop : propsList) {
                    String fieldName = (String) prop.get("fieldName");
                    String fieldModuleNameName = (String) prop.get("fieldModuleNameName");

                    if (moduleVsFieldsMap.containsKey(fieldModuleNameName) && moduleVsFieldsMap.get(fieldModuleNameName).containsKey(fieldName)) {
                        FacilioField fieldObj = moduleVsFieldsMap.get(fieldModuleNameName).get(fieldName);
                        FacilioModule moduleObj = moduleNameVsModuleObj.get(fieldModuleNameName);
                        prop.put("##ReadingFieldModule##", moduleObj);
                        prop.put("fieldId", fieldObj.getFieldId());
                    }
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
                        continue;
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
                                if (lookupDataId != null && lookupDataId > 0) {
                                    moduleIdVsOldRecordIds.computeIfAbsent(lookupModuleModuleId, k -> new ArrayList<>());
                                    moduleIdVsOldRecordIds.get(lookupModuleModuleId).add(lookupDataId);
                                }
                            }
                            break;

                        case MULTI_LOOKUP:
                            FacilioModule multiLookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                            String multiLookupModuleName = multiLookupModule.getName();
                            long multiLookupModuleModuleId = multiLookupModule.getModuleId();
                            if (!PackageUtil.nameVsComponentType.containsKey(multiLookupModuleName) && multiLookupModuleModuleId > 0 && org.apache.commons.collections4.CollectionUtils.isNotEmpty((List<Map<String, Object>>) value)) {
                                for (Map<String, Object> lookupData : (List<Map<String, Object>>) value) {
                                    Long lookupDataId = (Long) (lookupData).get("id");
                                    if (lookupDataId != null && lookupDataId > 0) {
                                        moduleIdVsOldRecordIds.computeIfAbsent(multiLookupModuleModuleId, k -> new ArrayList<>());
                                        moduleIdVsOldRecordIds.get(multiLookupModuleModuleId).add(lookupDataId);
                                    }
                                }
                            }
                            break;

                        case NUMBER:
                            if (MapUtils.isNotEmpty(numberLookUps) && numberLookUps.containsKey(fieldName) && !isSpecialTypeNumberLookUp(numberLookUps.get(fieldName))) {
                                long lookupDataId;
                                if (value instanceof Double) {
                                    lookupDataId = ((Double) value).longValue();
                                } else if (value instanceof String) {
                                    lookupDataId = Long.parseLong((String) value);
                                } else {
                                    lookupDataId = (Long) value;
                                }
                                if (lookupDataId > 0) {
                                    FacilioModule parentLookupModule = (FacilioModule) numberLookUps.get(fieldName).get("lookupModule");
                                    Long parentModuleId = parentLookupModule != null ? parentLookupModule.getModuleId() : -1;
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
                Map<String, Object> lookUpData = (Map<String, Object>) fieldValue;
                if (org.apache.commons.collections4.MapUtils.isNotEmpty(lookUpData) && ((Long) (lookUpData).get("id")) > 0) {
                    Long newId = null;
                    Long lookupDataId = (Long) (lookUpData).get("id");
                    if (PackageUtil.nameVsComponentType.containsKey(lookupModule.getName())) {
                        newId = getMetaConfNewId(lookupModule.getName(), lookupDataId, componentTypeVsOldVsNewId);
                    } else if (MapUtils.isNotEmpty(moduleIdVsOldNewIdMapping) && moduleIdVsOldNewIdMapping.containsKey(lookupModule.getModuleId())) {
                        newId = moduleIdVsOldNewIdMapping.get(lookupModule.getModuleId()).get(lookupDataId);
                    }
                    if (newId == null || newId < 0) {
                        LOGGER.info("####Data Migration Tracking - Cannot get NewId for OldId - " + lookupDataId + " for FieldName - " + fieldName + " LookupModuleName - " + lookupModule.getName());
                    } else {
                        lookUpData.put("id", newId);
                        dataProp.put(fieldName, lookUpData);
                    }
                }
                break;

            case MULTI_LOOKUP:
                FacilioModule multiLookupModule = ((MultiLookupField) fieldObj).getLookupModule();
                if (multiLookupModule != null && CollectionUtils.isNotEmpty((List<Map<String, Object>>) fieldValue)) {
                    List<Map<String, Object>> updatedMultiLookupData = new ArrayList<>();
                    boolean isValueUpdated = false;
                    for (Map<String, Object> lookupData : (List<Map<String, Object>>) fieldValue) {
                        Long lookupDataId = (Long) (lookupData).get("id");
                        if ((Long) (lookupData).get("id") > 0) {
                            Long newId = null;
                            if (PackageUtil.nameVsComponentType.containsKey(multiLookupModule.getName())) {
                                newId = getMetaConfNewId(multiLookupModule.getName(), lookupDataId, componentTypeVsOldVsNewId);
                            } else if (MapUtils.isNotEmpty(moduleIdVsOldNewIdMapping) && moduleIdVsOldNewIdMapping.containsKey(multiLookupModule.getModuleId())) {
                                newId = moduleIdVsOldNewIdMapping.get(multiLookupModule.getModuleId()).get(lookupDataId);
                            }
                            if (newId == null || newId < 0) {
                                LOGGER.info("####Data Migration Tracking - Cannot get NewId for OldId - " + lookupDataId + " for FieldName - " + fieldName + " LookupModuleName - " + multiLookupModule.getName());
                            } else {
                                isValueUpdated = true;
                                lookupData.put("id", newId);
                                updatedMultiLookupData.add(lookupData);
                            }
                        }
                    }
                    if (isValueUpdated) {
                        dataProp.put(fieldName, updatedMultiLookupData);
                    }
                }
                break;

            case NUMBER:
                if (isSpecialTypeNumberLookUp(numberLookups.get(fieldName))) {
                    dataProp.put(fieldName, fieldValue);
                } else if (MapUtils.isNotEmpty(numberLookups) && numberLookups.containsKey(fieldName)) {
                    Long lookupDataId;
                    if (fieldValue instanceof Double) {
                        lookupDataId = ((Double) fieldValue).longValue();
                    } else if (fieldValue instanceof String) {
                        lookupDataId = Long.parseLong((String) fieldValue);
                    } else {
                        lookupDataId = (Long) fieldValue;
                    }
                    if (lookupDataId != null && lookupDataId > 0) {
                        Long newId = null;
                        FacilioModule parentLookupModule = (FacilioModule) numberLookups.get(fieldName).get("lookupModule");
                        Long parentModuleId = parentLookupModule != null ? parentLookupModule.getModuleId() : -1;
                        String lookupModuleName = (String) numberLookups.get(fieldName).get("lookupModuleName");
                        if (StringUtils.isNotEmpty(lookupModuleName) && PackageUtil.nameVsComponentType.containsKey(lookupModuleName)) {
                            newId = getMetaConfNewId(lookupModuleName, lookupDataId, componentTypeVsOldVsNewId);
                        } else if (MapUtils.isNotEmpty(moduleIdVsOldNewIdMapping) && moduleIdVsOldNewIdMapping.containsKey(parentModuleId)) {
                            newId = moduleIdVsOldNewIdMapping.get(parentModuleId).get(lookupDataId);
                        }
                        if (newId == null || newId < 0) {
                            LOGGER.info("####Data Migration Tracking - Cannot get NewId for OldId - " + lookupDataId + " for FieldName - " + fieldName + " LookupModuleName - " + lookupModuleName);
                        } else {
                            dataProp.put(fieldName,newId);
                        }
                    }
                }
                break;

            default:
                dataProp.put(fieldName, fieldValue);
                break;
        }
    }

    private static boolean isSpecialTypeNumberLookUp(Map<String, Object> numberLookUpDetails) {
        return MapUtils.isNotEmpty(numberLookUpDetails) && (boolean) numberLookUpDetails.getOrDefault("isSpecialType", false);
    }

    public static Long getMetaConfNewId(String compName, Long oldId, Map<ComponentType, Map<Long, Long>> compTypeVsOldVsNewIdMap) {
        ComponentType componentType = PackageUtil.nameVsComponentType.get(compName);
        if (org.apache.commons.collections4.MapUtils.isEmpty(compTypeVsOldVsNewIdMap) || oldId == null || componentType == null || !compTypeVsOldVsNewIdMap.containsKey(componentType)) {
            return -1L;
        }

        Map<Long, Long> oldIdVsNewId = compTypeVsOldVsNewIdMap.get(componentType);
        long newId = oldIdVsNewId.getOrDefault(oldId, -1L);
        if (newId < 0 && (componentType.equals(ComponentType.USER) || componentType.equals(ComponentType.PEOPLE))) {
            try {
                long orgId = AccountUtil.getCurrentOrg().getOrgId();
                User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(orgId);
                if (superAdmin != null) {
                    newId = componentType.equals(ComponentType.USER) ? superAdmin.getOuid() : superAdmin.getPeopleId();
                }
            } catch (Exception e) {
                LOGGER.info("####Sandbox Migration - Super Admin User not found", e);
            }
        }
        return newId;
    }

    public static Map<Long, Map<Long, Long>> getOldIdVsNewIdMapping(DataMigrationBean migrationBean, long dataMigrationId, Map<Long, List<Long>> moduleIdVsOldRecordIds) throws Exception {
        Map<Long, Map<Long, Long>> moduleIdVsOldNewIdMapping = new HashMap<>();
        if (org.apache.commons.collections4.MapUtils.isNotEmpty(moduleIdVsOldRecordIds)) {
            for (Map.Entry<Long, List<Long>> moduleVsIds : moduleIdVsOldRecordIds.entrySet()) {
                if (moduleVsIds.getKey() == null || moduleVsIds.getKey() < 0 || CollectionUtils.isEmpty(moduleVsIds.getValue())) {
                    continue;
                }
                HashSet<Long> nonDuplicateValues = new HashSet<>(moduleVsIds.getValue());
                Map<Long, Long> idMappings = migrationBean.getOldVsNewId(dataMigrationId, moduleVsIds.getKey(), new ArrayList<>(nonDuplicateValues));
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
