package com.facilio.bmsconsole.context;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedListWidgetContext extends PageSectionWidgetContext {
    private long id;
    private Long widgetId = -1L;
    private long widgetGroupWidgetId = -1L;
    private String subModuleDisplayName;
    private String displayName;
    private FacilioModule module;
    private String subModuleName;
    private Long subModuleId = -1L;
    private Long connectedAppWidgetId = -1L;
    private FacilioField field;
    private Long fieldId = -1L;
    private String fieldName;
    private Double sequenceNumber;
    private RelatedListEnum relatedListEnum;
    private Boolean status = false;

    public enum RelatedListEnum {
        COMMON_REL_LIST,
        CUSTOM_LINE_ITEM_REL_LIST
    }
}
