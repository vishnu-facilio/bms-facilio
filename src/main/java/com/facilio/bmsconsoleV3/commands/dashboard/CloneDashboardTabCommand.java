package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.AddOrUpdateDashboardFilterCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.*;

public class CloneDashboardTabCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        Long tabId  = (Long) context.get("tab_id");
        Long dashboardId = (Long) context.get("dashboard_id");
        String cloned_dashboard_name  = (String) context.get("cloned_dashboard_name");
        Long dashboard_folder_id  = (Long) context.get("folder_id");
        Long target_app_id = (Long) context.get("target_app_id");
        Long cloned_app_id = (Long) context.get("cloned_app_id");
        boolean isCloneDashboard = (boolean) context.get("clone_dashboard");
        if(tabId != null)
        {
            DashboardContext dashboardContext = new DashboardContext();
            DashboardTabContext tabContext = new DashboardTabContext();
            if(isCloneDashboard){
                dashboardContext = DashboardUtil.getDashboardWithWidgets(dashboardId);
            }
            else{
                tabContext = DashboardUtil.getDashboardTabWithWidgets(tabId);
            }
            if(dashboardContext == null || tabContext == null){
                throw new RESTException(ErrorCode.UNHANDLED_EXCEPTION, "Tab or Dashboard Not present");
            }
            Long parentDashboardId = null;
            if(tabId!= null && tabId > 0){
                parentDashboardId = DashboardUtil.getDashboardFromTabId(tabId);
            }
            else{
                parentDashboardId = dashboardId;
            }
            DashboardContext parentDashboard = DashboardUtil.getDashboard(parentDashboardId);

            //code to create tab
            if(dashboardId != null && dashboardId > 0){
                checkIsTabsEnabled(dashboardId);
                DashboardTabContext cloned_tab = new DashboardTabContext();
                cloned_tab.setDashboardId(dashboardId);
                setClonedTabSequence(cloned_tab,parentDashboard);
                cloned_tab.setName(cloned_dashboard_name);
                addDashboardTabForClone(cloned_tab);
                cloneWidgetsToTab(tabContext,dashboardContext,cloned_tab,target_app_id,cloned_app_id,context);
                context.put("cloned_tab", DashboardUtil.getDashboardTabWithWidgets(cloned_tab.getId()));

            }
            else{
                //code to create dashboard
                DashboardContext cloned_dashboard = new DashboardContext();
                cloned_dashboard.setMobileEnabled(parentDashboard.getMobileEnabled());
                setAddDashboardData(cloned_dashboard, cloned_dashboard_name, dashboard_folder_id);
                cloned_dashboard.setDashboardTabPlacement(parentDashboard.getDashboardTabPlacement());

                FacilioContext adddashboard_context = new FacilioContext();
                adddashboard_context.put(FacilioConstants.ContextNames.DASHBOARD, cloned_dashboard);;
                FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardChain();
                addDashboardChain.setContext(adddashboard_context);
                addDashboardChain.execute();
                cloneFilters(context,tabContext,null,cloned_dashboard, null);
                cloneWidgetsToDashboard(tabContext,cloned_dashboard,target_app_id,cloned_app_id,context);
                context.put("cloned_dashboard", DashboardUtil.getDashboardWithWidgets(cloned_dashboard.getLinkName(), null));
            }
        }
        return false;
    }

    private void cloneWidgetsToDashboard(DashboardTabContext dashboardTab,DashboardContext cloned_dashboard,Long target_app_id, Long cloned_app_id,Context context) throws Exception{
        List<DashboardWidgetContext> widgets = dashboardTab.getDashboardWidgets();
        if(widgets != null){
            List<DashboardWidgetContext> widgets_with_section =  DashboardUtil.getDashboardWidgetsWithSection(widgets);
            if(widgets_with_section != null) {
                cloneDashboardWidgets(widgets_with_section, cloned_dashboard.getId(),null, target_app_id, cloned_app_id, context);
            }
        }
    }
    private void cloneWidgetsToTab(DashboardTabContext dashboardTab,DashboardContext dashboard,DashboardTabContext cloned_tab,Long target_app_id, Long cloned_app_id,Context context) throws Exception{
        List<DashboardWidgetContext> widgets = new ArrayList<>();
        if(dashboardTab != null && dashboardTab.getId() != -1){
            widgets = dashboardTab.getDashboardWidgets();
        }
        else if(dashboard != null && dashboard.getId() != -1){
            widgets = dashboard.getDashboardWidgets();
        }
        if(widgets != null){
            List<DashboardWidgetContext> widgets_with_section =  DashboardUtil.getDashboardWidgetsWithSection(widgets);
            if(widgets_with_section != null) {
                cloneDashboardWidgets(widgets_with_section, dashboard.getId(),cloned_tab.getId(), target_app_id, cloned_app_id, context);
            }
        }
    }
    private void cloneFilters(Context context, DashboardTabContext dashboardTab,DashboardContext dashboard, DashboardContext cloned_dashboard,DashboardTabContext cloned_tab) throws Exception{
        if(dashboardTab != null || dashboard != null)
        {
            Boolean isFilterCloneNeeded = (Boolean) context.get("isFilterCloneNeeded");
            if(isFilterCloneNeeded != null && isFilterCloneNeeded )
            {
                FacilioContext filter_context = new FacilioContext();
                DashboardFilterContext filter = new DashboardFilterContext();
                AddOrUpdateDashboardFilterCommand tabFilter = new AddOrUpdateDashboardFilterCommand();
                filter.setIsTimelineFilterEnabled(true);
                filter.setDateOperator(28);
                filter.setDateLabel("Current Month");
                filter.setHideFilterInsideWidgets(false);
                if(cloned_dashboard != null){
                    filter.setDashboardId(cloned_dashboard.getId());
                    filter_context.put(FacilioConstants.ContextNames.DASHBOARD,cloned_dashboard);
                }
                else if(cloned_tab != null){
                    filter.setDashboardTabId(cloned_tab.getId());
                    filter_context.put(FacilioConstants.ContextNames.DASHBOARD_TAB,cloned_tab);
                }
                filter_context.put(FacilioConstants.ContextNames.DASHBOARD_FILTER,filter);
                tabFilter.executeCommand(filter_context);
                context.put("dashboardFilterId",filter.getId());
            }
        }
    }
    private void addDashboardTabForClone(DashboardTabContext dashboardTabContext)throws Exception
    {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.DASHBOARD_TAB, dashboardTabContext);
        FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardTabChain();
        addDashboardChain.execute(context);
    }
    private void setAddDashboardData(DashboardContext dashboard, String cloned_dashboard_name, Long dashboard_folder_id )throws Exception
    {
        dashboard.setDashboardName(cloned_dashboard_name);
        dashboard.setDashboardFolderId(dashboard_folder_id);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        dashboard.setModuleId(modBean.getModule("workorder").getModuleId());
    }

    private void cloneDashboardWidgets(List<DashboardWidgetContext> widgetContexts, Long dashboardId, Long tabId,Long target_app_id, Long cloned_app_id, Context context)throws Exception
    {
        for(DashboardWidgetContext widget : widgetContexts)
        {
            createDashboardWidgetWithSections(widget , dashboardId, tabId, target_app_id, cloned_app_id, context);
        }
    }
    private void setClonedTabSequence(DashboardTabContext cloned_tab,DashboardContext dashboardContext) throws Exception{
        int sequence = -1;
        List<DashboardTabContext> tabs = DashboardUtil.getDashboardTabs(dashboardContext.getId());
        for(DashboardTabContext dashboardTabContext: tabs){
            sequence = dashboardTabContext.getSequence();
        }
        cloned_tab.setSequence(sequence + 1);
    }
    private void checkIsTabsEnabled(Long dashboardId) throws Exception {
        if(dashboardId != null){
            DashboardContext dashboard = DashboardUtil.getDashboard(dashboardId);
            if(dashboard.getTabEnabled() == null || !dashboard.getTabEnabled()) {
                dashboard.setTabEnabled(true);
                GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                        .table(ModuleFactory.getDashboardModule().getTableName())
                        .fields(FieldFactory.getDashboardFields())
                        .andCondition(CriteriaAPI.getIdCondition(dashboardId, ModuleFactory.getDashboardModule()));
                Map<String,Object> prop = FieldUtil.getAsProperties(dashboard);
                updateBuilder.update(prop);
            }
        }
    }
    private void createDashboardWidgetWithSections(DashboardWidgetContext widget, Long dashboardId, Long dashboardTabId, Long target_app_id, Long cloned_app_Id, Context addContext)throws Exception
    {
        Long parentId = null;
        FacilioChain addWidgetChain = null;
        FacilioContext context = null;
        if(widget.getWidgetType().equals(DashboardWidgetContext.WidgetType.SECTION))
        {
            FacilioChain addSectionWidgetChain = TransactionChainFactoryV3.getAddWidgetChainV3();
            widget.setDashboardTabId(dashboardTabId);
            widget.setDashboardId(dashboardId);
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
                    dashboard_widget.setDashboardId(dashboardId);
                    dashboard_widget.setSectionId(widget.getId());
                    if(dashboard_widget.getWidgetType() == DashboardWidgetContext.WidgetType.FILTER){
                        DashboardUserFilterContext presentFilter =  DashboardFilterUtil.getDashboardUserFiltersForWidgetId(dashboard_widget.getId());
                        Long dashboardFilterId = (Long) addContext.get("dashboardFilterId");
                        if(dashboardFilterId == null){
                            DashboardFilterContext filterContext = DashboardFilterUtil.getDashboardFilter(null,dashboardTabId);
                            dashboardFilterId = filterContext.getId();
                        }
                        WidgetDashboardFilterContext filterWidget = (WidgetDashboardFilterContext) dashboard_widget;
                        filterWidget.setDashboardFilterId(dashboardFilterId);
                        if(presentFilter != null){
                            filterWidget.setModuleId(presentFilter.getModuleId());
                            filterWidget.setModuleName(presentFilter.getModuleName());
                            filterWidget.setFieldId(presentFilter.getFieldId());
                            filterWidget.setDashboardUserFilterJson(presentFilter.getDashboardUserFilterJson());
                            filterWidget.setDashboardFilterId(dashboardFilterId);
                            filterWidget.setCommonFieldId(presentFilter.getCommonFieldId());
                            filterWidget.setCriteriaId(presentFilter.getCriteriaId());
                            filterWidget.setComponentType(presentFilter.getComponentType());
                        }
                    } else if (dashboard_widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD) {
                        WidgetCardContext cardWidget = (WidgetCardContext) dashboard_widget;
                        if(cardWidget.getCardLayout().equals("combo_card")){
                            parentId = cardWidget.getId();
                        }
                        else if(cardWidget.getParentId() != null && cardWidget.getParentId() > 0){
                            cardWidget.setParentId(parentId);
                        }
                    }
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
            widget.setDashboardId(dashboardId != null ? dashboardId : null);
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
