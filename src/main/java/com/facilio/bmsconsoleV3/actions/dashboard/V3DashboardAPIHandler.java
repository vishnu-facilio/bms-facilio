package com.facilio.bmsconsoleV3.actions.dashboard;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsoleV3.context.WidgetSectionContext;
import com.facilio.modules.FieldUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class V3DashboardAPIHandler {

    public static void updateDashboardProp(DashboardContext dashboard, JSONObject dashboardMeta)throws Exception
    {
        dashboard.setDashboardName((String) dashboardMeta.get("dashboardName"));
        if(dashboardMeta.get("dashboardFolderId") != null) {
            dashboard.setDashboardFolderId((Long) dashboardMeta.get("dashboardFolderId"));
        }
        if (dashboardMeta.get("dashboardTabPlacement") != null) {
            int dashboardTabPlacement  = ((Long) dashboardMeta.get("dashboardTabPlacement")).intValue();
            dashboard.setDashboardTabPlacement(dashboardTabPlacement);
        }
    }
    public static void updateDashboardTabProp(DashboardTabContext dashboardTabContext, JSONObject dashboardMeta)throws Exception
    {
        List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");
        List<DashboardWidgetContext> widgets = V3DashboardAPIHandler.getDashboardSectionWidgetFromWidgetMeta(dashboardWidgets);
        dashboardTabContext.setDashboardWidgets(widgets);
    }
    public static void updateDashboardData(DashboardContext dashboard, JSONObject dashboardMeta)throws Exception
    {
        V3DashboardAPIHandler.updateDashboardProp(dashboard, dashboardMeta);
        if(dashboardMeta.get("mobileEnabled") != null) {
            dashboard.setMobileEnabled((boolean)dashboardMeta.get("mobileEnabled"));
        }
        if(dashboard.isTabEnabled() && dashboardMeta.get("tabEnabled") != null && !((Boolean)dashboardMeta.get("tabEnabled")))
        {
            dashboard.setSkipDefaultWidgetDeletion(true);
        }
        if (dashboardMeta.get("tabEnabled") != null) {
            dashboard.setTabEnabled((boolean) dashboardMeta.get("tabEnabled"));
        }
        if(dashboardMeta.get("clientMetaJsonString") != null) {
            dashboard.setClientMetaJsonString(dashboardMeta.get("clientMetaJsonString").toString());
        }
        List dashboardWidgets = (List) dashboardMeta.get("dashboardWidgets");

        List<DashboardWidgetContext> widgets = getDashboardSectionWidgetFromWidgetMeta(dashboardWidgets);
        dashboard.setDashboardWidgets(widgets);

    }

    private static List<DashboardWidgetContext> getDashboardSectionWidgetFromWidgetMeta(List dashboardWidgets)throws Exception
    {
        List<DashboardWidgetContext> widgets = new ArrayList<>();
        if (dashboardWidgets != null)
        {
            for (int i = 0; i < dashboardWidgets.size(); i++) {
                Map widget = (Map) dashboardWidgets.get(i);
                Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();

                DashboardWidgetContext widgetContext = null;
                if (widgetType == DashboardWidgetContext.WidgetType.SECTION.getValue()) {
                    widgetContext = new WidgetSectionContext();
                    widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetSectionContext.class);
                    widgetContext.setType(widgetType);
                    widgets.add(widgetContext);
                    List widgets_in_section = (ArrayList) widget.get("section");
                    if(widgets_in_section != null && widgets_in_section.size() > 0) {
                        int size = widgets_in_section.size();
                        List<DashboardWidgetContext> widget_list = new ArrayList<DashboardWidgetContext>();
                        for(int j=0;j<size; j++)
                        {
                            Map widget_map = (Map) widgets_in_section.get(j);
                            DashboardWidgetContext widget_context = getDashboardWidgetFromWidgetMeta(widget_map);
                            if(widget_context != null)
                            {
                                widget_list.add(widget_context);
                            }
                        }
                        if(widget_list.size() > 0)
                        {
                            ((WidgetSectionContext) widgetContext).setWidgets_in_section(widget_list);
                        }
                    }
                }
                else {
                    DashboardWidgetContext widget_context = getDashboardWidgetFromWidgetMeta(widget);
                    widgets.add(widget_context);
                }
            }
        }
        return widgets;
    }
    private static DashboardWidgetContext getDashboardWidgetFromWidgetMeta(Map widget)
    {
        Integer widgetType = DashboardWidgetContext.WidgetType.getWidgetType(widget.get("type").toString()).getValue();

        DashboardWidgetContext widgetContext = null;
        if (widgetType == DashboardWidgetContext.WidgetType.CHART.getValue()) {
            widgetContext = new WidgetChartContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetChartContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.LIST_VIEW.getValue()) {
            widgetContext = new WidgetListViewContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetListViewContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.STATIC.getValue()) {
            widgetContext = new WidgetStaticContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetStaticContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.WEB.getValue()) {
            widgetContext = new WidgetWebContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetWebContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.GRAPHICS.getValue()) {
            widgetContext = new WidgetGraphicsContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetGraphicsContext.class);
        }
        else if (widgetType == DashboardWidgetContext.WidgetType.CARD.getValue()) {
            widgetContext = new WidgetCardContext();
            widgetContext = FieldUtil.getAsBeanFromMap(widget, WidgetCardContext.class);
        }

        widgetContext.setLayoutPosition(Integer.parseInt(widget.get("order").toString()));
        widgetContext.setType(widgetType);

        return widgetContext;
    }

}
