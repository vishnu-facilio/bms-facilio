package com.facilio.bmsconsole.context.filters;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
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

        if (field instanceof BaseEnumField) {
            populateOptionsForEnum(((BaseEnumField) field).getValues());
        }
        else if (field instanceof StringSystemEnumField) {
            populateOptionsForEnum(((StringSystemEnumField) field).getValues());
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
        else if (field instanceof BaseLookupField) {
            FacilioModule lookup = ((BaseLookupField) field).getLookupModule();
            lookupModule = new FilterFieldLookupModule(
                    lookup.getName(),
                    lookup.getDisplayName(),
                    showLookupPopup != null ? showLookupPopup : lookup.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && StringUtils.isEmpty(((BaseLookupField) field).getSpecialType()),
                    lookupFilters);
        }
    }

    private <I> void populateOptionsForEnum(List<EnumFieldValue<I>> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            options = new ArrayList<>();
            for (EnumFieldValue value : values) {
                if (value.isVisible()) { //Have to check if this is needed. What if user wants to check old records with deleted enum options
                    options.add(new FieldOption<>(value.getIndex(), value.getValue()));
                }
            }
        }
    }

    public String getName() {
        return field == null ? null : field.getName();
    }

    @JSON(serialize = false)
    public boolean isMain() {
        return field == null ? false : field.isMainField();
    }

    public Boolean getMainField() { //Have separate getter to return null, so that this key is ignored in client response if it's not main field
        return field == null ? null : field.getMainField();
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
        return field == null ? null : field.getDisplayType() == null || field.getDataTypeEnum() == FieldType.SYSTEM_ENUM ? defaultDisplayTypeForBackwardCompatibility(field.getDataTypeEnum()) : field.getDisplayType().name();
    }
    
    @JSON(serialize = false)
    public FacilioField getField() {
        return field;
    }

    private String defaultDisplayTypeForBackwardCompatibility (FieldType dataType) {
        if (dataType != null) {
            FacilioField.FieldDisplayType type = FieldFactory.getDefaultDisplayTypeFromDataType(dataType);
            if (type != null) {
                return type.name();
            }
        }
        return null;
    }

    @Getter
    private FilterFieldLookupModule lookupModule;

    @Getter @Setter
    private List<FilterOperator> operators;

    @Getter
    private List<FieldOption> options;

    @Getter
    public static class FilterFieldLookupModule {
        private String name, displayName;
        private Boolean showPopup;
        private JSONObject filters;

        public FilterFieldLookupModule (String name, String displayName, boolean showPopup, JSONObject filters) {
            this.name = name;
            this.displayName = displayName;
            this.showPopup = showPopup;
            this.filters = filters;
        }

        public FilterFieldLookupModule (FacilioModule module) {
            this.name = module.getName();
            this.displayName = module.getDisplayName();
        }
    }

}
