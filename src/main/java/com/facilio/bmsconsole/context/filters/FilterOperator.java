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

    public FilterOperator(String displayName, int operatorId, boolean valueNeeded) {
        this(displayName, operatorId, valueNeeded, null);
    }

    public FilterOperator(String displayName, int operatorId, boolean valueNeeded, String defaultValue) {
        this(displayName, null, operatorId, valueNeeded, defaultValue);
    }

    public FilterOperator(String displayName, String tagDisplayName, int operatorId, boolean valueNeeded, String defaultValue) {
        this.displayName = displayName;
        this.tagDisplayName = tagDisplayName;
        this.operatorId = operatorId;
        this.valueNeeded = valueNeeded;
        this.defaultValue = defaultValue;
    }

    private String displayName;
    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : operator == null ? null : operator.getOperator();
    }

    private String tagDisplayName;
    public String getTagDisplayName() {
        return StringUtils.isNotEmpty(tagDisplayName) ? tagDisplayName : operator == null ? null : operator.getTagDisplayName();
    }

    private Integer operatorId;
    public Integer getOperatorId() {
        return operatorId != null ? operatorId : operator == null ? null : operator.getOperatorId();
    }

    private Boolean valueNeeded = null;
    public boolean isValueNeeded() {
        return valueNeeded != null ? valueNeeded : operator == null ? true : operator.isValueNeeded();
    }

    private String defaultValue;
    public String getDefaultValue() {
        return defaultValue;
    }
}
