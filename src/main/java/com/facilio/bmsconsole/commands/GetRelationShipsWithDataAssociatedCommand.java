package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GetRelationShipsWithDataAssociatedCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip= (Map<Long, List<RelationRequestContext>>) context.get(FacilioConstants.ContextNames.MODULEID_VS_RELATION_MAPPING);
        List<Long> relationModuleIdsWithData= (List<Long>) context.get(FacilioConstants.ContextNames.RELATION_MODULEID_WITH_DATA);
        List<Long> relationModuleIdsWithRightPosition = (List<Long>) context.get(FacilioConstants.ContextNames.SAME_MODULE_RELATIONSHIP_WITH_DATA_AT_RIGHT_POSITION);
        List<RelationRequestContext> relationMapping=new ArrayList<>();

        if(CollectionUtils.isNotEmpty(relationModuleIdsWithData)) {
            addRelationMapping(relationMapping, moduleIdVsRelationShip, relationModuleIdsWithData, false);
        }
        if(CollectionUtils.isNotEmpty(relationModuleIdsWithRightPosition)){
            addRelationMapping(relationMapping,moduleIdVsRelationShip, relationModuleIdsWithRightPosition,true);
        }
        Collections.sort(relationMapping, Comparator.comparing(RelationRequestContext::getId));
        context.put(FacilioConstants.ContextNames.RELATIONSHIP_LIST,relationMapping);
        return false;
    }
    public static void addRelationMapping(List<RelationRequestContext> relationMapping,Map<Long,List<RelationRequestContext>> moduleIdVsRelationShip,List<Long> relationModuleIdsWithData,boolean relationShipWithRightPos) {
        for (Long moduleId : relationModuleIdsWithData) {
            List<RelationRequestContext> relationRequestList = moduleIdVsRelationShip.get(moduleId);
            if (CollectionUtils.isNotEmpty(relationRequestList)) {
                if (relationShipWithRightPos) {
                    if (relationRequestList.get(0).getFromModuleId() == relationRequestList.get(0).getToModuleId()) {
                        relationMapping.addAll(relationRequestList.stream().filter(i -> i.getPosition() == RelationMappingContext.Position.RIGHT.getIndex()).collect(Collectors.toList()));
                    }
                } else {
                    if (relationRequestList.get(0).getFromModuleId() == relationRequestList.get(0).getToModuleId()) {
                        relationMapping.addAll(relationRequestList.stream().filter(i -> i.getPosition() == RelationMappingContext.Position.LEFT.getIndex()).collect(Collectors.toList()));
                    }else{
                        relationMapping.addAll(relationRequestList);
                    }
                }
            }
        }
    }
}
