package com.facilio.attribute.context;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class ClassificationAttributeContext extends V3Context {
    private String name;
    private String description;
    private FieldType fieldType;
    private int metric;
    private int unitId;
    public void setFieldType(int fieldType) {
        this.fieldType = FieldType.getCFType(fieldType);
    }
    public int getFieldType() {
        if (fieldType == null) {
            return -1;
        }
        return fieldType.getTypeAsInt();
    }

    @JsonIgnore
    public void checkFieldType() {
        if (fieldType == null) {
            throw new IllegalArgumentException("Attribute type cannot be empty");
        }
        switch (fieldType) {
            case NUMBER:
            case DECIMAL:
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute type");
        }

    }
}
