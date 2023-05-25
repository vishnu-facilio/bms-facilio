package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.page.PageWidget;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WidgetContext {
   public WidgetContext(){
   }

   public WidgetContext(String name, String displayName, PageWidget.WidgetType widgetType){
      this.name = name;
      this.displayName = displayName;
      this.widgetType = widgetType;
   }

   public WidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, List<WidgetConfigContext> configs){
      this.name = name;
      this.displayName = displayName;
      this.widgetType = widgetType;
      if(configs != null){
         this.widgetConfigs = configs;
      }
   }
   public WidgetContext(String name, String displayName, PageWidget.WidgetType widgetType, String moduleName, List<WidgetConfigContext> configs){
      this.name = name;
      this.displayName = displayName;
      this.widgetType = widgetType;
      this.setModuleName(moduleName);
      if(configs != null){
         this.widgetConfigs = configs;
      }
   }
   private long id =-1;
   private String name;
   private String displayName;
   @JsonFormat(shape = JsonFormat.Shape.STRING)
   private PageWidget.WidgetType widgetType;
   private String moduleName;
   @JsonIgnore
   private ModuleWidgets done;
   private List<WidgetConfigContext> widgetConfigs = new ArrayList<>();
   public void setWidgetType(String widgetType){
      this.widgetType = PageWidget.WidgetType.valueOf(widgetType);
   }

   public WidgetContext addWidgetConfigs(WidgetConfigContext.ConfigType configType, long minHeight, long minWidth) {
      this.getWidgetConfigs().add(new WidgetConfigContext(configType, minHeight, minWidth));
      return this;
   }
   public ModuleWidgets done() {
      return this.done;
   }

}