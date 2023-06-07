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
                .addFixedWidgetConfig("webMeterWidget-10*12", "Full Width Meter Connections  Widget - 10*12",20,12, PagesContext.PageLayoutType.WEB)
                .done();
    }

    @WidgetsForModule(FacilioConstants.UTILITY_INTEGRATION_BILLS)
    public static Supplier<ModuleWidgets> getUtilityBillWidgets(){
        return () -> new ModuleWidgets()
                .addModuleWidget("billSummaryWidget" , "null",PageWidget.WidgetType.BILL_SUMMARY_WIDGET)
                .addFixedWidgetConfig("webBillSummaryWidget_65_12", "Full Width Bill Summary  Widget - 65-12",65,12, PagesContext.PageLayoutType.WEB)
                .done();
    }
}
