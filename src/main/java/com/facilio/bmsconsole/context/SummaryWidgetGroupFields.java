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
    private String widgetId;
    private long widgetGroupId;
    private long fieldId;
    private int sequenceNumber;
    private FacilioField field;
}
