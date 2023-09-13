package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.WidgetGroupUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class AddWidgetGroupConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        Objects.requireNonNull(widgetId, "widgetId can't be null");

        WidgetGroupContext widgetGroup = (WidgetGroupContext) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);
        if (widgetGroup != null) {
            WidgetGroupConfigContext config = widgetGroup.getConfig();
            config.setWidgetId(widgetId);

            WidgetGroupUtil.insertWidgetGroupConfigToDB(config);

            Map<Long, List<WidgetGroupSectionContext>> widgetGroupSectionsMap = new HashMap<>();
            widgetGroupSectionsMap.put(widgetId, widgetGroup.getSections());
            context.put(FacilioConstants.WidgetGroup.WIDGETGROUP_SECTIONS_MAP, widgetGroupSectionsMap);
        }

        return false;
    }
}
