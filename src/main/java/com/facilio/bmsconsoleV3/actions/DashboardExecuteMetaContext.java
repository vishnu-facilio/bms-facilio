package com.facilio.bmsconsoleV3.actions;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Setter
@Getter
@Log4j
public class DashboardExecuteMetaContext {

    public Long trigger_widget_id;
    public Long dashboardId;
    public JSONObject trigger_meta;
}
