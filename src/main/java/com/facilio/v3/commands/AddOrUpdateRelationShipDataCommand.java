package com.facilio.v3.commands;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;


import java.util.List;
import java.util.Map;

public class AddOrUpdateRelationShipDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);

        if (MapUtils.isEmpty(recordMap) || CollectionUtils.isEmpty(recordMap.get(moduleName))) {
            return false;
        }
        List<ModuleBaseWithCustomFields> records = recordMap.get(moduleName);

        for (ModuleBaseWithCustomFields record : records) {
            long id = record.getId();
            Map<String, Map<String, List<Long>>> relationShip = record.getRelationShip();
            if (MapUtils.isEmpty(relationShip)) {
                continue;
            }

            for (String relationshipLinkName : relationShip.keySet()) {
                RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationshipLinkName);
                String toModuleName = relationMapping.getToModule().getName();

                Map<String, List<Long>> associateDissociateIdsMap = relationShip.get(relationshipLinkName);
                List<Long> associateIds = associateDissociateIdsMap.get(FacilioConstants.RelationshipField.ASSOCIATE_IDS);
                List<Long> dissociateIds = associateDissociateIdsMap.get(FacilioConstants.RelationshipField.DISSOCIATE_IDS);

                if (CollectionUtils.isNotEmpty(associateIds)) {
                    JSONObject data = new JSONObject();
                    data.put(toModuleName, associateIds);

                    FacilioChain associateDissociateDataChain = ReadOnlyChainFactoryV3.getAssociateDissociateDataChain();
                    FacilioContext associateChainContext = associateDissociateDataChain.getContext();

                    associateChainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATIONSHIP);
                    associateChainContext.put(FacilioConstants.ContextNames.RELATIONSHIP_ACTION_TYPE, "ASSOCIATE");
                    associateChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                    associateChainContext.put(FacilioConstants.ContextNames.RELATION_NAME, relationshipLinkName);
                    associateChainContext.put(FacilioConstants.ContextNames.DATA, data);
                    associateChainContext.put(FacilioConstants.ContextNames.ID, id);
                    associateDissociateDataChain.execute();
                }

                if (CollectionUtils.isNotEmpty(dissociateIds)) {
                    JSONObject data = new JSONObject();
                    data.put(toModuleName, dissociateIds);

                    FacilioChain dissociateDataChain = ReadOnlyChainFactoryV3.getAssociateDissociateDataChain();
                    FacilioContext dissociateChainContext = dissociateDataChain.getContext();

                    dissociateChainContext.put(FacilioConstants.ContextNames.WIDGET_TYPE, FacilioConstants.ContextNames.RELATIONSHIP);
                    dissociateChainContext.put(FacilioConstants.ContextNames.RELATIONSHIP_ACTION_TYPE, "DISSOCIATE");
                    dissociateChainContext.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                    dissociateChainContext.put(FacilioConstants.ContextNames.RELATION_NAME, relationshipLinkName);
                    dissociateChainContext.put(FacilioConstants.ContextNames.DATA, data);
                    dissociateChainContext.put(FacilioConstants.ContextNames.ID, id);
                    dissociateDataChain.execute();
                }

            }
        }

        return false;
    }
}
