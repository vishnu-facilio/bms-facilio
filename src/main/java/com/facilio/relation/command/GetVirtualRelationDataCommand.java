package com.facilio.relation.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class GetVirtualRelationDataCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
        String relationName = (String) context.get(FacilioConstants.ContextNames.RELATION_NAME);
        int page = (int) context.getOrDefault(FacilioConstants.ContextNames.PAGE, -1);
        int perPage = (int) context.getOrDefault(FacilioConstants.ContextNames.PER_PAGE, -1);

        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationName);

        RelationMappingContext.Position positionEnum = relationMapping.getPositionEnum();
        String orderBy = Objects.equals(positionEnum, RelationMappingContext.Position.LEFT) ? "DESC" : "ASC";
        long relationId = relationMapping.getRelationId();

        List<Long> virtualRelationIds = RelationUtil.getVirtualRelationIds(relationId, orderBy);
        String relationOrderBy = "FIELD(ID, " + StringUtils.join(virtualRelationIds, ',') + ")";
        List<RelationContext> relations = RelationUtil.getRelations(virtualRelationIds, true, relationOrderBy);

        FacilioField rightIdField = FieldFactory.getField("rightId", "RIGHT_ID", FieldType.NUMBER);
        FacilioField leftIdField = FieldFactory.getField("leftId", "LEFT_ID", FieldType.NUMBER);
        FacilioField moduleIdField = FieldFactory.getField("moduleId", "MODULEID", FieldType.NUMBER);

        List<Long> relatedIds;
        RelationContext lastRelation = relations.get(relations.size() - 1);
        RelationMappingContext mapping;

        if (Objects.equals(positionEnum, RelationMappingContext.Position.RIGHT)) {
            mapping = lastRelation.getMapping1();
            relatedIds = RelationUtil.getRelatedIds(relations, recordId, rightIdField, leftIdField, moduleIdField, page, perPage);
        } else {
            mapping = lastRelation.getMapping2();
            relatedIds = RelationUtil.getRelatedIds(relations, recordId, leftIdField, rightIdField, moduleIdField, page, perPage);
        }

        FacilioModule toModule = mapping.getToModule();

        String relationModuleName = toModule.getName();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, relationModuleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, relatedIds);
        return false;
    }
}
