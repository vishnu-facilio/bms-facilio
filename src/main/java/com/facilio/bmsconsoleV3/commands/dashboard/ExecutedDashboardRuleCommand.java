package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ExecutedDashboardRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardExecuteMetaContext dashboard_execute_meta = (DashboardExecuteMetaContext) context.get("action_meta");
        if(dashboard_execute_meta.getTrigger_widget_id() != null)
        {
            Long trigger_widget_id =  dashboard_execute_meta.getTrigger_widget_id();
            JSONObject trigger_meta =  dashboard_execute_meta.getTrigger_meta();
            V3DashboardAPIHandler.executeDashboardRules(trigger_widget_id, trigger_meta);
        }
        return false;
    }
}
