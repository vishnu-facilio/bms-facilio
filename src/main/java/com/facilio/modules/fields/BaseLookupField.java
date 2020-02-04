package com.facilio.modules.fields;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;

public class BaseLookupField extends FacilioField {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public BaseLookupField() {
        super();
    }

    protected BaseLookupField(BaseLookupField field) { // Do not forget to Handle here if new property is added
        super(field);
        this.lookupModule = field.lookupModule;
        this.specialType = field.specialType;
    }

    public BaseLookupField(FacilioModule module, String name, String displayName, FieldDisplayType displayType,
                       String columnName, FieldType dataType, Boolean required, Boolean disabled, Boolean isDefault,
                       Boolean isMainField, FacilioModule lookupModule) {
        super(module, name, displayName, displayType, columnName, dataType, required, disabled, isDefault, isMainField);
        this.lookupModule = lookupModule;
    }

    public BaseLookupField(FacilioModule module, String name, String displayName, FieldDisplayType displayType,
                       String columnName, FieldType dataType, Boolean required, Boolean disabled, Boolean isDefault,
                       Boolean isMainField, String specialType) {
        super(module, name, displayName, displayType, columnName, dataType, required, disabled, isDefault, isMainField);
        this.specialType = specialType;
    }

    private long lookupModuleId = -1;
    public long getLookupModuleId() {
        return lookupModuleId;
    }
    public void setLookupModuleId(long lookupModuleId) {
        this.lookupModuleId = lookupModuleId;
    }


    private FacilioModule lookupModule;
    public FacilioModule getLookupModule() {
        return lookupModule;
    }
    public void setLookupModule(FacilioModule lookupModule) {
        this.lookupModule = lookupModule;
    }

    private String specialType;
    public String getSpecialType() {
        return specialType;
    }
    public void setSpecialType(String specialType) {
        this.specialType = specialType;
    }
}
