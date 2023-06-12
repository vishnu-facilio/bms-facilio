package com.facilio.bmsconsole.context;

import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryWidgetGroupFields {
    private long id=-1;
    private long orgId;
    private String name;
    private String displayName;
    private FacilioField.FieldDisplayType displayType;
    private long widgetId;
    private long widgetGroupId;
    private long fieldId;
    private int rowIndex;
    private int colIndex;
    private int colSpan;
    private FacilioField field;
    private boolean isSecondLevelLookup;
    private long parentLookupFieldId = -1;
    private FacilioField parentLookupField;
}
