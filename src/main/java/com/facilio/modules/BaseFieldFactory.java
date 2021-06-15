package com.facilio.modules;

import com.facilio.modules.fields.*;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BaseFieldFactory {
    // Have to more methods here

    public static FacilioField getIdField() {
        return getIdField(null);
    }

    public static FacilioField getIdField(FacilioModule module) {
        FacilioField field = new FacilioField();
        field.setName("id");
        field.setDataType(FieldType.ID);
        field.setDisplayName("Id");
        field.setColumnName("ID");
        if (module != null) {
            field.setModule(module);
        }
        return field;
    }

    public static FacilioField getField(String name, String colName, FacilioModule module, FieldType type) {
        return getField(name, null, colName, module, type);
    }

    public static FacilioField getField(String name, String displayName, String colName, FacilioModule module,
                                        FieldType type) {
        FacilioField columnFld = null;
        switch (type) {
            case NUMBER:
            case DECIMAL:
                columnFld = new NumberField();
                break;
            case BOOLEAN:
                columnFld = new BooleanField();
                break;
            case LOOKUP:
                columnFld = new LookupField();
                break;
            case ENUM:
                columnFld = new EnumField();
                break;
            case SYSTEM_ENUM:
                columnFld = new SystemEnumField();
                break;
            case FILE:
                columnFld = new FileField();
                break;
            case LINE_ITEM:
                columnFld = new LineItemField();
                break;
            case MULTI_LOOKUP:
                columnFld = new MultiLookupField();
                break;
            case MULTI_ENUM:
                columnFld = new MultiEnumField();
                break;
            case STRING_SYSTEM_ENUM:
                columnFld = new StringSystemEnumField();
                break;
            case LARGE_TEXT:
                columnFld = new LargeTextField();
                break;
            default:
                columnFld = new FacilioField();
                break;
        }
        columnFld.setName(name);
        if (displayName != null) {
            columnFld.setDisplayName(displayName);
        }

        columnFld.setColumnName(colName);
        columnFld.setDataType(type);
        if (module != null) {
            columnFld.setModule(module);
        }
        return columnFld;
    }



    public static Map<String, FacilioField> getAsMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getName, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }

    public static FacilioField filterField (Collection<FacilioField> fields, String fieldName) {
        Optional<FacilioField> field = fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst();
        if (field.isPresent()) {
            return field.get();
        }
        else {
            return null;
        }
    }

    public static Map<Long, FacilioField> getAsIdMap(Collection<FacilioField> fields) {
        return fields.stream()
                .collect(Collectors.toMap(FacilioField::getFieldId, Function.identity(), (prevValue, curValue) -> {
                    return prevValue;
                }));
    }
}
