package com.facilio.relation.util;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.RelationshipOperator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

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

    public static JSONObject getRecordsWithRelationship(Long relationMappingId, Long parentId, int page, int perPage) throws Exception {
        RelationMappingContext mappingData = getRelationMapping(relationMappingId);
        return getRecordsWithRelationship(mappingData.getMappingLinkName(), mappingData.getFromModule().getName(), parentId, page, perPage);
    }

    public static JSONObject getRecordsWithReverseRelationship(Long relationMappingId, Long parentId, int page, int perPage) throws Exception {
        RelationMappingContext mappingData = getRelationMapping(relationMappingId);
        RelationContext relation = RelationUtil.getRelation(mappingData.getRelationId(), true);
        if (relation == null) {
            throw new IllegalArgumentException("Invalid Relation");
        }
        String relationMappingName = null;
        for(RelationMappingContext mapping : relation.getMappings()) {
            if(!mapping.getMappingLinkName().equals(mappingData.getMappingLinkName())) {
                relationMappingName = mapping.getMappingLinkName();
                break;
            }
        }

        return getRecordsWithRelationship(relationMappingName, mappingData.getToModule().getName(), parentId, page, perPage);
    }

    public static JSONObject getRecordsWithRelationship(String relationLinkName, String moduleName, Long parentId, int page, int perPage) throws Exception {
        JSONObject relatedData = new JSONObject();
        page = (page < 1) ? 1 : page;
        perPage = (perPage < 1) ? WorkflowV2Util.SELECT_DEFAULT_LIMIT : perPage;

        Criteria serverCriteria = new Criteria();
        serverCriteria.addAndCondition(CriteriaAPI.getCondition(relationLinkName, String.valueOf(parentId), RelationshipOperator.CONTAINS_RELATION));

        FacilioContext listContext = V3Util.fetchList(moduleName, true, null, null, false, null, null,
                null, null, page, perPage, false, null, serverCriteria);

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);
        relatedData.put(FacilioConstants.ContextNames.DATA, recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            relatedData.put(FacilioConstants.ContextNames.META, (JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }

        return relatedData;
    }

    public static RelationMappingContext getRelationMapping(Long mappingId) throws Exception {
        FacilioModule module = ModuleFactory.getRelationMappingModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(mappingId), NumberOperators.EQUALS));

        RelationMappingContext mapping = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), RelationMappingContext.class);
        return mapping;
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
        return getAllRelations(module, false, null, null);
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module, boolean isSetupPage, JSONObject pagination, String searchString) throws Exception {

        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        StringBuilder joinCondition = new StringBuilder();
        joinCondition = joinCondition.append(relationFields.get("id").getCompleteColumnName() + " = " + mappingFields.get("relationId").getCompleteColumnName());
        if(isSetupPage) {
            joinCondition.append(" AND ((")
                    .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" = ").append(mappingFields.get("toModuleId").getCompleteColumnName()).append(" AND ")
                    .append(mappingFields.get("position").getCompleteColumnName()).append(" = ").append(RelationMappingContext.Position.LEFT.getIndex()).append(") OR (")
                    .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" != ").append(mappingFields.get("toModuleId").getCompleteColumnName() + ")")
                    .append(")");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(joinCondition.toString())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("name"), searchString, StringOperators.CONTAINS));
        }

        StringBuilder orderBy = new StringBuilder().append(relationFields.get("id").getCompleteColumnName()).append(" DESC");
        builder.orderBy(orderBy.toString());

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page-1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, module.getModuleId());
        return relationRequests;
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule fromModule, FacilioModule toModule) throws Exception {

        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(relationFields.get("id").getCompleteColumnName() + " = " + mappingFields.get("relationId").getCompleteColumnName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), String.valueOf(fromModule.getModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("toModuleId"), String.valueOf(toModule.getModuleId()), NumberOperators.EQUALS));

        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, fromModule.getModuleId());
        return relationRequests;
    }

    /**
     * Method will fetch all the relationships and filtered with parent id which is associated with any relationships.
     *
     * @param module - source module
     * @param parentId - resource id which is associated with relationships
     * @return Map of resource list
     * @throws Exception
     */
    public static Map<RelationRequestContext, List<Long>> getAllRelationsWithReverseResourceList(FacilioModule module, Long parentId) throws Exception {
        List<RelationRequestContext> allRelations = getAllRelations(module);
        Map<RelationRequestContext, List<Long>> relReqResMap = new HashMap<>();

        for (RelationRequestContext relReqContext : allRelations) {
            JSONObject recordsWithRelationship = RelationUtil.getRecordsWithRelationship(relReqContext.getReverseRelationLinkName(), relReqContext.getToModuleName(), parentId, 1, -1);
            if (recordsWithRelationship == null) {
                continue;
            }
            JSONObject data = (JSONObject) recordsWithRelationship.get("data");
            if (data != null) {
                List<LinkedHashMap> resObjList = (List<LinkedHashMap>) data.get(relReqContext.getToModuleName());
                List<Long> resourceList = resObjList.stream().map(lhm -> (Long) lhm.get("id")).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(resourceList)) {
                    relReqResMap.put(relReqContext, resourceList);
                }
            }

        }
        return relReqResMap;
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
        List<Long> sameModuleRelationIds = new ArrayList<>();
        for (RelationContext relation : relations) {
            RelationRequestContext request = new RelationRequestContext();
            fillRelationMetaInfo(request, relation);
            boolean isSameModule = false;
            for (RelationMappingContext mapping : relation.getMappings()) {
                if (mapping.getFromModuleId() != mapping.getToModuleId()) {
                    if (mapping.getFromModuleId() == moduleId) { // this will get mapping for current module
                        fillForwardMappingDatainRequest(request, mapping);
                    } else { // this will fetch reverse mapping
                        fillReverseMappingDataInRequest(request, mapping);
                    }
                }
                else {
                    isSameModule = true;
                    if(sameModuleRelationIds.contains(relation.getId())) {
                        if (mapping.getPositionEnum().equals(RelationMappingContext.Position.LEFT)) {
                            fillReverseMappingDataInRequest(request, mapping);
                        } else {
                            fillForwardMappingDatainRequest(request, mapping);
                        }
                    }
                    else {
                        if (mapping.getPositionEnum().equals(RelationMappingContext.Position.LEFT)) {
                            fillForwardMappingDatainRequest(request, mapping);
                        } else {
                            fillReverseMappingDataInRequest(request, mapping);
                        }
                    }
                }
            }
            if(isSameModule) {
                sameModuleRelationIds.add(relation.getId());
            }
            requests.add(request);
        }

        return requests;
    }

    private static void fillSameModuleRelationRequest(RelationRequestContext request, RelationContext relation, RelationMappingContext forwardMapping, RelationMappingContext reverseMapping) throws Exception{
        fillRelationMetaInfo(request, relation);
        fillForwardMappingDatainRequest(request, forwardMapping);
        fillReverseMappingDataInRequest(request, reverseMapping);
    }

    private static void fillRelationMetaInfo(RelationRequestContext request, RelationContext relation) throws Exception {
        request.setId(relation.getId());
        request.setName(relation.getName());
        request.setDescription(relation.getDescription());
        request.setLinkName(relation.getLinkName());
        request.setRelationModule(relation.getRelationModule());
    }

    private static void fillForwardMappingDatainRequest(RelationRequestContext request, RelationMappingContext mapping) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
    }

    private static void fillReverseMappingDataInRequest(RelationRequestContext request, RelationMappingContext mapping) throws Exception {
        request.setReverseRelationName(mapping.getRelationName());
        request.setReverseRelationLinkName(mapping.getMappingLinkName());
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

    public static Map<String, Object> getSimpleModuleRecordSummary(FacilioModule module, Long recordId,
                                                                         List<FacilioField> selectableFields) throws Exception {
        Map<String, Object> resultProp = null;
        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>();
        selectRecordsBuilder
                .module(module)
                .fetchDeleted()
                .select(selectableFields)
                .andCondition(CriteriaAPI.getIdCondition(recordId, module));

        List<Map<String, Object>> propsList = selectRecordsBuilder.getAsProps();
        if (CollectionUtils.isNotEmpty(propsList)) {
            resultProp = propsList.get(0);
        }
        return resultProp;
    }

    public static void deleteCustomRelation(FacilioModule relationModule, long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(relationModule.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id, relationModule))
                .andCondition(CriteriaAPI.getModuleIdIdCondition(relationModule.getModuleId(), relationModule));

        builder.delete();
    }

    public static boolean isToOneRelationShipType(RelationMappingContext relationMapping) {
        return (relationMapping.getRelationType() == RelationRequestContext.RelationType.ONE_TO_ONE.getIndex()
                || relationMapping.getRelationType() == RelationRequestContext.RelationType.MANY_TO_ONE.getIndex());
    }

}
