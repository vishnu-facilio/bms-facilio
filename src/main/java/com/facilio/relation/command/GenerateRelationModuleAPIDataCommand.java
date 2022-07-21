package com.facilio.relation.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateRelationModuleAPIDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        JSONObject data = (JSONObject) context.get(FacilioConstants.ContextNames.DATA);

        String relationMappingLinkName = (String) Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.RELATION_NAME);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, FacilioConstants.ContextNames.PARENT_ID));

        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationMappingLinkName);
        if(relationMapping == null) {
            throw new IllegalArgumentException("Invalid relation");
        }
        RelationContext relation = RelationUtil.getRelation(relationMapping.getRelationId(), false);
        if(relation == null) {
            throw new IllegalArgumentException("Invalid relation");
        }
        String toModuleName = relationMapping.getToModule().getName();
        Map<String, Object> parentData = new HashMap<>();
        parentData.put("id", (Long)parentId);

        List relationshipData = new ArrayList();

        List associateArray = (ArrayList) data.get(toModuleName);
        if(CollectionUtils.isEmpty(associateArray)) {
            throw new IllegalArgumentException("Invalid Data passed");
        }

        for(Object id : associateArray) {
            Map<String, Object> relationData = new HashMap<>();
            Map<String, Object> childData = new HashMap<>();
            childData.put("id", (Long)id);
            relationData.put(relationMapping.getPositionEnum().getFieldName(), parentData);
            relationData.put(relationMapping.getReversePosition().getFieldName(), childData);
            relationshipData.add(relationData);
        }

        data.remove(toModuleName);
        data.put(relation.getRelationModule().getName(), relationshipData);
        data.put(FacilioConstants.ContextNames.MODULE_NAME , relation.getRelationModule().getName());
        context.put(FacilioConstants.ContextNames.RELATION_MODULE_NAME, relation.getRelationModule().getName());

        return false;
    }
}
