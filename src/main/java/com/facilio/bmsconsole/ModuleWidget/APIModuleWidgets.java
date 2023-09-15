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
    public static Supplier<ModuleWidgets> getAssetWidgets() {
        return ModuleWidgets::new;

    }
    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_CUSTOMER)
    public static Supplier<ModuleWidgets> getUtilityCustomerWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("meterWidget" , "Meter Connections",PageWidget.WidgetType.METER_WIDGET)
                //.addWidgetConfig("webMeterWidget-10*12","Full Width Meter Connections  Widget - 10*12", WidgetConfigContext.ConfigType.FIXED,65,12,PagesContext.PageLayoutType.WEB)
                .addFixedWidgetConfig("webMeterWidget_24_12", "Meter Connections  Widget -24-12",24,12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<ModuleWidgets> getUtilityBillWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("billSummaryWidget" , "null",PageWidget.WidgetType.BILL_SUMMARY_WIDGET)
                .addFixedWidgetConfig("webBillSummaryWidget_65_12", "Bill Summary Widget -65-12",65,12, PagesContext.PageLayoutType.WEB)
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
