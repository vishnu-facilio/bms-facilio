package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedListWidgetContext extends PageSectionWidgetContext {
    private long id;
    private Long widgetId;
    private long widgetGroupWidgetId;
    private String subModuleDisplayName;
    private String displayName;
    private FacilioModule module;
    private String subModuleName;
    private Long subModuleId;
    private FacilioField field;
    private Long fieldId;
    private String fieldName;
    private Double sequenceNumber;
    private Boolean status = false;
}
