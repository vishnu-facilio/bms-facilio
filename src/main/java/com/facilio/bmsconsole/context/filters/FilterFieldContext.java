package com.facilio.bmsconsole.context.filters;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterFieldContext {

    public FilterFieldContext() {

    }

    private FacilioField field;
    private JSONObject lookupFilters;
    public FilterFieldContext(FacilioField field) {
        this (field, null, null);
    }

    public FilterFieldContext(FacilioField field, boolean showLookupPopup) {
        this (field, null, showLookupPopup);
    }

    public FilterFieldContext(FacilioField field, JSONObject lookupFilters) {
        this (field, lookupFilters, null);
    }

    public FilterFieldContext(FacilioField field, JSONObject lookupFilters, Boolean showLookupPopup) {
        this.field = field;
        this.lookupFilters = lookupFilters;

        if (field instanceof EnumField) {
            List<EnumFieldValue> values = ((EnumField) field).getValues();
            if (CollectionUtils.isNotEmpty(values)) {
                options = new ArrayList<>();
                for (EnumFieldValue value : values) {
                    if (value.isVisible()) { //Have to check if this is needed. What if user wants to check old records with deleted enum options
                        options.add(new FieldOption<>(value.getIndex(), value.getValue()));
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
            options.add(new FieldOption<>("true", trueVal));
            String falseVal = ((BooleanField) field).getFalseVal();
            if (StringUtils.isEmpty(falseVal)) {
                falseVal = "False";
            }
            options.add(new FieldOption<>("false", falseVal));
        }
        else if (field instanceof LookupField) {
            FacilioModule lookup = ((LookupField) field).getLookupModule();
            lookupModule = new FilterFieldLookupModule(
                    lookup.getName(),
                    lookup.getDisplayName(),
                    showLookupPopup != null ? showLookupPopup : lookup.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && StringUtils.isEmpty(((LookupField) field).getSpecialType()),
                    lookupFilters);
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
        return field == null ? null : field.getDisplayType() == null ? defaultDisplayTypeForBackwardCompatibility(field.getDataTypeEnum()) : field.getDisplayType().name();
    }

    private String defaultDisplayTypeForBackwardCompatibility (FieldType dataType) {
        if (dataType != null) {
            switch (dataType) {
                case STRING:
                    return FacilioField.FieldDisplayType.TEXTBOX.name();
                case NUMBER:
                case DECIMAL:
                case ID:
                case COUNTER:
                case SCORE:
                    return FacilioField.FieldDisplayType.NUMBER.name();
                case BOOLEAN:
                    return FacilioField.FieldDisplayType.DECISION_BOX.name();
                case LOOKUP:
                    return FacilioField.FieldDisplayType.LOOKUP_SIMPLE.name();
                case ENUM:
                case SYSTEM_ENUM:
                    return FacilioField.FieldDisplayType.SELECTBOX.name();
                case DATE:
                case DATE_TIME:
                    return FacilioField.FieldDisplayType.DATE.name();
                case FILE:
                    return FacilioField.FieldDisplayType.FILE.name();
            }
        }
        return null;
    }

    private FilterFieldLookupModule lookupModule;
    public FilterFieldLookupModule getLookupModule() {
        return lookupModule;
    }

    private List<FilterOperator> operators;
    public List<FilterOperator> getOperators() {
        return operators;
    }
    public void setOperators(List<FilterOperator> operators) {
        this.operators = operators;
    }

    private List<FieldOption> options;
    public List<FieldOption> getOptions() {
        return options;
    }

    public static class FilterFieldLookupModule {
        String name, displayName;
        boolean showPopup;
        JSONObject filters;

        private FilterFieldLookupModule (String name, String displayName, boolean showPopup, JSONObject filters) {
            this.name = name;
            this.displayName = displayName;
            this.showPopup = showPopup;
            this.filters = filters;
        }

        public String getName() {
            return name;
        }
        public  String getDisplayName() {
            return displayName;
        }
        public boolean isShowPopup() {
            return showPopup;
        }
        public JSONObject getFilters() {
            return filters;
        }
    }

}
