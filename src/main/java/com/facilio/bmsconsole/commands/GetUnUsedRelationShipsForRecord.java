package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetUnUsedRelationShipsForRecord extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<Long,List<RelationRequestContext>> moduleIdVsRelationship= (Map<Long, List<RelationRequestContext>>) context.get(FacilioConstants.ContextNames.MODULEID_VS_RELATION_MAPPING);
        List<Long> relationModuleIdsWithData= (List<Long>) context.get(FacilioConstants.ContextNames.RELATION_MODULEID_WITH_DATA);
        List<Long> relationModuleIdsWithRightPosition= (List<Long>) context.get(FacilioConstants.ContextNames.SAME_MODULE_RELATIONSHIP_WITH_DATA_AT_RIGHT_POSITION);
        List<RelationRequestContext> allRelations= (List<RelationRequestContext>) context.get(FacilioConstants.ContextNames.RELATION_LIST);
        List<RelationRequestContext> relationMapping=new ArrayList<>();

        if( CollectionUtils.isEmpty(relationModuleIdsWithData) && CollectionUtils.isEmpty(relationModuleIdsWithRightPosition)){
            relationMapping.addAll(allRelations);
        }else{
            addRelationMappingWithNoData(relationMapping, moduleIdVsRelationship, relationModuleIdsWithData, false);
            addRelationMappingWithNoData(relationMapping,moduleIdVsRelationship,relationModuleIdsWithRightPosition,true);

        }
        Collections.sort(relationMapping, Comparator.comparing(RelationRequestContext::getId));
        context.put(FacilioConstants.ContextNames.RELATIONSHIP_LIST,relationMapping);
        return false;
    }
    public static void addRelationMappingWithNoData(List<RelationRequestContext> relationMapping, Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip, List<Long> relationModuleIdsWithData, boolean relationShipWithRightPos) {

        for (Long moduleId : moduleIdVsRelationShip.keySet()) {
            List<RelationRequestContext> relationRequestList = moduleIdVsRelationShip.get(moduleId);
            if (CollectionUtils.isNotEmpty(relationRequestList)) {
                if (relationShipWithRightPos) {
                    if (!relationModuleIdsWithData.contains(moduleId) && relationRequestList.get(0).getFromModuleId() == relationRequestList.get(0).getToModuleId()) {
                        relationMapping.addAll(relationRequestList.stream().filter(i->i.getPosition()==RelationMappingContext.Position.RIGHT.getIndex()).collect(Collectors.toList()));
                    }
                }else{
                    if(!relationModuleIdsWithData.contains(moduleId)) {
                        if (relationRequestList.get(0).getFromModuleId() == relationRequestList.get(0).getToModuleId()) {
                            relationMapping.addAll(relationRequestList.stream().filter(i -> i.getPosition() == RelationMappingContext.Position.LEFT.getIndex()).collect(Collectors.toList()));
                        } else {
                            relationMapping.addAll(relationRequestList);
                        }
                    }
                }
            }
        }
    }
}




