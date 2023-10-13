package com.facilio.report.context;

import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

public class ReportHavingContext {

    private String fieldName;
    @Getter
    @Setter
    private String moduleName;
    public String getFieldName() {
        if (StringUtils.isEmpty(fieldName) && getField() != null) {
            fieldName = field.getName();
        }
        return fieldName;
    }
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    private long fieldId = -1;
    public long getFieldId() {
        if (fieldId == -1 && getField() != null) {
            fieldId = field.getFieldId();
        }
        return fieldId;
    }
    public void setFieldId(long fieldId) {
        this.fieldId = fieldId;
    }

    @JsonIgnore
    private FacilioField field;
    public FacilioField getField() {
        return field;
    }
    public void setField(FacilioField field) {
        this.field = field;
    }

    private AggregateOperator aggregateOperator;
    public int getAggregateOperator() {
        if (aggregateOperator != null) {
            return aggregateOperator.getValue();
        }
        return -1;
    }
    public AggregateOperator getAggregateOperatorEnum() {
        return aggregateOperator;
    }
    public void setAggregateOperator(AggregateOperator aggregateOperator) {
        this.aggregateOperator = aggregateOperator;
    }
    public void setAggregateOperator(int aggregateOperatorInt) {
        aggregateOperator = AggregateOperator.getAggregateOperator(aggregateOperatorInt);
    }

    private Operator operator;
    public Operator getOperatorEnum() {
        return operator;
    }
    public int getOperator() {
        if (operator != null) {
            return operator.getOperatorId();
        }
        return -1;
    }
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
    public void setOperator(int operatorId) {
        operator = Operator.getOperator(operatorId);
    }

    private Number value;
    public Number getValue() {
        return value;
    }
    public void setValue(Number value) {
        this.value = value;
    }
}
