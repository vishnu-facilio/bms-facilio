package com.facilio.bmsconsole.TemplatePages;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;

public class ServicePlanTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN;
    }
    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLAN_ACTIVITY);
        return new PagesContext(null, null, "", null, true, false, false)
                .addWebLayout()
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanTasks", null, null)
                .addWidget("servicePlanTasks", "Tasks", PageWidget.WidgetType.SERVICE_PLAN_TASKS, "webServicePlanTasks_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanInventory", null, null)
                .addWidget("servicePlanInventory", "Inventory", PageWidget.WidgetType.SERVICE_PLAN_INVENTORY, "webServicePlanInventory_5_12", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("widgetGroup", null, null)
                .addWidget("widgetGroup", "Widget Group", PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("servicePlanRelationship", "Relationships", "List of relationships and types between records across modules")
                .addWidget("servicePlanRelationship", "Related", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("servicePlanRelatedList", "Related List", "List of all related records across modules")
                .addWidget("servicePlanRelatedList", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "Activity", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_10", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()
                .layoutDone()
                ;
    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject commentWidgetParam = new JSONObject();
        commentWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("activityModuleName", FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentWidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile ? "flexiblemobilecomment_8" : "flexiblewebcomment_5", 0, 4, commentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentWidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile ? "flexiblemobileattachment_8" : "flexiblewebattachment_5", 0, 4, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}
