package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.SystemButtonApi;
import com.facilio.bmsconsole.workflow.rule.CustomButtonRuleContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonAppRelContext;
import com.facilio.bmsconsole.workflow.rule.SystemButtonRuleContext;
import com.facilio.db.criteria.operators.*;
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
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.v3.context.Constants;

import java.util.*;

import static com.facilio.bmsconsole.util.SystemButtonApi.addSystemButton;

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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        assetView.setAppLinkNames(appLinkNames);

        return assetView;
    }
    public void addData() throws Exception {
        addSystemButtons();
        addListSystemButtons();
    }
    private static void addSystemButtons() throws Exception {
        SystemButtonRuleContext editAsset = new SystemButtonRuleContext();
        editAsset.setName("Edit");
        editAsset.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editAsset.setIdentifier("editAsset");
        editAsset.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        editAsset.setPermission(AccountConstants.ModulePermission.UPDATE.name());
        editAsset.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ASSET,editAsset);
        SystemButtonRuleContext duplicateAsset = new SystemButtonRuleContext();
        duplicateAsset.setName("Duplicate");
        duplicateAsset.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        duplicateAsset.setIdentifier("duplicateAsset");
        duplicateAsset.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        duplicateAsset.setPermission(AccountConstants.ModulePermission.CREATE.name());
        duplicateAsset.setPermissionRequired(true);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ASSET,duplicateAsset);
        SystemButtonRuleContext applyDepreciation = new SystemButtonRuleContext();
        applyDepreciation.setName("Apply Depreciation");
        applyDepreciation.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        applyDepreciation.setIdentifier("assetDepreciation");
        applyDepreciation.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ASSET,applyDepreciation);
        SystemButtonRuleContext moveToStoreRoom = new SystemButtonRuleContext();
        moveToStoreRoom.setName("Move to Storeroom");
        moveToStoreRoom.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        moveToStoreRoom.setIdentifier("moveToStoreroom");
        moveToStoreRoom.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ASSET,moveToStoreRoom);
        SystemButtonRuleContext addMeterRelationShip = new SystemButtonRuleContext();
        addMeterRelationShip.setName("Add Meter Relationship");
        addMeterRelationShip.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        addMeterRelationShip.setIdentifier("addMeterRelationShip");
        addMeterRelationShip.setPositionType(CustomButtonRuleContext.PositionType.SUMMARY.getIndex());
        List<SystemButtonAppRelContext> systemButtonAppRels = new ArrayList<>();
        SystemButtonAppRelContext energyAppRelationshipButton = new SystemButtonAppRelContext();
        energyAppRelationshipButton.setAppId(ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP));
        systemButtonAppRels.add(energyAppRelationshipButton);
        addMeterRelationShip.setSystemButtonAppRels(systemButtonAppRels);
        SystemButtonApi.addSystemButton(FacilioConstants.ContextNames.ASSET,addMeterRelationShip);
    }

    public static void  addListSystemButtons() throws Exception {


        // Table Top Bar buttons
        SystemButtonRuleContext createButtonListTop = new SystemButtonRuleContext();
        createButtonListTop.setName("Create");
        createButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.CREATE.getIndex());
        createButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        createButtonListTop.setIdentifier("create");
        createButtonListTop.setPermissionRequired(true);
        createButtonListTop.setPermission("CREATE");
        addSystemButton(FacilioConstants.ContextNames.ASSET, createButtonListTop);

        // Export buttons
        SystemButtonRuleContext exportAsExcelButtonListTop = new SystemButtonRuleContext();
        exportAsExcelButtonListTop.setName("Export as excel");
        exportAsExcelButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS .getIndex());
        exportAsExcelButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsExcelButtonListTop.setIdentifier("export_as_excel");
        exportAsExcelButtonListTop.setPermissionRequired(true);
        exportAsExcelButtonListTop.setPermission("EXPORT");
        addSystemButton(FacilioConstants.ContextNames.ASSET, exportAsExcelButtonListTop);

        SystemButtonRuleContext exportAsCsvButtonListTop = new SystemButtonRuleContext();
        exportAsCsvButtonListTop.setName("Export as CSV");
        exportAsCsvButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS .getIndex());
        exportAsCsvButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_TOP.getIndex());
        exportAsCsvButtonListTop.setIdentifier("export_as_csv");
        exportAsCsvButtonListTop.setPermissionRequired(true);
        exportAsCsvButtonListTop.setPermission("EXPORT");
        addSystemButton(FacilioConstants.ContextNames.ASSET, exportAsCsvButtonListTop);

        // Table bar - Bulk action buttons
        SystemButtonRuleContext updateButtonListTop = new SystemButtonRuleContext();
        updateButtonListTop.setName("Update");
        updateButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        updateButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        updateButtonListTop.setIdentifier("update_bulk");
        updateButtonListTop.setPermissionRequired(true);
        updateButtonListTop.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.ASSET, updateButtonListTop);

        SystemButtonRuleContext printQRButtonListTop = new SystemButtonRuleContext();
        printQRButtonListTop.setName("Print QR");
        printQRButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        printQRButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        printQRButtonListTop.setIdentifier("printQR");
        addSystemButton(FacilioConstants.ContextNames.ASSET, printQRButtonListTop);

        SystemButtonRuleContext downloadQRButtonListTop = new SystemButtonRuleContext();
        downloadQRButtonListTop.setName("Download QR");
        downloadQRButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.OTHERS.getIndex());
        downloadQRButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        downloadQRButtonListTop.setIdentifier("downloadQR");
        addSystemButton(FacilioConstants.ContextNames.ASSET, downloadQRButtonListTop);

        SystemButtonRuleContext deleteButtonListTop = new SystemButtonRuleContext();
        deleteButtonListTop.setName("Delete");
        deleteButtonListTop.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        deleteButtonListTop.setPositionType(CustomButtonRuleContext.PositionType.LIST_BAR.getIndex());
        deleteButtonListTop.setIdentifier("delete_bulk");
        deleteButtonListTop.setPermissionRequired(true);
        deleteButtonListTop.setPermission("DELETE");
        addSystemButton(FacilioConstants.ContextNames.ASSET, deleteButtonListTop);

        // Each Record wise
        SystemButtonRuleContext editButtonList = new SystemButtonRuleContext();
        editButtonList.setName("Edit");
        editButtonList.setButtonType(SystemButtonRuleContext.ButtonType.EDIT.getIndex());
        editButtonList.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        editButtonList.setIdentifier("edit_list");
        editButtonList.setPermissionRequired(true);
        editButtonList.setPermission("UPDATE");
        addSystemButton(FacilioConstants.ContextNames.ASSET, editButtonList);

        SystemButtonRuleContext deleteButtonList = new SystemButtonRuleContext();
        deleteButtonList.setName("Delete");
        deleteButtonList.setButtonType(SystemButtonRuleContext.ButtonType.DELETE.getIndex());
        deleteButtonList.setPositionType(CustomButtonRuleContext.PositionType.LIST_ITEM.getIndex());
        deleteButtonList.setIdentifier("delete_list");
        deleteButtonList.setPermissionRequired(true);
        deleteButtonList.setPermission("DELETE");
        addSystemButton(FacilioConstants.ContextNames.ASSET, deleteButtonList);

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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        for (String appName : appLinkNames) {
            if(appName.equals(FacilioConstants.ApplicationLinkNames.ENERGY_APP)){
                appNameVsPage.put(appName,createEnergyAppAssetDefaultPage(ApplicationApi.getApplicationForLinkName(appName),module,false,true));
            }
            else if(appName.equals(FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING)){
                appNameVsPage.put(appName,createRemoteMonitoringAssetDefaultPage(ApplicationApi.getApplicationForLinkName(appName),module,false,true));
            }
            else if(appName.equals(FacilioConstants.ApplicationLinkNames.FSM_APP)){
                appNameVsPage.put(appName,createFsmAssetDefaultPage(ApplicationApi.getApplicationForLinkName(appName),module,false,true));
            }
            else {
                appNameVsPage.put(appName, createAssetDefaultPage(ApplicationApi.getApplicationForLinkName(appName), module, false, true));
            }
        }
        return appNameVsPage;
    }
    public static List<PagesContext> createAssetDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);

        Criteria criteria = new Criteria();
        Condition rotatingItem = new Condition();
        rotatingItem.setFieldName("rotatingItem");
        rotatingItem.setColumnName("Assets.ROTATING_ITEM");
        rotatingItem.setOperator(CommonOperators.IS_NOT_EMPTY);
        rotatingItem.setModuleName(FacilioConstants.ContextNames.ASSET);
        criteria.addOrCondition(rotatingItem);

        Condition rotatingTool = new Condition();
        rotatingTool.setFieldName("rotatingTool");
        rotatingTool.setColumnName("Assets.ROTATING_TOOL");
        rotatingTool.setOperator(CommonOperators.IS_NOT_EMPTY);
        rotatingTool.setModuleName(FacilioConstants.ContextNames.ASSET);
        criteria.addOrCondition(rotatingTool);


        if(app.getDomainType() == AppDomain.AppDomainType.FACILIO.getIndex()) {

            List<String> moduleToRemove=new ArrayList<>();
            moduleToRemove.add(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
            moduleToRemove.add("multiResource");
            moduleToRemove.add(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);
            moduleToRemove.add(FacilioConstants.ContextNames.SERVICE_ORDER);
            moduleToRemove.add("serviceAppointment");
            moduleToRemove.add("serviceOrderItems");
            moduleToRemove.add("rawAlarm");
            moduleToRemove.add("flaggedEvent");
            moduleToRemove.add("filteredAlarm");
            moduleToRemove.add("alarmAssetTagging");
            moduleToRemove.add("command");
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null, isTemplate, isDefault, true)

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


                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetRelatedReadings", null, null)
                    .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()


                    .addTab("spareparts", "Spare Parts", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.ASSET_SPARE_PARTS)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("spareparts", null, null)
                    .addWidget("spareparts", "Spare Parts", PageWidget.WidgetType.SPARE_PARTS, "flexiblewebspareparts_6", 0, 0, null, null)
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

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true,null)
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

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
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
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
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

                    .layoutDone()
                    .pageDone()



                    .addPage("assetinventorypage", "Asset Inventory Page", "", criteria, isTemplate, isDefault, true)
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


                    .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("assetreadings", null, null)
                    .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .addSection("assetRelatedReadings", null, null)
                    .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("spareparts", "Spare Parts", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.ASSET_SPARE_PARTS)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("spareparts", null, null)
                    .addWidget("spareparts", "Spare Parts", PageWidget.WidgetType.SPARE_PARTS, "flexiblewebspareparts_6", 0, 0, null, null)
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

                    .addTab("inventoryusage", "Inventory Usage", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("inventoryusage", null, null)
                    .addWidget("inventoryusage", "Inventory Usage", PageWidget.WidgetType.INVENTORY_USAGE, "flexiblewebinventoryusage_6", 0, 0, null, null)
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

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
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

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
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
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
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

                    .layoutDone()
                    .pageDone()


            .getCustomPages();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.VENDOR_PORTAL.getIndex()) {
            List<String> moduleToRemove=new ArrayList<>();
            moduleToRemove.add(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
            moduleToRemove.add("multiResource");
            moduleToRemove.add(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);
            moduleToRemove.add(FacilioConstants.ContextNames.SERVICE_ORDER);
            moduleToRemove.add("serviceAppointment");
            moduleToRemove.add("serviceOrderItems");
            moduleToRemove.add("rawAlarm");
            moduleToRemove.add("flaggedEvent");
            moduleToRemove.add("filteredAlarm");
            moduleToRemove.add("alarmAssetTagging");
            moduleToRemove.add("command");

            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
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
                    .addSection("assetRelatedReadings", null, null)
                    .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("spareparts", "Spare Parts", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.ASSET_SPARE_PARTS)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("spareparts", null, null)
                    .addWidget("spareparts", "Spare Parts", PageWidget.WidgetType.SPARE_PARTS, "flexiblewebspareparts_6", 0, 0, null, null)
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

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
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

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
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
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
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
                    .layoutDone()
                    .pageDone()

                    .addPage("assetinventorypage", "Asset Inventory Page", "", criteria, isTemplate, isDefault, true)
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
                    .addSection("assetRelatedReadings", null, null)
                    .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .addTab("spareparts", "Spare Parts", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.ASSET_SPARE_PARTS)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("spareparts", null, null)
                    .addWidget("spareparts", "Spare Parts", PageWidget.WidgetType.SPARE_PARTS, "flexiblewebspareparts_6", 0, 0, null, null)
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

                    .addTab("inventoryusage", "Inventory Usage", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("inventoryusage", null, null)
                    .addWidget("inventoryusage", "Inventory Usage", PageWidget.WidgetType.INVENTORY_USAGE, "flexiblewebinventoryusage_6", 0, 0, null, null)
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

                    .addTab("safetyplan", "Safety Plan", PageTabContext.TabType.SIMPLE, true, null)
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

                    .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
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
                    .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
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
                    .layoutDone()
                    .pageDone()
                    .getCustomPages();
        }
        else if(app.getDomainType() == AppDomain.AppDomainType.CLIENT_PORTAL.getIndex()) {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
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

                    .layoutDone()

                    .pageDone()

                    .addPage("assetinventorypage", "Asset Inventory Page", "", criteria, isTemplate, isDefault, true)
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


                    .addTab("inventoryusage", "Inventory Usage", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("inventoryusage", null, null)
                    .addWidget("inventoryusage", "Inventory Usage", PageWidget.WidgetType.INVENTORY_USAGE, "flexiblewebinventoryusage_6", 0, 0, null, null)
                    .widgetDone()
                    .sectionDone()
                    .columnDone()
                    .tabDone()

                    .layoutDone()

                    .pageDone()

                    .getCustomPages();
        }
        else {
            return new ModulePages()
                    .addPage("assetdefaultpage", "Default Asset Page", "", null,  isTemplate, isDefault, true)
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

                    .layoutDone()
                    .pageDone()


                    .addPage("assetinventorypage", "Asset Inventory Page", "", criteria, isTemplate, isDefault, true)
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

                    .addTab("inventoryusage", "Inventory Usage", PageTabContext.TabType.SIMPLE, true, AccountUtil.FeatureLicense.INVENTORY)
                    .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                    .addSection("inventoryusage", null, null)
                    .addWidget("inventoryusage", "Inventory Usage", PageWidget.WidgetType.INVENTORY_USAGE, "flexiblewebinventoryusage_6", 0, 0, null, null)
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

                    .layoutDone()
                    .pageDone()


                    .getCustomPages();
        }

    }
    public static List<PagesContext> createEnergyAppAssetDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);

        List<String> moduleToRemove=new ArrayList<>();
        moduleToRemove.add(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
        moduleToRemove.add("multiResource");
        moduleToRemove.add(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);
        moduleToRemove.add(FacilioConstants.ContextNames.SERVICE_ORDER);
        moduleToRemove.add("serviceAppointment");
        moduleToRemove.add("serviceOrderItems");
        moduleToRemove.add("rawAlarm");
        moduleToRemove.add("flaggedEvent");
        moduleToRemove.add("filteredAlarm");
        moduleToRemove.add("alarmAssetTagging");

        return new ModulePages()
                .addPage("assetdefaultpage", "Default Asset Page", "", null, isTemplate, isDefault, true)

                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
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
                .addSection("assetRelatedReadings", null, null)
                .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
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


                .addTab("related", "Related", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("relationships", "Relationships", "List of relationships and types between records across modules")
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relatedlist", "Related List", "List of all related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .addTab("meters", "Meters", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("meterRelations", "", "")
                .addWidget("meterRelationsWidget", "Relationships", PageWidget.WidgetType.METER_RELATIONSHIPS,"flexiblewebmeterrelationshipwidget_6", 0, 0, null, null)
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

                .layoutDone()
                .pageDone()

                .getCustomPages();
    }

    public static List<PagesContext> createRemoteMonitoringAssetDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);

        List<String> moduleToRemove=new ArrayList<>();
        moduleToRemove.add(FacilioConstants.ContextNames.ASSET_SPARE_PARTS);
        moduleToRemove.add("multiResource");
        moduleToRemove.add(FacilioConstants.ContextNames.ASSET_DEPRECIATION_REL);
        moduleToRemove.add(FacilioConstants.ContextNames.SERVICE_ORDER);
        moduleToRemove.add("serviceAppointment");
        moduleToRemove.add("serviceOrderItems");

        return new ModulePages()
                .addPage("assetdefaultpage", "Default Asset Page", "", null, isTemplate, isDefault, true)

                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
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

                .addTab("readings", "Readings", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("assetreadings", null, null)
                .addWidget("readings", "Readings", PageWidget.WidgetType.READINGS, "flexiblewebreadings_7", 0, 0, null, null)
                .widgetDone()
                .sectionDone()
                .addSection("assetRelatedReadings", null, null)
                .addWidget("relatedReadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS, "flexiblewebrelatedreadings_7", 0, 0, null, null)
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

                .addTab("classification", "Classification", PageTabContext.TabType.SIMPLE, true, null)
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
                .addWidget("bulkrelationshipwidget", "Relationships", PageWidget.WidgetType.BULK_RELATION_SHIP_WIDGET, "flexiblewebbulkrelationshipwidget_6", 0, 0, null, RelationshipWidgetUtil.fetchRelationshipsOfModule(module))
                .widgetDone()
                .sectionDone()
                .addSection("relatedlist", "Related List", "List of all related records across modules")
                .addWidget("bulkrelatedlist", "Related List", PageWidget.WidgetType.BULK_RELATED_LIST, "flexiblewebbulkrelatedlist_6", 0, 0, null, RelatedListWidgetUtil.fetchAllRelatedListForModule(module,false,null,moduleToRemove))
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

                .layoutDone()
                .pageDone()
                .getCustomPages();
    }

    public static List<PagesContext> createFsmAssetDefaultPage(ApplicationContext app, FacilioModule module, boolean isTemplate, boolean isDefault) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        JSONObject historyWidgetParam = new JSONObject();
        historyWidgetParam.put("activityModuleName", FacilioConstants.ContextNames.ASSET_ACTIVITY);

        return new ModulePages()
                .addPage("assetdefaultpage", "Default Asset Page", "", null, isTemplate, isDefault, true)

                .addLayout(PagesContext.PageLayoutType.WEB)
                .addTab("summary", "Summary", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.THREE_QUARTER_WIDTH)
                .addSection("summaryfields", "", null)
                .addWidget("summaryFieldsWidget", "Asset details", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET, "flexiblewebsummaryfieldswidget_5", 0, 0, null, getSummaryWidgetDetails(module.getName(), app))
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

                .addTab("history", "History", PageTabContext.TabType.SIMPLE, true, null)
                .addColumn(PageColumnContext.ColumnWidth.FULL_WIDTH)
                .addSection("activity", null, null)
                .addWidget("activity", "History", PageWidget.WidgetType.ACTIVITY, "flexiblewebactivity_4", 0, 0, historyWidgetParam, null)
                .widgetDone()
                .sectionDone()
                .columnDone()
                .tabDone()

                .layoutDone()
                .pageDone()
                .getCustomPages();
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
                .addWidget("commentwidget", "Comment", PageWidget.WidgetType.COMMENT, isMobile?"flexiblemobilecomment_8":"flexiblewebcomment_5", 0, 0, notesWidgetParam, null)
                .widgetGroupWidgetDone()
                .widgetGroupSectionDone()
                .addSection("documents", "Documents", "")
                .addWidget("attachmentwidget", "Documents", PageWidget.WidgetType.ATTACHMENT, isMobile?"flexiblemobileattachment_8":"flexiblewebattachment_5", 0, 0, attachmentWidgetParam, null)
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
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.FSM_APP);
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
        assetForm.setAppLinkNamesForForm(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP,FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.REMOTE_MONITORING,FacilioConstants.ApplicationLinkNames.FSM_APP));

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
        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }


    private FormRuleContext addBinFilterRule() throws Exception {

        FormRuleContext singleRule = new FormRuleContext();
        singleRule.setName("Bin Filter Rule");
        singleRule.setRuleType(FormRuleContext.RuleType.ACTION.getIntVal());
        singleRule.setTriggerType(FormRuleContext.TriggerType.FIELD_UPDATE.getIntVal());
        singleRule.setType(FormRuleContext.FormRuleType.FROM_FORM.getIntVal());

        FormRuleTriggerFieldContext itemField = new FormRuleTriggerFieldContext();
        itemField.setFieldName("Rotating - Item Type");

        FormRuleTriggerFieldContext storeRoom = new FormRuleTriggerFieldContext();
        storeRoom.setFieldName("Storeroom");
        singleRule.setTriggerFields(Arrays.asList(itemField,storeRoom));

        List<FormRuleActionContext> actions = new ArrayList<FormRuleActionContext>();

        FormRuleActionContext filterAction = new FormRuleActionContext();
        filterAction.setActionType(FormActionType.APPLY_FILTER.getVal());

        FormRuleActionFieldsContext actionField = new FormRuleActionFieldsContext();

        actionField.setFormFieldName("Bin");

        actionField.setCriteria(binItemCriteria());

        filterAction.setFormRuleActionFieldsContext(Collections.singletonList(actionField));
        actions.add(filterAction);
        singleRule.setActions(actions);

        singleRule.setAppLinkNamesForRule(Arrays.asList(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP,FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP, FacilioConstants.ApplicationLinkNames.ENERGY_APP,FacilioConstants.ApplicationLinkNames.FSM_APP));
        return singleRule;
    }
    private static Criteria binItemCriteria() throws Exception {
        Criteria rotatingItemTypeCriteria = new Criteria();
        rotatingItemTypeCriteria.addAndCondition(getStoreRoomCondition());
        rotatingItemTypeCriteria.addAndCondition(getItemTypeCondition());
        return rotatingItemTypeCriteria;
    }

    private static Condition getStoreRoomCondition() throws Exception {
        ModuleBean modBean = Constants.getModBean();

        Criteria criteriaValue = new Criteria();
        Condition storeRoomCondition = CriteriaAPI.getCondition(modBean.getField("storeRoom","item"),"${asset.storeRoom.id}",StringOperators.IS);
        criteriaValue.addAndCondition(storeRoomCondition);

        LookupField itemField = (LookupField) modBean.getField("item", "bin");
        itemField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ITEM));

        Condition storeRoomParentCondition = new Condition();
        storeRoomParentCondition.setField(itemField);
        storeRoomParentCondition.setOperator(LookupOperator.LOOKUP);
        storeRoomParentCondition.setCriteriaValue(criteriaValue);

        return storeRoomParentCondition;
    }
    private static Condition getItemTypeCondition() throws Exception {
        ModuleBean modBean = Constants.getModBean();

        Criteria criteriaValue = new Criteria();
        Condition itemTypeCondition = CriteriaAPI.getCondition(modBean.getField("itemType","item"),"${asset.rotatingItemType.id}",StringOperators.IS);
        criteriaValue.addAndCondition(itemTypeCondition);

        LookupField itemField = (LookupField) modBean.getField("item", "bin");
        itemField.setLookupModule(modBean.getModule(FacilioConstants.ContextNames.ITEM));

        Condition itemTypeParentCondition = new Condition();
        itemTypeParentCondition.setField(itemField);
        itemTypeParentCondition.setOperator(LookupOperator.LOOKUP);
        itemTypeParentCondition.setCriteriaValue(criteriaValue);

        return itemTypeParentCondition;
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

        ScopeVariableModulesFields energyApp = new ScopeVariableModulesFields();
        energyApp.setScopeVariableId(ScopingUtil.getScopeVariableId("default_energy_site"));
        energyApp.setModuleId(module.getModuleId());
        energyApp.setFieldName("siteId");

        scopeConfigList = Arrays.asList(maintenanceApp,energyApp);
        return scopeConfigList;
    }

    @Override
    public void addClassificationDataModule() throws Exception {
        String tableName="Assets_Classification_Data";
        ClassificationUtil.addClassificationDataModule(Constants.getModBean().getModule(FacilioConstants.ContextNames.ASSET), tableName);
    }
}
