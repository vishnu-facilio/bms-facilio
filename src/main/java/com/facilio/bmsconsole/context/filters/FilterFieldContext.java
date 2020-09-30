package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;

import java.util.List;

public class FilterFieldContext {

    public FilterFieldContext() {

    }

    private FacilioField field;
    public FilterFieldContext(FacilioField field) {
        this.field = field;
    }

    public String getName() {
        return field == null ? null : field.getName();
    }

    public boolean isDefault() {
        return field == null ? false : field.isDefault();
    }

    public String getDisplayName() {
        return field == null ? null : field.getDisplayName();
    }

    public FieldType getDataTypeEnum() {
        return field == null ? null : field.getDataTypeEnum();
    }
    public Integer getDataType() {
        return field == null ? null : field.getDataType();
    }

    public FacilioField.FieldDisplayType getDisplayTypeEnum() {
        return field == null ? null : field.getDisplayType();
    }
    public Integer getDisplayType() {
        return field == null ? null : field.getDisplayTypeInt();
    }

    private List<Operator> operators;
    public List<Operator> getOperators() {
        return operators;
    }
    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }
}
