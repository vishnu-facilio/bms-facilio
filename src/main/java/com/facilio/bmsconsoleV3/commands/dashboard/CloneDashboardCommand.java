package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardTabContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.List;

public class CloneDashboardCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        String dashboard_link_name  = (String) context.get("dashboard_link_name");
        String cloned_dashboard_name  = (String) context.get("cloned_dashboard_name");
        Long dashboard_folder_id  = null;
        if(dashboard_link_name != null && !"".equals(dashboard_link_name))
        {
            DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboard_link_name, null);
            if(dashboard == null){
                throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Dashboard Not present");
            }
            if(dashboard_folder_id == null || dashboard_folder_id <= 0){
                dashboard_folder_id = dashboard.getDashboardFolderId();
            }

            //code to create dashboard

            DashboardContext cloned_dashboard = new DashboardContext();
            setAddDashboardData(cloned_dashboard, cloned_dashboard_name, dashboard_folder_id);
            if(dashboard.isTabEnabled()){
                cloned_dashboard.setTabEnabled(true);
            }

            FacilioContext adddashboard_context = new FacilioContext();
            adddashboard_context.put(FacilioConstants.ContextNames.DASHBOARD, cloned_dashboard);;
            FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardChain();
            addDashboardChain.setContext(adddashboard_context);
            addDashboardChain.execute();
            if(dashboard != null && dashboard.getDashboardTabContexts() != null )
            {
                List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
                if(widgets != null) {
                    cloneDashboardWidgets(widgets, cloned_dashboard.getId());
                }
                for(DashboardTabContext dashboardTabContext : dashboard.getDashboardTabContexts())
                {
                    DashboardTabContext newDashboardTabContext = setNewDashboardTabData(dashboardTabContext);
                    newDashboardTabContext.setDashboardId(cloned_dashboard.getId());
                    addDashboardTabForClone(newDashboardTabContext);
                    if(newDashboardTabContext != null && newDashboardTabContext.getId() != -1)
                    {
                        DashboardTabContext dashboardTabContext_obj = DashboardUtil.getDashboardTabWithWidgets(dashboardTabContext.getId());
                        List<DashboardWidgetContext>  dashboard_tab_widgets = dashboardTabContext_obj.getDashboardWidgets();
                        if(dashboard_tab_widgets != null && dashboard_tab_widgets.size() > 0)
                        {
                            addWidgetForDashboardTab(dashboard_tab_widgets , newDashboardTabContext.getId(), cloned_dashboard.getId());
                        }
                    }
                }
                context.put("cloned_dashbaord", DashboardUtil.getDashboardWithWidgets(cloned_dashboard.getLinkName(), null));
            }
            else if(dashboard != null)
            {
                List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
                if(widgets != null) {
                    cloneDashboardWidgets(widgets, cloned_dashboard.getId());
                    context.put("cloned_dashbaord", DashboardUtil.getDashboardWithWidgets(cloned_dashboard.getLinkName(), null));
                }
            }
        }
        return false;
    }

    private void setAddDashboardData(DashboardContext dashboard, String cloned_dashboard_name, Long dashboard_folder_id )throws Exception
    {
        dashboard.setDashboardName(cloned_dashboard_name);
        dashboard.setDashboardFolderId(dashboard_folder_id);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        dashboard.setModuleId(modBean.getModule("workorder").getModuleId());
    }

    private void cloneDashboardWidgets(List<DashboardWidgetContext> widgetContexts, Long dashboardId)throws Exception
    {
        FacilioChain addWidgetChain = null;
        FacilioContext context = null;
        for(DashboardWidgetContext widget : widgetContexts)
        {
            addWidgetChain = TransactionChainFactory.getAddWidgetChain();
            widget.setDashboardId(dashboardId);
            if(widget.getDashboardTabId() != null){
                   continue;
            }
            context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.WIDGET, widget);
            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
            context.put(FacilioConstants.ContextNames.DASHBOARD_ID, dashboardId);
            addWidgetChain.execute(context);
        }
    }
    private DashboardTabContext setNewDashboardTabData(DashboardTabContext dashboardTabContext)throws Exception
    {
        DashboardTabContext newDashboardTabContext = new DashboardTabContext();
        newDashboardTabContext.setDashboardId(dashboardTabContext.getDashboardId());
        newDashboardTabContext.setName(dashboardTabContext.getName());
        newDashboardTabContext.setSequence(dashboardTabContext.getSequence());
        return  newDashboardTabContext;
    }
    private void addDashboardTabForClone(DashboardTabContext dashboardTabContext)throws Exception
    {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
        FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardTabChain();
        addDashboardChain.execute(context);
    }
    private void addWidgetForDashboardTab(List<DashboardWidgetContext> widgets, Long dashboardTabId, Long newdashboardId)throws Exception
    {
        FacilioChain addWidgetChain = null;
        FacilioContext context = null;
        for (DashboardWidgetContext widget : widgets)
        {
            widget.setId(-1);
            addWidgetChain = TransactionChainFactory.getAddWidgetChain();
            context = new FacilioContext();
            widget.setDashboardTabId(dashboardTabId);
            widget.setDashboardId(widget.getDashboardId() != null ? newdashboardId : null);
            context.put(FacilioConstants.ContextNames.WIDGET, widget);
            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
            addWidgetChain.execute(context);
        }
    }
}
