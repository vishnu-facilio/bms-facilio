package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.context.WidgetContext;
import com.facilio.bmsconsole.context.WidgetToModulesContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AddNewWidgetsCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddNewWidgetsCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.info("Started adding widgets(if new) to Widget_List table for ORGID --"+ AccountUtil.getCurrentOrg().getId());

        List<WidgetContext> widgets = (List<WidgetContext>) context.get(FacilioConstants.Widget.WIDGETS);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        List<WidgetToModulesContext> widgetToModules = new ArrayList<>();
        List<WidgetConfigContext> configs = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(widgets)) {

            List<WidgetContext> widgetTypes = WidgetAPI.getWidgetTypes(); //get existing widgetTypes

            Map<String, WidgetContext> widgetTypesMap = new HashMap<>();
            List<Long> widgetIds = new ArrayList<>();

            if(CollectionUtils.isNotEmpty(widgetTypes)) {
                widgetTypesMap = widgetTypes.stream()
                        .collect(Collectors.toMap(widget->widget.getWidgetType().name(), Function.identity()));
            }

            for (WidgetContext widget : widgets) {

                long id = 0;
                if (widget.getWidgetType() == null || widget.getWidgetType().name().isEmpty()) {
                    LOGGER.error("Invalid widget type");
                    throw new IllegalArgumentException("Invalid widget type");
                }

                boolean isWidgetExist = false;
                if(MapUtils.isNotEmpty(widgetTypesMap)){
                    isWidgetExist = widgetTypesMap.containsKey(widget.getWidgetType().name());
                }

                if(isWidgetExist) {
                    id = widgetTypesMap.get(widget.getWidgetType().name()).getId();
                    widget.setId(id);
                    widgetIds.add(id);
                }
                else{
                    if(widget.getName()==null) {
                        widget.setName(widget.getWidgetType().name());
                    }
                    WidgetAPI.addWidgetList(widget);
                    widgetTypesMap.put(widget.getWidgetType().name(), widget);
                    id = widget.getId();
                }

                setWidgetId(id,moduleName,widget,widgetToModules,configs);
            }

            context.put(FacilioConstants.Widget.WIDGET_MODULE,widgetToModules);
            context.put(FacilioConstants.Widget.WIDGET_CONFIGS,configs);
            context.put(FacilioConstants.Widget.WIDGET_IDS,widgetIds);
        }

        LOGGER.info("Completed Adding New Widgets to Widget_List Table For ORGID --"+ Objects.requireNonNull(AccountUtil.getCurrentOrg()).getId());

        return false;
    }

    private void setWidgetId(long id,String moduleName, WidgetContext widget,List<WidgetToModulesContext> widgetToModules, List<WidgetConfigContext> configs)throws Exception{
         if(moduleName == null) {
             moduleName = widget.getModuleName();
         }

        if(StringUtils.isNotEmpty(moduleName)){

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(moduleName);
                Objects.requireNonNull(module, "Invalid moduleName");

                WidgetToModulesContext widMod = new WidgetToModulesContext(id, module.getModuleId());
                widgetToModules.add(widMod);

        }
        else{
            WidgetToModulesContext widMod = new WidgetToModulesContext(id, null);
            widgetToModules.add(widMod);
        }

        if(CollectionUtils.isNotEmpty(widget.getWidgetConfigs())){
            widget.getWidgetConfigs().forEach(f->f.setWidgetId(id));
            configs.addAll(widget.getWidgetConfigs());
        }
    }
}
