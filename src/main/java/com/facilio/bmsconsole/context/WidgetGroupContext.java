package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WidgetGroupContext extends PageSectionWidgetContext {
    private WidgetGroupConfigContext config;
    private List<WidgetGroupSectionContext> sections;
}
