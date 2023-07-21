package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.page.PageWidget;
import com.facilio.modules.FieldUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

   public void setWidgetType(PageWidget.WidgetType widgetType) throws Exception {
      this.widgetType = widgetType;
      this.setWidgetTypeObj(FieldUtil.getAsProperties(widgetType));
   }

   @JsonFormat(shape = JsonFormat.Shape.STRING)
   private PageWidget.WidgetType widgetType;
   private Map<String, Object> widgetTypeObj;
   private String moduleName;
   @JsonIgnore
   private ModuleWidgets done;
   private List<WidgetConfigContext> widgetConfigs = new ArrayList<>();
   public void setWidgetType(String widgetType){
      this.widgetType = PageWidget.WidgetType.valueOf(widgetType);
   }

   public WidgetContext addWidgetConfig(String name, String displayName, @NonNull WidgetConfigContext.ConfigType configType, long minHeight, long minWidth, PagesContext.PageLayoutType layoutType) {
      if(configType == WidgetConfigContext.ConfigType.FLEXIBLE) {
         return addFlexibleWidgetConfig(name, displayName, minHeight, layoutType);
      }
      else {
         return addFixedWidgetConfig(name, displayName, minHeight, minWidth, layoutType);
      }
   }
   public WidgetContext addFlexibleWidgetConfig(String name, String displayName, long minHeight, PagesContext.PageLayoutType layoutType) {
      layoutType = layoutType != null? layoutType: PagesContext.PageLayoutType.WEB;
      if(this.getWidgetConfigs() == null) {
         this.setWidgetConfigs(new ArrayList<>());
      }
      this.getWidgetConfigs().add(new WidgetConfigContext(name, displayName, minHeight, layoutType));
      return this;
   }
   public WidgetContext addFixedWidgetConfig(String name, String displayName, long minHeight, long minWidth, PagesContext.PageLayoutType layoutType) {
      layoutType = layoutType != null? layoutType: PagesContext.PageLayoutType.WEB;
      if(this.getWidgetConfigs() == null) {
         this.setWidgetConfigs(new ArrayList<>());
      }
      this.getWidgetConfigs().add(new WidgetConfigContext(name, displayName, WidgetConfigContext.ConfigType.FIXED, minHeight, minWidth, layoutType));
      return this;
   }
   public ModuleWidgets done() {
      return this.done;
   }

}
