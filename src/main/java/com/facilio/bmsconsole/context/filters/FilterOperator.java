package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.Operator;

public class FilterOperator {

    public FilterOperator() {

    }

    private Operator operator;
    public FilterOperator(Operator operator) {
        this.operator = operator;
    }

    public String getDisplayName() {
        return operator == null ? null : operator.getOperator();
    }
    public Integer getOperatorId() {
        return operator == null ? null : operator.getOperatorId();
    }
    public boolean isValueNeeded() {
        return operator == null ? true : operator.isValueNeeded();
    }
}
