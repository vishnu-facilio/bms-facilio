package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.bmsconsole.widgetConfig.WidgetWrapperType;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddOrUpdateRelationshipWidgetDetailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long widgetId = (Long) context.get(FacilioConstants.CustomPage.WIDGETID);
        WidgetWrapperType widgetWrapperType = (WidgetWrapperType) context.get(FacilioConstants.CustomPage.WIDGET_WRAPPER_TYPE);
        if((widgetId == null || widgetId <= 0)) {
            throw new IllegalArgumentException("widgetId should be defined to create relationship widget");
        }
        List<Long> existingRelMappingIdInWidget = RelationshipWidgetUtil.getRelationMappingIdInWidget(widgetId, widgetWrapperType);
        if(CollectionUtils.isNotEmpty(existingRelMappingIdInWidget)) {
            RelationshipWidgetUtil.deleteRelationshipsOfWidget(widgetId, existingRelMappingIdInWidget);
        }
        RelationshipWidget relShip = (RelationshipWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        relShip.setSequenceNumber(10D);
        RelationshipWidgetUtil.setWidgetIdForRelList(widgetId, relShip, widgetWrapperType);
        FacilioUtil.throwIllegalArgumentException(relShip.getRelationMappingId() <= 0, "Invalid relation mapping id to add relationship");

        RelationshipWidgetUtil.insertRelationshipWidgetToDB(relShip);

        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, RelationshipWidgetUtil.getRelationshipOfWidget(widgetId, widgetWrapperType));
        return false;
    }
}
