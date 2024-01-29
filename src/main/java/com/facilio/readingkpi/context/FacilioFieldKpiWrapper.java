package com.facilio.readingkpi.context;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacilioFieldKpiWrapper {
    private int dataType;
    private Boolean isDefault;
    private String displayName;
    private Long fieldId;
    private int moduleType;
    private String name;
    private String unit;
    private Long resourceId;
    private boolean isLeft;

    public FacilioFieldKpiWrapper(FacilioField fld) {
        this.dataType = fld.getDataType();
        this.isDefault = fld.getDefault();
        this.displayName = fld.getDisplayName();
        this.fieldId = fld.getFieldId();
        this.moduleType = fld.getModule().getType();
        this.name = fld.getName();
        this.unit = fld instanceof NumberField ? ((NumberField) fld).getUnit() : null;
    }

}
