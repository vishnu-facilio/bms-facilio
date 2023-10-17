package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class AddDefaultRelationsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        // Site to Building Relation
        ModuleBean modBean = Constants.getModBean();
        List<RelationRequestContext> relationRequestContexts = new ArrayList<>();

        FacilioModule siteModule = modBean.getModule(FacilioConstants.ContextNames.SITE);
        FacilioModule buildingModule = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
        FacilioModule floorModule = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
        FacilioModule spaceModule = modBean.getModule(FacilioConstants.ContextNames.SPACE);

        relationRequestContexts.add(getRelationRequest(siteModule, buildingModule, FacilioConstants.SystemRelationMappingNames.SITE_TO_BUILDING_MAPPING_NAME, FacilioConstants.SystemRelationMappingNames.BUILDING_TO_SITE_MAPPING_NAME));
        relationRequestContexts.add(getRelationRequest(buildingModule, floorModule, FacilioConstants.SystemRelationMappingNames.BUILDING_TO_FLOOR_MAPPING_NAME, FacilioConstants.SystemRelationMappingNames.FLOOR_TO_BUILDING_MAPPING_NAME));
        relationRequestContexts.add(getRelationRequest(floorModule, spaceModule, FacilioConstants.SystemRelationMappingNames.FLOOR_TO_SPACE_MAPPING_NAME, FacilioConstants.SystemRelationMappingNames.SPACE_TO_FLOOR_MAPPING_NAME));
        relationRequestContexts.add(getRelationRequest(buildingModule, spaceModule, FacilioConstants.SystemRelationMappingNames.BUILDING_TO_SPACE_MAPPING_NAME, FacilioConstants.SystemRelationMappingNames.SPACE_TO_BUILDING_MAPPING_NAME));
        relationRequestContexts.add(getRelationRequest(siteModule, spaceModule, FacilioConstants.SystemRelationMappingNames.SITE_TO_SPACE_MAPPING_NAME, FacilioConstants.SystemRelationMappingNames.SPACE_TO_SITE_MAPPING_NAME));

        for (RelationRequestContext relationRequestContext : relationRequestContexts) {
            FacilioChain addRelationRequestChain = TransactionChainFactory.getAddOrUpdateRelationChain();
            FacilioContext relationRequestChainContext = addRelationRequestChain.getContext();
            relationRequestChainContext.put(FacilioConstants.ContextNames.RELATION, relationRequestContext);
            addRelationRequestChain.execute();
        }

        return false;
    }
    private RelationRequestContext getRelationRequest(FacilioModule fromModule, FacilioModule toModule, String forwardRelLinkName, String reverseRelLinkName) {
        RelationRequestContext relationRequestContext = new RelationRequestContext();
        relationRequestContext.setName(fromModule.getDisplayName() + " to "+ toModule.getDisplayName() + " Relation");
        relationRequestContext.setRelationType(RelationRequestContext.RelationType.ONE_TO_MANY);
        relationRequestContext.setFromModuleId(fromModule.getModuleId());
        relationRequestContext.setToModuleId(toModule.getModuleId());
        relationRequestContext.setRelationName(fromModule.getDisplayName() + " to "+ toModule.getDisplayName() + " Relation");
        relationRequestContext.setReverseRelationName(toModule.getDisplayName() + " to "+ fromModule.getDisplayName() + " Relation");
        relationRequestContext.setForwardRelationLinkName(forwardRelLinkName);
        relationRequestContext.setReverseRelationLinkName(reverseRelLinkName);
        relationRequestContext.setRelationCategory(RelationContext.RelationCategory.HIDDEN);
        relationRequestContext.setIsCustom(false);
        return relationRequestContext;
    }
}
