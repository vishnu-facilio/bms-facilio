package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.*;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.json.simple.JSONObject;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.forms.*;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.RelatedListWidgetUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsoleV3.context.ScopeVariableModulesFields;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.classification.util.ClassificationUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.*;

public class AssetModule extends BaseModuleConfig{
    public AssetModule() throws Exception {
        setModuleName(FacilioConstants.ContextNames.ASSET);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails = new HashMap<>();

        int order = 1;
        ArrayList<FacilioView> asset = new ArrayList<FacilioView>();
        asset.add(getAllAssetsView().setOrder(order++));
        asset.add(getAssets("Energy").setOrder(order++));
        asset.add(getAssets("HVAC").setOrder(order++));
        asset.add(getAssetsByState("Active").setOrder(order++));
        asset.add(getAssetsByState("Retired").setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "assetviews");
        groupDetails.put("displayName", "Asset");
        groupDetails.put("views", asset);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllAssetsView() {
        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Assets");
        allView.setSortFields(getSortFields(FacilioConstants.ContextNames.ASSET));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        allView.setAppLinkNames(appLinkNames);

        return allView;
    }

    private static FacilioView getAssets(String category) {

        FacilioView assetView = new FacilioView();
        if (category.equals("Energy")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("energy");
            assetView.setDisplayName("Energy Assets");
            assetView.setCriteria(criteria);
        } else if (category.equals("HVAC")) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getAssetCategoryCondition(category));

            assetView.setName("hvac");
            assetView.setDisplayName("HVAC Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        assetView.setAppLinkNames(appLinkNames);

        return assetView;
    }

    @Override
    public Map<String, List<PagesContext>> fetchSystemPageConfigs() throws Exception {
        Map<String,List<PagesContext>> appNameVsPage = new HashMap<>();

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        for (String appName : appLinkNames) {
            appNameVsPage.put(appName, createAssetDefaultPage(ApplicationApi.getApplicationForLinkName(appName), module, false, true));
        }
        return appNameVsPage;
    }
    public static List<PagesContext> createAssetDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);


        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null, isTemplate, isDefault, true)

                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_13", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_18", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_36", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_36", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_12", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("fault", "Fault", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("faultreport", "", null)
                    .addWidget("assetfaultreport", "Fault Reports", PageWidget.WidgetType.FAULT_REPORT, "flexiblewebfaultreport_27", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("faultinsight", null, null)
                    .addWidget("assetfaultinsight", "Fault Insights", PageWidget.WidgetType.FAULT_INSIGHT, "flexiblewebfaultinsight_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_33", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetcommand", null, null)
                    .addWidget("assetcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_34", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("performance", "Performance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("latestdowntime", null, null)
                    .addWidget("assetlatestdowntime", "Latest Downtime Reported", PageWidget.WidgetType.LATEST_DOWNTIME, "weblatestdowntime_16_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetoveralldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME, "weboveralldowntime_16_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("downtimehistory", null, null)
                    .addWidget("assetdowntimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY, "flexibledowntimehistory_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("failurerate", null, null)
                    .addWidget("assetfailurerate", "Mean Time Between Failure", PageWidget.WidgetType.FAILURE_RATE, "webfailurerate_41_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetaveragerepairtime", "Mean Time To Repair", PageWidget.WidgetType.AVERAGE_REPAIR_TIME, "webaveragerepairtime_41_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("financial", "Financial", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("costdetail", null, null)
                    .addWidget("assetcostdetail", "Asset Value Details", PageWidget.WidgetType.ASSET_COST_DETAILS, "webassetcostdetail_25_4", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetcostbreakup", "Maintenance Cost Value", PageWidget.WidgetType.COST_BREAKUP, "webcostbreakup_25_8", 4, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("maintenancecost", null, null)
                    .addWidget("assetmaintenancecost", "Maintenance Cost Analysis", PageWidget.WidgetType.MAINTENANCE_COST_TREND, "flexiblewebmaintenancecosttrend_31", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationschedule", null, null)
                    .addWidget("assetdepreciationschedule", "Depreciation Timeline", PageWidget.WidgetType.DEPRECIATION_SCHEDULE, "flexiblewebdepreciationschedule_20", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationcosttrend", null, null)
                    .addWidget("assetdepreciationcosttrend", "Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_COST_TREND, "flexiblewebdepreciationcosttrend_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("assetsafetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetsafetyplanprecaution", null, null)
                    .addWidget("assetsafetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of all related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone()
                    .pageDone()


            .getCustomPages();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
                    .addLayout(PagesContext.PageLayoutType.WEB)
                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_13", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_18", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_36", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_36", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_12", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("fault", "Fault", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("faultreport", "", null)
                    .addWidget("assetfaultreport", "Fault Reports", PageWidget.WidgetType.FAULT_REPORT, "flexiblewebfaultreport_27", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("faultinsight", null, null)
                    .addWidget("assetfaultinsight", "Fault Insights", PageWidget.WidgetType.FAULT_INSIGHT, "flexiblewebfaultinsight_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_33", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetcommand", null, null)
                    .addWidget("assetcommand", "Commands", PageWidget.WidgetType.COMMANDS_WIDGET, "flexiblewebcommandswidget_34", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("performance", "Performance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("latestdowntime", null, null)
                    .addWidget("assetlatestdowntime", "Latest Downtime Reported", PageWidget.WidgetType.LATEST_DOWNTIME, "weblatestdowntime_16_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetoveralldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME, "weboveralldowntime_16_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("downtimehistory", null, null)
                    .addWidget("assetdowntimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY, "flexibledowntimehistory_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("failurerate", null, null)
                    .addWidget("assetfailurerate", "Mean Time Between Failure", PageWidget.WidgetType.FAILURE_RATE, "webfailurerate_41_6", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetaveragerepairtime", "Mean Time To Repair", PageWidget.WidgetType.AVERAGE_REPAIR_TIME, "webaveragerepairtime_41_6", 6, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("financial", "Financial", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("costdetail", null, null)
                    .addWidget("assetcostdetail", "Asset Value Details", PageWidget.WidgetType.ASSET_COST_DETAILS, "webassetcostdetail_25_4", 0, 0, null, null)
                    .widgetDone()
                    .addWidget("assetcostbreakup", "Maintenance Cost Value", PageWidget.WidgetType.COST_BREAKUP, "webcostbreakup_25_8", 4, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("maintenancecost", null, null)
                    .addWidget("assetmaintenancecost", "Maintenance Cost Analysis", PageWidget.WidgetType.MAINTENANCE_COST_TREND, "flexiblewebmaintenancecosttrend_31", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationschedule", null, null)
                    .addWidget("assetdepreciationschedule", "Depreciation Timeline", PageWidget.WidgetType.DEPRECIATION_SCHEDULE, "flexiblewebdepreciationschedule_20", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("depreciationcosttrend", null, null)
                    .addWidget("assetdepreciationcosttrend", "Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_COST_TREND, "flexiblewebdepreciationcosttrend_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.SAFETY_PLAN)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("safetyplanhazard", null, null)
                    .addWidget("assetsafetyplanhazard", "Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD, "flexiblewebsafetyplanhazard_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetsafetyplanprecaution", null, null)
                    .addWidget("assetsafetyplanprecaution", "Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS, "flexiblewebsafetyplanprecautions_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.CLASSIFICATION)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("classification", null, null)
                    .addWidget("classification", "Classification", PageWidget.WidgetType.CLASSIFICATION, "flexiblewebclassification_28", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                    .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_29", 0, 0,  null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                    .widgetDone()
                    .sectionDone()
                    .addSection("relatedlist", "Related List", "List of all related records across modules")
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_29", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()
                    .layoutDone()
                    .pageDone().getCustomPages();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_13", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_18", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_36", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_36", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_12", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone()

                    .pageDone().getCustomPages();
        }
        else {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
                    .addLayout(PagesContext.PageLayoutType.WEB)

                    .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("summaryfields", "", null)
                    .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_24", 0, 0, null, getSummaryWidgetDetails(module.getName(),app))
                    .widgetDone()
                    .sectionDone()
                    .addSection("widgetGroup", null, null)
                    .addWidget("widgetGroup", null, PageWidget.WidgetType.WIDGET_GROUP, "flexiblewebwidgetgroup_20", 0, 0, null, getWidgetGroup(false))
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("locationdetails", null, null)
                    .addWidget("assetlocationdetails", "Location details", PageWidget.WidgetType.ASSET_LOCATION, "flexiblewebassetlocation_13", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("operatinghours", null, null)
                    .addWidget("assetoperatinghours", "Operating hours", PageWidget.WidgetType.OPERATING_HOURS, "flexibleweboperatinghours_18", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("maintenance", "Maintenance", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                    .addSection("plannedmaintenance", "", null)
                    .addWidget("assetplannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE, "flexiblewebplannedmaintenance_36", 0, 0, null,null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("unplannedmaintenance", "", null)
                    .addWidget("assetunplannedmaintenance", "Reactive Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE, "flexiblewebunplannedmaintenance_36", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .addColumn(PageColumnContext.ColumnWidth.QUARTER_WIDTH)
                    .addSection("workorderdetails", null, null)
                    .addWidget("assetworkorderdetail", "Maintenance Insights", PageWidget.WidgetType.WORKORDER_INSIGHT, "flexiblewebworkorderinsight_12", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("recentlyclosedppm", null, null)
                    .addWidget("assetrecentlyclosed", "Recently Closed Work order", PageWidget.WidgetType.RECENTLY_CLOSED_PM, "flexiblewebrecentlyclosedpm_23", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("activity", null, null)
                    .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_20", 0, 0, historyWidgetParam, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone()
                    .pageDone().getCustomPages();
        }

    }
    private static JSONObject getSummaryWidgetDetails(String moduleName,  ApplicationContext app) throws Exception {
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
    private static org.json.simple.JSONObject getWidgetGroup(boolean isMobile) throws Exception {

        JSONObject notesWidgetParam = new JSONObject();
        notesWidgetParam.put("notesModuleName", "assetnotes");

        JSONObject attachmentWidgetParam = new JSONObject();
        attachmentWidgetParam.put("attachmentsModuleName", "assetattachments");

        WidgetGroupContext widgetGroup = new WidgetGroupContext()
                .addConfig(WidgetGroupConfigContext.ConfigType.TAB)
                .addSection("notes", "Notes", "")
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_27", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_27", 0, 0, attachmentWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone();
        return FieldUtil.getAsJSON(widgetGroup);
    }








    private static FacilioView getAssetsByState(String state) {

        FacilioView assetView = new FacilioView();
        Criteria criteria = getAssetStatusCriteria(state);
        if (state.equals("Active")) {
            assetView.setName("active");
            assetView.setDisplayName("Active Assets");
            assetView.setCriteria(criteria);
        } else if (state.equals("Retired")) {
            assetView.setName("retired");
            assetView.setDisplayName("Retired Assets");
            assetView.setCriteria(criteria);
        }

        FacilioField localId = new FacilioField();
        localId.setName("localId");
        localId.setColumnName("LOCAL_ID");
        localId.setDataType(FieldType.NUMBER);
        localId.setModule(ModuleFactory.getAssetsModule());

        assetView.setSortFields(Arrays.asList(new SortField(localId, false)));

        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
        assetView.setAppLinkNames(appLinkNames);

        return assetView;
    }

    public static List<SortField> getSortFields(String moduleName, FacilioModule...module) {
        List<SortField> fields = new ArrayList<>();
        switch (moduleName) {
            case FacilioConstants.ContextNames.ASSET:
                FacilioField localId = new FacilioField();
                localId.setName("localId");
                localId.setColumnName("LOCAL_ID");
                localId.setDataType(FieldType.NUMBER);
                localId.setModule(ModuleFactory.getAssetsModule());

                fields = Arrays.asList(new SortField(localId, false));
                break;
            default:
                if (module.length > 0) {
                    FacilioField createdTime = new FacilioField();
                    createdTime.setName("sysCreatedTime");
                    createdTime.setDataType(FieldType.NUMBER);
                    createdTime.setColumnName("CREATED_TIME");
                    createdTime.setModule(module[0]);

                    fields = Arrays.asList(new SortField(createdTime, false));
                }
                break;
        }
        return fields;
    }

    private static Condition getAssetCategoryCondition(String category) {
        FacilioModule module = ModuleFactory.getAssetsModule();
        LookupField statusField = new LookupField();
        statusField.setName("category");
        statusField.setColumnName("CATEGORY");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(module);
        statusField.setLookupModule(ModuleFactory.getAssetCategoryModule());

        Condition open = new Condition();
        open.setField(statusField);
        open.setOperator(LookupOperator.LOOKUP);
        open.setCriteriaValue(getAssetCategoryCriteria(category));

        return open;
    }

    private static Criteria getAssetStatusCriteria(String status) {
        FacilioField statusTypeField = new FacilioField();
        statusTypeField.setName("status");
        statusTypeField.setColumnName("STATUS");
        statusTypeField.setDataType(FieldType.STRING);
        statusTypeField.setModule(ModuleFactory.getTicketStatusModule());

        Condition statusActive = new Condition();
        statusActive.setField(statusTypeField);
        statusActive.setOperator(StringOperators.IS);
        statusActive.setValue(status);

        Criteria statusCriteria = new Criteria();
        statusCriteria.addAndCondition(statusActive);

        LookupField statusField = new LookupField();
        statusField.setName("moduleState");
        statusField.setColumnName("MODULE_STATE");
        statusField.setDataType(FieldType.LOOKUP);
        statusField.setModule(ModuleFactory.getAssetsModule());
        statusField.setLookupModule(ModuleFactory.getTicketStatusModule());

        Condition ticketActive = new Condition();
        ticketActive.setField(statusField);
        ticketActive.setOperator(LookupOperator.LOOKUP);
        ticketActive.setCriteriaValue(statusCriteria);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(ticketActive);
        return criteria;
    }

    private static Criteria getAssetCategoryCriteria(String category) {
        FacilioField categoryType = new FacilioField();
        categoryType.setName("categoryType");
        categoryType.setColumnName("CATEGORY_TYPE");
        categoryType.setDataType(FieldType.NUMBER);
        categoryType.setModule(ModuleFactory.getAssetCategoryModule());

        Condition statusOpen = new Condition();
        statusOpen.setField(categoryType);
        statusOpen.setOperator(NumberOperators.EQUALS);
        if (category.equals("Energy")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.ENERGY.getIntVal()));
        } else if (category.equals("HVAC")) {
            statusOpen.setValue(String.valueOf(AssetCategoryContext.AssetCategoryType.HVAC.getIntVal()));
        }

        Criteria criteria = new Criteria();
        criteria.addAndCondition(statusOpen);

        return criteria;
    }
    @Override
    public List<FacilioForm> getModuleForms() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);

        FacilioForm assetForm =new FacilioForm();
        assetForm.setDisplayName("Standard");
        assetForm.setName("default_asset_web");
        assetForm.setModule(assetModule);
        assetForm.setLabelPosition(FacilioForm.LabelPosition.TOP);
        assetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING));

        List<FormField> assetFormFields = new ArrayList<>();
        assetFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, "name", 1, 1));
        assetFormFields.add(new FormField("description", FacilioField.FieldDisplayType.TEXTAREA, "Description", FormField.Required.OPTIONAL, 2, 1));
        assetFormFields.add(new FormField("siteId", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Site", FormField.Required.REQUIRED, "site", 3, 2));
        FormField categoryField = new FormField("category", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Category", FormField.Required.REQUIRED, "assetcategory", 4, 2);
        categoryField.setIsDisabled(true);
        assetFormFields.add(categoryField);
        assetFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.OPTIONAL,"assetdepartment", 4, 3));
        assetFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Asset Location", FormField.Required.OPTIONAL, 5, 2));
        assetFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.OPTIONAL,"assettype", 5, 3));
        assetFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 6, 2));
        assetFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 6, 3));
        assetFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 7, 2));
        assetFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 3));
        assetFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 2));
        assetFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 8, 3));
        assetFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATETIME, "Purchased Date", FormField.Required.OPTIONAL, 9, 2));
        assetFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATETIME, "Retire Date", FormField.Required.OPTIONAL, 9, 3));
        assetFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 2));
        assetFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATETIME, "Warranty Expiry Date", FormField.Required.OPTIONAL, 10, 3));
        assetFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 11, 2));
        // new fields
       // assetFormFields.add(new FormField("rotatingItem", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Item", FormField.Required.OPTIONAL, "item", 12,2));
       // assetFormFields.add(new FormField("rotatingTool", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating Tool", FormField.Required.OPTIONAL, "tool", 12,3));
        assetFormFields.add(new FormField("geoLocationEnabled", FacilioField.FieldDisplayType.DECISION_BOX, "Is Movable", FormField.Required.OPTIONAL, 13,2));
        assetFormFields.add(new FormField("moveApprovalNeeded", FacilioField.FieldDisplayType.DECISION_BOX, "Is Move Approval Needed", FormField.Required.OPTIONAL, 13,2));
        assetFormFields.add(new FormField("boundaryRadius", FacilioField.FieldDisplayType.NUMBER, "Boundary Radius", FormField.Required.OPTIONAL, 14, 2));
        assetFormFields.add(new FormField("failureClass", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Failure Class", FormField.Required.OPTIONAL, "failureclass",8, 2));

        assetFormFields.add(new FormField("rotatingItemType", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Rotating - Item Type", FormField.Required.OPTIONAL, "itemTypes", 15,2));
        assetFormFields.add(new FormField("storeRoom", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Storeroom", FormField.Required.OPTIONAL, "storeRoom", 16,2));

        FormSection section = new FormSection("Default", 1, assetFormFields, false);
        section.setSectionType(FormSection.SectionType.FIELDS);
        assetForm.setSections(Collections.singletonList(section));
        assetForm.setIsSystemForm(true);
        assetForm.setType(FacilioForm.Type.FORM);

        FormRuleContext singleRule = addRotatingItemTypeFilterRule();
        assetForm.setDefaultFormRules(Arrays.asList(singleRule));

        FacilioForm mobileAssetForm = new FacilioForm();
        mobileAssetForm.setDisplayName("Asset");
        mobileAssetForm.setName("default_asset_mobile");
        mobileAssetForm.setModule(assetModule);
        mobileAssetForm.setLabelPosition(FacilioForm.LabelPosition.LEFT);
        mobileAssetForm.setShowInMobile(true);
        mobileAssetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP));

        List<FormField> mobileAssetFormFields = new ArrayList<>();
        mobileAssetFormFields.add(new FormField("name", FacilioField.FieldDisplayType.TEXTBOX, "Name", FormField.Required.REQUIRED, 1, 1));
        mobileAssetFormFields.add(new FormField("type", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Type", FormField.Required.OPTIONAL,"assettype", 2, 1));
        mobileAssetFormFields.add(new FormField("space", FacilioField.FieldDisplayType.SPACECHOOSER, "Asset Location", FormField.Required.OPTIONAL, 3, 1 ));
        mobileAssetFormFields.add(new FormField("department", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "Department", FormField.Required.OPTIONAL,"assetdepartment", 4, 1));
        mobileAssetFormFields.add(new FormField("manufacturer", FacilioField.FieldDisplayType.TEXTBOX, "Manufacturer", FormField.Required.OPTIONAL, 5, 1));
        mobileAssetFormFields.add(new FormField("model", FacilioField.FieldDisplayType.TEXTBOX, "Model", FormField.Required.OPTIONAL, 6, 1));
        mobileAssetFormFields.add(new FormField("serialNumber", FacilioField.FieldDisplayType.TEXTBOX, "Serial Number", FormField.Required.OPTIONAL, 7, 1));
        mobileAssetFormFields.add(new FormField("tagNumber", FacilioField.FieldDisplayType.TEXTBOX, "Tag", FormField.Required.OPTIONAL, 8, 1));
        mobileAssetFormFields.add(new FormField("partNumber", FacilioField.FieldDisplayType.TEXTBOX, "Part No.", FormField.Required.OPTIONAL, 9, 1));
        mobileAssetFormFields.add(new FormField("unitPrice", FacilioField.FieldDisplayType.NUMBER, "Unit Price", FormField.Required.OPTIONAL, 10, 1));
        mobileAssetFormFields.add(new FormField("supplier", FacilioField.FieldDisplayType.TEXTBOX, "Supplier", FormField.Required.OPTIONAL, 11, 1));
        mobileAssetFormFields.add(new FormField("purchasedDate", FacilioField.FieldDisplayType.DATE, "Purchased Date", FormField.Required.OPTIONAL, 12, 1));
        mobileAssetFormFields.add(new FormField("retireDate", FacilioField.FieldDisplayType.DATE, "Retire Date", FormField.Required.OPTIONAL, 13, 1));
        mobileAssetFormFields.add(new FormField("warrantyExpiryDate", FacilioField.FieldDisplayType.DATE, "Warranty Expiry Date", FormField.Required.OPTIONAL, 14, 1));
        mobileAssetFormFields.add(new FormField("qrVal", FacilioField.FieldDisplayType.TEXTBOX, "QR Value", FormField.Required.OPTIONAL, 15, 1));

        FormSection mobileAssetFormSection = new FormSection("Default", 1, mobileAssetFormFields, false);
        mobileAssetFormSection.setSectionType(FormSection.SectionType.FIELDS);
        mobileAssetForm.setSections(Collections.singletonList(mobileAssetFormSection));
        mobileAssetForm.setIsSystemForm(true);
        mobileAssetForm.setType(FacilioForm.Type.FORM);

        List<FacilioForm> assetModuleForms = new ArrayList<>();
        assetModuleForms.add(assetForm);
        assetModuleForms.add(mobileAssetForm);

        return assetModuleForms;
    }
    private FormRuleContext addRotatingItemTypeFilterRule() {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Rotating Item Type Filter Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FORM_ON_LOAD.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_RULE.getIntVal());

        FormRuleTriggerFieldContext triggerField = new FormRuleTriggerFieldContext();
        triggerField.setFieldName("Rotating - Item Type");
        singleRule.setTriggerFields(Collections.singletonList(triggerField));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Rotating - Item Type");

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("Item_Types.INDIVIDUAL_TRACKING","isRotating", String.valueOf(true), BooleanOperators.IS));

        actionField.setCriteria(criteria);

        filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));

        actions.add(filterAction);

        singleRule.setActions(actions);
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        return singleRule;
    }

    @Override
    public List<ScopeVariableModulesFields> getGlobalScopeConfig() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(getModuleName());
        List<ScopeVariableModulesFields> scopeConfigList;

        ScopeVariableModulesFields maintenanceApp = new ScopeVariableModulesFields();
        maintenanceApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_maintenance_site"));
        maintenanceApp.setModuleId(module.getModuleId());
        maintenanceApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp);
        return scopeConfigList;
    }

    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="Assets_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET), tableName);
    }
}
