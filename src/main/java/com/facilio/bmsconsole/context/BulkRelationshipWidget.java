package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkRelationshipWidget extends PageSectionWidgetContext{
    List<RelationshipWidget> relationships;
}
