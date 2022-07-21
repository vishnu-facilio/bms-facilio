package com.facilio.relation.util;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class RelationUtil {

    public static RelationContext getRelation(long id, boolean fetchRelations) throws Exception {
        FacilioModule module = ModuleFactory.getRelationModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        RelationContext relation = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), RelationContext.class);
        if (relation != null) {
            if (fetchRelations) {
                fillRelation(Collections.singletonList(relation));
            }
        }
        return relation;
    }

    public static RelationMappingContext getRelationMapping(String mappingLinkName) throws Exception {
        FacilioModule module = ModuleFactory.getRelationMappingModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME", "mappingLinkName", mappingLinkName, StringOperators.IS));

        RelationMappingContext mapping = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), RelationMappingContext.class);
        return mapping;
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(ModuleFactory.getRelationModule().getTableName() + ".ID = " + ModuleFactory.getRelationMappingModule().getTableName() + ".RELATION_ID")
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition("FROM_MODULE_ID", "fromModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, module.getModuleId());
        return relationRequests;
    }

    public static RelationContext getRelation(FacilioModule relationModule, boolean fetchRelations) throws Exception {
        FacilioModule module = ModuleFactory.getRelationModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition("RELATION_MODULE_ID", "relationModuleId", String.valueOf(relationModule.getModuleId()), NumberOperators.EQUALS));

        RelationContext relation = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), RelationContext.class);
        if (relation != null && fetchRelations) {
            fillRelation(Collections.singletonList(relation));
        }
        return relation;
    }

    public static void fillRelation(List<RelationContext> relations) throws Exception {
        if (CollectionUtils.isEmpty(relations)) {
            return;
        }

        Map<Long, List<RelationContext>> map = new HashMap<>();
        for (RelationContext relation : relations) {
            List<RelationContext> relationContexts = map.get(relation.getId());
            if (relationContexts == null) {
                relationContexts = new ArrayList<>();
                map.put(relation.getId(), relationContexts);
            }
            relationContexts.add(relation);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationMappingModule().getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition("RELATION_ID", "relationId", StringUtils.join(map.keySet(), ","), NumberOperators.EQUALS));
        List<RelationMappingContext> mappingList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationMappingContext.class);
        if (CollectionUtils.isNotEmpty(mappingList)) {
            for (RelationMappingContext mapping : mappingList) {
                for (RelationContext relation : map.get(mapping.getRelationId())) {
                    mapping.setRelationContext(relation);
                    relation.addMapping(mapping);
                }
            }
        }
    }

    public static RelationRequestContext convertToRelationRequest(RelationContext relation, long moduleId) throws Exception {
        return convertToRelationRequest(Collections.singletonList(relation), moduleId).get(0);
    }

    public static List<RelationRequestContext> convertToRelationRequest(List<RelationContext> relations, long moduleId) throws Exception {
        List<RelationRequestContext> requests = new ArrayList<>();
        for (RelationContext relation : relations) {
            RelationRequestContext request = new RelationRequestContext();

            request.setId(relation.getId());
            request.setName(relation.getName());
            request.setDescription(relation.getDescription());
            request.setLinkName(relation.getLinkName());
            request.setRelationModule(relation.getRelationModule());
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

            for (RelationMappingContext mapping : relation.getMappings()) {
                if (mapping.getFromModuleId() == moduleId) { // this will get mapping for current module
                    FacilioModule fromModule = modBean.getModule(mapping.getFromModuleId());
                    request.setFromModule(fromModule);
                    request.setFromModuleId(mapping.getFromModuleId());
                    request.setFromModuleName(fromModule.getName());
                    FacilioModule toModule = modBean.getModule(mapping.getToModuleId());
                    request.setToModule(toModule);
                    request.setToModuleId(mapping.getToModuleId());
                    request.setToModuleName(toModule.getName());
                    request.setRelationName(mapping.getRelationName());
                    request.setForwardRelationLinkName(mapping.getMappingLinkName());
                    request.setRelationType(mapping.getRelationTypeEnum());
                    request.setPosition(mapping.getPositionEnum());
                    request.setReversePositionFieldName(mapping.getReversePosition().getFieldName());
                } else { // this will fetch reverse mapping
                    request.setReverseRelationName(mapping.getRelationName());
                    request.setReverseRelationLinkName(mapping.getMappingLinkName());
                }
            }
            requests.add(request);
        }

        return requests;
    }

    public static RelationContext convertToRelationContext(RelationRequestContext relationRequest) {
        RelationContext relationContext = new RelationContext();
        relationContext.setId(relationRequest.getId());
        relationContext.setName(relationRequest.getName());
        relationContext.setDescription(relationRequest.getDescription());
        relationContext.setLinkName(relationRequest.getLinkName());

        relationContext.addMapping(getRelationMapping(relationContext, relationRequest.getRelationName(),
                relationRequest.getRelationTypeEnum(), relationRequest.getFromModuleId(), relationRequest.getToModuleId(), RelationMappingContext.Position.LEFT));
        relationContext.addMapping(getRelationMapping(relationContext, relationRequest.getReverseRelationName(),
                relationRequest.getRelationTypeEnum().getReverseRelationType(), relationRequest.getToModuleId(), relationRequest.getFromModuleId(), RelationMappingContext.Position.RIGHT));
        return relationContext;
    }

    private static RelationMappingContext getRelationMapping(RelationContext relationContext,
                                                             String relationName,
                                                             RelationRequestContext.RelationType relationType,
                                                             long fromModuleId, long toModuleId,
                                                             RelationMappingContext.Position position) {
        RelationMappingContext mapping = new RelationMappingContext();
        mapping.setRelationId(relationContext.getId());
        mapping.setRelationContext(relationContext);
        mapping.setRelationName(relationName);
        mapping.setRelationType(relationType);
        mapping.setFromModuleId(fromModuleId);
        mapping.setToModuleId(toModuleId);
        mapping.setPosition(position);
        return mapping;
    }

    public static RelationMappingContext.Position getReversePosition(RelationMappingContext.Position position) {
        return (position == RelationMappingContext.Position.LEFT) ? RelationMappingContext.Position.RIGHT : RelationMappingContext.Position.LEFT;
    }

}
