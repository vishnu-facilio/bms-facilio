package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.page.PageWidget;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ModuleWidgets {
    List<WidgetContext> widgets;

    public WidgetContext addModuleWidget(String name, String displayName, PageWidget.WidgetType widgetType) {
        WidgetContext widget = new WidgetContext(name, displayName, widgetType);
        if(this.widgets == null) {
            this.widgets = new ArrayList<>(Arrays.asList(widget));
        }
        else {
            this.widgets.add(widget);
        }
        widget.setDone(this);
        return widget;
    }

}
