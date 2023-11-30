package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class DashboardReadingWidgetFilterContext extends ModuleBaseWithCustomFields {
    private long id;
    private Long userFilterId;
    private Long widgetId;
    private Long widgetFieldId;
    private String moduleName;
}
