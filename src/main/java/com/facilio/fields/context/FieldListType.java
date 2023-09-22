package com.facilio.fields.context;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum FieldListType {
    SORTABLE("sortable"),
    VIEW_FIELDS("viewFields"),
    ADVANCED_FILTER_FIELDS("advancedFilterFields"),
    PAGE_BUILDER_CRITERIA_FIELDS("pageBuilderCriteriaFields", ADVANCED_FILTER_FIELDS);

    FieldListType(String name) {
        this.name = name;
    }

    FieldListType(String name, FieldListType fieldListTypeConfig) {
        this.name = name;
        this.fieldListTypeConfig = fieldListTypeConfig;
    }

    public static FieldListType getValueOfType(String name) {
        if (StringUtils.isNotEmpty(name)) {
            for (FieldListType fieldListType : FieldListType.values()) {
                if (fieldListType.name.equalsIgnoreCase(name)) {
                    return fieldListType;
                }
            }
        }
        return null;
    }

    private final String name;
    private FieldListType fieldListTypeConfig;
}
