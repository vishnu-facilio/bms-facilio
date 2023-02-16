package com.facilio.attribute.context;

import com.facilio.classification.context.ClassificationDataContext;
import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;
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
    private String metricName;
    private Object unit;

    public Object getUnit() {
        return unit;
    }
    public void setUnit(Object unit) {
        this.unit = unit;
    }

    private int unitId;

    private Object value;
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
            case STRING:
            case BIG_STRING:
            case BOOLEAN:
            case DATE_TIME:
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute type");
        }

    }
    public void setClassificationData(ClassificationDataContext classificationDataContext){
        switch (fieldType) {
            case NUMBER:
                FacilioUtil.throwIllegalArgumentException(!((value instanceof Long)||(value instanceof Integer)), "Invalid Field Type Value For " + name);
                classificationDataContext.setNumberValue(FacilioUtil.parseLong(value));
                break;
            case DECIMAL:
                FacilioUtil.throwIllegalArgumentException(!(value instanceof Number), "Invalid Field Type Value For " + name);
                classificationDataContext.setDecimalValue(FacilioUtil.parseDouble(value));
                break;
            case STRING:
                FacilioUtil.throwIllegalArgumentException(!(value instanceof String),"Invalid Field Type Value For " + name);
                classificationDataContext.setTextValue(value.toString());
                break;
            case BIG_STRING:
                FacilioUtil.throwIllegalArgumentException(!(value instanceof String),"Invalid Field Type Value For " + name);
                classificationDataContext.setTextAreaValue(value.toString());
                break;
            case BOOLEAN:
                FacilioUtil.throwIllegalArgumentException(!(value instanceof Boolean),"Invalid Field Type Value For " + name);
                classificationDataContext.setBooleanValue(FacilioUtil.parseBoolean(value));
                break;
            case DATE_TIME:
                FacilioUtil.throwIllegalArgumentException(!((value instanceof Long)||(value instanceof Integer)), "Invalid Field Type Value For " + name);
                classificationDataContext.setDateTimeValue(FacilioUtil.parseLong(value));
                break;

            default:
                throw new IllegalArgumentException("Invalid attribute type");
        }
    }
    public void setDataFromClassificationDataContext(ClassificationDataContext classificationDataContext){
        switch (fieldType){
            case NUMBER:
                this.setValue(classificationDataContext.getNumberValue());
                break;
            case DECIMAL:
                this.setValue(classificationDataContext.getDecimalValue());
                break;
            case STRING:
               this.setValue(classificationDataContext.getTextValue());
                break;
            case BIG_STRING:
                this.setValue(classificationDataContext.getTextAreaValue());
                break;
            case BOOLEAN:
               this.setValue(classificationDataContext.getBooleanValue());
                break;
            case DATE_TIME:
              this.setValue(classificationDataContext.getDateTimeValue());
                break;
            default:
                throw new IllegalArgumentException("Invalid attribute type");
        }
    }
}
