package com.facilio.bmsconsole.context;

import com.facilio.relation.context.RelationRequestContext;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipWidget extends PageSectionWidgetContext {
    private long id;
    private long widgetId;
    private long widgetGroupWidgetId;
    private String displayName;
    private String relationName;
    private long relationMappingId;
    private Double sequenceNumber;
    private RelationRequestContext relation;
}
