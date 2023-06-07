package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BulkRelationshipWidget;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.relation.util.RelationshipWidgetUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;
import java.util.Objects;

public class GetBulkRelationShipWidgetCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long pageWidgetId = (Long) context.get(FacilioConstants.CustomPage.PAGE_SECTION_WIDGET_ID);
        if(pageWidgetId == null || pageWidgetId <= 0) {
            throw new IllegalArgumentException("Invalid PageSectionWidget id to fetch relation ships");
        }

        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        Objects.requireNonNull(module, "Module name can't be empty to fetch relationship widget");

        BulkRelationshipWidget bulkRelShip = RelationshipWidgetUtil.getBulkRelationShipWidgetForWidgetId(pageWidgetId);

        if(bulkRelShip != null && CollectionUtils.isNotEmpty(bulkRelShip.getRelationships())) {
            Map<Long, RelationRequestContext>  relRequests = RelationshipWidgetUtil.getRelationsAsMapOfMappingId(module);

            if(MapUtils.isNotEmpty(relRequests)) {
                bulkRelShip.getRelationships().forEach(f -> {
                    RelationRequestContext relReq = relRequests.get(f.getRelationMappingId());
                    if(relReq != null) {
                        f.setDisplayName(relReq.getName());
                        f.setRelationMappingId(relReq.getRelMappingId());
                        f.setRelationName(relReq.getRelationName());
                        f.setRelation(relReq);
                    }
                });
            }
        }
        context.put(FacilioConstants.CustomPage.WIDGET_DETAIL, bulkRelShip);
        return false;
    }
}
