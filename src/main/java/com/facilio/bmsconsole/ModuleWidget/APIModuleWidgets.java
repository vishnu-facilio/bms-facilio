package com.facilio.bmsconsole.ModuleWidget;

import com.facilio.bmsconsole.context.ModuleWidgets;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.annotation.Config;

import java.util.function.Supplier;

@Config
public class APIModuleWidgets {


    /*
    @WidgetsForModule("moduleName")
    public static Supplier<ModuleWidgets> getModuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("summary", "SUMMARY", PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)     ----> add widget
                .addWidgetConfigs(WidgetConfigContext.ConfigType.FIXED, 6, 8 )                    ---->addWidgetConfigurations for the specified widget
                .addWidgetConfigs(WidgetConfigContext.ConfigType.FLEXIBLE, 7, 8)
                .done()                                                                    ----->returns the list of moduleWidgets
                .addModuleWidget("summary2", "SUMMARY2", PageWidget.WidgetType.SECONDARY_DETAILS_WIDGET)
                .addWidgetConfigs(WidgetConfigContext.ConfigType.FLEXIBLE, 6, 8)
                .done();

    }
    */
    @WidgetsForModule("client")
    public static Supplier<ModuleWidgets> getClientModuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("sitelistwidget", "Site List Widget", PageWidget.WidgetType.SITE_LIST_WIDGET)
                .addWidgetConfig("flexiblewebsitelistwidget_29","Site List Widget - 29",WidgetConfigContext.ConfigType.FLEXIBLE,29,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("clientContactlistwidget", "Client Contact List Widget", PageWidget.WidgetType.CLIENT_CONTACT_LIST_WIDGET)
                .addWidgetConfig("flexibleclientcontactlistwidget_29","Workorder List Widget - 29",WidgetConfigContext.ConfigType.FLEXIBLE,29,-1, PagesContext.PageLayoutType.WEB)
                .done();

    }
    @WidgetsForModule("workorder")
    public static Supplier<ModuleWidgets> getWorkorderModuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("multiresource", "Multiresource", PageWidget.WidgetType.MULTIRESOURCE)
                .addWidgetConfig("flexiblewebmultiresource_19","MultiResource - 19",WidgetConfigContext.ConfigType.FLEXIBLE,19,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("responsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY)
                .addWidgetConfig("flexiblewebresponsibility_14","Responsibility - 14",WidgetConfigContext.ConfigType.FLEXIBLE,14,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("timedetails","Time Details",PageWidget.WidgetType.TIME_DETAILS)
                .addWidgetConfig("flexiblewebtimedetails_31","Time Details - 31",WidgetConfigContext.ConfigType.FLEXIBLE,31,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("resource","Resource",PageWidget.WidgetType.RESOURCE)
                .addWidgetConfig("flexiblewebresource_13","Resource - 13",WidgetConfigContext.ConfigType.FLEXIBLE,13,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costdetails","Cost Details",PageWidget.WidgetType.COST_DETAILS)
                .addWidgetConfig("flexiblewebcostdetails_16","Cost Details - 16",WidgetConfigContext.ConfigType.FLEXIBLE,16,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("plans", "Plans", PageWidget.WidgetType.PLANS)
                .addFlexibleWidgetConfig("flexiblewebplans_24","Plans - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("actuals", "Actuals", PageWidget.WidgetType.ACTUALS)
                .addFlexibleWidgetConfig("flexiblewebactuals_24","Actuals - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("tasks", "Tasks", PageWidget.WidgetType.TASKS)
                .addFlexibleWidgetConfig("flexiblewebtasks_24","Tasks - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderitems", "Work Order Items", PageWidget.WidgetType.WORK_ORDER_ITEMS)
                .addFlexibleWidgetConfig("flexiblewebworkorderitems_24","Work Order Items - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workordertools", "Work Order Tools", PageWidget.WidgetType.WORK_ORDER_TOOLS)
                .addFlexibleWidgetConfig("flexiblewebworkordertools_24","Work Order Tools - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderservice", "Work Order Service", PageWidget.WidgetType.WORK_ORDER_SERVICE)
                .addFlexibleWidgetConfig("flexiblewebworkorderservice_24","Work Order Service - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderlabour", "Work Order Labour", PageWidget.WidgetType.WORK_ORDER_LABOUR)
                .addFlexibleWidgetConfig("flexiblewebworkorderlabour_24","Work Order Labour - 24", 24, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("safetyplanhazard", "SafetyPlan Hazard", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyplanprecautions", "Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done()

                .addModuleWidget("relatedrecords", "Related Records", PageWidget.WidgetType.RELATED_RECORDS)
                .addWidgetConfig("flexiblewebrelatedrecords_24","Related Records - 24",WidgetConfigContext.ConfigType.FLEXIBLE,24,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("failurehierarchy", "Failure Hierarchy", PageWidget.WidgetType.FAILURE_HIERARCHY)
                .addWidgetConfig("flexiblewebfailurehierarchy_24","Failure Hierarchy - 24",WidgetConfigContext.ConfigType.FLEXIBLE,24,-1, PagesContext.PageLayoutType.WEB)
                .done();


    }

    /*
  Naming Conventions to be followed for Widget Config Name and Display Name

  configType-Flexible
         Name --> (layoutType)+(widgetTypeName)+(-minHeight)           //"websummaryfieldswidget-4
         DisplayName --> Full Width+(widgetTypeName) +(-4)         //"Full Width Summary Fields Widget - 4"

  configType-Fixed
         Name --> (layoutType)+(widgetTypeName)+(-minHeight)*(minWidth)         //websummaryfieldswidget-4*4
         DisplayName -->(widgetTypeName) +(- minHeight*minWidth)   //Summary Fields Widget - 4*4

 */
    @WidgetsForModule("asset")
    public static Supplier<ModuleWidgets> getAssetWigets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("assetlocation", "Asset Location", PageWidget.WidgetType.ASSET_LOCATION)
                .addWidgetConfig("flexiblewebassetlocation_13", "Asset Location - 13", WidgetConfigContext.ConfigType.FLEXIBLE, 13, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("operatinghours", "Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("flexibleweboperatinghours_18", "Operating Hours - 18", WidgetConfigContext.ConfigType.FLEXIBLE, 18, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("plannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebplannedmaintenance_36", "Planned Maintenance - 36", WidgetConfigContext.ConfigType.FLEXIBLE, 36, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("unplannedmaintenance", "Unplanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebunplannedmaintenance_36", "UnPlanned Maintenance - 36", WidgetConfigContext.ConfigType.FLEXIBLE, 36, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workorderinsight", "Workorder Insight", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .addWidgetConfig("flexiblewebworkorderinsight_12", "Workorder Insight - 12", WidgetConfigContext.ConfigType.FLEXIBLE, 12, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("recentlyclosedpm", "Recently Closed Pm", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .addWidgetConfig("flexiblewebrecentlyclosedpm_23", "Recently Closed Pm - 23", WidgetConfigContext.ConfigType.FLEXIBLE, 23, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("faultreport", "Fault Report", PageWidget.WidgetType.FAULT_REPORT)
                .addWidgetConfig("flexiblewebfaultreport_27", "Fault Report - 27", WidgetConfigContext.ConfigType.FLEXIBLE, 27, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("faultinsight", "Fault Insight", PageWidget.WidgetType.FAULT_INSIGHT)
                .addWidgetConfig("flexiblewebfaultinsight_23", "Fault Insight - 23", WidgetConfigContext.ConfigType.FLEXIBLE, 23, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("readings", "Readings", PageWidget.WidgetType.READINGS)
                .addWidgetConfig("flexiblewebreadings_33", "Readings - 33", WidgetConfigContext.ConfigType.FLEXIBLE, 33, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("commandswidget", "Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .addWidgetConfig("flexiblewebcommandswidget_34", "Commands Widget - 34", WidgetConfigContext.ConfigType.FLEXIBLE, 34, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("latestdowntime", "Latest Downtime", PageWidget.WidgetType.LATEST_DOWNTIME)
                .addWidgetConfig("weblatestdowntime_16_6", "Latest Downtime - 16 - 6", WidgetConfigContext.ConfigType.FIXED, 16, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("overalldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME)
                .addWidgetConfig("weboveralldowntime_16_6", "Overall Downtime - 16 - 6", WidgetConfigContext.ConfigType.FIXED, 16, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("downtimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY)
                .addWidgetConfig("flexibledowntimehistory_28", "Downtime History - 28", WidgetConfigContext.ConfigType.FLEXIBLE, 28, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("failurerate", "Failure Rate", PageWidget.WidgetType.FAILURE_RATE)
                .addWidgetConfig("webfailurerate_41_6", "Failure Rate - 41 - 6", WidgetConfigContext.ConfigType.FIXED, 41, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("averagerepairtime", "Average Repair Time", PageWidget.WidgetType.AVERAGE_REPAIR_TIME)
                .addWidgetConfig("webaveragerepairtime_41_6", "Average Repair Time - 41 - 6", WidgetConfigContext.ConfigType.FIXED, 41, 6, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("assetcostdetail", "Asset Cost Detail", PageWidget.WidgetType.ASSET_COST_DETAILS)
                .addWidgetConfig("webassetcostdetail_25_4", "Asset Cost Detail - 25 - 4", WidgetConfigContext.ConfigType.FIXED, 25, 4, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costbreakup", "Cost Breakup", PageWidget.WidgetType.COST_BREAKUP)
                .addWidgetConfig("webcostbreakup_25_8", "Cost Breakup - 25 - 8", WidgetConfigContext.ConfigType.FIXED, 25, 8, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("maintenancecosttrend", "Maintenance Cost Trend", PageWidget.WidgetType.MAINTENANCE_COST_TREND)
                .addWidgetConfig("flexiblewebmaintenancecosttrend_31", "Maintenance Cost Trend - 31", WidgetConfigContext.ConfigType.FLEXIBLE, 31, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationschedule", "Depreciation Schedule", PageWidget.WidgetType.DEPRECIATION_SCHEDULE)
                .addWidgetConfig("flexiblewebdepreciationschedule_20", "Depreciation Schedule - 20", WidgetConfigContext.ConfigType.FLEXIBLE, 20, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationcosttrend", "Depreciation Cost Trend", PageWidget.WidgetType.DEPRECIATION_COST_TREND)
                .addWidgetConfig("flexiblewebdepreciationcosttrend_28", "Depreciation Cost Trend - 28", WidgetConfigContext.ConfigType.FLEXIBLE, 28, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("safetyplanhazard", "SafetyPlan Hazard", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .addWidgetConfig("flexiblewebsafetyplanhazard_28", "Safetyplan Hazard - 28", WidgetConfigContext.ConfigType.FLEXIBLE, 28, -1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("safetyplanprecautions", "Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .addWidgetConfig("flexiblewebsafetyplanprecautions_28", "Safety Plan Precaution - 28", WidgetConfigContext.ConfigType.FLEXIBLE, 28, -1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("relatedreadings", "Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .addWidgetConfig("flexiblewebrelatedreadings_33", "Related Readings - 33", WidgetConfigContext.ConfigType.FLEXIBLE, 33, -1, PagesContext.PageLayoutType.WEB)
                .done()


                ;
    }

    @WidgetsForModule("employee")
    public static Supplier<ModuleWidgets> getEmployeeWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("lastKnownLocation","Last Known Location",PageWidget.WidgetType.LAST_KNOWN_LOCATION)
                    .addWidgetConfig("webLastKnownLocation_22_6","Last Known Location - 22 - 6", WidgetConfigContext.ConfigType.FIXED,22,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("currentStatus","Employee Current Status",PageWidget.WidgetType.CURRENT_STATUS)
                    .addWidgetConfig("webCurrentStatus_22_6","Employee Current Status - 22 - 6", WidgetConfigContext.ConfigType.FIXED,22,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("skill","Skill",PageWidget.WidgetType.SKILL)
                    .addWidgetConfig("flexibleSkill_48","Skill - 48", WidgetConfigContext.ConfigType.FLEXIBLE,48,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workSchedule","Work Schedule",PageWidget.WidgetType.WORK_SCHEDULE)
                    .addWidgetConfig("flexibleWorkSchedule_50","Work Schedule - 50", WidgetConfigContext.ConfigType.FLEXIBLE,50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("employeeLocation","Employee Location",PageWidget.WidgetType.EMPLOYEE_LOCATION)
                    .addWidgetConfig("flexibleEmployeeLocation_50","Employee Location - 50", WidgetConfigContext.ConfigType.FLEXIBLE, 50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER)
    public static Supplier<ModuleWidgets> getUtilityCustomerWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("meterWidget" , "Meter Connections",PageWidget.WidgetType.METER_WIDGET)
                .addFixedWidgetConfig("webMeterWidget_24_12", "Meter Connections - 24 - 12",24,12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<ModuleWidgets> getUtilityBillWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("billSummaryWidget" , "Bill",PageWidget.WidgetType.BILL_SUMMARY_WIDGET)
                .addFixedWidgetConfig("webBillSummaryWidget_65_12", "Bill Summary Widget - 65 - 12",65,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule("meter")
    public static Supplier<ModuleWidgets> getMeterWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("meterReadings","Meter Readings",PageWidget.WidgetType.METER_READINGS)
                .addWidgetConfig("flexiblewebmeterreadings_33", "Meter Readings - 33",WidgetConfigContext.ConfigType.FLEXIBLE,33,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("monthlyConsumption","Monthly Consumption",PageWidget.WidgetType.MONTHLY_CONSUMPTION)
                .addWidgetConfig("webmonthlyconsumption_14_12","Monthly Consumption - 14 - 12",WidgetConfigContext.ConfigType.FIXED,14,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("totalConsumption","Total Consumption",PageWidget.WidgetType.TOTAL_CONSUMPTION)
                .addWidgetConfig("webtotalconsumption_14_12","Total Consumption - 14 - 12",WidgetConfigContext.ConfigType.FIXED,14,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("peakDemand","Peak Demand",PageWidget.WidgetType.PEAK_DEMAND)
                .addWidgetConfig("webpeakDemamd_14_12","Peak Demand - 14 - 12",WidgetConfigContext.ConfigType.FIXED,14,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule("virtualMeterTemplate")
    public static Supplier<ModuleWidgets> getVirtualMeterTemplateWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("vmTemplateReadings","VM Template Readings",PageWidget.WidgetType.VIRTUAL_METER_TEMPLATE_READINGS)
                .addWidgetConfig("flexiblewebvmtemplatereadings_30","VM Template Readings - 30",WidgetConfigContext.ConfigType.FLEXIBLE,30,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("relatedVMList","Related VM List",PageWidget.WidgetType.RELATED_VIRTUAL_METERS_LIST)
                .addWidgetConfig("flexiblewebrelatedvmlist_30", "Related VM List - 30",WidgetConfigContext.ConfigType.FLEXIBLE,30,-1,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME)
    public static Supplier<ModuleWidgets> getCalendarWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("calendarEventView" , "Calendar Event View",PageWidget.WidgetType.CALENDAR_EVENT_VIEW)
                .addFixedWidgetConfig("webCalendarEventView_47_12", "Calendar Event View - 47 - 12",47,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("calendarEventList","Calendar Event List",PageWidget.WidgetType.CALENDAR_EVENT_LIST)
                .addFixedWidgetConfig("webCalendarEventList_28_12","Calendar Event List - 28 - 12",28,12,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
    public static Supplier<ModuleWidgets> getControlActionWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("criteriaListView","Criteria List View",PageWidget.WidgetType.CRITERIA_LIST_VIEW)
                .addFixedWidgetConfig("webCriteriaListView_28_6", "Criteria List View - 28 - 6",28,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("commandsListView","Commands List View",PageWidget.WidgetType.COMMANDS_LIST_VIEW)
                .addFixedWidgetConfig("webCommandsListView_32_12", "Commands List View - 32 - 12",32,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("actionsListView","Actions List View",PageWidget.WidgetType.ACTIONS_LIST_VIEW)
                .addFixedWidgetConfig("webActionsListView_32_12", "Actions List View - 32 - 6",32,12, PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME)
    public static Supplier<ModuleWidgets> getControlActionTemplateWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("criteriaListView","Criteria List View",PageWidget.WidgetType.CRITERIA_LIST_VIEW)
                .done()
                .addModuleWidget("actionsListView","Actions List View",PageWidget.WidgetType.ACTIONS_LIST_VIEW)
                .done()
                ;
    }

    @WidgetsForModule("vendorcontact")
    public static Supplier<ModuleWidgets> getVendorContactWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("vendorContactLastKnownLocation","Vendor Contact Last Known Location",PageWidget.WidgetType.VENDOR_CONTACT_LAST_KNOWN_LOCATION)
                .addWidgetConfig("webVendorContactLastKnownLocation_22_6","Last Known Location - 22 - 6", WidgetConfigContext.ConfigType.FIXED,22,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactCurrentStatus","Vendor Contact Current Status",PageWidget.WidgetType.VENDOR_CONATCT_CURRENT_STATUS)
                .addWidgetConfig("webVendorContactCurrentStatus_22_6","Current Status - 22 - 6", WidgetConfigContext.ConfigType.FIXED,22,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactSkill","Vendor Contact Skill",PageWidget.WidgetType.VENDOR_CONTACT_SKILL)
                .addWidgetConfig("flexibleVendorContactSkill_48","Skill - 48", WidgetConfigContext.ConfigType.FLEXIBLE,48,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactWorkSchedule","Vendor Contact Work Schedule",PageWidget.WidgetType.VENDOR_CONTACT_WORK_SCHEDULE)
                .addWidgetConfig("flexibleVendorContactWorkSchedule_50","Work Schedule - 50", WidgetConfigContext.ConfigType.FLEXIBLE,50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorLocation","Vendor Location",PageWidget.WidgetType.VENDOR_LOCATION)
                .addWidgetConfig("flexibleVendorLocation_50","Vendor Location - 50", WidgetConfigContext.ConfigType.FLEXIBLE,50, -1,PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule("space")
    public static Supplier<ModuleWidgets> getSpaceWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .addWidgetConfig("webSpaceInsights_19_7","Space Insights - 19 - 7", WidgetConfigContext.ConfigType.FIXED,19,7, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("webOperatingHours_19_5","Operating Hours - 19 - 5", WidgetConfigContext.ConfigType.FIXED,19,5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("subSpaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES)
                .addWidgetConfig("flexibleWebSubSpaces_32","Sub Spaces - 32", WidgetConfigContext.ConfigType.FLEXIBLE,32,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("readings","Readings", PageWidget.WidgetType.READINGS)
                .done()

                .addModuleWidget("commandsWidget","Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .done()

                .addModuleWidget("relatedReadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .done()

                .addModuleWidget("plannedMaintenance","Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("unplannedMaintenance","UnPlanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("workorderInsights","Workorder Insights", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .done()

                .addModuleWidget("recentlyClosedPM","Recently Closed PM", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .done()

                .addModuleWidget("safetyPlanHazards","Safety Plan Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyPlanPrecautions","Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done();
    }
    @WidgetsForModule("floor")
    public static Supplier<ModuleWidgets> getFloorWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .done()

                .addModuleWidget("spaceOperatingHours","Space Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .done()

                .addModuleWidget("spaces","Spaces", PageWidget.WidgetType.SPACES)
                .addWidgetConfig("flexibleWebSpaces_32","Spaces - 32", WidgetConfigContext.ConfigType.FLEXIBLE,32,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("readings","Readings", PageWidget.WidgetType.READINGS)
                .done()

                .addModuleWidget("commandsWidget","Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .done()

                .addModuleWidget("relatedReadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .done()

                .addModuleWidget("plannedMaintenance","Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("unplannedMaintenance","UnPlanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("workorderInsights","Workorder Insights", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .done()

                .addModuleWidget("recentlyClosedPM","Recently Closed PM", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .done()

                .addModuleWidget("safetyPlanHazards","Safety Plan Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyPlanPrecautions","Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done();
    }

    @WidgetsForModule("building")
    public static Supplier<ModuleWidgets> getBuildingWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .done()

                .addModuleWidget("spaceOperatingHours","Space Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .done()


                .addModuleWidget("floors","Floors", PageWidget.WidgetType.FLOORS)
                .addWidgetConfig("flexibleWebFloors_32","Floors - 32", WidgetConfigContext.ConfigType.FLEXIBLE,32,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("spaces","Spaces", PageWidget.WidgetType.SPACES)
                .done()

                .addModuleWidget("readings","Readings", PageWidget.WidgetType.READINGS)
                .done()

                .addModuleWidget("commandsWidget","Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .done()

                .addModuleWidget("relatedReadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .done()

                .addModuleWidget("plannedMaintenance","Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("unplannedMaintenance","UnPlanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("workorderInsights","Workorder Insights", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .done()

                .addModuleWidget("recentlyClosedPM","Recently Closed PM", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .done()

                .addModuleWidget("safetyPlanHazards","Safety Plan Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyPlanPrecautions","Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done();
    }


    @WidgetsForModule("site")
    public static Supplier<ModuleWidgets> getSiteWidgets() {
        return () -> new ModuleWidgets()

                .addModuleWidget("spaceLocation","Space Location", PageWidget.WidgetType.SPACE_LOCATION)
                .addWidgetConfig("webSpaceLocation_19_3","Space Location - 19 - 3", WidgetConfigContext.ConfigType.FIXED,19,3, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .addWidgetConfig("webSpaceInsights_19_6","Space Insights - 19 - 6", WidgetConfigContext.ConfigType.FIXED,19,6, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("webOperatingHours_19_3","Operating Hours - 19 - 3", WidgetConfigContext.ConfigType.FIXED,19,3, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("weatherCard","Weather card", PageWidget.WidgetType.WEATHER_CARD)
                .addWidgetConfig("webWeatherCard_28_4","Weather Card - 28 - 4", WidgetConfigContext.ConfigType.FIXED,28,4, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("depreciationAnalysis","Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_ANALYSIS)
                .addWidgetConfig("webDepreciationAnalysis_28_8","Depreciation Analysis - 28 - 8", WidgetConfigContext.ConfigType.FIXED,28,8, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("hourlyForecast","Hourly forecast", PageWidget.WidgetType.HOURLY_FORECAST)
                .addWidgetConfig("flexibleWebHourlyForecast_32","Hourly forecast - 32", WidgetConfigContext.ConfigType.FLEXIBLE,32,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("buildings","Buildings", PageWidget.WidgetType.BUILDINGS)
                .addWidgetConfig("flexibleWebBuildings_32","Buildings - 32", WidgetConfigContext.ConfigType.FLEXIBLE,32,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("spaces","Spaces", PageWidget.WidgetType.SPACES)
                .done()

                .addModuleWidget("readings","Readings", PageWidget.WidgetType.READINGS)
                .done()

                .addModuleWidget("commandsWidget","Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .done()

                .addModuleWidget("relatedReadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .done()

                .addModuleWidget("plannedMaintenance","Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("unplannedMaintenance","UnPlanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .done()

                .addModuleWidget("workorderInsights","Workorder Insights", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .done()

                .addModuleWidget("recentlyClosedPM","Recently Closed PM", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .done()

                .addModuleWidget("safetyPlanHazards","Safety Plan Hazards", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyPlanPrecautions","Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done();
         }
    @WidgetsForModule(FacilioConstants.ContextNames.TENANT)
    public static Supplier<ModuleWidgets> getTenantWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("webtenantdetailcontactwidget", "Tenant Detail Contact", PageWidget.WidgetType.TENANT_DETAIL_CONTACT)
                .addWidgetConfig("webtenantdetailcontactwidget_13_6","Tenant Detail Contact - 13 - 6",WidgetConfigContext.ConfigType.FIXED,13,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantdetailoverviewwidget", "Tenant Detail Overview", PageWidget.WidgetType.TENANT_DETAIL_OVERVIEW)
                .addWidgetConfig("webtenantdetailoverviewwidget_13_6","Tenant Detail Overview - 13 - 6",WidgetConfigContext.ConfigType.FIXED,13,6,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantspecialwidget","Tenant Special Widget",PageWidget.WidgetType.TENANT_SPECIAL_WIDGET)
                .addWidgetConfig("webtenantspecialwidget_31_9","Tenant Special Widget - 31 - 9",WidgetConfigContext.ConfigType.FIXED,31,9,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantworkorderswidget","Tenant WorkOrders",PageWidget.WidgetType.TENANT_WORKORDERS)
                .addWidgetConfig("webtenantworkorders_13_3","Tenant WorkOrders -13 -3",WidgetConfigContext.ConfigType.FIXED,13,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantrecentlyclosedworkorderwidget","Recently Closed Work order",PageWidget.WidgetType.TENANT_RECENTLY_CLOSED_WORKORDER)
                .addWidgetConfig("webtenantrecentlyclosedworkorder_23_3","Recently Closed Work order -23 -3",WidgetConfigContext.ConfigType.FIXED,23,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantfacilitybookingwidget","Facility Bookings",PageWidget.WidgetType.TENANT_BOOKINGS)
                .addWidgetConfig("webtenantfacilitybookingwidget_13_3","Facility Bookings -13 -3",WidgetConfigContext.ConfigType.FIXED,13,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantupcomingfacilitybookingwidget","Upcoming Facility Booking",PageWidget.WidgetType.TENANT_UPCOMING_BOOKING)
                .addWidgetConfig("webtenantupcomingfacilitybookingwidget_10_3","Upcoming Facility Booking -10 -3",WidgetConfigContext.ConfigType.FIXED,10,3,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.VENDOR_QUOTES)
    public static Supplier<ModuleWidgets> getVendorQuoteTemplateWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("vendorQuoteAllQuoteItems", "Vendor Quoted Items", PageWidget.WidgetType.VENDOR_QUOTES_LINE_ITEMS)
                .addFlexibleWidgetConfig("flexiblewebvendorquoteditems_30","Vendor Quoted Items -30",30,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)
    public static Supplier<ModuleWidgets> getTenantUnitWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("webtenantunitoccupantwidget", "Occupant", PageWidget.WidgetType.TENANT_UNIT_TENANT)
                .addFlexibleWidgetConfig("webtenantunitoccupantwidget_13","Occupant -13",13, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitlocationwidget", "Location Details", PageWidget.WidgetType.TENANT_UNIT_LOCATION)
                .addFlexibleWidgetConfig("webtenantunitlocationwidget_13","Location Details - 13",13,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunithistorywidget", "Tenant  History", PageWidget.WidgetType.TENANT_UNIT_SPECIAL_WIDGET)
                .addFlexibleWidgetConfig("webtenantunithistorywidget_32","Tenant History -32",32,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitinsightswidget","Insights",PageWidget.WidgetType.TENANT_UNIT_OVERVIEW)
                .addFlexibleWidgetConfig("webtenantunitinsightswidget_20","Insights - 20",20,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitworkorderswidget","Tenant Unit Workorders",PageWidget.WidgetType.TENANT_UNIT_WORKORDER)
                .addFlexibleWidgetConfig("webtenantunitworkorderswidget_13","Tenant Unit Workorders - 13",13,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitrecentlyclosedworkorderwidget","Recently Closed Workorder",PageWidget.WidgetType.TENANT_UNIT_RECENTLY_CLOSED_WORKORDER)
                .addFlexibleWidgetConfig("webtenantunitrecentlyclosedworkorderwidget_23","Recently Closed Workorder - 23",23,PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.ContextNames.SERVICE_REQUEST)
    public static Supplier<ModuleWidgets> getServiceRequestWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("servicerequestemailthread", "Service Request Email Thread", PageWidget.WidgetType.SR_EMAIL_THREAD)
                .addWidgetConfig("flexibleservicerequestemailthread_50","Service Request Email Thread - 50",WidgetConfigContext.ConfigType.FLEXIBLE,50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("servicerequestdetailswidget", "Service Request Details Widget", PageWidget.WidgetType.SR_DETAILS_WIDGET)
                .addWidgetConfig("servicerequestdetails_50_3","Service Request Details Widget - 50 - 3",WidgetConfigContext.ConfigType.FLEXIBLE,50,3,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY)
    public static Supplier<ModuleWidgets> getFacilityWidgets(){
        return ()->new ModuleWidgets()
                .addModuleWidget("facilityphotos","Facility Photos",PageWidget.WidgetType.FACILITY_PHOTOS)
                .addFixedWidgetConfig("webfacilityphotos_15_12","Facility Photos 15 - 12",15,12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityfeatures","Facility Features",PageWidget.WidgetType.FACILITY_FEATURES)
                .addFlexibleWidgetConfig("flexiblewebfacilityfeatures_12","Facility Features - 12",12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityslotinformation","Facility Slot Information",PageWidget.WidgetType.FACILITY_SLOT_INFORMATION)
                .addFlexibleWidgetConfig("flexiblewebfacilityslotinformation_49","Facility Slot Information - 49",49,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityspecialavailability","Facility Special Availability",PageWidget.WidgetType.FACILITY_SPECIAL_AVAILABILITY)
                .addFlexibleWidgetConfig("flexiblewebfacilityspecialavalability_28","Facility Special Availability - 28",28,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING)
    public static Supplier<ModuleWidgets> getFacilityBookingWidgets(){
        return ()->new ModuleWidgets()
                .addModuleWidget("bookinginfo","Booking Info",PageWidget.WidgetType.BOOKING_INFO)
                .addFixedWidgetConfig("webbookinginfo_5_12","Booking Info - 5 - 12",5,12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("slotinformation","Booking Slot Information",PageWidget.WidgetType.BOOKING_SLOT_INFORMATION)
                .addFlexibleWidgetConfig("flexiblewebbookinginfo_12","Booking Slot Information - 12",12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("bookinginternalattendees","Booking Internal attendees",PageWidget.WidgetType.BOOKING_INTERNAL_ATTENDEES)
                .addFlexibleWidgetConfig("flexiblewebbookinginternalattendees_12","Booking Internal attendees - 12",12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("bookingexternalattendees","Booking External Attendees",PageWidget.WidgetType.BOOKING_EXTERNAL_ATTENDEES)
                .addFlexibleWidgetConfig("flexiblewebbookingexternalattendees_12","Booking External Attendees - 12",12,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }

    @WidgetsForModule("serviceOrder")
    public static Supplier<ModuleWidgets> getServiceOrderWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("taskList","Tasks",PageWidget.WidgetType.SERVICE_TASK_WIDGET)
                .addWidgetConfig("webTaskList_50_12","Task List - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("servicePlans","Plans",PageWidget.WidgetType.SERVICE_ORDER_PLANS)
                .addWidgetConfig("webServicePlans_50_12","Plans - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceActuals","Actuals",PageWidget.WidgetType.SERVICE_ORDER_ACTUALS)
                .addWidgetConfig("webServiceActuals_50_12","Actuals - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("appointments","Appointments",PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS)
                .addWidgetConfig("webAppointments_50_12","Appointments - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }

    @WidgetsForModule("territory")
    public static Supplier<ModuleWidgets> getTerritoryWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("sites","Sites",PageWidget.WidgetType.TERRITORY_SITES)
                .addWidgetConfig("flexibleSites_50","Sites - 50", WidgetConfigContext.ConfigType.FLEXIBLE,50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("fieldAgents","Field Agents",PageWidget.WidgetType.FIELD_AGENTS)
                .addWidgetConfig("flexibleFieldAgents_50","Field Agents - 50", WidgetConfigContext.ConfigType.FLEXIBLE,50,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("geography","Geography",PageWidget.WidgetType.GEOGRAPHY)
                .addWidgetConfig("webGeography_22_6","Geography - 22 - 6", WidgetConfigContext.ConfigType.FIXED,22,6, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }

    @WidgetsForModule("serviceAppointment")
    public static Supplier<ModuleWidgets> getServiceAppointmentWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("serviceAppointmentServiceTasks","Service Tasks",PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS)
                .addWidgetConfig("webServiceAppointmentServiceTasks_50_12","Service Tasks - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentPlans","Plans",PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS)
                .addWidgetConfig("webServiceAppointmentPlans_50_12","Plans - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentActuals","Actuals",PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS)
                .addWidgetConfig("webServiceAppointmentActuals_50_12","Actuals - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentTimeSheet","Time Sheet",PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET)
                .addWidgetConfig("webServiceAppointmentTimeSheet_50_12","Time Sheet - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentTrip","Trip",PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP)
                .addWidgetConfig("webServiceAppointmentTrip_50_12","Trip - 50 - 12", WidgetConfigContext.ConfigType.FIXED,50,12, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }
    @WidgetsForModule("timeSheet")
    public static Supplier<ModuleWidgets> getTimeSheetWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("timeSheetTasks","Tasks",PageWidget.WidgetType.TIMESHEET_TASKS)
                .addWidgetConfig("webTimeSheetTasks_23_12","Tasks - 23 - 12",WidgetConfigContext.ConfigType.FIXED,23,12,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule("trip")
    public static Supplier<ModuleWidgets> getTripWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("tripJourney","Journey",PageWidget.WidgetType.TRIP_JOURNEY)
                .addWidgetConfig("webTripJourney_22_6","Journey - 22 - 6",WidgetConfigContext.ConfigType.FIXED,22,6,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }

}
