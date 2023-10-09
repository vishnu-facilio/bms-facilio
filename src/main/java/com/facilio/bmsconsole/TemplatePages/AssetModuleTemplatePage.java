package com.facilio.bmsconsole.TemplatePages;
        import com.facilio.accounts.dto.AppDomain;
        import com.facilio.accounts.util.AccountUtil;
        import com.facilio.beans.ModuleBean;
        import com.facilio.bmsconsole.context.*;
        import com.facilio.bmsconsole.page.PageWidget;
        import com.facilio.bmsconsole.util.ApplicationApi;
        import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
        import com.facilio.constants.FacilioConstants;
        import com.facilio.fw.BeanFactory;
        import com.facilio.modules.FacilioModule;
        import com.facilio.modules.FieldUtil;
        import com.facilio.modules.fields.FacilioField;
        import com.facilio.relation.util.RelationshipWidgetUtil;
        import org.json.simple.JSONObject;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.List;
public class AssetModuleTemplatePage implements TemplatePageFactory{
    @Override
    public String getModuleName() {
        return FacilioConstants.ContextNames.ASSET;
    }
    @Override
    public PagesContext getTemplatePage(ApplicationContext app, FacilioModule module) throws Exception {
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);



        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {

            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("fault", "Fault", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("faultreport", "", null)
                    .addWidget("assetfaultreport", "Fault Reports", PageWidget.WidgetType.FAULT_REPORT, "flexiblewebfaultreport_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("faultinsight", null, null)
                    .addWidget("assetfaultinsight", "Fault Insights", PageWidget.WidgetType.FAULT_INSIGHT, "flexiblewebfaultinsight_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetcommand", null, null)
                    .addWidget("assetcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("performance", "Performance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("latestdowntime", null, null)
                    .addWidget("assetlatestdowntime", "Latest Downtime Reported", PageWidget.WidgetType.LATEST_DOWNTIME, "weblatestdowntime_3_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetoveralldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME, "weboveralldowntime_3_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("downtimehistory", null, null)
                    .addWidget("assetdowntimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY, "flexibledowntimehistory_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("failurerate", null, null)
                    .addWidget("assetfailurerate", "Mean Time Between Failure", PageWidget.WidgetType.FAILURE_RATE, "webfailurerate_8_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetaveragerepairtime", "Mean Time To Repair", PageWidget.WidgetType.AVERAGE_REPAIR_TIME, "webaveragerepairtime_8_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("financial", "Financial", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("costdetail", null, null)
                    .addWidget("assetcostdetail", "Asset Value Details", PageWidget.WidgetType.ASSET_COST_DETAILS, "webassetcostdetail_5_4", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetcostbreakup", "Maintenance Cost Value", PageWidget.WidgetType.COST_BREAKUP, "webcostbreakup_5_8", 4, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("maintenancecost", null, null)
                    .addWidget("assetmaintenancecost", "Maintenance Cost Analysis", PageWidget.WidgetType.MAINTENANCE_COST_TREND, "flexiblewebmaintenancecosttrend_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationschedule", null, null)
                    .addWidget("assetdepreciationschedule", "Depreciation Timeline", PageWidget.WidgetType.DEPRECIATION_SCHEDULE, "flexiblewebdepreciationschedule_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationcosttrend", null, null)
                    .addWidget("assetdepreciationcosttrend", "Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_COST_TREND, "flexiblewebdepreciationcosttrend_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("assetsafetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetsafetyplanprecaution", null, null)
                    .addWidget("assetsafetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of all related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("fault", "Fault", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("faultreport", "", null)
                    .addWidget("assetfaultreport", "Fault Reports", PageWidget.WidgetType.FAULT_REPORT, "flexiblewebfaultreport_5", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("faultinsight", null, null)
                    .addWidget("assetfaultinsight", "Fault Insights", PageWidget.WidgetType.FAULT_INSIGHT, "flexiblewebfaultinsight_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetcommand", null, null)
                    .addWidget("assetcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("performance", "Performance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("latestdowntime", null, null)
                    .addWidget("assetlatestdowntime", "Latest Downtime Reported", PageWidget.WidgetType.LATEST_DOWNTIME, "weblatestdowntime_3_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetoveralldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME, "weboveralldowntime_3_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("downtimehistory", null, null)
                    .addWidget("assetdowntimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY, "flexibledowntimehistory_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("failurerate", null, null)
                    .addWidget("assetfailurerate", "Mean Time Between Failure", PageWidget.WidgetType.FAILURE_RATE, "webfailurerate_8_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetaveragerepairtime", "Mean Time To Repair", PageWidget.WidgetType.AVERAGE_REPAIR_TIME, "webaveragerepairtime_8_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("financial", "Financial", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("costdetail", null, null)
                    .addWidget("assetcostdetail", "Asset Value Details", PageWidget.WidgetType.ASSET_COST_DETAILS, "webassetcostdetail_5_4", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetcostbreakup", "Maintenance Cost Value", PageWidget.WidgetType.COST_BREAKUP, "webcostbreakup_5_8", 4, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("maintenancecost", null, null)
                    .addWidget("assetmaintenancecost", "Maintenance Cost Analysis", PageWidget.WidgetType.MAINTENANCE_COST_TREND, "flexiblewebmaintenancecosttrend_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationschedule", null, null)
                    .addWidget("assetdepreciationschedule", "Depreciation Timeline", PageWidget.WidgetType.DEPRECIATION_SCHEDULE, "flexiblewebdepreciationschedule_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationcosttrend", null, null)
                    .addWidget("assetdepreciationcosttrend", "Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_COST_TREND, "flexiblewebdepreciationcosttrend_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("assetsafetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetsafetyplanprecaution", null, null)
                    .addWidget("assetsafetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of all related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }

        else if(app.getDomainType() == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }
        else {
            return new PagesContext(null, null, "", null, true, false, false)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_4", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_7", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_3", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_4", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone();
        }

    }
    private static JSONObject getSummaryWidgetDetails(String moduleName , ApplicationContext app) throws Exception {
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = moduleBean.getModule(moduleName);
        FacilioField descFields = moduleBean.getField("description", moduleName);
        FacilioField categoryField = moduleBean.getField("category", moduleName);
        FacilioField typeField=moduleBean.getField("type",moduleName);
        FacilioField departmentField=moduleBean.getField("department",moduleName);
        FacilioField manufacturerField = moduleBean.getField("manufacturer", moduleName);
        FacilioField unitPriceField=moduleBean.getField("unitPrice",moduleName);

        SummaryWidget pageWidget = new SummaryWidget();
        SummaryWidgetGroup widgetGroup = new SummaryWidgetGroup();
        widgetGroup.setName("generalInformation");
        widgetGroup.setDisplayName("General Information");
        addSummaryFieldInWidgetGroup(widgetGroup, descFields,1, 1, 4);
        addSummaryFieldInWidgetGroup(widgetGroup, categoryField, 2 , 1, 1);
        addSummaryFieldInWidgetGroup(widgetGroup,typeField,2,2,1);
        addSummaryFieldInWidgetGroup(widgetGroup,departmentField,2,3,1);
        addSummaryFieldInWidgetGroup(widgetGroup,manufacturerField,2,4,1);
        addSummaryFieldInWidgetGroup(widgetGroup,unitPriceField,3,1,1);

        widgetGroup.setColumns(4);


        FacilioField sysCreatedByField=moduleBean.getField("sysCreatedBy",moduleName);
        FacilioField sysCreatedTimeField = moduleBean.getField("sysCreatedTime", moduleName);
        FacilioField sysModifiedByField = moduleBean.getField("sysModifiedBy", moduleName);
        FacilioField sysModifiedTimeField=moduleBean.getField("sysModifiedTime",moduleName);



        SummaryWidgetGroup otherWidgetGroup = new SummaryWidgetGroup();
        otherWidgetGroup.setName("systemInformation");
        otherWidgetGroup.setDisplayName("System Information");
        addSummaryFieldInWidgetGroup(otherWidgetGroup,sysCreatedByField ,1, 1, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup, sysCreatedTimeField, 1 , 2, 1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,sysModifiedByField,1,3,1);
        addSummaryFieldInWidgetGroup(otherWidgetGroup,sysModifiedTimeField,1,4,1);

        otherWidgetGroup.setColumns(4);


        List<SummaryWidgetGroup> widgetGroupList = new ArrayList<>();
        widgetGroupList.add(widgetGroup);
        widgetGroupList.add(otherWidgetGroup);
        pageWidget.setDisplayName("Asset Details");
        pageWidget.setModuleId(module.getModuleId());
        pageWidget.setAppId(app.getId());
        pageWidget.setGroups(widgetGroupList);
        return FieldUtil.getAsJSON(pageWidget);
    }
    private static void addSummaryFieldInWidgetGroup(SummaryWidgetGroup widgetGroup, FacilioField field, int rowIndex, int colIndex, int colSpan) {
        if(field != null) {
            SummaryWidgetGroupFields summaryField = new SummaryWidgetGroupFields();
            summaryField.setName(field.getName());
            summaryField.setDisplayName(field.getDisplayName());
            summaryField.setFieldId(field.getFieldId());
            summaryField.setRowIndex(rowIndex);
            summaryField.setColIndex(colIndex);
            summaryField.setColSpan(colSpan);
            if(widgetGroup.getFields() == null) {
                widgetGroup.setFields(new ArrayList<>(Arrays.asList(summaryField)));
            }
            else {
                widgetGroup.getFields().add(summaryField);
            }
        }
    }
    private static JSONObject getWidgetGroup(boolean isMobile) throws Exception {
        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "assetnotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "assetattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }
}