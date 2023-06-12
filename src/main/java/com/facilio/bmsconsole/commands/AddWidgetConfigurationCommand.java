package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class AddWidgetConfigurationCommand extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddWidgetConfigurationCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        LOGGER.info("Started adding widget configurations to Widget_Config table for ORGID --"+ AccountUtil.getCurrentOrg().getId());

        List<WidgetConfigContext> configs = (List<WidgetConfigContext>) context.get(FacilioConstants.Widget.WIDGET_CONFIGS);
        if(CollectionUtils.isNotEmpty(configs)){

            for (WidgetConfigContext config : configs){
                if(config.getConfigType() == WidgetConfigContext.ConfigType.FIXED){
                    if(!(config.getMinHeight() > 0 && config.getMinWidth() > 0)){
                        LOGGER.error("Min height and min width should be valid for widget --"+config.getDisplayName());
                        throw new IllegalArgumentException("Invalid widget configuration");
                    }
                }

                if(config.getConfigType() == WidgetConfigContext.ConfigType.FLEXIBLE){
                    config.setMinWidth(-1);
                    if(!(config.getMinHeight() > 0 )){
                        LOGGER.error("Min height should be valid for widget --"+config.getDisplayName());
                        throw new IllegalArgumentException("Invalid widget configuration");
                    }
                }

                FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(config.getName()) || StringUtils.isEmpty(config.getDisplayName()),
                        "name and displayName should be defined for widget config");
            }

            WidgetAPI.addWidgetConfigs(configs);
        }

        LOGGER.info("Completed adding widget configurations for ORGID --"+AccountUtil.getCurrentOrg().getId());

        return false;
    }
}
