package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BulkRelatedListContext extends PageSectionWidgetContext{
    List<RelatedListWidgetContext> relatedLists;
}
