package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;

public class ExecutedNewDashboardRuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardExecuteMetaContext dashboard_execute_meta = (DashboardExecuteMetaContext) context.get("action_meta");
        if(dashboard_execute_meta != null)
        {
            Long trigger_widget_id =  dashboard_execute_meta.getTrigger_widget_id();
            JSONArray result_json = V3DashboardAPIHandler.executeNewDashboardRules(trigger_widget_id, dashboard_execute_meta);
            context.put("result", result_json);

        }
        return false;
    }
}
