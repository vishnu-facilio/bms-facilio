package com.facilio.relation.command;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AddVirtualRelationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        RelationRequestContext relationRequest = (RelationRequestContext) context.get(FacilioConstants.ContextNames.RELATION);
        List<Long> relationIds = relationRequest.getVirtualRelationIds();

        if (CollectionUtils.isEmpty(relationIds) || (relationIds.size() <= 1)) {
            throw new IllegalArgumentException("Minimum two Relations required to create a Virtual Relation");
        }

        Long firstRelationId = relationIds.get(0);
        Long lastRelationId = relationIds.get(relationIds.size() - 1);

        Map<Long, RelationMappingContext> relationMapping = getRelationMapping(relationIds);
        validateVirtualRelation(new ArrayList<>(relationMapping.values()));

        relationRequest.setIsVirtual(true);

        RelationContext.RelationCategory relationCategory = relationRequest.getRelationCategoryEnum();
        relationCategory = relationCategory == null ? RelationContext.RelationCategory.NORMAL : relationCategory;
        relationRequest.setRelationCategory(relationCategory);

        boolean isManyToManyType = relationMapping.values().stream().anyMatch(relationMappingContext -> !(relationMappingContext.getRelationTypeEnum().equals(RelationRequestContext.RelationType.ONE_TO_ONE)));
        RelationRequestContext.RelationType relationType = isManyToManyType ? RelationRequestContext.RelationType.MANY_TO_MANY : RelationRequestContext.RelationType.ONE_TO_ONE ;
        relationRequest.setRelationType(relationType);

        relationRequest.setFromModuleId(relationMapping.get(firstRelationId).getFromModuleId());
        relationRequest.setToModuleId(relationMapping.get(lastRelationId).getToModuleId());

        FacilioChain chain = TransactionChainFactory.getAddOrUpdateRelationChain();
        FacilioContext relationContext = chain.getContext();
        relationContext.put(FacilioConstants.ContextNames.RELATION, relationRequest);
        chain.execute();

        RelationRequestContext relationRequestContext = (RelationRequestContext) relationContext.get(FacilioConstants.ContextNames.RELATION);
        long virtualRelationId = relationRequestContext.getId();

        createVirtualRelationConfig(virtualRelationId, relationIds);

        return false;
    }

    private void validateVirtualRelation(List<RelationMappingContext> relationMappingContexts) {
        boolean isValid = relationMappingContexts.stream()
                .limit(relationMappingContexts.size() - 1)
                .allMatch(context ->
                        context.getToModuleId() == relationMappingContexts.get(relationMappingContexts.indexOf(context) + 1).getFromModuleId());
        if (!isValid) {
            throw new IllegalArgumentException("Virtual Relation is not Valid");
        }
    }

    private void createVirtualRelationConfig(long virtualRelationId, List<Long> relationIds) throws SQLException {
        FacilioModule module = ModuleFactory.getVirtualRelationshipConfigModule();
        int seqNum = 1;
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getVirtualRelationshipConfigFields());
        for (Long relationId : relationIds) {
            Map<String, Object> record = new HashMap<>();
            record.put("parentId", virtualRelationId);
            record.put("relationId", relationId);
            record.put("sequenceNumber", seqNum++);
            builder.addRecord(record);
        }
        builder.save();
    }

    private static Map<Long, RelationMappingContext> getRelationMapping(List<Long> relationIds) throws Exception {
        FacilioModule module = ModuleFactory.getRelationMappingModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition("RELATION_ID","relationId", StringUtils.join(relationIds, ','), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("POSITION","position",String.valueOf(RelationMappingContext.Position.LEFT.getIndex()), NumberOperators.EQUALS))
                .orderBy("FIELD(RELATION_ID, "+ StringUtils.join(relationIds, ',') +")");
        List<Map<String, Object>> maps = builder.get();
        List<RelationMappingContext> relationMappingList = FieldUtil.getAsBeanListFromMapList(maps, RelationMappingContext.class);
        Map<Long, RelationMappingContext> relationMappingContextMap = new HashMap<>();
        for (RelationMappingContext relationMappingContext : relationMappingList) {
            relationMappingContextMap.put(relationMappingContext.getRelationId(), relationMappingContext);
        }
        return relationMappingContextMap;
    }
}
