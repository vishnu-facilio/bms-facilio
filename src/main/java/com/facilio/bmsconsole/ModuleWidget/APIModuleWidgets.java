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
                .addWidgetConfig("flexiblewebassetlocation_13","Asset Location - 13",WidgetConfigContext.ConfigType.FLEXIBLE,13,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("operatinghours", "Operating Hours", PageWidget.WidgetType.OPERATING_HOURS)
                .addWidgetConfig("flexibleweboperatinghours_18","Operating Hours - 18",WidgetConfigContext.ConfigType.FLEXIBLE,18,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("plannedmaintenance", "Planned Maintenance", PageWidget.WidgetType.PLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebplannedmaintenance_36","Planned Maintenance - 36",WidgetConfigContext.ConfigType.FLEXIBLE,36,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("unplannedmaintenance", "Unplanned Maintenance", PageWidget.WidgetType.UNPLANNED_MAINTENANCE)
                .addWidgetConfig("flexiblewebunplannedmaintenance_36","UnPlanned Maintenance - 36",WidgetConfigContext.ConfigType.FLEXIBLE,36,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workorderinsight", "Workorder Insight", PageWidget.WidgetType.WORKORDER_INSIGHT)
                .addWidgetConfig("flexiblewebworkorderinsight_12","Workorder Insight - 12",WidgetConfigContext.ConfigType.FLEXIBLE,12,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("recentlyclosedpm", "Recently Closed Pm", PageWidget.WidgetType.RECENTLY_CLOSED_PM)
                .addWidgetConfig("flexiblewebrecentlyclosedpm_23","Recently Closed Pm - 23",WidgetConfigContext.ConfigType.FLEXIBLE,23,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("faultreport", "Fault Report", PageWidget.WidgetType.FAULT_REPORT)
                .addWidgetConfig("flexiblewebfaultreport_27","Fault Report - 27",WidgetConfigContext.ConfigType.FLEXIBLE,27,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("faultinsight", "Fault Insight", PageWidget.WidgetType.FAULT_INSIGHT)
                .addWidgetConfig("flexiblewebfaultinsight_23","Fault Insight - 23",WidgetConfigContext.ConfigType.FLEXIBLE,23,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("readings", "Readings", PageWidget.WidgetType.READINGS)
                .addWidgetConfig("flexiblewebreadings_33","Readings - 33",WidgetConfigContext.ConfigType.FLEXIBLE,33,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("commandswidget", "Commands Widget", PageWidget.WidgetType.COMMANDS_WIDGET)
                .addWidgetConfig("flexiblewebcommandswidget_34","Commands Widget - 34",WidgetConfigContext.ConfigType.FLEXIBLE,34,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("latestdowntime", "Latest Downtime", PageWidget.WidgetType.LATEST_DOWNTIME)
                .addWidgetConfig("weblatestdowntime_16_6","Latest Downtime - 16 - 6",WidgetConfigContext.ConfigType.FIXED,16,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("overalldowntime", "Overall Downtime", PageWidget.WidgetType.OVERALL_DOWNTIME)
                .addWidgetConfig("weboveralldowntime_16_6","Overall Downtime - 16 - 6",WidgetConfigContext.ConfigType.FIXED,16,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("downtimehistory", "Downtime History", PageWidget.WidgetType.DOWNTIME_HISTORY)
                .addWidgetConfig("flexibledowntimehistory_28","Downtime History - 28",WidgetConfigContext.ConfigType.FLEXIBLE,28,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("failurerate", "Failure Rate", PageWidget.WidgetType.FAILURE_RATE)
                .addWidgetConfig("webfailurerate_41_6","Failure Rate - 41 - 6",WidgetConfigContext.ConfigType.FIXED,41,6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("averagerepairtime", "Average Repair Time", PageWidget.WidgetType.AVERAGE_REPAIR_TIME)
                .addWidgetConfig("webaveragerepairtime_41_6","Average Repair Time - 41 - 6",WidgetConfigContext.ConfigType.FIXED,41,6, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("assetcostdetail", "Asset Cost Detail", PageWidget.WidgetType.ASSET_COST_DETAILS)
                .addWidgetConfig("webassetcostdetail_25_4","Asset Cost Detail - 25 - 4",WidgetConfigContext.ConfigType.FIXED,25,4, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costbreakup", "Cost Breakup", PageWidget.WidgetType.COST_BREAKUP)
                .addWidgetConfig("webcostbreakup_25_8","Cost Breakup - 25 - 8",WidgetConfigContext.ConfigType.FIXED,25,8, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("maintenancecosttrend", "Maintenance Cost Trend", PageWidget.WidgetType.MAINTENANCE_COST_TREND)
                .addWidgetConfig("flexiblewebmaintenancecosttrend_31","Maintenance Cost Trend - 31",WidgetConfigContext.ConfigType.FLEXIBLE,31,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationschedule", "Depreciation Schedule", PageWidget.WidgetType.DEPRECIATION_SCHEDULE)
                .addWidgetConfig("flexiblewebdepreciationschedule_20","Depreciation Schedule - 20",WidgetConfigContext.ConfigType.FLEXIBLE,20,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("depreciationcosttrend", "Depreciation Cost Trend", PageWidget.WidgetType.DEPRECIATION_COST_TREND)
                .addWidgetConfig("flexiblewebdepreciationcosttrend_28","Depreciation Cost Trend - 28",WidgetConfigContext.ConfigType.FLEXIBLE,28,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("safetyplanhazard", "SafetyPlan Hazard", PageWidget.WidgetType.SAFETYPLAY_HAZARD)
                .addWidgetConfig("flexiblewebsafetyplanhazard_28","Safetyplan Hazard - 28",WidgetConfigContext.ConfigType.FLEXIBLE,28,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("safetyplanprecautions", "Safety Plan Precautions", PageWidget.WidgetType.SAFETY_PLAN_PRECAUTIONS)
                .addWidgetConfig("flexiblewebsafetyplanprecautions_28","Safety Plan Precaution - 28",WidgetConfigContext.ConfigType.FLEXIBLE,28,-1, PagesContext.PageLayoutType.WEB)
                .done()

                .addModuleWidget("relatedreadings","Related Readings", PageWidget.WidgetType.RELATED_READINGS)
                .addWidgetConfig("flexiblewebrelatedreadings_33","Related Readings - 33", WidgetConfigContext.ConfigType.FLEXIBLE,33,-1, PagesContext.PageLayoutType.WEB)
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
                .addWidgetConfig("webmonthlyconsumption_14_24","Monthly Consumption - 14 - 24",WidgetConfigContext.ConfigType.FIXED,14,24, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("totalConsumption","Total Consumption",PageWidget.WidgetType.TOTAL_CONSUMPTION)
                .addWidgetConfig("webtotalconsumption_14_24","Total Consumption - 14 - 24",WidgetConfigContext.ConfigType.FIXED,14,24, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("peakDemand","Peak Demand",PageWidget.WidgetType.PEAK_DEMAND)
                .addWidgetConfig("webpeakDemamd_14_24","Peak Demand - 14 - 24",WidgetConfigContext.ConfigType.FIXED,14,24, PagesContext.PageLayoutType.WEB)
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
                .addFixedWidgetConfig("webCalendarEventViewWidget-47*12", "Full Width Calendar Event View Widget - 47*12",47,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule(FacilioConstants.Control_Action.CONTROL_ACTION_MODULE_NAME)
    public static Supplier<ModuleWidgets> getControlActionWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("controlActionCriteriaView","Control Action Criteria View",PageWidget.WidgetType.CONRTOL_ACTION_CRITERIA)
                .addFixedWidgetConfig("webControlActionCriteriaViewWidget-28*6", "Half Width Control Action Criteria View Widget - 28*6",28,6, PagesContext.PageLayoutType.WEB)
                .done()
                ;
    }
    @WidgetsForModule(FacilioConstants.Control_Action.CONTROL_ACTION_TEMPLATE_MODULE_NAME)
    public static Supplier<ModuleWidgets> getControlActionTemplateWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("controlActionTemplateCriteriaView","Control Action Template Criteria View",PageWidget.WidgetType.CONRTOL_ACTION_CRITERIA)
                .done()
                ;
    }

    @WidgetsForModule("newreadingalarm")
    public static Supplier<ModuleWidgets> getReadingAlarmWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("occurrenceHistory", "Occurrence History", PageWidget.WidgetType.OCCURRENCE_HISTORY)
                .addFixedWidgetConfig("webOccurrenceHistory_58_12", "Occurrence History - 58 - 12", 58, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("alarmDuration","Alarm Duration",PageWidget.WidgetType.ALARM_DURATION)
                .addFixedWidgetConfig("webAlarmDuration_14_6","Alarm Duration - 14 - 6",14,6,PagesContext.PageLayoutType.WEB)
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
                .addFixedWidgetConfig("webLocationDetails_14_6","Location Details - 14 - 6",14,6,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("alarmRca", "Alarm RCA", PageWidget.WidgetType.ALARM_RCA)
                .addFixedWidgetConfig("webAlarmRca_58_12", "Alarm RCA - 58 - 12", 58, 12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.ReadingRules.NEW_READING_RULE)
    public static Supplier<ModuleWidgets> getNewReadingRuleWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("rootCauseAndImpact", "Root Cause and Impact", PageWidget.WidgetType.ROOT_CAUSE_AND_IMPACT)
                .addFixedWidgetConfig("webRootCauseAndImpact_30_3", "Root Cause and Impact - 30 - 3", 30, 3, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAssetsAndAlarm", "Rule Assets And Alarm", PageWidget.WidgetType.RULE_ASSETS_AND_ALARM)
                .addFixedWidgetConfig("webRuleAssetsAndAlarm_30_4", "Rule Assets And Alarm - 30 - 4", 30, 4, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAlarmInsights", "Rule Alarm Insights", PageWidget.WidgetType.RULE_ALARM_INSIGHT)
                .addFixedWidgetConfig("webRuleAlarmInsights_30_5", "Rule Alarm Insights - 30 - 5", 30, 5, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleAssociatedWorkOrders", "Rule Associated Work Orders", PageWidget.WidgetType.RULE_ASSOCIATED_WORK_ORDERS)
                .addFixedWidgetConfig("webRuleAssociatedWorkOrders_15_6", "Rule Associated Work Orders - 15 - 6", 15, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleWorkOrderDuration", "Rule Work Order Duration", PageWidget.WidgetType.RULE_WORK_ORDER_DURATION)
                .addFixedWidgetConfig("webRuleWorkOrderDuration_15_6", "Rule Work Order Duration - 15 - 6", 15, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("rootCauses", "Root Causes", PageWidget.WidgetType.ROOT_CAUSES)
                .addFixedWidgetConfig("webRootCauses_32_12", "Root Causes - 32 - 12", 32, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleInsight", "Rule Insight", PageWidget.WidgetType.RULE_INSIGHT)
                .addFixedWidgetConfig("webRuleInsight_32_12", "Rule Insight - 32 - 12", 32, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("ruleLogs", "Rule Logs", PageWidget.WidgetType.RULE_LOGS)
                .addFixedWidgetConfig("webRuleLogs_32_12", "Rule Logs - 32 - 12", 60, 12, PagesContext.PageLayoutType.WEB)
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
}
