package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BulkRelationshipWidget;
import com.facilio.bmsconsole.context.RelationshipWidget;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.util.RelationshipWidgetUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AddBulkRelationshipWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId == null || pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to create relationship widget");
        }
        List<Long> existingRelMappingIdInWidget = RelationshipWidgetUtil.getRelationMappingIdInWidget(pageWidgetId);
        if(CollectionUtils.isNotEmpty(existingRelMappingIdInWidget)) {
            throw new IllegalArgumentException("Relationships already exists in the widget, add call is invalid");
        }
        BulkRelationshipWidget bulkRelationShipWidget = (BulkRelationshipWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        if(bulkRelationShipWidget != null && CollectionUtils.isNotEmpty(bulkRelationShipWidget.getRelationships())) {
            List<RelationshipWidget> relShips = new ArrayList<>();
            double sequenceNumber = 0;
            for(RelationshipWidget relShip : bulkRelationShipWidget.getRelationships()) {
                relShip.setSequenceNumber(sequenceNumber += 10);
                relShip.setWidgetId(pageWidgetId);
                FacilioUtil.throwIllegalArgumentException(relShip.getRelationMappingId() <= 0, "Invalid relation mapping id to add relationship");
                relShips.add(relShip);
            }

            if(CollectionUtils.isNotEmpty(relShips)) {
                RelationshipWidgetUtil.insertRelationshipWidgetsToDB(relShips);//add newRelationships
            }
        }

        context.put(FacilioConstants.WidgetNames.BULK_RELATION_SHIP_WIDGET, RelationshipWidgetUtil.getBulkRelationShipsWithDetails(moduleName, pageWidgetId));

        return false;
    }
}
