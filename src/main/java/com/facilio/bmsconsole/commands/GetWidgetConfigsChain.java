package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.WidgetConfigContext;
import com.facilio.bmsconsole.util.WidgetAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class GetWidgetConfigsChain extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long id = (Long) context.get(FacilioConstants.ContextNames.ID);
        if(id ==null || id <= 0){
            throw new IllegalArgumentException("Invalid id to fetch widget configs");
        }
        List<WidgetConfigContext> configs = WidgetAPI.getWidgetConfigs(id);
        context.put(FacilioConstants.Widget.WIDGET_CONFIGS,configs);
        return false;
    }
}
