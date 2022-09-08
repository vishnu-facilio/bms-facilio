package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class DashboardTriggerAndTargetWidgetContext {

    public Long id=-1l;
    public Long actionId;
    public Long target_widget_id;
    public Long datapoint_id;
    public Long criteriaId;
    public Criteria criteria;
    public String moduleName;
    public DashboardWidgetContext.WidgetType widgetType;
}
