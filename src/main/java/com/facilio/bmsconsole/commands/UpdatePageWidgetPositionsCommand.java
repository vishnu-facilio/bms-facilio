package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.CustomPageAPI;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class UpdatePageWidgetPositionsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        List<Map<String, Object>> widgetPositions = (List<Map<String, Object>>) context.get(FacilioConstants.CustomPage.WIDGETS_POSITIONS);

        if(CollectionUtils.isNotEmpty(widgetPositions)) {

            for(Map<String, Object> widgetPosition : widgetPositions) {
                if(widgetPosition.containsKey("id") && widgetPosition.containsKey("positionX") && widgetPosition.containsKey("positionY")){
                }
                else {
                    throw new IllegalArgumentException("Invalid widget positions to update");
                }
            }
            CustomPageAPI.updateWidgetPositions(widgetPositions, widgetWrapperType);
        }
        return false;
    }
}
