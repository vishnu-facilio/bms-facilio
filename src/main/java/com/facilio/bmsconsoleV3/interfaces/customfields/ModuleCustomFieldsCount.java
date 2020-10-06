package com.facilio.bmsconsoleV3.interfaces.customfields;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public interface ModuleCustomFieldsCount {

    String getNewColumnNameForFieldType(Integer fieldTypeInt, List<String> existingColumns);

    static String getColumnName(String[] columns, List<String> existingColumns) {
        if (columns != null && columns.length > 0) {
            if (CollectionUtils.isEmpty(existingColumns)) {
                return columns[0];
            } else {
                for (String column : columns) {
                    if (!existingColumns.contains(column)) {
                        return column;
                    }
                }
            }
        }
        return null;
    }

}
