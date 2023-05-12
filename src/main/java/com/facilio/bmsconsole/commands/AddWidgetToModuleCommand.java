package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WidgetToModulesContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;

public class AddWidgetToModuleCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddWidgetToModuleCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.info("Started adding widget to module(if new) to Widget_Modules table for ORGID --"+ AccountUtil.getCurrentOrg().getId());

        List<WidgetToModulesContext> widgetToModules = (List<WidgetToModulesContext>) context.get(FacilioConstants.Widget.WIDGET_MODULE);
        List<Long> widgetIds = (List<Long>) context.get(FacilioConstants.Widget.WIDGET_IDS);

        if(CollectionUtils.isNotEmpty(widgetToModules)){

            List<Long> commonWidgetTypes = WidgetAPI.getCommonWidgetIds();
            Map<Long,List<Long>> widgetToModulesMap = WidgetAPI.getModuleIdsAsMapOfWidgetId(widgetIds);
            List<WidgetToModulesContext> newWidgetToModules = new ArrayList<>();

            for(WidgetToModulesContext widMod:widgetToModules) {

                if (!commonWidgetTypes.contains(widMod.getWidgetId()) && (widgetToModulesMap.get(widMod.getWidgetId()) == null ||
                        (widgetToModulesMap.get(widMod.getWidgetId()) != null && !widgetToModulesMap.get(widMod.getWidgetId()).contains(widMod.getModuleId())))) {
                    newWidgetToModules.add(widMod);
                    if (widMod.getModuleId() != null) {
                        if(widgetToModulesMap.get(widMod.getWidgetId()) == null){
                            widgetToModulesMap.put(widMod.getWidgetId(), new ArrayList<>(Arrays.asList(widMod.getModuleId())));
                        }
                        else {
                            widgetToModulesMap.get(widMod.getWidgetId()).add(widMod.getModuleId());
                        }
                    }
                    else {
                        if (CollectionUtils.isNotEmpty(commonWidgetTypes)) {
                            commonWidgetTypes.add(widMod.getWidgetId());
                        }
                        else {
                            commonWidgetTypes = new ArrayList<>(Arrays.asList(widMod.getWidgetId()));
                        }
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(newWidgetToModules)){
                WidgetAPI.addWidgetToModules(newWidgetToModules); //new widget to module records are added to table
            }
        }

        LOGGER.info("Completed adding widget to module values to Widget_Modules table for ORGID --"+ AccountUtil.getCurrentOrg().getId());

        return false;
    }
}
