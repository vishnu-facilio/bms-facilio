package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.actions.dashboard.V3DashboardAPIHandler;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardRuleContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class DashboardRuleCREDCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        DashboardRuleContext dashboard_rule = (DashboardRuleContext) context.get("dashboard_rule");
        Boolean isCreate = (Boolean) context.get("isCreate");
        Boolean isUpdate = (Boolean) context.get("isUpdate");
        Boolean isDelete = (Boolean) context.get("isDelete");
        Boolean isGet = (Boolean) context.get("isGet");
        if(isCreate != null && isCreate && dashboard_rule != null)
        {
            dashboard_rule.setCreated_by(AccountUtil.getCurrentUser().getId());
            dashboard_rule.setCreated_time(System.currentTimeMillis());
            V3DashboardAPIHandler.createDashboardRule(dashboard_rule);
            V3DashboardAPIHandler.addTriggerWidgetMapping(dashboard_rule.getId(), dashboard_rule.getTrigger_widgets());
            V3DashboardAPIHandler.addDashboardRuleActions(dashboard_rule.getId(), dashboard_rule.getActions());
        }
        else if(isUpdate != null && isUpdate && dashboard_rule != null && dashboard_rule.getId() != null)
        {
            DashboardRuleContext old_dashboard_rule = V3DashboardAPIHandler.getDashboardRule(dashboard_rule.getId(), null);
            if(old_dashboard_rule != null)
            {
                V3DashboardAPIHandler.deleteOldDashboardRuleData(old_dashboard_rule);
                dashboard_rule.setModified_by(AccountUtil.getCurrentUser().getId());
                dashboard_rule.setModified_time(System.currentTimeMillis());
                V3DashboardAPIHandler.updateDashboardRule(dashboard_rule);
                V3DashboardAPIHandler.addTriggerWidgetMapping(dashboard_rule.getId(), dashboard_rule.getTrigger_widgets());
                V3DashboardAPIHandler.addDashboardRuleActions(dashboard_rule.getId(), dashboard_rule.getActions());
            }
        }
        else if(isDelete != null && isDelete){
            Long dashboard_rule_id = (Long)context.get("dashboard_rule_id");
            V3DashboardAPIHandler.deleteDashboardRule(dashboard_rule_id);
        }
        else if(isGet != null && isGet)
        {
            Long dashboard_rule_id = (Long)context.get("dashboard_rule_id");
            if(dashboard_rule_id != null && dashboard_rule_id > 0) {
                DashboardRuleContext dashboard_rule_context = V3DashboardAPIHandler.getDashboardRule(dashboard_rule_id, null);
                context.put("dashboard_rule", dashboard_rule_context);
            }
        }
        return false;
    }
}
