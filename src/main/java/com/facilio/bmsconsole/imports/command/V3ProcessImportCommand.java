package com.facilio.bmsconsole.imports.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.actions.ImportProcessContext;
import com.facilio.bmsconsole.commands.ImportProcessLogContext;
import com.facilio.bmsconsole.context.ImportRowContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.enums.SourceType;
import com.facilio.bmsconsole.exceptions.importExceptions.ImportParseException;
import com.facilio.bmsconsole.imports.annotations.RowFunction;
import com.facilio.bmsconsole.util.ImportAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiEnumField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class V3ProcessImportCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(V3ProcessImportCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        HashMap<String, List<ReadingContext>> groupedContext = new HashMap<String, List<ReadingContext>>();
        ImportProcessContext importProcessContext = (ImportProcessContext) context.get(ImportAPI.ImportProcessConstants.IMPORT_PROCESS_CONTEXT);
        HashMap<String, String> fieldMapping = importProcessContext.getFieldMapping();

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        if (StringUtils.isEmpty(moduleName)) {
            moduleName = importProcessContext.getModuleName();
        }
        if (StringUtils.isEmpty(moduleName)) {
            throw new IllegalArgumentException("Module name is empty");
        }

        JSONObject importMeta = importProcessContext.getImportJobMetaJson();
        RowFunction beforeImportFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.BEFORE_IMPORT_FUNCTION);
        RowFunction afterImportFunction = (RowFunction) context.get(ImportAPI.ImportProcessConstants.AFTER_IMPORT_FUNCTION);

        List<Map<String, Object>> allRows = ImportAPI.getValidatedRows(importProcessContext.getId());

        List<SiteContext> sites = SpaceAPI.getAllSites();
        Map<String, SiteContext> sitesMap = null;
        if (CollectionUtils.isNotEmpty(sites)) {
            sitesMap = sites.stream().collect(Collectors.toMap(site -> site.getName().trim(), Function.identity()));
        }

        for(Map<String, Object> row: allRows) {
            ImportProcessLogContext rowLogContext = FieldUtil.getAsBeanFromMap(row, ImportProcessLogContext.class);

            ImportRowContext rowContext;
            if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.NO_VALIDATION_REQUIRED.getValue()){
                rowContext = rowLogContext.getRowContexts().get(0);
            }
            else if(rowLogContext.getError_resolved() == ImportProcessContext.ImportLogErrorStatus.RESOLVED.getValue()) {
                rowContext = rowLogContext.getCorrectedRow();
            } else {
                continue;
            }

            int rowNo = rowContext.getRowNumber();
            HashMap<String, Object> rowVal = rowContext.getColVal();

            LOGGER.info("row -- " + rowNo + " rowVal --- " + rowVal);

            HashMap<String, Object> props = new LinkedHashMap<String, Object>();

            // adding source_type and source_id in the props
            props.put(FacilioConstants.ContextNames.SOURCE_TYPE, SourceType.IMPORT.getIndex());
            props.put(FacilioConstants.ContextNames.SOURCE_ID, importProcessContext.getId());

            if (fieldMapping.containsKey(moduleName + "__formId") && rowVal.get(fieldMapping.get(moduleName + "__formId")) != null) {
                props.put("formId", rowVal.get(fieldMapping.get(moduleName + "__formId")));
            }

            // add site only for insert update
            if (!(importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE.getValue() || importProcessContext.getImportSetting() == ImportProcessContext.ImportSetting.UPDATE_NOT_NULL.getValue())) {
                if (MapUtils.isNotEmpty(sitesMap)) {
                    if (rowVal.containsKey(fieldMapping.get(moduleName + "__site"))) {
                        String siteName = (String) rowVal.get(fieldMapping.get(moduleName + "__site"));
                        SiteContext siteContext = sitesMap.get(siteName.trim().toLowerCase());
                        props.put("siteId", siteContext.getId());
                    }
                }
            }

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = getFields(modBean, context, moduleName);

            if (beforeImportFunction != null) {
                beforeImportFunction.apply(rowNo, rowVal, context);
            }

            for (FacilioField field : fields) {
                String key = field.getModule().getName() + "__" + field.getName();
                Object cellValue = rowVal.get(fieldMapping.get(key));

                if (cellValue == null || cellValue.toString().equals("") || (cellValue.toString().equals("n/a"))) {
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

                    switch (field.getDataTypeEnum()) {
                        case DATE:
                        case DATE_TIME: {
                            if (!(cellValue instanceof Long)) {
                                long millis;
                                JSONObject dateFormats = (JSONObject) importMeta.get(ImportAPI.ImportProcessConstants.DATE_FORMATS);
                                if (dateFormats.get(fieldMapping.get(key)).equals(ImportAPI.ImportProcessConstants.TIME_STAMP_STRING)) {
                                    millis = Long.parseLong(cellValue.toString());
                                } else {
                                    Instant dateInstant = DateTimeUtil.getTimeInstant(dateFormats.get(fieldMapping.get(key)).toString(), cellValue.toString());
                                    millis = dateInstant.toEpochMilli();
                                }
                                if (!props.containsKey(field.getName())) {
                                    props.put(field.getName(), millis);
                                }
                            }
                            break;
                        }
                        case ENUM: {
                            EnumField enumField = (EnumField) field;
                            String enumString = (String) rowVal.get(fieldMapping.get(key));
                            int enumIndex = enumField.getIndex(enumString);

                            if (!props.containsKey(field.getName())) {
                                props.put(field.getName(), enumIndex);
                            }
                            break;
                        }
                        case MULTI_ENUM: {
                            MultiEnumField multiEnumField = (MultiEnumField) field;
                            String enumString = (String) rowVal.get(fieldMapping.get(key));
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
//                        String value = (String) rowVal.get(fieldMapping.get(key));
//                        List<Map<String,Object>> lookupRecords = new ArrayList<>();
//                        if (StringUtils.isNotEmpty(value)) {
//                            lookupRecords = getRecordsForMultiLookupField((MultiLookupField)field, value);
//                        }
//                        if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(lookupRecords)) {
//                            props.put(field.getName(), lookupRecords);
//                        }
                            break;
                        }
                        case LOOKUP: {
                            // check with Krishna on how to get data
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
                } catch (Exception ex) {
                    LOGGER.severe("Process Import Exception -- Row No --" + rowNo + " Fields Mapping --" + fieldMapping.get(key));
                    throw new ImportParseException(rowNo, fieldMapping.get(key), ex);
                }
            }

            if (afterImportFunction != null) {
                afterImportFunction.apply(rowNo, rowVal, context);
            }

            ReadingContext reading = FieldUtil.getAsBeanFromMap(props, ReadingContext.class);
            if(groupedContext.containsKey(moduleName)) {
                List<ReadingContext> existingList = groupedContext.get(moduleName);
                existingList.add(reading);
            }
            else {
                ArrayList<ReadingContext> tempList = new ArrayList<ReadingContext>();
                tempList.add(reading);
                groupedContext.put(moduleName, tempList);
            }
        }

//        c.put(ImportAPI.ImportProcessConstants.GROUPED_FIELDS, groupedFields);
        context.put(ImportAPI.ImportProcessConstants.GROUPED_READING_CONTEXT, groupedContext);
//        c.put(FacilioConstants.ContextNames.RECORD_LIST, recordsList);

        return false;
    }

    private List<FacilioField> getFields(ModuleBean modBean, Context context, String moduleName) throws Exception {
        List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
        if (CollectionUtils.isEmpty(fields)) {
            fields = modBean.getAllFields(moduleName);
        }
        if (CollectionUtils.isEmpty(fields)) {
            throw new IllegalArgumentException("Fields not found for module " + moduleName);
        }
        return fields;
    }
}
