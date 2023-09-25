package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsoleV3.actions.DashboardExecuteMetaContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.dashboard.DashboardRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Log4j
public class V3DashboardRuleAction extends V3Action {

    public DashboardRuleContext dashboard_rule;
    private Long dashboard_rule_id;
    private Long dashboardId;
    private String dashboard_link_name;
    private Long dashboardTabId;
    private DashboardExecuteMetaContext action_meta = new DashboardExecuteMetaContext();

    public String create()throws Exception
    {
        if(dashboard_rule != null)
        {
            String str = "";
            FacilioChain chain = TransactionChainFactoryV3.getCREDDashboardRuleChain();
            FacilioContext context = chain.getContext();
            context.put("dashboard_rule", dashboard_rule);
            context.put("isCreate", true);
            chain.execute();
        }
        return SUCCESS;
    }

    public String getAllDashboardRules()throws Exception
    {
        if( (dashboardId == null || dashboardId <=0))
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard id can not be empty");
        }
        try
        {
            List<DashboardRuleContext> dashboard_rules = V3DashboardAPIHandler.getDashboardRules(dashboardId, dashboardTabId);
            if (dashboard_rules != null) {
                setData("dashboard_rule", dashboard_rules);
            } else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid url");
            }
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard rules info"+e);
        }
        return SUCCESS;
    }

    public String getDashboardRule()throws Exception
    {
        if(dashboard_rule_id == null || dashboard_rule_id <= 0)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Rule Id can not be empty");
        }
        try
        {
            FacilioChain chain = TransactionChainFactoryV3.getCREDDashboardRuleChain();
            FacilioContext context = chain.getContext();
            context.put("isGet", true);
            context.put("dashboard_rule_id", dashboard_rule_id);
            chain.execute();
            if(context.containsKey("dashboard_rule"))
            {
                setData("dashboard_rule", context.get("dashboard_rule"));
            }
        }
        catch (Exception e)
        {
            throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Error while getting dashboard rule info"+e);
        }
        return SUCCESS;
    }

    public String deleteDashboardRule()throws Exception
    {
        if(dashboard_rule_id == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Rule Id can not be empty");
        }
        FacilioChain chain = TransactionChainFactoryV3.getCREDDashboardRuleChain();
        FacilioContext context = chain.getContext();
        context.put("dashboard_rule_id", dashboard_rule_id);
        context.put("isDelete", true);
        chain.execute();
        return SUCCESS;
    }

    public String update()throws Exception
    {
        if(dashboard_rule == null)
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Rule data can not be empty");
        }
        if(dashboard_rule != null && (dashboard_rule.getId() == null || dashboard_rule.getId() < 0)){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Dashboard Rule Id can not be empty");
        }
        FacilioChain chain = TransactionChainFactoryV3.getCREDDashboardRuleChain();
        FacilioContext context = chain.getContext();
        context.put("dashboard_rule", dashboard_rule);
        context.put("isUpdate", true);
        chain.execute();

        return SUCCESS;
    }
    public String runMigrationForWidgetLinkName()throws Exception
    {
        V3DashboardAPIHandler.runMigrationForWidgetLinkName();
        return SUCCESS;
    }


    public String execute()throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.getDashboardRuleExecuteChain();
        FacilioContext context = chain.getContext();
        context.put("action_meta", action_meta);
        chain.execute();
        if(context.containsKey("result")) {
            setData("result", context.get("result"));
        }
        return SUCCESS;
    }
    public String newDashboardFilterRuleExecute()throws Exception{

        FacilioChain chain = TransactionChainFactoryV3.getNewDashboardRuleExecuteChain();
        FacilioContext context = chain.getContext();
        context.put("action_meta", action_meta);
        chain.execute();
        if(context.containsKey("result")) {
            setData("result", context.get("result"));
        }
        return SUCCESS;
    }

    public String getTriggerWidgets()throws Exception{
        if(dashboardId != null || dashboardTabId != null){
            FacilioChain chain = TransactionChainFactoryV3.getDashboardWidgetsChain();
            FacilioContext context = chain.getContext();
            context.put("dashboardId", dashboardId);
            context.put("dashboardTabId", dashboardTabId);
            chain.execute();
            if(context.containsKey("widgets")){
                List widgets = (ArrayList) context.get("widgets");
                setData("widgets" , widgets);
            }else{
                setData("error" , "error");
            }
        }
        return SUCCESS;
    }
}
