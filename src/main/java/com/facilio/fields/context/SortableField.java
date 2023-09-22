package com.facilio.fields.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SortableField {
    private String name, displayName;
    private long id;
    private boolean isMainField;

    public SortableField(FacilioField field) {
        name = field.getName();
        displayName = field.getDisplayName();
        id = field.getId();
        isMainField = field.isMainField();
    }
}
