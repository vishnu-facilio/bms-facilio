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
                .addModuleWidget("workordermultiresourceWidget", "WORKORDER MULTIRESOURCE", PageWidget.WidgetType.MULTIRESOURCE)
                .addWidgetConfig("flexibleworkordermultiresource_17","Workorder MultiResource",WidgetConfigContext.ConfigType.FLEXIBLE,19,-1, PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workorderresponsibility", "WORKORDER RESPONSIBILITY", PageWidget.WidgetType.RESPONSIBILITY)
                .addWidgetConfig("flexibleworkorderresponsibility_14","Workorder Responsibility",WidgetConfigContext.ConfigType.FLEXIBLE,14,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workordertimedetails","WORKORDER TIME DETAILS",PageWidget.WidgetType.TIME_DETAILS)
                .addWidgetConfig("flexibleworkordertimedetails_26","Workorder Time Details",WidgetConfigContext.ConfigType.FLEXIBLE,31,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workorderlocationdetails","WORKORDER LOCATION DETAILS",PageWidget.WidgetType.RESOURCE)
                .addWidgetConfig("fixedworkorderlocationdetails_13","Workorder Location Detail",WidgetConfigContext.ConfigType.FIXED,13,32,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workordercostdetails","WORKORDER COST DETAILS",PageWidget.WidgetType.QUOTATION)
                .addWidgetConfig("flexibleworkordercostdetails_11","Workorder Cost Detail",WidgetConfigContext.ConfigType.FLEXIBLE,19,-1,PagesContext.PageLayoutType.WEB)
                .done()
                .addModuleWidget("workordersummary","WORKORDER SUMMARY DETAILS",PageWidget.WidgetType.SUMMARY_FIELDS_WIDGET)
                .addWidgetConfig("flexibleworkordersummary_33","Workorder Summary Detail",WidgetConfigContext.ConfigType.FLEXIBLE,33,-1,PagesContext.PageLayoutType.WEB)
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
}
