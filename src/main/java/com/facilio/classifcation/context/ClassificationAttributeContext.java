package com.facilio.classifcation.context;

import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ClassificationAttributeContext implements Serializable {

    private long id = -1l;
    private String attributeName;
    private long classificationId = -1;

    private FieldType fieldType;
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
    public FacilioField generateField() {
        if (fieldType == null) {
            throw new IllegalArgumentException("Attribute type cannot be empty");
        }

        FacilioField field;
        switch (fieldType) {
            case NUMBER:
            case DECIMAL:
                field = new NumberField();
                field.setDisplayType(FacilioField.FieldDisplayType.NUMBER);
                field.setDataType(fieldType);
                break;

            default:
                throw new IllegalArgumentException("Invalid attribute type");
        }

        field.setDisplayName(attributeName);
        return field;
    }
}
