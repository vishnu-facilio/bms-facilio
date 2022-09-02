package com.facilio.bmsconsoleV3.context.dashboard;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DashboardRuleActionContext {

    public Long id=-1l;
    public String actionType;
    public Integer type;
    public Long dashboard_rule_id;
    public DashboardRuleActionMetaContext action_meta = new DashboardRuleActionMetaContext();
    public List<DashboardTriggerAndTargetWidgetContext> target_widgets =  new ArrayList<>();
}
