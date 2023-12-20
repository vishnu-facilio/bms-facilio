package com.facilio.bmsconsole.context.filters;

import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;
import org.apache.xpath.operations.Bool;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FilterFieldContext {

    public FilterFieldContext() {

    }

    private FacilioField field;

    private Boolean isSpecialType;

   public Boolean getIsSpecialType() {
       if (this.field != null && this.field instanceof BaseLookupField){
           String specialType = ((BaseLookupField)this.field).getSpecialType();
           this.isSpecialType = specialType != null ? true : false;
       }
       return this.isSpecialType;
   }

    public void setSpecialType(Boolean specialType) {
        isSpecialType = specialType;
    }

    private JSONObject lookupFilters;
    public FilterFieldContext(FacilioField field) {
        this (field, null, null, null);
    }

    public FilterFieldContext(FacilioField field, String displayName) {
        this (field, displayName, null, null);
    }

    public FilterFieldContext(FacilioField field, boolean showLookupPopup) {
        this (field, null, null, showLookupPopup);
    }

    public FilterFieldContext(FacilioField field, JSONObject lookupFilters) {
        this (field, null, lookupFilters, null);
    }

    public FilterFieldContext(FacilioField field, String displayName, JSONObject lookupFilters, Boolean showLookupPopup) {
        this.field = field;
        this.displayName = displayName;
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
                trueVal = "Yes";
            }
            options.add(new FieldOption<>("true", trueVal));
            String falseVal = ((BooleanField) field).getFalseVal();
            if (StringUtils.isEmpty(falseVal)) {
                falseVal = "No";
            }
            options.add(new FieldOption<>("false", falseVal));
        }
        else if (field instanceof BaseLookupField && !(field instanceof BaseSystemLookupField)) { // System lookup field shouldn't have these as that system lookup module is never exposed
            FacilioModule lookup = ((BaseLookupField) field).getLookupModule();
            lookupModule = new FilterFieldLookupModule(
                    lookup.getName(),
                    lookup.getDisplayName(),
                    showLookupPopup != null ? showLookupPopup : lookup.getTypeEnum() != FacilioModule.ModuleType.PICK_LIST && StringUtils.isEmpty(((BaseLookupField) field).getSpecialType()),
                    lookupFilters, lookup.getTypeEnum());
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

    private String displayName;
    public String getDisplayName() {
        return StringUtils.isNotEmpty(displayName) ? displayName : field == null ? null : field.getDisplayName();
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
        private String typeEnum;

        public FilterFieldLookupModule (String name, String displayName, boolean showPopup, JSONObject filters, FacilioModule.ModuleType moduleType) {
            this.name = name;
            this.displayName = displayName;
            this.showPopup = showPopup;
            this.filters = filters;
            typeEnum = moduleType != null? moduleType.name() : null;
        }

        public FilterFieldLookupModule (FacilioModule module) {
            this.name = module.getName();
            this.displayName = module.getDisplayName();
        }
    }

}
