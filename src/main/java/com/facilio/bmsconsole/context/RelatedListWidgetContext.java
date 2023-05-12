package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatedListWidgetContext{
    private long id;
    private Long widgetId;
    private String subModuleDisplayName;
    private String displayName;
    private String subModuleName;
    private Long subModuleId;
    private Long fieldId;
    private String fieldName;
    private Double sequenceNumber;
    private Boolean status = false;
}
