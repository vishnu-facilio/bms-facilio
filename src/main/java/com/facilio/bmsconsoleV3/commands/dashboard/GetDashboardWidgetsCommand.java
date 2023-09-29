package com.facilio.bmsconsoleV3.commands.dashboard;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardFilterUtil;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.dashboard.WidgetDashboardFilterContext;
import com.facilio.command.FacilioCommand;
import com.facilio.report.context.ReportContext;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetDashboardWidgetsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception
    {
        Long dashboardId = (Long) context.get("dashboardId");
        Long dashboardTabId = (Long) context.get("dashboardTabId");
        if(dashboardTabId != null){
            List<DashboardWidgetContext> new_widget_list = new ArrayList<>();
            DashboardTabContext dashboardTabContext = DashboardUtil.getDashboardTabWithWidgets(dashboardTabId);
            if(dashboardTabContext != null)
            {
                List<DashboardWidgetContext> widgets = dashboardTabContext.getDashboardWidgets();
                for(DashboardWidgetContext widget : widgets){
                    if(widget != null && (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD || widget.getWidgetType() == DashboardWidgetContext.WidgetType.CHART ||
                            widget.getWidgetType() == DashboardWidgetContext.WidgetType.FILTER || widget.getWidgetType() == DashboardWidgetContext.WidgetType.LIST_VIEW))
                    {
                        if(widget instanceof WidgetChartContext){
                            Long  reportId = ((WidgetChartContext)widget).getNewReportId();
                            if(reportId != null && reportId >0)
                            {
                                ReportContext report = ReportUtil.getReport(reportId);
                                if(report != null && report.getTypeEnum() != null){
                                    ((WidgetChartContext) widget).setReportType(report.getTypeEnum().name());
                                }
                            }
                        }
                        new_widget_list.add(widget);
                    }
                }
            }
            DashboardFilterContext dashboardTab_filter = DashboardFilterUtil.getDashboardFilter(null, dashboardTabId);
            if(dashboardTab_filter != null) {
                List<DashboardUserFilterContext>  dashboard_user_filters = DashboardFilterUtil.getDashboardUserFilters(dashboardTab_filter.getId());
                if(dashboard_user_filters != null && dashboard_user_filters.size() > 0){
                    for(DashboardUserFilterContext userFilterContext : dashboard_user_filters)
                    {
                        if(userFilterContext.getWidget_id() != null && userFilterContext.getWidget_id() > 0)
                        {
                            WidgetDashboardFilterContext filter_widget = new WidgetDashboardFilterContext();
                            filter_widget.setDashboardFilterId(userFilterContext.getDashboardFilterId());
                            filter_widget.setLabel(userFilterContext.getLabel());
                            filter_widget.setField(userFilterContext.getField());
                            filter_widget.setFieldId(userFilterContext.getFieldId());
                            filter_widget.setModuleName(userFilterContext.getModuleName());
                            filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getValue());
                            filter_widget.setId(userFilterContext.getWidget_id());
                            filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getName());
                            DashboardWidgetContext user_filter_widget = DashboardUtil.getWidget(userFilterContext.getWidget_id());
                            filter_widget.setLinkName(user_filter_widget.getLinkName());
                            filter_widget.setId(userFilterContext.getWidget_id());
                            new_widget_list.add(filter_widget);
                        }
                    }
                }
            }
            context.put("widgets", new_widget_list);
        }
        else if(dashboardId != null)
        {
            List<DashboardWidgetContext> new_widget_list = new ArrayList<>();
            DashboardContext dashboard = DashboardUtil.getDashboardWithWidgets(dashboardId);
            if(dashboard != null) {
                List<DashboardWidgetContext> widgets = dashboard.getDashboardWidgets();
                for(DashboardWidgetContext widget : widgets){
                    if(widget != null && (widget.getWidgetType() == DashboardWidgetContext.WidgetType.CARD || widget.getWidgetType() == DashboardWidgetContext.WidgetType.CHART ||
                            widget.getWidgetType() == DashboardWidgetContext.WidgetType.FILTER || widget.getWidgetType() == DashboardWidgetContext.WidgetType.LIST_VIEW))
                    {
                        if(widget instanceof WidgetChartContext){
                            Long  reportId = ((WidgetChartContext)widget).getNewReportId();
                            if(reportId != null && reportId >0)
                            {
                                ReportContext report = ReportUtil.getReport(reportId);
                                if(report != null && report.getTypeEnum() != null){
                                    ((WidgetChartContext) widget).setReportType(report.getTypeEnum().name());
                                }
                            }
                        }
                        new_widget_list.add(widget);
                    }
                }
            }
            DashboardFilterContext dashboard_filter = DashboardFilterUtil.getDashboardFilter(dashboardId, null);
            if(dashboard_filter != null) {
                List<DashboardUserFilterContext>  dashboard_user_filters = DashboardFilterUtil.getDashboardUserFilters(dashboard_filter.getId());
                if(dashboard_user_filters != null && dashboard_user_filters.size() > 0){
                    for(DashboardUserFilterContext userFilterContext : dashboard_user_filters)
                    {
                        if(userFilterContext.getWidget_id() != null && userFilterContext.getWidget_id() > 0)
                        {
                            WidgetDashboardFilterContext filter_widget = new WidgetDashboardFilterContext();
                            filter_widget.setDashboardFilterId(userFilterContext.getDashboardFilterId());
                            filter_widget.setLabel(userFilterContext.getLabel());
                            filter_widget.setField(userFilterContext.getField());
                            filter_widget.setFieldId(userFilterContext.getFieldId());
                            filter_widget.setModuleName(userFilterContext.getModuleName());
                            filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getValue());
                            filter_widget.setId(userFilterContext.getWidget_id());
                            filter_widget.setType(DashboardWidgetContext.WidgetType.FILTER.getName());
                            DashboardWidgetContext user_filter_widget = DashboardUtil.getWidget(userFilterContext.getWidget_id());
                            filter_widget.setLinkName(user_filter_widget.getLinkName());
                            filter_widget.setId(userFilterContext.getWidget_id());
                            new_widget_list.add(filter_widget);
                        }
                    }
                }
            }
            context.put("widgets", new_widget_list);
        }
        return false;
    }
}
