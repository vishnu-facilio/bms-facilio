package com.facilio.bmsconsole.context;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WidgetToModulesContext {
    public WidgetToModulesContext(){

    }
    public WidgetToModulesContext(long widgetId, Long moduleId){
        this.setWidgetId(widgetId);
        this.setModuleId(moduleId);
    }

    private long id =-1;
    private long widgetId=-1;
    private Long moduleId = -1L;
}
