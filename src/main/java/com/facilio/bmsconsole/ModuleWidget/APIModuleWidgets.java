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
                .addWidgetConfig("flexiblewebsitelistwidget_6","Site List Widget - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("clientContactlistwidget", "Client Contact List Widget", PageWidget.WidgetType.CLIENT_CONTACT_LIST_WIDGET)
                .addWidgetConfig("flexibleclientcontactlistwidget_6","Workorder List Widget - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done();

    }
    @WidgetsForModule("workorder")
    public static Supplier<ModuleWidgets> getWorkorderModuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("multiresource", "Multiresource", PageWidget.WidgetType.MULTIRESOURCE)
                .addWidgetConfig("flexiblewebmultiresource_4","MultiResource - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("responsibility", "Responsibility", PageWidget.WidgetType.RESPONSIBILITY)
                .addWidgetConfig("flexiblewebresponsibility_3","Responsibility - 3",WidgetConfigContext.ConfigType.FLEXIBLE,3,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("timedetails","Time Details",PageWidget.WidgetType.TIME_DETAILS)
                .addWidgetConfig("flexiblewebtimedetails_6","Time Details - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("resource","Resource",PageWidget.WidgetType.RESOURCE)
                .addWidgetConfig("flexiblewebresource_3","Resource - 3",WidgetConfigContext.ConfigType.FLEXIBLE,3,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costdetails","Cost Details",PageWidget.WidgetType.COST_DETAILS)
                .addWidgetConfig("flexiblewebcostdetails_3","Cost Details - 3",WidgetConfigContext.ConfigType.FLEXIBLE,3,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("plans", "Plans", PageWidget.WidgetType.PLANS)
                .addFlexibleWidgetConfig("flexiblewebplans_5","Plans - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("actuals", "Actuals", PageWidget.WidgetType.ACTUALS)
                .addFlexibleWidgetConfig("flexiblewebactuals_5","Actuals - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("tasks", "Tasks", PageWidget.WidgetType.TASKS)
                .addFlexibleWidgetConfig("flexiblewebtasks_5","Tasks - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderitems", "Work Order Items", PageWidget.WidgetType.WORK_ORDER_ITEMS)
                .addFlexibleWidgetConfig("flexiblewebworkorderitems_5","Work Order Items - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workordertools", "Work Order Tools", PageWidget.WidgetType.WORK_ORDER_TOOLS)
                .addFlexibleWidgetConfig("flexiblewebworkordertools_5","Work Order Tools - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderservice", "Work Order Service", PageWidget.WidgetType.WORK_ORDER_SERVICE)
                .addFlexibleWidgetConfig("flexiblewebworkorderservice_5","Work Order Service - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("workorderlabour", "Work Order Labour", PageWidget.WidgetType.WORK_ORDER_LABOUR)
                .addFlexibleWidgetConfig("flexiblewebworkorderlabour_5","Work Order Labour - 5", 5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("safetyplanhazard", "SafetyPlan Hazard", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .done()

                .addModuleWidget("safetyplanprecautions", "Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .done()

                .addModuleWidget("relatedrecords", "Related Records", PageWidget.WidgetType.RELATED_RECORDS)
                .addWidgetConfig("flexiblewebrelatedrecords_5","Related Records - 5",WidgetConfigContext.ConfigType.FLEXIBLE,5,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("failurehierarchy", "Failure Hierarchy", PageWidget.WidgetType.FAILURE_HIERARCHY)
                .addWidgetConfig("flexiblewebfailurehierarchy_5","Failure Hierarchy - 5",WidgetConfigContext.ConfigType.FLEXIBLE,5,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("flexiblewebassetlocation_3","Asset Location - 3",WidgetConfigContext.ConfigType.FLEXIBLE,3,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("operatinghours", "Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("flexibleweboperatinghours_4","Operating Hours - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("plannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebplannedmaintenance_7","Planned Maintenance - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("unplannedmaintenance", "Unplanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebunplannedmaintenance_7","UnPlanned Maintenance - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workorderinsight", "Workorder Insight", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .addWidgetConfig("flexiblewebworkorderinsight_3","Workorder Insight - 3",WidgetConfigContext.ConfigType.FLEXIBLE,3,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("recentlyclosedpm", "Recently Closed Pm", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .addWidgetConfig("flexiblewebrecentlyclosedpm_4","Recently Closed Pm - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("faultreport", "Fault Report", PageWidget.WidgetType.FAULT_REPORT)
                .addWidgetConfig("flexiblewebfaultreport_5","Fault Report - 5",WidgetConfigContext.ConfigType.FLEXIBLE,5,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("faultinsight", "Fault Insight", PageWidget.WidgetType.FAULT_INSIGHT)
                .addWidgetConfig("flexiblewebfaultinsight_4","Fault Insight - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("readings", "Readings", PageWidget.WidgetType.READINGS)
                .addWidgetConfig("flexiblewebreadings_7","Readings - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("commandswidget", "Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .addWidgetConfig("flexiblewebcommandswidget_7","Commands Widget - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("latestdowntime", "Latest Downtime", PageWidget.WidgetType.LATEST_DOWNTIME)
                .addWidgetConfig("weblatestdowntime_3_6","Latest Downtime - 3 - 6",WidgetConfigContext.ConfigType.FIXED,3,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("overalldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME)
                .addWidgetConfig("weboveralldowntime_3_6","Overall Downtime - 3 - 6",WidgetConfigContext.ConfigType.FIXED,3,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("downtimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY)
                .addWidgetConfig("flexibledowntimehistory_6","Downtime History - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("failurerate", "Failure Rate", PageWidget.WidgetType.FAILURE_RATE)
                .addWidgetConfig("webfailurerate_8_6","Failure Rate - 8 - 6",WidgetConfigContext.ConfigType.FIXED,8,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("averagerepairtime", "Average Repair Time", PageWidget.WidgetType.AVERAGE_REPAIR_TIME)
                .addWidgetConfig("webaveragerepairtime_8_6","Average Repair Time - 8 - 6",WidgetConfigContext.ConfigType.FIXED,8,6, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("assetcostdetail", "Asset Cost Detail", PageWidget.WidgetType.ASSET_COST_DETAILS)
                .addWidgetConfig("webassetcostdetail_5_4","Asset Cost Detail - 5 - 4",WidgetConfigContext.ConfigType.FIXED,5,4, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costbreakup", "Cost Breakup", PageWidget.WidgetType.COST_BREAKUP)
                .addWidgetConfig("webcostbreakup_5_8","Cost Breakup - 5 - 8",WidgetConfigContext.ConfigType.FIXED,5,8, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("maintenancecosttrend", "Maintenance Cost Trend", PageWidget.WidgetType.MAINTENANCE_COST_TREND)
                .addWidgetConfig("flexiblewebmaintenancecosttrend_6","Maintenance Cost Trend - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationschedule", "Depreciation Schedule", PageWidget.WidgetType.DEPRECIATION_SCHEDULE)
                .addWidgetConfig("flexiblewebdepreciationschedule_4","Depreciation Schedule - 4",WidgetConfigContext.ConfigType.FLEXIBLE,4,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationcosttrend", "Depreciation Cost Trend", PageWidget.WidgetType.DEPRECIATION_COST_TREND)
                .addWidgetConfig("flexiblewebdepreciationcosttrend_6","Depreciation Cost Trend - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("safetyplanhazard", "SafetyPlan Hazard", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .addWidgetConfig("flexiblewebsafetyplanhazard_6","Safetyplan Hazard - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("safetyplanprecautions", "Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .addWidgetConfig("flexiblewebsafetyplanprecautions_6","Safety Plan Precaution - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("relatedreadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .addWidgetConfig("flexiblewebrelatedreadings_7","Related Readings - 7", WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
                .done()


                ;
    }

    @WidgetsForModule("employee")
    public static Supplier<ModuleWidgets> getEmployeeWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("lastKnownLocation","Last Known Location",PageWidget.WidgetType.LAST_KNOWN_LOCATION)
                    .addWidgetConfig("webLastKnownLocation_5_6","Last Known Location - 5 - 6", WidgetConfigContext.ConfigType.FIXED,5,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("currentStatus","Employee Current Status",PageWidget.WidgetType.CURRENT_STATUS)
                    .addWidgetConfig("webCurrentStatus_5_6","Employee Current Status - 5 - 6", WidgetConfigContext.ConfigType.FIXED,5,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("skill","Skills",PageWidget.WidgetType.SKILL)
                    .addWidgetConfig("flexibleSkill_10","Skills - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workSchedule","Work Schedule",PageWidget.WidgetType.WORK_SCHEDULE)
                    .addWidgetConfig("flexibleWorkSchedule_10","Work Schedule - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("employeeLocation","Location",PageWidget.WidgetType.EMPLOYEE_LOCATION)
                    .addWidgetConfig("flexibleEmployeeLocation_10","Location - 10", WidgetConfigContext.ConfigType.FLEXIBLE, 10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER)
    public static Supplier<ModuleWidgets> getUtilityCustomerWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("meterWidget" , "Meter Connections",PageWidget.WidgetType.METER_WIDGET)
                .addFixedWidgetConfig("webMeterWidget_5_12", "Meter Connections - 5 - 12",5,12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<ModuleWidgets> getUtilityBillWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("billSummaryWidget" , "Bill",PageWidget.WidgetType.BILL_SUMMARY_WIDGET)
                .addFixedWidgetConfig("webBillSummaryWidget_13_12", "Bill Summary Widget - 13 - 12",13,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule("meter")
    public static Supplier<ModuleWidgets> getMeterWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("meterReadings","Meter Readings",PageWidget.WidgetType.METER_READINGS)
                .addWidgetConfig("flexiblewebmeterreadings_7", "Meter Readings - 7",WidgetConfigContext.ConfigType.FLEXIBLE,7,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("monthlyConsumption","Monthly Consumption",PageWidget.WidgetType.MONTHLY_CONSUMPTION)
                .addWidgetConfig("webmonthlyconsumption_3_12","Monthly Consumption - 3 - 12",WidgetConfigContext.ConfigType.FIXED,3,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("totalConsumption","Total Consumption",PageWidget.WidgetType.TOTAL_CONSUMPTION)
                .addWidgetConfig("webtotalconsumption_3_12","Total Consumption - 3 - 12",WidgetConfigContext.ConfigType.FIXED,3,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("peakDemand","Peak Demand",PageWidget.WidgetType.PEAK_DEMAND)
                .addWidgetConfig("webpeakDemamd_3_12","Peak Demand - 3 - 12",WidgetConfigContext.ConfigType.FIXED,3,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule("virtualMeterTemplate")
    public static Supplier<ModuleWidgets> getVirtualMeterTemplateWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("vmTemplateReadings","VM Template Readings",PageWidget.WidgetType.VIRTUAL_METER_TEMPLATE_READINGS)
                .addWidgetConfig("flexiblewebvmtemplatereadings_6","VM Template Readings - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("relatedVMList","Related VM List",PageWidget.WidgetType.RELATED_VIRTUAL_METERS_LIST)
                .addWidgetConfig("flexiblewebrelatedvmlist_6", "Related VM List - 6",WidgetConfigContext.ConfigType.FLEXIBLE,6,-1,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.Calendar.CALENDAR_MODULE_NAME)
    public static Supplier<ModuleWidgets> getCalendarWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("calendarEventView" , "Calendar Event View",PageWidget.WidgetType.CALENDAR_EVENT_VIEW)
                .addFixedWidgetConfig("webCalendarEventView_9_12", "Calendar Event View - 9 - 12",9,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("calendarEventList","Calendar Event List",PageWidget.WidgetType.CALENDAR_EVENT_LIST)
                .addFixedWidgetConfig("webCalendarEventList_6_12","Calendar Event List - 6 - 12",6,12,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
    public static Supplier<ModuleWidgets> getControlActionWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("criteriaListView","Criteria List View",PageWidget.WidgetType.CRITERIA_LIST_VIEW)
                .addFixedWidgetConfig("webCriteriaListView_6_6", "Criteria List View - 6 - 6",6,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("commandsListView","Commands List View",PageWidget.WidgetType.COMMANDS_LIST_VIEW)
                .addFixedWidgetConfig("webCommandsListView_6_12", "Commands List View - 6 - 12",6,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("actionsListView","Actions List View",PageWidget.WidgetType.ACTIONS_LIST_VIEW)
                .addFixedWidgetConfig("webActionsListView_6_12", "Actions List View - 6 - 12",6,12, PagesContext.PageLayoutType.WEB)
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
                .addModuleWidget("vendorContactLastKnownLocation","Last Known Location",PageWidget.WidgetType.VENDOR_CONTACT_LAST_KNOWN_LOCATION)
                .addWidgetConfig("webVendorContactLastKnownLocation_4_6","Last Known Location - 4 - 6", WidgetConfigContext.ConfigType.FIXED,4,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactCurrentStatus","Current Status",PageWidget.WidgetType.VENDOR_CONATCT_CURRENT_STATUS)
                .addWidgetConfig("webVendorContactCurrentStatus_4_6","Current Status - 4 - 6", WidgetConfigContext.ConfigType.FIXED,4,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactSkill","Skills",PageWidget.WidgetType.VENDOR_CONTACT_SKILL)
                .addWidgetConfig("flexibleVendorContactSkill_10","Skills - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorContactWorkSchedule","Work Schedule",PageWidget.WidgetType.VENDOR_CONTACT_WORK_SCHEDULE)
                .addWidgetConfig("flexibleVendorContactWorkSchedule_10","Work Schedule - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("vendorLocation","Location",PageWidget.WidgetType.VENDOR_LOCATION)
                .addWidgetConfig("flexibleVendorLocation_10","Location - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10, -1,PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule("newreadingalarm")
    public static Supplier<ModuleWidgets> getReadingAlarmWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("occurrenceHistory", "Occurrence History", PageWidget.WidgetType.OCCURRENCE_HISTORY)
                .addFixedWidgetConfig("webOccurrenceHistory_58_12", "Occurrence History - 58 - 12", 58, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("alarmDuration","Alarm Duration",PageWidget.WidgetType.ALARM_DURATION)
                .addFixedWidgetConfig("webAlarmDuration_13_3","Alarm Duration - 13 - 3",13,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("meanTimeBetweenOccurrence", "Mean Time Between Occurrence", PageWidget.WidgetType.MEAN_TIME_BETWEEN_OCCURRENCE)
                .addFixedWidgetConfig("webMeanTimeBetweenOccurrence_14_6", "Mean Time Between Occurrence - 14 - 6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("meanTimeToClear", "Mean Time To Clear", PageWidget.WidgetType.MEAN_TIME_TO_CLEAR)
                .addFixedWidgetConfig("webMeanTimeToClear_14_6", "Mean Time To Clear - 14 - 6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("noOfOccurrences", "No. of occurrences", PageWidget.WidgetType.NO_OF_OCCURRENCES)
                .addFixedWidgetConfig("webNoOfOccurrences_12_6",  "No Of Occurrences - 12 - 6", 12, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("impactInfo", "Impact Info", PageWidget.WidgetType.IMPACT_INFO)
                .addFixedWidgetConfig("webImpactInfo_12_6", "Impact Info - 12 - 6", 12, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costImpact", "Cost Impact", PageWidget.WidgetType.COST_IMPACT)
                .addFixedWidgetConfig("webCostImpact_14_6", "Cost Impact - 14 - 6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("energyImpact", "Energy Impact", PageWidget.WidgetType.ENERGY_IMPACT)
                .addFixedWidgetConfig("webEnergyImpact_14_6", "Energy Impact - 14 - 6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("impactReport", "Impact Report", PageWidget.WidgetType.IMPACT_REPORT)
                .addFlexibleWidgetConfig("webImpactReport_51", "Impact Report - 51", 51, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("locationDetails","Location Details",PageWidget.WidgetType.LOCATION_DETAILS)
                .addFixedWidgetConfig("webLocationDetails_13_3","Location Details - 13 - 3",13,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("alarmRca", "Alarm RCA", PageWidget.WidgetType.ALARM_RCA)
                .addFixedWidgetConfig("webAlarmRca_58_12", "Alarm RCA - 58 - 12", 58, 12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.ReadingRules.NEW_READING_RULE)
    public static Supplier<ModuleWidgets> getNewReadingRuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("rootCauseAndImpact", "Root Cause and Impact", PageWidget.WidgetType.ROOT_CAUSE_AND_IMPACT)
                .addFixedWidgetConfig("webRootCauseAndImpact_4_3", "Root Cause and Impact - 4 - 3", 4, 3, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAssetsAndAlarm", "Rule Assets And Alarm", PageWidget.WidgetType.RULE_ASSETS_AND_ALARM)
                .addFixedWidgetConfig("webRuleAssetsAndAlarm_4_4", "Rule Assets And Alarm - 4 - 4", 4, 4, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAlarmInsights", "Rule Alarm Insights", PageWidget.WidgetType.RULE_ALARM_INSIGHT)
                .addFixedWidgetConfig("webRuleAlarmInsights_4_5", "Rule Alarm Insights - 4 - 5", 4, 5, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAssociatedWorkOrders", "Rule Associated Work Orders", PageWidget.WidgetType.RULE_ASSOCIATED_WORK_ORDERS)
                .addFixedWidgetConfig("webRuleAssociatedWorkOrders_3_6", "Rule Associated Work Orders - 3 - 6", 3, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleWorkOrderDuration", "Rule Work Order Duration", PageWidget.WidgetType.RULE_WORK_ORDER_DURATION)
                .addFixedWidgetConfig("webRuleWorkOrderDuration_3_6", "Rule Work Order Duration - 3 - 6", 3, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("rootCauses", "Root Causes", PageWidget.WidgetType.ROOT_CAUSES)
                .addFixedWidgetConfig("webRootCauses_6_12", "Root Causes - 6 - 12", 6, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleInsight", "Rule Insight", PageWidget.WidgetType.RULE_INSIGHT)
                .addFixedWidgetConfig("webRuleInsight_6_12", "Rule Insight - 6 - 12", 6, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleLogs", "Rule Logs", PageWidget.WidgetType.RULE_LOGS)
                .addFixedWidgetConfig("webRuleLogs_12_12", "Rule Logs - 12 - 12", 12, 12, PagesContext.PageLayoutType.WEB)
                .done();

    }

    @WidgetsForModule("space")
    public static Supplier<ModuleWidgets> getSpaceWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .addWidgetConfig("webSpaceInsights_4_7","Space Insights - 4 - 7", WidgetConfigContext.ConfigType.FIXED,4,7, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("webOperatingHours_4_5","Operating Hours - 4 - 5", WidgetConfigContext.ConfigType.FIXED,4,5, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("subSpaces","Sub Spaces", PageWidget.WidgetType.SUB_SPACES)
                .addWidgetConfig("flexibleWebSubSpaces_7","Sub Spaces - 7", WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("flexibleWebSpaces_7","Spaces - 7", WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("flexibleWebFloors_7","Floors - 7", WidgetConfigContext.ConfigType.FLEXIBLE,7,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("webSpaceLocation_4_3","Space Location - 4 - 3", WidgetConfigContext.ConfigType.FIXED,4,3, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("spaceInsights","Space Insights", PageWidget.WidgetType.SPACE_INSIGHTS)
                .addWidgetConfig("webSpaceInsights_4_6","Space Insights - 4 - 6", WidgetConfigContext.ConfigType.FIXED,4,6, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("operatingHours","Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("webOperatingHours_4_3","Operating Hours - 4 - 3", WidgetConfigContext.ConfigType.FIXED,4,3, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("weatherCard","Weather card", PageWidget.WidgetType.WEATHER_CARD)
                .addWidgetConfig("webWeatherCard_6_4","Weather Card - 6 - 4", WidgetConfigContext.ConfigType.FIXED,6,4, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("depreciationAnalysis","Depreciation Analysis", PageWidget.WidgetType.DEPRECIATION_ANALYSIS)
                .addWidgetConfig("webDepreciationAnalysis_6_8","Depreciation Analysis - 6 - 8", WidgetConfigContext.ConfigType.FIXED,6,8, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("hourlyForecast","Hourly forecast", PageWidget.WidgetType.HOURLY_FORECAST)
                .addWidgetConfig("flexibleWebHourlyForecast_6","Hourly forecast - 6", WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("buildings","Buildings", PageWidget.WidgetType.BUILDINGS)
                .addWidgetConfig("flexibleWebBuildings_6","Buildings - 6", WidgetConfigContext.ConfigType.FLEXIBLE,6,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("webtenantdetailcontactwidget_3_6","Tenant Detail Contact - 3 - 6",WidgetConfigContext.ConfigType.FIXED,3,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantdetailoverviewwidget", "Tenant Detail Overview", PageWidget.WidgetType.TENANT_DETAIL_OVERVIEW)
                .addWidgetConfig("webtenantdetailoverviewwidget_3_6","Tenant Detail Overview - 3 - 6",WidgetConfigContext.ConfigType.FIXED,3,6,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantspecialwidget","Tenant Special Widget",PageWidget.WidgetType.TENANT_SPECIAL_WIDGET)
                .addWidgetConfig("webtenantspecialwidget_6_9","Tenant Special Widget - 6 - 9",WidgetConfigContext.ConfigType.FIXED,6,9,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantworkorderswidget","Tenant WorkOrders",PageWidget.WidgetType.TENANT_WORKORDERS)
                .addWidgetConfig("webtenantworkorders_3_3","Tenant WorkOrders -3 -3",WidgetConfigContext.ConfigType.FIXED,3,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantrecentlyclosedworkorderwidget","Recently Closed Work order",PageWidget.WidgetType.TENANT_RECENTLY_CLOSED_WORKORDER)
                .addWidgetConfig("webtenantrecentlyclosedworkorder_4_3","Recently Closed Work order -4 -3",WidgetConfigContext.ConfigType.FIXED,4,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantfacilitybookingwidget","Facility Bookings",PageWidget.WidgetType.TENANT_BOOKINGS)
                .addWidgetConfig("webtenantfacilitybookingwidget_3_3","Facility Bookings -3 -3",WidgetConfigContext.ConfigType.FIXED,3,3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantupcomingfacilitybookingwidget","Upcoming Facility Booking",PageWidget.WidgetType.TENANT_UPCOMING_BOOKING)
                .addWidgetConfig("webtenantupcomingfacilitybookingwidget_2_3","Upcoming Facility Booking -2 -3",WidgetConfigContext.ConfigType.FIXED,2,3,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.VENDOR_QUOTES)
    public static Supplier<ModuleWidgets> getVendorQuoteTemplateWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("vendorQuoteAllQuoteItems", "Vendor Quoted Items", PageWidget.WidgetType.VENDOR_QUOTES_LINE_ITEMS)
                .addFlexibleWidgetConfig("flexiblewebvendorquoteditems_6","Vendor Quoted Items - 6",6,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.TENANT_UNIT_SPACE)
    public static Supplier<ModuleWidgets> getTenantUnitWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("webtenantunitoccupantwidget", "Occupant", PageWidget.WidgetType.TENANT_UNIT_TENANT)
                .addFlexibleWidgetConfig("webtenantunitoccupantwidget_3","Occupant - 3",3, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitlocationwidget", "Location Details", PageWidget.WidgetType.TENANT_UNIT_LOCATION)
                .addFlexibleWidgetConfig("webtenantunitlocationwidget_3","Location Details - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunithistorywidget", "Tenant  History", PageWidget.WidgetType.TENANT_UNIT_SPECIAL_WIDGET)
                .addFlexibleWidgetConfig("webtenantunithistorywidget_6","Tenant History -6",6,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitinsightswidget","Insights",PageWidget.WidgetType.TENANT_UNIT_OVERVIEW)
                .addFlexibleWidgetConfig("webtenantunitinsightswidget_4","Insights - 4",4,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitworkorderswidget","Tenant Unit Workorders",PageWidget.WidgetType.TENANT_UNIT_WORKORDER)
                .addFlexibleWidgetConfig("webtenantunitworkorderswidget_3","Tenant Unit Workorders - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("webtenantunitrecentlyclosedworkorderwidget","Recently Closed Workorder",PageWidget.WidgetType.TENANT_UNIT_RECENTLY_CLOSED_WORKORDER)
                .addFlexibleWidgetConfig("webtenantunitrecentlyclosedworkorderwidget_5","Recently Closed Workorder - 5",5,PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.ContextNames.SERVICE_REQUEST)
    public static Supplier<ModuleWidgets> getServiceRequestWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("servicerequestemailthread", "Service Request Email Thread", PageWidget.WidgetType.SR_EMAIL_THREAD)
                .addWidgetConfig("flexibleservicerequestemailthread_10","Service Request Email Thread - 10",WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("servicerequestdetailswidget", "Service Request Details Widget", PageWidget.WidgetType.SR_DETAILS_WIDGET)
                .addWidgetConfig("servicerequestdetails_10_3","Service Request Details Widget - 10 - 3",WidgetConfigContext.ConfigType.FLEXIBLE,10,3,PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY)
    public static Supplier<ModuleWidgets> getFacilityWidgets(){
        return ()->new ModuleWidgets()
                .addModuleWidget("facilityphotos","Facility Photos",PageWidget.WidgetType.FACILITY_PHOTOS)
                .addFixedWidgetConfig("webfacilityphotos_3_12","Facility Photos 3 - 12",3,12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityfeatures","Facility Features",PageWidget.WidgetType.FACILITY_FEATURES)
                .addFlexibleWidgetConfig("flexiblewebfacilityfeatures_3","Facility Features - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityslotinformation","Facility Slot Information",PageWidget.WidgetType.FACILITY_SLOT_INFORMATION)
                .addFlexibleWidgetConfig("flexiblewebfacilityslotinformation_10","Facility Slot Information - 10",10,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("facilityspecialavailability","Facility Special Availability",PageWidget.WidgetType.FACILITY_SPECIAL_AVAILABILITY)
                .addFlexibleWidgetConfig("flexiblewebfacilityspecialavalability_6","Facility Special Availability - 6",6,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING)
    public static Supplier<ModuleWidgets> getFacilityBookingWidgets(){
        return ()->new ModuleWidgets()
                .addModuleWidget("bookinginfo","Booking Info",PageWidget.WidgetType.BOOKING_INFO)
                .addFixedWidgetConfig("webbookinginfo_1_12","Booking Info - 1 - 12",1,12,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("slotinformation","Booking Slot Information",PageWidget.WidgetType.BOOKING_SLOT_INFORMATION)
                .addFlexibleWidgetConfig("flexiblewebbookinginfo_3","Booking Slot Information - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("bookinginternalattendees","Booking Internal attendees",PageWidget.WidgetType.BOOKING_INTERNAL_ATTENDEES)
                .addFlexibleWidgetConfig("flexiblewebbookinginternalattendees_3","Booking Internal attendees - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("bookingexternalattendees","Booking External Attendees",PageWidget.WidgetType.BOOKING_EXTERNAL_ATTENDEES)
                .addFlexibleWidgetConfig("flexiblewebbookingexternalattendees_3","Booking External Attendees - 3",3,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }

    @WidgetsForModule("serviceOrder")
    public static Supplier<ModuleWidgets> getServiceOrderWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("taskList","Tasks",PageWidget.WidgetType.SERVICE_TASK_WIDGET)
                .addWidgetConfig("webTaskList_10_12","Tasks - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("servicePlans","Plans",PageWidget.WidgetType.SERVICE_ORDER_PLANS)
                .addWidgetConfig("webServicePlans_10_12","Plans - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceActuals","Actuals",PageWidget.WidgetType.SERVICE_ORDER_ACTUALS)
                .addWidgetConfig("webServiceActuals_10_12","Actuals - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("appointments","Service Appointment",PageWidget.WidgetType.SERVICE_ORDER_APPOINTMENTS)
                .addWidgetConfig("webAppointments_10_12","Service Appointment - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }

    @WidgetsForModule("territory")
    public static Supplier<ModuleWidgets> getTerritoryWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("sites","Sites",PageWidget.WidgetType.TERRITORY_SITES)
                .addWidgetConfig("flexibleSites_10","Sites - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("fieldAgents","Field Agents",PageWidget.WidgetType.FIELD_AGENTS)
                .addWidgetConfig("flexibleFieldAgents_10","Field Agents - 10", WidgetConfigContext.ConfigType.FLEXIBLE,10,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("geography","Geography",PageWidget.WidgetType.GEOGRAPHY)
                .addWidgetConfig("webGeography_5_6","Geography - 5 - 6", WidgetConfigContext.ConfigType.FIXED,5,6, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }

    @WidgetsForModule("serviceAppointment")
    public static Supplier<ModuleWidgets> getServiceAppointmentWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("serviceAppointmentServiceTasks","Tasks",PageWidget.WidgetType.SERVICE_APPOINTMENT_SERVICE_TASKS)
                .addWidgetConfig("webServiceAppointmentServiceTasks_10_12","Tasks - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentPlans","Plans",PageWidget.WidgetType.SERVICE_APPOINTMENT_PLANS)
                .addWidgetConfig("webServiceAppointmentPlans_10_12","Plans - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentActuals","Actuals",PageWidget.WidgetType.SERVICE_APPOINTMENT_ACTUALS)
                .addWidgetConfig("webServiceAppointmentActuals_10_12","Actuals - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentTimeSheet","Time Sheet",PageWidget.WidgetType.SERVICE_APPOINTMENT_TIMESHEET)
                .addWidgetConfig("webServiceAppointmentTimeSheet_10_12","Time Sheet - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("serviceAppointmentTrip","Trip",PageWidget.WidgetType.SERVICE_APPOINTMENT_TRIP)
                .addWidgetConfig("webServiceAppointmentTrip_10_12","Trip - 10 - 12", WidgetConfigContext.ConfigType.FIXED,10,12, PagesContext.PageLayoutType.WEB)
                .done()
                ;

    }
    @WidgetsForModule("timeSheet")
    public static Supplier<ModuleWidgets> getTimeSheetWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("timeSheetTasks","Tasks",PageWidget.WidgetType.TIMESHEET_TASKS)
                .addWidgetConfig("webTimeSheetTasks_5_12","Tasks - 5 - 12",WidgetConfigContext.ConfigType.FIXED,5,12,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule("trip")
    public static Supplier<ModuleWidgets> getTripWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("tripJourney","Journey",PageWidget.WidgetType.TRIP_JOURNEY)
                .addWidgetConfig("webTripJourney_5_6","Journey - 5 - 6",WidgetConfigContext.ConfigType.FIXED,5,6,PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }

}
