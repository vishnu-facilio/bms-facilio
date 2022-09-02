package com.facilio.bmsconsoleV3.context.dashboard;

import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.db.criteria.Criteria;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

@Setter
@Getter
public class DashboardTriggerWidgetContext {

    public Long id = -1l;
    public Long dashboard_rule_id;
    public Long trigger_widget_id;
    public Long criteriaId;
    public Criteria criteria;
    public DashboardWidgetContext.WidgetType widgetType;
}
