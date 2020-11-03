package com.facilio.bmsconsole.context.filters;

import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.modules.FacilioModule;
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
                        options.add(new FieldOption(String.valueOf(value.getIndex()), value.getValue()));
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
            options.add(new FieldOption("true", trueVal));
            String falseVal = ((BooleanField) field).getFalseVal();
            if (StringUtils.isEmpty(falseVal)) {
                falseVal = "False";
            }
            options.add(new FieldOption("false", falseVal));
        }
        else if (field instanceof LookupField) {
            FacilioModule lookup = ((LookupField) field).getLookupModule();
            lookupModule = new FilterFieldLookupModule(lookup.getName(), lookup.getDisplayName());
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

    private FilterFieldLookupModule lookupModule;
    public FilterFieldLookupModule getLookupModule() {
        return lookupModule;
    }

    private List<FieldOperator> operators;
    public List<FieldOperator> getOperators() {
        return operators;
    }
    public void setOperators(List<FieldOperator> operators) {
        this.operators = operators;
    }

    private List<FieldOption> options;
    public List<FieldOption> getOptions() {
        return options;
    }

    public static class FilterFieldLookupModule {
        String name, displayName;

        private FilterFieldLookupModule (String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }

        public String getName() {
            return name;
        }
        public  String getDisplayName() {
            return displayName;
        }
    }

}
