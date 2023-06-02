package com.facilio.componentpackage.implementation;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.componentpackage.constants.PackageConstants.FieldXMLConstants;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.field.validation.date.DateValidatorType;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.lang3.StringUtils;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.xml.builder.XMLBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.v3.context.Constants;
import com.facilio.chain.FacilioContext;
import com.facilio.chain.FacilioChain;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.*;
import com.facilio.modules.*;
import java.time.DayOfWeek;

import java.util.*;
import java.util.stream.Collectors;

public class FieldPackageBeanImpl implements PackageBean<FacilioField> {

    @Override
    public List<Long> fetchSystemComponentIdsToPackage() throws Exception {
        return null;
    }

    @Override
    public List<Long> fetchCustomComponentIdsToPackage() throws Exception {
        return null;
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
            if (value instanceof String) {
                fieldElement.element(key).text(String.valueOf(value));
            } else if (value instanceof Map) {
                for (Map.Entry<String, Object> specialProp : ((Map<String, Object>) value).entrySet()) {
                    String specialPropKey = specialProp.getKey();
                    Object specialPropValue = specialProp.getValue();
                    fieldElement.element(specialPropKey).text(String.valueOf(specialPropValue));
                }
            }
        }
    }

    @Override
    public Map<String, String> validateComponentToCreate(List<XMLBuilder> components) throws Exception {
        Map<String, String> fieldNameVsErrorMessage = new HashMap<>();
        ModuleBean moduleBean = Constants.getModBean();

        Map<String, List<String>> moduleNameVsFieldNamesMap = new HashMap<>();
        for (XMLBuilder xmlBuilder : components) {
            XMLBuilder fieldElement = xmlBuilder.getElement(ComponentType.FIELD.getValue());
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
    public Map<String, Long> createComponentFromXML(List<XMLBuilder> components) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        Map<String, Long> uniqueIdentifierVsComponentId = new HashMap<>();
        Map<String, List<FacilioField>> moduleNameVsFields = new HashMap<>();
        Map<String, Map<String, String>> moduleNameVsFieldNameVsUniqueIdentifier = new HashMap<>();

        for (XMLBuilder xmlBuilder : components) {
            XMLBuilder fieldElement = xmlBuilder.getElement(ComponentType.FIELD.getValue());
            if (fieldElement != null) {
                FacilioField facilioField = getFieldFromXMLComponent(fieldElement);
                String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();
                String uniqueIdentifier = fieldElement.getElement(PackageConstants.UNIQUE_IDENTIFIER).getText();

                if (StringUtils.isNotEmpty(moduleName) && !moduleNameVsFields.containsKey(moduleName)) {
                    moduleNameVsFields.put(moduleName, new ArrayList<>());
                }
                moduleNameVsFields.get(moduleName).add(facilioField);

                if(!moduleNameVsFieldNameVsUniqueIdentifier.containsKey(moduleName)) {
                    moduleNameVsFieldNameVsUniqueIdentifier.put(moduleName, new HashMap<>());
                }
                moduleNameVsFieldNameVsUniqueIdentifier.get(moduleName).put(facilioField.getName(), uniqueIdentifier);
            }
        }

        for (Map.Entry<String, List<FacilioField>> moduleFields : moduleNameVsFields.entrySet()) {
            String moduleName = moduleFields.getKey();
            List<FacilioField> fieldsList = moduleFields.getValue();
            FacilioModule module = moduleBean.getModule(moduleName);
            fieldsList = createFields(module, fieldsList);

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
    public void updateComponentFromXML(Map<Long, XMLBuilder> uniqueIdentifierVsComponents) throws Exception {
//        ModuleBean moduleBean = Constants.getModBean();
//        Map<Long, String> newUniqueIdentifierVsComponentId = new HashMap<>();
//
//        for (Map.Entry<String, XMLBuilder> uniqueIdentifierVsComponent : uniqueIdentifierVsComponents.entrySet()) {
//            String uniqueIdentifier = uniqueIdentifierVsComponent.getKey();
//            XMLBuilder xmlBuilder = uniqueIdentifierVsComponent.getValue();
//            XMLBuilder fieldElement = xmlBuilder.getElement(ComponentType.FIELD.getValue());
//            if (fieldElement != null) {
//                String moduleName = fieldElement.getElement(PackageConstants.MODULENAME).getText();
//                FacilioField facilioField = getFieldFromXMLComponent(fieldElement);
//                FacilioModule currModule = moduleBean.getModule(moduleName);
//                facilioField.setFieldId(currModule.getModuleId());
//                updateField(facilioField);
//
//                if (!uniqueIdentifierVsComponentId.containsKey(uniqueIdentifier)){
//                    uniqueIdentifierVsComponentId.put(uniqueIdentifier, facilioField.getFieldId());
////                    newUniqueIdentifierVsComponentId.put(facilioField.getFieldId(), uniqueIdentifier);
//                }
//            }
//        }

//        if (MapUtils.isNotEmpty(newUniqueIdentifierVsComponentId)) {
//            Organization organization = AccountUtil.getCurrentOrg();
//            PackageContext packageContext = PackageUtil.getPackage(organization);
//            PackageUtil.createBulkChangesetMapping(packageContext.getId(), newUniqueIdentifierVsComponentId, packageContext.getVersion(),
//                    ComponentType.FIELD, false);
//        }
    }

    @Override
    public void deleteComponentFromXML(List<Long> ids) throws Exception {

    }

    public List<FacilioField> createFields(FacilioModule module, List<FacilioField> newFields) throws Exception {
        FacilioChain addFieldsChain = TransactionChainFactory.getAddFieldsChain();
        FacilioContext addFieldsContext = addFieldsChain.getContext();
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE, module);
        addFieldsContext.put(FacilioConstants.ContextNames.MODULE_FIELD_LIST, newFields);
        addFieldsChain.execute();

        newFields = (List<FacilioField>) addFieldsContext.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
        return newFields;
    }

    public void updateField(FacilioField field) throws Exception {
        FacilioChain updateFieldChain = FacilioChainFactory.getUpdateFieldChain();

        FacilioContext context = updateFieldChain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_FIELD, field);
        context.put(FacilioConstants.ContextNames.CHECK_FIELD_DISPLAY_NAME_DUPLICATION, Boolean.FALSE);
        updateFieldChain.execute();
    }

    public static FacilioField getFieldFromXMLComponent(XMLBuilder fieldElement) throws Exception {
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

    public static Map<String, Object> fetchAdditionalFieldProps(FacilioField oldField) throws Exception {
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
                fieldProps.put("allowedDate", ((DateField) oldField).getAllowedDate().name());
                fieldProps.put("allowedDays", daysOfWeekProps);
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
                }
                fieldProps.put("specialType", ((BaseLookupField) oldField).getSpecialType());
                fieldProps.put("relatedListDisplayName", ((BaseLookupField) oldField).getRelatedListDisplayName());
                break;

            case FILE:
                fieldProps.put("format", ((FileField) oldField).getFormatEnum().name());
                break;

            default:
                break;
        }
        return fieldProps;
    }

    public static List<Map<String, Object>> getEnumFieldValuesProps(List<EnumFieldValue<Integer>> enumFieldValues) {
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

    public static List<Map<String, Object>> getDaysOfWeekProps(List<DayOfWeek> values) {
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

    public static FacilioField setAdditionalFieldProps(Map<String, Object> fieldProp, XMLBuilder fieldElement) throws Exception {
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
                Double minValue = Double.parseDouble(fieldElement.getElement("minValue").getText());
                Double maxValue = Double.parseDouble(fieldElement.getElement("maxValue").getText());
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
                facilioField = (BaseEnumField) FieldUtil.getAsBeanFromMap(fieldProp, BaseEnumField.class);
                XMLBuilder enumValuesBuilder = fieldElement.getElement("values");
                List<EnumFieldValue<Integer>> enumValues = getEnumFieldValues(enumValuesBuilder);
                ((BaseEnumField) facilioField).setValues(enumValues);
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
                String allowedDateStr = fieldElement.getElement("allowedDate").getText();
                DateValidatorType allowedDate = DateValidatorType.valueOf(allowedDateStr);
                XMLBuilder valuesBuilder = fieldElement.getElement("values");
                List<DayOfWeek> daysOfWeek = getDaysOfWeek(valuesBuilder);
                ((DateField) facilioField).setAllowedDays(daysOfWeek);
                ((DateField) facilioField).setAllowedDate(allowedDate);
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
                String fileFormatString = fieldElement.getElement("format").getText();
                FileInfo.FileFormat fileFormat = FileInfo.FileFormat.valueOf(fileFormatString);
                ((FileField) facilioField).setFormat(fileFormat);
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

                facilioField = (BaseLookupField) FieldUtil.getAsBeanFromMap(fieldProp, BaseLookupField.class);
                ((BaseLookupField) facilioField).setRelatedListDisplayName(relatedListDisplayName);
                ((BaseLookupField) facilioField).setLookupModuleId(lookupModuleId);
                ((BaseLookupField) facilioField).setLookupModule(lookupModule);
                ((BaseLookupField) facilioField).setSpecialType(specialType);
                break;

            default:
                facilioField = (FacilioField) FieldUtil.getAsBeanFromMap(fieldProp, FacilioField.class);
                break;
        }
        return facilioField;
    }

    public static List<EnumFieldValue<Integer>> getEnumFieldValues(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<EnumFieldValue<Integer>> enumFieldValues = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList("value");
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

    public static List<DayOfWeek> getDaysOfWeek(XMLBuilder valuesBuilder) {
        if (valuesBuilder == null) {
            return null;
        }

        List<DayOfWeek> dayOfWeeks = new ArrayList<>();

        List<XMLBuilder> allValues = valuesBuilder.getElementList("value");
        for (XMLBuilder xmlBuilder : allValues) {
            String value = xmlBuilder.getElement("value").getText();
            DayOfWeek dayOfWeek = DayOfWeek.valueOf(value);
            dayOfWeeks.add(dayOfWeek);
        }

        return dayOfWeeks;
    }
}
