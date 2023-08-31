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
                .addFixedWidgetConfig("webOccurrenceHistory-58*12", "Occurrence History - 58*12", 58, 12, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("mtba", "Mean time between occurrence", PageWidget.WidgetType.MTBA_CARD)
                .addFixedWidgetConfig("webMtba-14*6", "Mtba - 14*6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("mttc", "Mean time to clear", PageWidget.WidgetType.MTTC_CARD)
                .addFixedWidgetConfig("webMttc-14*6", "Mttc - 14*6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("alarmDuration", "No. of occurrences", PageWidget.WidgetType.ALARM_DURATION)
                .addFixedWidgetConfig("webAlarmDuration-12*6", "Alarm Duration - 12*6", 12, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("impactInfo", "Impact Template", PageWidget.WidgetType.IMPACT_INFO)
                .addFixedWidgetConfig("webImpactInfo-12*6", "Impact Info - 12*6", 12, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("costImpact", "Cost Impact", PageWidget.WidgetType.COST_IMPACT)
                .addFixedWidgetConfig("webCostImpact-14*6", "Cost Impact - 14*6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("energyImpact", "Energy Impact", PageWidget.WidgetType.ENERGY_IMPACT)
                .addFixedWidgetConfig("webEnergyImpact-14*6", "Energy Impact - 14*6", 14, 6, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("impactReport", "Impact Report", PageWidget.WidgetType.IMPACT_REPORT)
                .addFlexibleWidgetConfig("webImpactReport-51", "Impact Report - 51", 51, PagesContext.PageLayoutType.WEB)
                .done();
    }
    @WidgetsForModule("bmsalarm")
    public static Supplier<ModuleWidgets> getBmsAlarmWidgets() {
        return () -> new ModuleWidgets()
                .addModuleWidget("bmsOccurrenceHistory", "BMS Alarm", PageWidget.WidgetType.OCCURRENCE_HISTORY)
                .addFlexibleWidgetConfig("webOccurrenceHistory-24", "Full Width Occurrence History - 24", 24, PagesContext.PageLayoutType.WEB)
                .done();
    }
}
