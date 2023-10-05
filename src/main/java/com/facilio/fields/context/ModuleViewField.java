package com.facilio.fields.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Getter
@Setter
public class ModuleViewField {
    private String name, displayName;
    private long id;
    private boolean fixed;
    private boolean fixedSelectable;
    private String customization;

    public ModuleViewField(FacilioField field) {
        name = field.getName();
        displayName = field.getDisplayName();
        id = field.getId();
        fixed = field.isMainField(); //mainFields are made fixed
    }
}
