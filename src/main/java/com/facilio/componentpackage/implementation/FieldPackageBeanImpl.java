package com.facilio.componentpackage.implementation;

import com.facilio.componentpackage.constants.PackageConstants.FieldXMLConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.field.validation.date.DateValidatorType;
import com.facilio.db.criteria.operators.StringOperators;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.Constants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.*;
import com.facilio.fs.FileInfo;
import com.facilio.modules.*;
import java.time.DayOfWeek;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class FieldPackageBeanImpl implements PackageBean<FacilioField> {
    private static final List<String> SYSTEM_FIELD_NAMES_WITH_CUSTOM_CONFIGURATION = Arrays.asList("textContent", "htmlContent");

    @Override
    public Map<Long, Long> fetchSystemComponentIdsToPackage() throws Exception {
        return getFieldIds(false);
    }

    @Override
    public Map<Long, Long> fetchCustomComponentIdsToPackage() throws Exception {
        return getFieldIds(true);
    }

    @Override
    public Map<Long, FacilioField> fetchComponents(List<Long> ids) throws Exception {
        Map<Long, FacilioField> fieldIdVsFieldMap = new HashMap<>();

        int fromIndex = 0;
        int toIndex = Math.min(ids.size(), 250);
        ModuleBean moduleBean = Constants.getModBean();

        List<Long> idsSubList;
        while (fromIndex < ids.size()) {
            idsSubList = ids.subList(fromIndex, toIndex);

            List<FacilioField> fieldList = moduleBean.getFields(idsSubList);
            fieldList.forEach(field -> fieldIdVsFieldMap.put(field.getFieldId(), field));

            fromIndex = toIndex;
            toIndex = Math.min((toIndex + 250), ids.size());
        }

        return fieldIdVsFieldMap;
    }

    @Override
    public void convertToXMLComponent(FacilioField facilioField, XMLBuilder fieldElement) throws Exception {
        fieldElement.element(PackageConstants.NAME).text(facilioField.getName());
        fieldElement.element(PackageConstants.DISPLAY_NAME).text(facilioField.getDisplayName());
        fieldElement.element(PackageConstants.MODULENAME).text(facilioField.getModule().getName());
        fieldElement.element(FieldXMLConstants.REQUIRED).text(String.valueOf(facilioField.isRequired()));
        fieldElement.element(FieldXMLConstants.IS_DEFAULT).text(String.valueOf(facilioField.isDefault()));
        fieldElement.element(FieldXMLConstants.DATA_TYPE).text(String.valueOf(facilioField.getDataType()));
        fieldElement.element(FieldXMLConstants.MAIN_FIELD).text(String.valueOf(facilioField.isMainField()));
        fieldElement.element(FieldXMLConstants.DISPLAY_TYPE).text(String.valueOf(facilioField.getDisplayTypeInt()));

        Map<String, Object> additionalFieldProps = fetchAdditionalFieldProps(facilioField);
        for (Map.Entry<String, Object> entry : additionalFieldProps.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            value = (value == null) ? "" : value;

            if (value instanceof List) {
                XMLBuilder valuesListElement = fieldElement.element(key);
                for (Map<String, Object> keyValuePair : (List<Map<String, Object>>) value) {
                    XMLBuilder valueElement = valuesListElement.element(PackageConstants.VALUE_ELEMENT);
                    for (Map.Entry<String, Object> specialProp : keyValuePair.entrySet()) {
                        String specialPropKey = specialProp.getKey();
                        Object specialPropValue = specialProp.getValue();
                        valueElement.element(specialPropKey).text(String.valueOf(specialPropValue));
                    }
                }
            } else {
                fieldElement.element(key).text(String.valueOf(value));
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> fieldNameVsErrorMessage = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        Map<String, List<String>> moduleNameVsFieldNamesMap = new HashMap<>();
        for (XMLBuilder fieldElement : components) {
            String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();
            String fieldName = fieldElement.getElement(PackageConstants.NAME).getText();

            if (!moduleNameVsFieldNamesMap.containsKey(moduleName)) {
                moduleNameVsFieldNamesMap.put(moduleName, new ArrayList<>());
            }
            moduleNameVsFieldNamesMap.get(moduleName).add(fieldName);
        }

        for (Map.Entry<String, List<String>> entry : moduleNameVsFieldNamesMap.entrySet()) {
            String moduleName = entry.getKey();
            List<String> fieldNames = entry.getValue();

            Criteria fieldNamCriteria = new Criteria();
            FacilioField fieldNameField = FieldFactory.getStringField("NAME", "name", ModuleFactory.getFieldsModule());
            fieldNamCriteria.addAndCondition(CriteriaAPI.getCondition(fieldNameField, StringUtils.join(fieldNames, ","), StringOperators.IS));

            List<FacilioField> moduleFields = moduleBean.getAllFields(moduleName, null, null, fieldNamCriteria);
            if (CollectionUtils.isNotEmpty(moduleFields)) {
                List<String> conflictedFieldNames = moduleFields.stream().map(FacilioField::getName).collect(Collectors.toList());
                for (String conflictFieldName : conflictedFieldNames) {
                    fieldNameVsErrorMessage.put(conflictFieldName, PackageConstants.FieldXMLConstants.DUPLICATE_FIELD_ERROR);
                }
            }
        }

        return fieldNameVsErrorMessage;
    }

    @Override
    public List<Long> getDeletedComponentIds(List<Long> componentIds) throws Exception {
        return null;
    }

    @Override
    public Map<String, Long> getExistingIdsByXMLData(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsFieldId = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            String uniqueIdentifier = idVsData.getKey();
            XMLBuilder fieldElement = idVsData.getValue();
            String fieldName = fieldElement.getElement(PackageConstants.NAME).getText();
            String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();

            FacilioModule module = moduleBean.getModule(moduleName);
            if (module == null) {
                LOGGER.info("###Sandbox - Module not found - " + moduleName);
                continue;
            }

            FacilioField field = getFieldFromDB(module, fieldName);
            if (fieldName.equals("id") || fieldName.equals("siteId")) {
                field = getFieldFromDB(module, fieldName);
            }
            if (field != null) {
                uniqueIdentifierVsFieldId.put(uniqueIdentifier, field.getFieldId());
            } else {
                LOGGER.info("###Sandbox - Field not found - ModuleName - " + moduleName + " FieldName - " + fieldName);
            }
        }
        return uniqueIdentifierVsFieldId;
    }

    @Override
    public Map<String, Long> createComponentFromXML(Map<String, XMLBuilder> uniqueIdVsXMLData) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, List<FacilioField>> moduleNameVsFields = new HashMap<>();
        Map<String, Map<String, String>> moduleNameVsFieldNameVsUniqueIdentifier = new HashMap<>();

        for (Map.Entry<String, XMLBuilder> idVsData : uniqueIdVsXMLData.entrySet()) {
            XMLBuilder fieldElement = idVsData.getValue();
            FacilioField facilioField = getFieldFromXMLComponent(fieldElement);
            String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();

            if (StringUtils.isNotEmpty(moduleName) && !moduleNameVsFields.containsKey(moduleName)) {
                moduleNameVsFields.put(moduleName, new ArrayList<>());
            }
            moduleNameVsFields.get(moduleName).add(facilioField);

            if(!moduleNameVsFieldNameVsUniqueIdentifier.containsKey(moduleName)) {
                moduleNameVsFieldNameVsUniqueIdentifier.put(moduleName, new HashMap<>());
            }
            moduleNameVsFieldNameVsUniqueIdentifier.get(moduleName).put(facilioField.getName(), idVsData.getKey());
        }

        for (Map.Entry<String, List<FacilioField>> moduleFields : moduleNameVsFields.entrySet()) {
            // create module fields
            String moduleName = moduleFields.getKey();
            List<FacilioField> fieldsList = moduleFields.getValue();
            FacilioModule module = moduleBean.getModule(moduleName);
            if (module == null) {
                LOGGER.info("###Sandbox - Module not found - " + moduleName);
                continue;
            }
            fieldsList = createFields(module, fieldsList);

            // set uniqueIdentifier vs fieldId
            if (moduleNameVsFieldNameVsUniqueIdentifier.containsKey(moduleName)) {
                Map<String, String> fieldNameVsUniqueIdentifier = moduleNameVsFieldNameVsUniqueIdentifier.get(moduleName);
                for (FacilioField field : fieldsList) {
                    String fieldName = field.getName();
                    String uniqueIdentifier = fieldNameVsUniqueIdentifier.get(fieldName);

                    uniqueIdentifierVsComponentId.put(uniqueIdentifier, field.getFieldId());
                }
            }
        }
        return uniqueIdentifierVsComponentId;
    }

    @Override
    public void updateComponentFromXML(Map<Long, XMLBuilder> idVsXMLComponents) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        for (Map.Entry<Long, XMLBuilder> uniqueIdentifierVsComponent : idVsXMLComponents.entrySet()) {
            Long fieldId = uniqueIdentifierVsComponent.getKey();
            if (fieldId != null && fieldId > 0) {
                XMLBuilder fieldElement = uniqueIdentifierVsComponent.getValue();
                String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();
                FacilioField facilioField = getFieldFromXMLComponent(fieldElement);
                facilioField.setFieldId(fieldId);

                // set additional details for EnumFieldValues
                if (facilioField.getDataTypeEnum() == FieldType.ENUM || facilioField.getDataTypeEnum() == FieldType.MULTI_ENUM) {
                    FacilioField dbField = moduleBean.getField(fieldId);
                    List<EnumFieldValue<Integer>> dbEnumFieldValues = ((BaseEnumField) dbField).getValues();
                    Map<Integer, Long> indexVsIdMap = dbEnumFieldValues.stream().collect(Collectors.toMap(EnumFieldValue::getIndex, EnumFieldValue::getId));

                    for (EnumFieldValue<Integer> value : ((BaseEnumField) facilioField).getValues()) {
                        if (indexVsIdMap.containsKey(value.getIndex())) {
                            value.setId(indexVsIdMap.get(value.getIndex()));
                            value.setFieldId(fieldId);
                        }
                    }
                }

                updateField(facilioField);
            }
        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        if (CollectionUtils.isNotEmpty(ids)) {
            moduleBean.deleteFields(ids);
        }
    }

    private Map<Long, Long> getFieldIds(boolean fetchCustom) throws Exception {
        Map<Long, Long> fieldIdVsModuleId = new HashMap<>();
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();

        List<FacilioField> selectableFields = new ArrayList<FacilioField>() {{
            add(FieldFactory.getModuleIdField(fieldsModule));
            add(FieldFactory.getNumberField("fieldId", "FIELDID", fieldsModule));
        }};

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(selectableFields)
                .table(fieldsModule.getTableName())
                .innerJoin("Modules").on("Fields.MODULEID = Modules.MODULEID")
                .andCondition(CriteriaAPI.getCondition("MODULE_TYPE", "type", StringUtils.join(PackageBeanUtil.INCLUDE_MODULE_TYPES, ","), NumberOperators.EQUALS))
                ;

        if (!fetchCustom) {
            selectBuilder.andCriteria(additionalCriteria());
        } else {
            selectBuilder.andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", Boolean.FALSE.toString(), BooleanOperators.IS))
                    .andCondition(CriteriaAPI.getCondition("Fields.NAME", "name", StringUtils.join(SYSTEM_FIELD_NAMES_WITH_CUSTOM_CONFIGURATION, ","), StringOperators.ISN_T))
                    .andCustomWhere("(COLUMN_NAME IS NULL OR COLUMN_NAME LIKE '%_CF%')");
        }

        List<Map<String, Object>> fieldProps = selectBuilder.get();

        if (CollectionUtils.isNotEmpty(fieldProps)) {
            long moduleId, fieldId;
            for (Map<String, Object> prop : fieldProps) {
                moduleId = prop.containsKey("moduleId") ? (Long) prop.get("moduleId") : -1;
                fieldId = prop.containsKey("fieldId") ? (Long) prop.get("fieldId") : -1;
                fieldIdVsModuleId.put(fieldId, moduleId);
            }
        }

        return fieldIdVsModuleId;
    }

    private Criteria additionalCriteria() {
        Criteria columnNameCriteria = new Criteria();
        columnNameCriteria.addAndCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", Boolean.FALSE.toString(), BooleanOperators.IS));
        columnNameCriteria.addAndCondition(CriteriaAPI.getCondition("COLUMN_NAME", "columnName", "", CommonOperators.IS_NOT_EMPTY));
        columnNameCriteria.addAndCondition(CriteriaAPI.getCondition("COLUMN_NAME", "columnName", "_CF", StringOperators.DOESNT_CONTAIN));

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("IS_DEFAULT", "isDefault", Boolean.TRUE.toString(), BooleanOperators.IS));
        criteria.addOrCondition(CriteriaAPI.getCondition("Fields.NAME", "name", StringUtils.join(SYSTEM_FIELD_NAMES_WITH_CUSTOM_CONFIGURATION, ","), StringOperators.IS));
        criteria.orCriteria(columnNameCriteria);

        return criteria;
    }

    private List<FacilioField> createFields(FacilioModule module, List<FacilioField> newFields) throws Exception {
        FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
        FacilioContext addFieldsContext = addFieldsChain.getContext();
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE, module);
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, newFields);
        addFieldsChain.execute();

        newFields = (List<FacilioField>) addFieldsContext.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
        return newFields;
    }

    private void updateField(FacilioField field) throws Exception {
        FacilioChain updateFieldChain = FacilioChainFactory.getUpdateFieldChain();

        FacilioContext context = updateFieldChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);
        context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, Boolean.FALSE);
        updateFieldChain.execute();
    }

    private FacilioField getFieldFromXMLComponent(XMLBuilder fieldElement) throws Exception {
        Map<String, Object> fieldProp = new HashMap<>();
        fieldProp.put("name", fieldElement.getElement(PackageConstants.NAME).getText());
        fieldProp.put("moduleName", fieldElement.getElement(PackageConstants.MODULENAME).getText());
        fieldProp.put("displayName", fieldElement.getElement(PackageConstants.DISPLAY_NAME).getText());
        fieldProp.put("dataType", Integer.parseInt(fieldElement.getElement(FieldXMLConstants.DATA_TYPE).getText()));
        fieldProp.put("isRequired", Boolean.parseBoolean(fieldElement.getElement(FieldXMLConstants.REQUIRED).getText()));
        fieldProp.put("isDefault", Boolean.parseBoolean(fieldElement.getElement(FieldXMLConstants.IS_DEFAULT).getText()));
        fieldProp.put("displayType", Integer.parseInt(fieldElement.getElement(FieldXMLConstants.DISPLAY_TYPE).getText()));
        fieldProp.put("isMainField", Boolean.parseBoolean(fieldElement.getElement(FieldXMLConstants.MAIN_FIELD).getText()));

        FacilioField facilioField = setAdditionalFieldProps(fieldProp, fieldElement);
        return facilioField;
    }

    private Map<String, Object> fetchAdditionalFieldProps(FacilioField oldField) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();

        Map<String, Object> fieldProps = new HashMap<>();

        switch (oldField.getDataTypeEnum()) {
            case STRING:
            case BIG_STRING:
                fieldProps.put("regex", ((StringField) oldField).getRegex());
                fieldProps.put("maxLength", ((StringField) oldField).getMaxLength());
                break;

            case NUMBER:
            case DECIMAL:
                fieldProps.put("unit", ((NumberField) oldField).getUnit());
                fieldProps.put("unitId", ((NumberField) oldField).getUnitId());
                fieldProps.put("metric", ((NumberField) oldField).getMetric());
                fieldProps.put("minValue", ((NumberField) oldField).getMinValue());
                fieldProps.put("maxValue", ((NumberField) oldField).getMaxValue());
                fieldProps.put("counterField", ((NumberField) oldField).getCounterField());
                break;

            case ENUM:
            case MULTI_ENUM:
                List<Map<String, Object>> enumValuesProps = getEnumFieldValuesProps(((BaseEnumField) oldField).getValues());
                fieldProps.put("values", enumValuesProps);
                break;

            case SYSTEM_ENUM:
                fieldProps.put("enumName", ((SystemEnumField) oldField).getEnumName());
                break;

            case STRING_SYSTEM_ENUM:
                fieldProps.put("enumName", ((StringSystemEnumField) oldField).getEnumName());
                break;

            case URL_FIELD:
                fieldProps.put("showAlt", ((UrlField) oldField).getShowAlt());
                fieldProps.put("target", ((UrlField) oldField).getTarget().toString());
                break;

            case DATE:
            case DATE_TIME:
                List<Map<String, Object>> daysOfWeekProps = getDaysOfWeekProps(((DateField) oldField).getAllowedDays());
                fieldProps.put("allowedDays", daysOfWeekProps);
                if (((DateField) oldField).getAllowedDate() != null) {
                    fieldProps.put("allowedDate", ((DateField) oldField).getAllowedDate().name());
                }
                break;

            case BOOLEAN:
                fieldProps.put("trueVal", ((BooleanField) oldField).getTrueVal());
                fieldProps.put("falseVal", ((BooleanField) oldField).getFalseVal());
                break;

            case LARGE_TEXT:
                fieldProps.put("skipSizeCheck", ((LargeTextField)oldField).getSkipSizeCheck());
                break;

            case LOOKUP:
            case MULTI_LOOKUP:
                long lookupModuleId = ((BaseLookupField) oldField).getLookupModuleId();
                if (lookupModuleId > 0) {
                    FacilioModule lookupModule = moduleBean.getModule(lookupModuleId);
                    fieldProps.put("lookupModuleName", lookupModule.getName());
                } else {
                    fieldProps.put("lookupModuleName", null);
                }
                fieldProps.put("specialType", ((BaseLookupField) oldField).getSpecialType());
                fieldProps.put("relatedListDisplayName", ((BaseLookupField) oldField).getRelatedListDisplayName());
                break;

            case FILE:
                if (((FileField) oldField).getFormatEnum() != null) {
                    fieldProps.put("format", ((FileField) oldField).getFormatEnum().name());
                }
                break;

            default:
                break;
        }
        return fieldProps;
    }

    private List<Map<String, Object>> getEnumFieldValuesProps(List<EnumFieldValue<Integer>> enumFieldValues) {
        if (CollectionUtils.isEmpty(enumFieldValues)) {
            return null;
        }

        List<Map<String, Object>> propsList = new ArrayList<>();

        for (EnumFieldValue<Integer> enumFieldValue : enumFieldValues) {
            Map<String, Object> additionalProp = new HashMap<>();
            additionalProp.put("index", enumFieldValue.getIndex());
            additionalProp.put("value", enumFieldValue.getValue());
            additionalProp.put("visible", enumFieldValue.getVisible());
            additionalProp.put("sequence", enumFieldValue.getSequence());
            propsList.add(additionalProp);
        }

        return propsList;
    }

    private List<Map<String, Object>> getDaysOfWeekProps(List<DayOfWeek> values) {
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        List<Map<String, Object>> propsList = new ArrayList<>();
        for (DayOfWeek value : values) {
            Map<String, Object> additionalProp = new HashMap<>();
            additionalProp.put("value", value.name());
            propsList.add(additionalProp);
        }

        return propsList;
    }

    private FacilioField setAdditionalFieldProps(Map<String, Object> fieldProp, XMLBuilder fieldElement) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        int dataTypeInt = (int) (fieldProp.get("dataType"));
        FacilioField facilioField;

        switch (FieldType.getCFType(dataTypeInt)) {
            case STRING:
            case BIG_STRING:
                String regex = fieldElement.getElement("regex").getText();
                int maxLength = Integer.parseInt(fieldElement.getElement("maxLength").getText());
                facilioField = (StringField) FieldUtil.getAsBeanFromMap(fieldProp, StringField.class);
                ((StringField)facilioField).setRegex(regex);
                ((StringField) facilioField).setMaxLength(maxLength);
                break;

            case NUMBER:
            case DECIMAL:
                String unit = fieldElement.getElement("unit").getText();
                int unitId = Integer.parseInt(fieldElement.getElement("unitId").getText());
                int metric = Integer.parseInt(fieldElement.getElement("metric").getText());
                Double minValue = StringUtils.isEmpty(fieldElement.getElement("minValue").getText()) ? null :
                        Double.parseDouble(fieldElement.getElement("minValue").getText());
                Double maxValue = StringUtils.isEmpty(fieldElement.getElement("maxValue").getText()) ? null :
                        Double.parseDouble(fieldElement.getElement("maxValue").getText());
                boolean counterField = Boolean.parseBoolean(fieldElement.getElement("counterField").getText());
                facilioField = (NumberField) FieldUtil.getAsBeanFromMap(fieldProp, NumberField.class);
                ((NumberField) facilioField).setUnit(unit);
                ((NumberField) facilioField).setUnitId(unitId);
                ((NumberField) facilioField).setMetric(metric);
                ((NumberField) facilioField).setMinValue(minValue);
                ((NumberField) facilioField).setMaxValue(maxValue);
                ((NumberField) facilioField).setCounterField(counterField);
                break;

            case ENUM:
            case MULTI_ENUM:
                XMLBuilder enumValuesBuilder = fieldElement.getElement("values");
                List<EnumFieldValue<Integer>> enumValues = getEnumFieldValues(enumValuesBuilder);
                if (dataTypeInt == 8) {
                    facilioField = (EnumField) FieldUtil.getAsBeanFromMap(fieldProp, EnumField.class);
                    ((EnumField) facilioField).setValues(enumValues);
                } else {
                    facilioField = (MultiEnumField) FieldUtil.getAsBeanFromMap(fieldProp, MultiEnumField.class);
                    ((MultiEnumField) facilioField).setValues(enumValues);
                }
                break;

            case SYSTEM_ENUM:
                facilioField = (SystemEnumField) FieldUtil.getAsBeanFromMap(fieldProp, SystemEnumField.class);
                String enumName = fieldElement.getElement("enumName").getText();
                ((SystemEnumField) facilioField).setEnumName(enumName);
                break;

            case STRING_SYSTEM_ENUM:
                facilioField = (StringSystemEnumField) FieldUtil.getAsBeanFromMap(fieldProp, StringSystemEnumField.class);
                String stringEnumName = fieldElement.getElement("enumName").getText();
                ((StringSystemEnumField) facilioField).setEnumName(stringEnumName);
                break;

            case URL_FIELD:
                facilioField = (UrlField) FieldUtil.getAsBeanFromMap(fieldProp, UrlField.class);
                boolean showAlt = Boolean.parseBoolean(fieldElement.getElement("showAlt").getText());
                String targetStr = fieldElement.getElement("target").getText();
                UrlField.UrlTarget urlTarget = UrlField.UrlTarget.valueOf(targetStr);
                ((UrlField) facilioField).setShowAlt(showAlt);
                ((UrlField) facilioField).setTarget(urlTarget);
                break;

            case DATE:
            case DATE_TIME:
                facilioField = (DateField) FieldUtil.getAsBeanFromMap(fieldProp, DateField.class);
                if (fieldElement.getElement("allowedDate") != null) {
                    String allowedDateStr = fieldElement.getElement("allowedDate").getText();
                    DateValidatorType allowedDate = DateValidatorType.valueOf(allowedDateStr);
                    ((DateField) facilioField).setAllowedDate(allowedDate);
                }
                XMLBuilder valuesBuilder = fieldElement.getElement("allowedDays");
                List<DayOfWeek> daysOfWeek = getDaysOfWeek(valuesBuilder);
                ((DateField) facilioField).setAllowedDays(daysOfWeek);
                break;

            case BOOLEAN:
                facilioField = (BooleanField) FieldUtil.getAsBeanFromMap(fieldProp, BooleanField.class);
                String trueVal = fieldElement.getElement("trueVal").getText();
                String falseVal = fieldElement.getElement("falseVal").getText();
                ((BooleanField) facilioField).setTrueVal(trueVal);
                ((BooleanField) facilioField).setFalseVal(falseVal);
                break;

            case LARGE_TEXT:
                facilioField = (LargeTextField) FieldUtil.getAsBeanFromMap(fieldProp, LargeTextField.class);
                boolean skipSizeCheck = Boolean.parseBoolean(fieldElement.getElement("skipSizeCheck").getText());
                ((LargeTextField) facilioField).setSkipSizeCheck(skipSizeCheck);
                break;

            case FILE:
                facilioField = (FileField) FieldUtil.getAsBeanFromMap(fieldProp, FileField.class);
                if (fieldElement.getElement("format") != null) {
                    String fileFormatString = fieldElement.getElement("format").getText();
                    FileInfo.FileFormat fileFormat = FileInfo.FileFormat.valueOf(fileFormatString);
                    ((FileField) facilioField).setFormat(fileFormat);
                }
                break;

            case LOOKUP:
            case MULTI_LOOKUP:
                long lookupModuleId = -1;
                FacilioModule lookupModule = null;
                String specialType = fieldElement.getElement("specialType").getText();
                String lookupModuleName = fieldElement.getElement("lookupModuleName").getText();
                String relatedListDisplayName = fieldElement.getElement("relatedListDisplayName").getText();
                if (StringUtils.isNotEmpty(lookupModuleName)) {
                    lookupModule = moduleBean.getModule(lookupModuleName);
                    if (lookupModule != null) lookupModuleId = lookupModule.getModuleId();
                }

                if (dataTypeInt == 7) {
                    facilioField = (LookupField) FieldUtil.getAsBeanFromMap(fieldProp, LookupField.class);
                    ((LookupField) facilioField).setRelatedListDisplayName(relatedListDisplayName);
                    ((LookupField) facilioField).setLookupModuleId(lookupModuleId);
                    ((LookupField) facilioField).setLookupModule(lookupModule);
                    ((LookupField) facilioField).setSpecialType(specialType);
                } else {
                    facilioField = (MultiLookupField) FieldUtil.getAsBeanFromMap(fieldProp, MultiLookupField.class);
                    ((MultiLookupField) facilioField).setRelatedListDisplayName(relatedListDisplayName);
                    ((MultiLookupField) facilioField).setLookupModuleId(lookupModuleId);
                    ((MultiLookupField) facilioField).setLookupModule(lookupModule);
                    ((MultiLookupField) facilioField).setSpecialType(specialType);
                }
                break;

            case CURRENCY_FIELD:
                facilioField = (CurrencyField) FieldUtil.getAsBeanFromMap(fieldProp, CurrencyField.class);
                break;

            default:
                facilioField = (FacilioField) FieldUtil.getAsBeanFromMap(fieldProp, FacilioField.class);
                break;
        }
        return facilioField;
    }

    private List<EnumFieldValue<Integer>> getEnumFieldValues(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<EnumFieldValue<Integer>> enumFieldValues = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList(PackageConstants.VALUE_ELEMENT);
        for (XMLBuilder xmlBuilder : allValues) {
            String value = xmlBuilder.getElement("value").getText();
            Integer index = Integer.valueOf(xmlBuilder.getElement("index").getText());
            int sequence = Integer.parseInt(xmlBuilder.getElement("sequence").getText());
            boolean visible = Boolean.parseBoolean(xmlBuilder.getElement("visible").getText());

            EnumFieldValue<Integer> enumFieldValue = new EnumFieldValue<>(index, value, sequence, visible);
            enumFieldValues.add(enumFieldValue);
        }

        return enumFieldValues;
    }

    private List<DayOfWeek> getDaysOfWeek(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList(PackageConstants.VALUE_ELEMENT);
        for (XMLBuilder xmlBuilder : allValues) {
            String value = xmlBuilder.getElement("value").getText();
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(value);
            dayOfWeeks.add(dayOfWeek);
        }

        return dayOfWeeks;
    }

    private FacilioField getFieldFromDB(FacilioModule module, String fieldName) throws Exception {
        FacilioModule fieldsModule = ModuleFactory.getFieldsModule();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(fieldsModule.getTableName())
                .select(Collections.singleton(FieldFactory.getNumberField("fieldId", "FIELDID", fieldsModule)))
                .andCondition(CriteriaAPI.getCondition("NAME", "name", fieldName, StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        Map<String, Object> fieldObj = selectBuilder.fetchFirst();

        if (MapUtils.isNotEmpty(fieldObj)) {
            return FieldUtil.getAsBeanFromMap(fieldObj, FacilioField.class);
        }
        return null;
    }
}
