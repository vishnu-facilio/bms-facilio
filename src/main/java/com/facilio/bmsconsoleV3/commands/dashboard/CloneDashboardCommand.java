package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
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
        Long dashboard_folder_id  = (Long) context.get("folder_id");
        Long target_app_id = (Long) context.get("target_app_id");
        Long cloned_app_id = (Long) context.get("cloned_app_id");
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
            cloned_dashboard.setMobileEnabled(dashboard.getMobileEnabled());
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
                if(widgets != null && widgets.size() > 0) {
                    List<DashboardWidgetContext> widgets_with_section =  DashboardUtil.getDashboardWidgetsWithSection(widgets);
                    cloneDashboardWidgets(widgets_with_section, cloned_dashboard.getId(),null,null);
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
                            List<DashboardWidgetContext> widgets_with_section =  DashboardUtil.getDashboardWidgetsWithSection(dashboard_tab_widgets);
                            addWidgetForDashboardTab(widgets_with_section , newDashboardTabContext.getId(), cloned_dashboard.getId(), target_app_id, cloned_app_id);
                        }
                        Boolean isFilterCloneNeeded = (Boolean) context.get("isFilterCloneNeeded");
                        if(isFilterCloneNeeded != null && isFilterCloneNeeded )
                        {
                            FacilioChain getDashboardFilterChain= ReadOnlyChainFactory.getFetchDashboardFilterChain();
                            FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
                            getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
                            getDashboardFilterChain.execute();

                            if(dashboardTabContext.getDashboardFilter() != null)
                            {
                                FacilioChain dbFilterUpdateChain = TransactionChainFactory.getAddOrUpdateDashboardFilterChain();
                                FacilioContext filter_context = dbFilterUpdateChain.getContext();
                                DashboardFilterContext cloned_dashboard_tab_Filter = dashboardTabContext.getDashboardFilter();
                                cloned_dashboard_tab_Filter.setDashboardTabId(newDashboardTabContext.getId());
                                cloned_dashboard_tab_Filter.setDashboardId(dashboardTabContext.getDashboardId() > 0 ? cloned_dashboard.getId() : -1l);
                                cloned_dashboard_tab_Filter.setId(-1);
                                if(cloned_dashboard_tab_Filter.getDashboardUserFilters() != null){
                                    for(DashboardUserFilterContext usr_filter : cloned_dashboard_tab_Filter.getDashboardUserFilters())
                                    {
                                        usr_filter.setId(-1);
                                    }
                                }
                                filter_context.put(FacilioConstants.ContextNames.DASHBOARD_FILTER, cloned_dashboard_tab_Filter);
                                dbFilterUpdateChain.execute();
                            }
                        }
                    }
                }
                context.put("cloned_dashbaord", DashboardUtil.getDashboardWithWidgets(cloned_dashboard.getLinkName(), null));
            }
            else if(dashboard != null)
            {
                List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
                List<DashboardWidgetContext> widgets_with_section =  DashboardUtil.getDashboardWidgetsWithSection(widgets);
                if(widgets_with_section != null) {
                    cloneDashboardWidgets(widgets_with_section, cloned_dashboard.getId(), target_app_id, cloned_app_id);
                    context.put("cloned_dashbaord", DashboardUtil.getDashboardWithWidgets(cloned_dashboard.getLinkName(), null));
                }
                Boolean isFilterCloneNeeded = (Boolean) context.get("isFilterCloneNeeded");
                if(isFilterCloneNeeded != null && isFilterCloneNeeded )
                {
                    FacilioChain getDashboardFilterChain= ReadOnlyChainFactory.getFetchDashboardFilterChain();
                    FacilioContext getDashboardFilterContext=getDashboardFilterChain.getContext();
                    getDashboardFilterContext.put(FacilioConstants.ContextNames.DASHBOARD,dashboard);
                    getDashboardFilterChain.execute();

                    if(dashboard.getDashboardFilter() != null)
                    {
                        FacilioChain dbFilterUpdateChain = TransactionChainFactory.getAddOrUpdateDashboardFilterChain();
                        FacilioContext filter_context = dbFilterUpdateChain.getContext();
                        DashboardFilterContext cloned_dashboardFilter = dashboard.getDashboardFilter();
                        cloned_dashboardFilter.setId(-1);
                        if(cloned_dashboardFilter.getDashboardUserFilters() != null){
                            for(DashboardUserFilterContext usr_filter : cloned_dashboardFilter.getDashboardUserFilters())
                            {
                                usr_filter.setId(-1);
                            }
                        }
                        cloned_dashboardFilter.setDashboardId(cloned_dashboard.getId());
                        filter_context.put(FacilioConstants.ContextNames.DASHBOARD_FILTER, cloned_dashboardFilter);
                        dbFilterUpdateChain.execute();
                    }
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

    private void cloneDashboardWidgets(List<DashboardWidgetContext> widgetContexts, Long dashboardId, Long target_app_id, Long cloned_app_id)throws Exception
    {
        for(DashboardWidgetContext widget : widgetContexts)
        {
            if(widget.getDashboardTabId() != null){
                continue;
            }
            createDashboardWidgetWithSections(widget , dashboardId, null, target_app_id, cloned_app_id);
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
    private void addWidgetForDashboardTab(List<DashboardWidgetContext> widgets, Long dashboardTabId, Long newdashboardId, Long target_app_id, Long cloned_app_Id)throws Exception
    {
        for (DashboardWidgetContext widget : widgets)
        {
            widget.setId(-1);
            createDashboardWidgetWithSections(widget, newdashboardId, dashboardTabId, target_app_id, cloned_app_Id);
        }
    }


    private void createDashboardWidgetWithSections(DashboardWidgetContext widget, Long dashboardId, Long dashboardTabId, Long target_app_id, Long cloned_app_Id)throws Exception
    {
        FacilioChain addWidgetChain = null;
        FacilioContext context = null;
        if(widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION))
        {
            FacilioChain addSectionWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
            widget.setDashboardTabId(dashboardTabId);
            widget.setDashboardId(widget.getDashboardId() != null ? dashboardId : null);
            context = addSectionWidgetChain.getContext();
            context.put(FacilioConstants.ContextNames.WIDGET, widget);
            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
            addSectionWidgetChain.execute();

            widget = (DashboardWidgetContext) context.get(FacilioConstants.ContextNames.WIDGET);
            if(widget != null && ((WidgetSectionContext) widget).getWidgets_in_section().size() > 0) {
                List<DashboardWidgetContext> widget_list = ((WidgetSectionContext) widget).getWidgets_in_section();
                for(DashboardWidgetContext dashboard_widget: widget_list)
                {
                    FacilioChain add_widgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
                    context = add_widgetChain.getContext();
                    dashboard_widget.setDashboardTabId(dashboardTabId);
                    dashboard_widget.setDashboardId(dashboard_widget.getDashboardId() != null ? dashboardId : null);
                    dashboard_widget.setSectionId(widget.getId());
                    context.put(FacilioConstants.ContextNames.WIDGET, dashboard_widget);
                    context.put(FacilioConstants.ContextNames.WIDGET_TYPE, dashboard_widget.getWidgetType());
                    add_widgetChain.execute();
                }
            }
        }
        else
        {
            addWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
            context = addWidgetChain.getContext();
            widget.setDashboardId(widget.getDashboardId() != null ? dashboardId : null);
            widget.setDashboardTabId(dashboardTabId);
            if(target_app_id != cloned_app_Id){
                context.put("isCloneToAnotherAPP",true);
            }
            context.put(FacilioConstants.ContextNames.WIDGET, widget);
            context.put("cloned_app_id",cloned_app_Id);
            context.put("target_app_id",target_app_id);
            context.put(FacilioConstants.ContextNames.WIDGET_TYPE, widget.getWidgetType());
            addWidgetChain.execute();
        }
    }
}
