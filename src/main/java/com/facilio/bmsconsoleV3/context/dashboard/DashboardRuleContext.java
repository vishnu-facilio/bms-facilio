package com.facilio.bmsconsoleV3.context.dashboard;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Log4j
public class DashboardRuleContext {

    public Long id=-1l;
    public Long dashboardId;
    public Long dashboardTabId;
    public String name;
    public String desc;
    public Integer trigger_type;
    public Boolean status;
    public Long created_time;
    public Long modified_time;
    public Long created_by;
    public Long modified_by;
    public List<DashboardTriggerWidgetContext>  trigger_widgets = new ArrayList<>();
    public List<DashboardRuleActionContext> actions = new ArrayList<>();
    public JSONArray result_json = new JSONArray();

}
