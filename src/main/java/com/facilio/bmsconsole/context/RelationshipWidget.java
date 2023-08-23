package com.facilio.bmsconsole.context;

import com.facilio.relation.context.RelationRequestContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipWidget extends PageSectionWidgetContext {
    private long id;
    private long widgetId = -1;
    private long widgetGroupWidgetId = -1;
    private String displayName;
    private String relationName;
    private long relationMappingId = -1;
    private Double sequenceNumber;
    private RelationRequestContext relation;
}
