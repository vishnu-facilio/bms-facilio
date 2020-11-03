package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.Operator;
import org.apache.commons.lang.StringUtils;

public class FilterOperator {

    public FilterOperator() {

    }

    private Operator operator;
    public FilterOperator(Operator operator) {
        this(operator, null);
    }

    public FilterOperator(Operator operator, String displayName) {
        this(operator, displayName, null, null, null);
    }

    public FilterOperator(Operator operator, String displayName, String tagDisplayName, Boolean valueNeeded, String defaultValue) {
        this (operator, displayName, tagDisplayName, valueNeeded, defaultValue, null);
    }

    public FilterOperator(Operator operator, String displayName, String tagDisplayName, Boolean valueNeeded, String defaultValue, Boolean defaultSelection) {
        this.operator = operator;
        this.displayName = displayName;
        this.tagDisplayName = tagDisplayName;
        this.valueNeeded = valueNeeded;
        this.defaultValue = defaultValue;
        this.defaultSelection = defaultSelection;
    }

    private String displayName;
    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : operator == null ? null : operator.getOperator();
    }

    private String tagDisplayName;
    public String getTagDisplayName() {
        return StringUtils.isNotEmpty(tagDisplayName) ? tagDisplayName : operator == null ? null : operator.getTagDisplayName();
    }

//    private Integer operatorId;
    public Integer getOperatorId() { // Always from operator
        // return operatorId != null ? operatorId : operator == null ? null : operator.getOperatorId();
        return operator == null ? null : operator.getOperatorId();
    }

    private Boolean valueNeeded = null;
    public boolean isValueNeeded() {
        return valueNeeded != null ? valueNeeded : operator == null ? true : operator.isValueNeeded();
    }

    private String defaultValue;
    public String getDefaultValue() {
        return defaultValue;
    }

    private Boolean defaultSelection = false;
    public boolean getDefaultSelection() {
        return defaultSelection != null ? defaultSelection : operator == null ? false : operator.isDefaultSelection();
    }
}
