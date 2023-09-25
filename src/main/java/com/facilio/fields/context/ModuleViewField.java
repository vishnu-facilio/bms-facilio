package com.facilio.fields.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModuleViewField {
    private String name, displayName;
    private long id;
    private boolean fixed;
    private boolean fixedSelectable;

    public ModuleViewField(FacilioField field) {
        name = field.getName();
        displayName = field.getDisplayName();
        id = field.getId();
        fixed = field.isMainField(); //mainFields are made fixed
    }
}
