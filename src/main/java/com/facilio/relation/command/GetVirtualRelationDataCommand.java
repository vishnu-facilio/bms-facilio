package com.facilio.relation.command;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

        List<Long> virtualRelationIds = getVirtualRelationIds(relationId, orderBy);
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
            relatedIds = getRelatedIds(relations, recordId, rightIdField, leftIdField, moduleIdField, page, perPage);
        } else {
            mapping = lastRelation.getMapping2();
            relatedIds = getRelatedIds(relations, recordId, leftIdField, rightIdField, moduleIdField, page, perPage);
        }

        FacilioModule toModule = mapping.getToModule();

        String relationModuleName = toModule.getName();

        context.put(FacilioConstants.ContextNames.MODULE_NAME, relationModuleName);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, relatedIds);
        return false;
    }

    private List<Long> getRelatedIds(List<RelationContext> relations, long recordId, FacilioField rightIdField, FacilioField leftIdField, FacilioField moduleIdField, int page, int perPage) throws Exception {
        int lastIndex = relations.size() - 1;
        RelationContext relationContext = relations.get(lastIndex);
        String subQueryString = constructBuilder(relations, lastIndex - 1, recordId, rightIdField, leftIdField, moduleIdField);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table("Custom_Relation")
                .select(Collections.singleton(rightIdField))
                .andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(relationContext.getRelationModuleId()), NumberOperators.EQUALS))
                .andCustomWhere(leftIdField.getCompleteColumnName() + " IN (" + subQueryString + ")");

        int offset = ((page-1) * perPage);
        if (offset < 0) {
            offset = 0;
        }
        selectRecordBuilder.offset(offset);
        selectRecordBuilder.limit(perPage);

        List<Map<String, Object>> maps = selectRecordBuilder.get();
        return maps.stream().map(record -> (Long) record.get(rightIdField.getName())).collect(Collectors.toList());
    }

    private String constructBuilder(List<RelationContext> relations, int i, long recordId, FacilioField rightIdField, FacilioField leftIdField, FacilioField moduleIdField) {
        RelationContext relationContext = relations.get(i);

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table("Custom_Relation")
                .select(Collections.singleton(rightIdField))
                .andCondition(CriteriaAPI.getCondition(moduleIdField, String.valueOf(relationContext.getRelationModuleId()), NumberOperators.EQUALS));

        if (i == 0) {
            selectRecordBuilder.andCustomWhere(leftIdField.getCompleteColumnName() + " = " + recordId);
        } else {
            selectRecordBuilder.andCustomWhere(leftIdField.getCompleteColumnName() + " IN (" + constructBuilder(relations, i - 1, recordId, rightIdField, leftIdField, moduleIdField) + ")");
        }
        return selectRecordBuilder.constructSelectStatement();
    }

    private List<Long> getVirtualRelationIds(long parentId, String orderBy) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getVirtualRelationshipConfigModule().getTableName())
                .select(FieldFactory.getVirtualRelationshipConfigFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(parentId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER "+ orderBy);
        List<Map<String, Object>> maps = selectRecordBuilder.get();
        return maps.stream().map(record -> (Long) record.get("relationId")).collect(Collectors.toList());
    }
}
