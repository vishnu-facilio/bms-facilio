package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FilterFieldContext {

    public FilterFieldContext() {

    }

    private FacilioField field;
    public FilterFieldContext(FacilioField field) {
        this.field = field;

        if (field instanceof EnumField) {
            List<EnumFieldValue> values = ((EnumField) field).getValues();
            if (CollectionUtils.isNotEmpty(values)) {
                options = new ArrayList<>();
                for (EnumFieldValue value : values) {
                    if (value.isVisible()) { //Have to check if this is needed. What if user wants to check old records with deleted enum options
                        options.add(new FilterFieldOptions(String.valueOf(value.getIndex()), value.getValue()));
                    }
                }
            }
        }
        else if (field instanceof BooleanField) {
            options = new ArrayList<>();
            String trueVal = ((BooleanField) field).getTrueVal();
            if (StringUtils.isEmpty(trueVal)) {
                trueVal = "True";
            }
            options.add(new FilterFieldOptions("true", trueVal));
            String falseVal = ((BooleanField) field).getFalseVal();
            if (StringUtils.isEmpty(falseVal)) {
                falseVal = "False";
            }
            options.add(new FilterFieldOptions("false", falseVal));
        }
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

    public String getDataType() {
        return field == null ? null : field.getDataTypeEnum().name();
    }

    public String getDisplayType() {
        return field == null ? null : field.getDisplayType() == null ? null : field.getDisplayType().name();
    }

    public String getLookupModuleName() {
        return field == null ? null : field instanceof LookupField ? ((LookupField) field).getLookupModule().getName() : null;
    }

    private List<FieldOperator> operators;
    public List<FieldOperator> getOperators() {
        return operators;
    }
    public void setOperators(List<FieldOperator> operators) {
        this.operators = operators;
    }

    private List<FilterFieldOptions> options;
    public List<FilterFieldOptions> getOptions() {
        return options;
    }

    public static class FilterFieldOptions {
        String label, value;

        FilterFieldOptions(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
        public String getValue() {
            return value;
        }
    }

}
