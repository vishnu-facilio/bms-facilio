package com.facilio.fields.fieldBuilder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fields.context.FieldListType;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;


@Getter
public class FieldConfig {

    private final Map<String, FieldListHandler> fieldListTypeHandlerMap = new HashMap<>();
    private ViewFieldListHandler viewFieldListHandler;

    public List<String> getExcludeFields() {
        return Collections.unmodifiableList(excludeFields);
    }

    public Map<AccountUtil.FeatureLicense, List<String>> getLicenseBasedFieldsMap() {
        return Collections.unmodifiableMap(licenseBasedFieldsMap);
    }

    public Map<String, FieldListHandler> getFieldListTypeHandlerMap() {
        return Collections.unmodifiableMap(fieldListTypeHandlerMap);
    }

    private final List<String> excludeFields = new ArrayList<>();
    private final Map<AccountUtil.FeatureLicense, List<String>> licenseBasedFieldsMap = new HashMap<>();

    public FieldListHandler sortFields() {
        return addType(FieldListType.SORTABLE);
    }
    public FieldListHandler advancedFields() {
        return addType(FieldListType.ADVANCED_FILTER_FIELDS);
    }
    public FieldListHandler pageBuilderCriteriaFields() {
        return addType(FieldListType.PAGE_BUILDER_CRITERIA_FIELDS);
    }
    public FieldListHandler summaryWidgetFields() {
        FieldListHandler fieldListHandler = addType(FieldListType.SUMMARY_WIDGET_FIELDS);
        if (!fieldListTypeHandlerMap.containsKey(FieldListType.RELATIONSHIP_SUMMARY_WIDGET_FIELDS.getName())) {
            fieldListTypeHandlerMap.put(FieldListType.RELATIONSHIP_SUMMARY_WIDGET_FIELDS.getName(), fieldListHandler);
        }
        return fieldListHandler;
    }

    public FieldListHandler relationshipSummaryFields() {
        String fieldListTypeName = FieldListType.RELATIONSHIP_SUMMARY_WIDGET_FIELDS.getName();
        fieldListTypeHandlerMap.put(fieldListTypeName, new FieldListHandler(this));
        return fieldListTypeHandlerMap.get(fieldListTypeName);
    }

    private FieldListHandler addType(FieldListType fieldListType) {
        String fieldListTypeName = fieldListType.getName();
        if (!fieldListTypeHandlerMap.containsKey(fieldListTypeName)) {
            fieldListTypeHandlerMap.put(fieldListTypeName, new FieldListHandler(this));
        }
        return fieldListTypeHandlerMap.get(fieldListTypeName);
    }

    public ViewFieldListHandler viewFields() {
        viewFieldListHandler = new ViewFieldListHandler(this);
        return this.viewFieldListHandler;
    }

    /**
     * fields in excludeFields are not allowed to fetch in any of the fieldListType in the defined module
     * @param fieldNames list of fieldNames which should not be fetched for the module
     * @return FieldConfig
     * @throws Exception if exclude fields are configuired after the fieldConfigurations
     */
    @SneakyThrows
    public FieldConfig exclude(List<String> fieldNames)  {
        if(CollectionUtils.isNotEmpty(fieldNames)) {
            this.excludeFields.addAll(fieldNames);
        }
        return this;
    }

    /**
     * values in licenseBasedFields are allowed to be in the list only if the license(key) is enabled
     * @param license license to be enabled to display the fields
     * @param fieldNames list of fields that belongs to the license
     * @return FieldConfig
     */
    public FieldConfig addLicenseBasedFields(AccountUtil.FeatureLicense license, List<String> fieldNames) {
        if(license != null && CollectionUtils.isNotEmpty(fieldNames)) {
            licenseBasedFieldsMap.put(license, fieldNames);
        }
        return this;
    }

}
