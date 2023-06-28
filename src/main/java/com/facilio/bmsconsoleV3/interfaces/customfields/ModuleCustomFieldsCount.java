package com.facilio.bmsconsoleV3.interfaces.customfields;

import com.facilio.modules.FieldType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public interface ModuleCustomFieldsCount {

    String getNewColumnNameForFieldType(FieldType fieldType, List<String> existingColumns);

    static String getColumnName(FieldType fieldType, Map<Integer, String[]> typeVsCustomColumns, List<String> existingColumns) {
        List<FieldType> fieldTypes = fieldType.getRelatedFieldTypes();
        for(FieldType dataType : fieldTypes) {
            String[] columns = typeVsCustomColumns.get(dataType.getTypeAsInt());
            if (columns != null && columns.length > 0) {
                for (String column : columns) {
                    if (CollectionUtils.isEmpty(existingColumns) || !existingColumns.contains(column)) {
                        return column;
                    }
                }
            }
        }
        return null;
    }

}
