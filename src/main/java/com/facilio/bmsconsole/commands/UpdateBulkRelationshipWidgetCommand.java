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

public class UpdateBulkRelationshipWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        long pageWidgetId = (long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to create relationship widget");
        }
        BulkRelationshipWidget bulkRelationShipWidget = (BulkRelationshipWidget) context.get(FacilioConstants.CustomPage.WIDGET_DETAIL);

        List<Long> existingRelMappingIdInWidget = RelationshipWidgetUtil.getRelationMappingIdInWidget(pageWidgetId);

        if(bulkRelationShipWidget != null && CollectionUtils.isNotEmpty(bulkRelationShipWidget.getRelationships())) {
            List<RelationshipWidget> newRelShip = new ArrayList<>();
            List<RelationshipWidget> updateRelShip = new ArrayList<>();
            double sequenceNumber = 0;

            for(RelationshipWidget relShip : bulkRelationShipWidget.getRelationships()) {
                relShip.setSequenceNumber(sequenceNumber+=10);
                relShip.setWidgetId(pageWidgetId);

                if(!(relShip.getId() > 0)) {
                    FacilioUtil.throwIllegalArgumentException(relShip.getRelationMappingId() <= 0, "Invalid relation mapping id to add relationship");
                    newRelShip.add(relShip);
                }
                else {
                    if(CollectionUtils.isNotEmpty(existingRelMappingIdInWidget)) {
                        existingRelMappingIdInWidget.remove(relShip.getRelationMappingId());
                    }
                    updateRelShip.add(relShip);
                }
            }

            if(CollectionUtils.isNotEmpty(newRelShip)) {
                RelationshipWidgetUtil.insertRelationshipWidgetsToDB(newRelShip);//add newRelShip
            }

            if(CollectionUtils.isNotEmpty(updateRelShip)) {
                RelationshipWidgetUtil.updateRelationshipsInWidget(updateRelShip);//update existing relShip sequenceNumber
            }
        }

        if(CollectionUtils.isNotEmpty(existingRelMappingIdInWidget)) {
            RelationshipWidgetUtil.deleteRelationshipsOfWidget(pageWidgetId, existingRelMappingIdInWidget);//deleting relShip
        }

        context.put(FacilioConstants.WidgetNames.BULK_RELATION_SHIP_WIDGET, RelationshipWidgetUtil.getBulkRelationShipsWithDetails(moduleName, pageWidgetId));
        return false;
    }
}
