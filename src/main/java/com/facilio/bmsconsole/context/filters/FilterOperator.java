package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.Operator;
import org.apache.commons.lang.StringUtils;

public class FilterOperator {

    public FilterOperator() {

    }

    private Operator operator;
    public FilterOperator(Operator operator) {
        this.operator = operator;
    }
    public FilterOperator(String displayName, String operatorId, boolean valueNeeded) {
        this.displayName = displayName;
        this.operatorId = operatorId;
        this.valueNeeded = valueNeeded;
    }

    private String displayName;
    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : operator == null ? null : operator.getOperator();
    }

    private String operatorId;
    public String getOperatorId() {
        return StringUtils.isNotEmpty(operatorId) ? operatorId : operator == null ? null : String.valueOf(operator.getOperatorId());
    }

    private Boolean valueNeeded = null;
    public boolean isValueNeeded() {
        return valueNeeded != null ? valueNeeded : operator == null ? true : operator.isValueNeeded();
    }
}
