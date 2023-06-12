package com.facilio.bmsconsole.ModuleWidget;

import com.facilio.bmsconsole.context.ModuleWidgets;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.page.PageWidget;
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
}
