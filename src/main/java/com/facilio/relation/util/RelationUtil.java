package com.facilio.relation.util;

import com.facilio.beans.ModuleBean;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.context.ImportRelationshipRequestContext;
import com.facilio.relation.context.*;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
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
        return getRecordsWithRelationship(mappingData.getMappingLinkName(), mappingData.getFromModule().getName(), parentId, page, perPage, null);
    }

    public static JSONObject getRecordsWithReverseRelationship(Long relationMappingId, Long parentId, int page, int perPage) throws Exception {
        RelationMappingContext mappingData = getRelationMapping(relationMappingId);
        RelationContext relation = RelationUtil.getRelation(mappingData.getRelationId(), true);
        if (relation == null) {
            throw new IllegalArgumentException("Invalid Relation");
        }
        String relationMappingName = null;
        for (RelationMappingContext mapping : relation.getMappings()) {
            if (!mapping.getMappingLinkName().equals(mappingData.getMappingLinkName())) {
                relationMappingName = mapping.getMappingLinkName();
                break;
            }
        }

        return getRecordsWithRelationship(relationMappingName, mappingData.getToModule().getName(), parentId, page, perPage, null);
    }

    public static JSONObject getRecordsWithRelationship(String relationLinkName, String moduleName, Long parentId, int page, int perPage) throws Exception {
        return getRecordsWithRelationship(relationLinkName, moduleName, parentId, page, perPage, null);
    }

    public static JSONObject getRecordsWithRelationship(String relationLinkName, String moduleName, Long parentId, int page, int perPage, Criteria serverCriteria) throws Exception {

        if (serverCriteria == null) {
            serverCriteria = new Criteria();
        }
        serverCriteria.addAndCondition(CriteriaAPI.getCondition(relationLinkName, String.valueOf(parentId), RelationshipOperator.CONTAINS_RELATION));

        return fetchRelatedData(moduleName, page, perPage, serverCriteria);
    }

    private static JSONObject fetchRelatedData(String moduleName, int page, int perPage, Criteria serverCriteria) throws Exception {
        JSONObject relatedData = new JSONObject();
        page = (page < 1) ? 1 : page;
        perPage = (perPage < 1) ? WorkflowV2Util.SELECT_DEFAULT_LIMIT : perPage;
        FacilioContext listContext = V3Util.fetchList(moduleName, true, null, null, false, null, null,
                null, null, page, perPage, false, null, serverCriteria,null);

        JSONObject recordJSON = Constants.getJsonRecordMap(listContext);
        relatedData.put(FacilioConstants.ContextNames.DATA, recordJSON);

        if (listContext.containsKey(FacilioConstants.ContextNames.META)) {
            relatedData.put(FacilioConstants.ContextNames.META, (JSONObject) listContext.get(FacilioConstants.ContextNames.META));
        }

        return relatedData;
    }

    public static JSONObject getRecordsWithRelationship(Long relationMappingId, Long parentId, int page, int perPage, Criteria userCriteria) throws Exception {
        RelationMappingContext relationMapping = getRelationMapping(relationMappingId);
        RelationContext relationContext = relationMapping.getRelationContext();
        if (relationContext.isVirtual()) {
            return getVirtualRelationData(relationMapping.getMappingLinkName(), parentId, page, perPage, userCriteria);
        }
        return getRecordsWithRelationship(relationMapping.getMappingLinkName(), relationMapping.getFromModule().getName(), parentId, page, perPage, userCriteria);
    }

    private static JSONObject getVirtualRelationData(String relationLinkName, Long parentId, int page, int perPage, Criteria serverCriteria) throws Exception {
        RelationMappingContext relationMapping = RelationUtil.getRelationMapping(relationLinkName);

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
            relatedIds = getRelatedIds(relations, parentId, rightIdField, leftIdField, moduleIdField, page, perPage);
        } else {
            mapping = lastRelation.getMapping2();
            relatedIds = getRelatedIds(relations, parentId, leftIdField, rightIdField, moduleIdField, page, perPage);
        }

        FacilioModule toModule = mapping.getToModule();

        String relationModuleName = toModule.getName();

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(relatedIds, Constants.getModBean().getModule(relationModuleName)));
        serverCriteria = serverCriteria == null || serverCriteria.isEmpty() ? new Criteria() : serverCriteria;
        serverCriteria.andCriteria(criteria);

        return fetchRelatedData(relationModuleName, page, perPage, serverCriteria);
    }

    public static List<Long> getRelatedIds(List<RelationContext> relations, long recordId, FacilioField rightIdField, FacilioField leftIdField, FacilioField moduleIdField, int page, int perPage) throws Exception {
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

    private static String constructBuilder(List<RelationContext> relations, int i, long recordId, FacilioField rightIdField, FacilioField leftIdField, FacilioField moduleIdField) {
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

    public static List<Long> getVirtualRelationIds(long parentId, String orderBy) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getVirtualRelationshipConfigModule().getTableName())
                .select(FieldFactory.getVirtualRelationshipConfigFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", String.valueOf(parentId), NumberOperators.EQUALS))
                .orderBy("SEQUENCE_NUMBER "+ orderBy);
        List<Map<String, Object>> maps = selectRecordBuilder.get();
        return maps.stream().map(record -> (Long) record.get("relationId")).collect(Collectors.toList());
    }

    public static RelationMappingContext getRelationMapping(Long mappingId) throws Exception {
        FacilioModule module = ModuleFactory.getRelationMappingModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), String.valueOf(mappingId), NumberOperators.EQUALS));

        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), RelationMappingContext.class);
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
        return getAllRelations(module, false, null, null, false, null, RelationContext.RelationCategory.NORMAL, false);
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module, boolean isSetupPage, JSONObject pagination, String searchString, RelationContext.RelationCategory relationCategory, boolean fromBuilder) throws Exception {
        return getAllRelations(module, isSetupPage, pagination, searchString, false, null, relationCategory, fromBuilder);
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module, Criteria criteria) throws Exception {
        return getAllRelations(module, false, null, null, false, criteria, null, false);
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module, boolean isSetupPage, JSONObject pagination, String searchString, boolean includeHiddenRelations, Criteria criteria, RelationContext.RelationCategory relationCategory) throws Exception {
        return getAllRelations(module, isSetupPage, pagination, searchString, includeHiddenRelations, criteria, relationCategory, false);
    }

    public static List<RelationRequestContext> getAllRelations(FacilioModule module, boolean isSetupPage, JSONObject pagination, String searchString, boolean includeHiddenRelations, Criteria criteria, RelationContext.RelationCategory relationCategory, boolean fromBuilder) throws Exception {

        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        long moduleId = module.getModuleId();
        List<Long> moduleIds = new ArrayList<>(Collections.singleton(moduleId));

        StringBuilder joinCondition = new StringBuilder();
        joinCondition = joinCondition.append(relationFields.get("id").getCompleteColumnName() + " = " + mappingFields.get("relationId").getCompleteColumnName());
        if (isSetupPage) {
            joinCondition.append(" AND ((")
                    .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" = ").append(mappingFields.get("toModuleId").getCompleteColumnName()).append(" AND ")
                    .append(mappingFields.get("position").getCompleteColumnName()).append(" = ").append(RelationMappingContext.Position.LEFT.getIndex()).append(") OR (")
                    .append(mappingFields.get("fromModuleId").getCompleteColumnName()).append(" != ").append(mappingFields.get("toModuleId").getCompleteColumnName() + ")")
                    .append(")");
        }

        if (FacilioConstants.Relationship.CHILD_MODULE_FETCH_RELATION.contains(module.getName())) {
            List<FacilioModule> childModules = Constants.getModBean().getChildModules(module, null, null, false);
            List<Long> childModIds = childModules.stream().map(FacilioModule::getModuleId).collect(Collectors.toList());
            moduleIds.addAll(childModIds);
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(joinCondition.toString())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), StringUtils.join(moduleIds, ','), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("name"), searchString, StringOperators.CONTAINS));
        }

        if (!includeHiddenRelations) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("relationCategory"), String.valueOf(RelationContext.RelationCategory.HIDDEN.getIndex()), NumberOperators.NOT_EQUALS));
        }

        if (fromBuilder) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("isVirtual"), String.valueOf(true), NumberOperators.NOT_EQUALS));
        }

        addRelationCategoryCriteriaToBuilder(relationCategory, builder, relationFields.get("relationCategory"));

        if (criteria != null && !criteria.isEmpty()) {
            builder.andCriteria(criteria);
        }

        StringBuilder orderBy = new StringBuilder().append(relationFields.get("id").getCompleteColumnName()).append(" DESC");
        builder.orderBy(orderBy.toString());

        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }

            builder.offset(offset);
            builder.limit(perPage);
        }

        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, moduleId);
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
     * @param module   - source module
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
    public static RelationRequestContext viewRelationRequest(RelationContext relation, long moduleId) throws Exception {
        RelationRequestContext relationRequestContext = new RelationRequestContext();
        List<Long> sameModuleRelationIds = new ArrayList<>();

        for (RelationMappingContext mapping : relation.getMappings()) {
            fillRelationMetaInfo(relationRequestContext, relation);
            if (isSameModule(mapping, moduleId)) {
                handleSameModuleMapping(relationRequestContext, mapping, sameModuleRelationIds,false);
            } else {
                if (mapping.getFromModuleId() == moduleId) {
                    fillForwardMappingDatainRequest(relationRequestContext, mapping);
                } else {
                    fillReverseMappingDataInRequest(relationRequestContext, mapping);
                }
            }
        }

        return relationRequestContext;
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
                if (isSameModule(mapping, moduleId)) {
                    boolean isChildModule = isChildModule(moduleId, mapping.getFromModuleId());
                    isSameModule = handleSameModuleMapping(request, mapping, sameModuleRelationIds, isChildModule);
                } else {
                    handleDifferentModuleMapping(request, mapping, moduleId);
                }
            }

            if (isSameModule) {
                sameModuleRelationIds.add(relation.getId());
            }

            requests.add(request);
        }

        return requests;
    }

    private static boolean isSameModule(RelationMappingContext mapping, long moduleId) throws Exception {
        FacilioModule module = Constants.getModBean().getModule(moduleId);
        return mapping.getFromModuleId() == mapping.getToModuleId() || (hasSameParentModule(mapping.getFromModuleId(), mapping.getToModuleId()) && FacilioConstants.Relationship.CHILD_MODULE_FETCH_RELATION.contains(module.getName()));
    }

    private static boolean handleSameModuleMapping(RelationRequestContext request, RelationMappingContext mapping, List<Long> sameModuleRelationIds, boolean isChildModule) throws Exception {

        if (sameModuleRelationIds.contains(mapping.getRelationId()) || isChildModule) {
            handleMappingForSameModule(request, mapping);
        } else {
            handleMappingForDifferentModule(request, mapping);
        }

        return true;
    }

    private static void handleDifferentModuleMapping(RelationRequestContext request, RelationMappingContext mapping, long moduleId) throws Exception {
        if (mapping.getFromModuleId() == moduleId || isChildModule(mapping.getFromModuleId(), moduleId)) {
            fillForwardMappingDatainRequest(request, mapping);
        } else {
            fillReverseMappingDataInRequest(request, mapping);
        }
    }

    private static void handleMappingForSameModule(RelationRequestContext request, RelationMappingContext mapping) throws Exception {
        if (mapping.getPositionEnum().equals(RelationMappingContext.Position.LEFT)) {
            fillReverseMappingDataInRequest(request, mapping);
        } else {
            fillForwardMappingDatainRequest(request, mapping);
        }
    }

    private static void handleMappingForDifferentModule(RelationRequestContext request, RelationMappingContext mapping) throws Exception {
        if (mapping.getPositionEnum().equals(RelationMappingContext.Position.LEFT)) {
            fillForwardMappingDatainRequest(request, mapping);
        } else {
            fillReverseMappingDataInRequest(request, mapping);
        }
    }

    private static boolean isChildModule(long childModuleId, long parentModuleId) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule mod = modBean.getModule(childModuleId);
        FacilioModule parentModule = modBean.getModule(parentModuleId);
        return (mod.getExtendModule() != null && mod.getExtendModule().getModuleId() == parentModuleId) && FacilioConstants.Relationship.CHILD_MODULE_FETCH_RELATION.contains(parentModule.getName());
    }

    private static boolean hasSameParentModule(long moduleId1, long moduleId2) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule module1 = modBean.getModule(moduleId1).getParentModule();
        FacilioModule module2 = modBean.getModule(moduleId2).getParentModule();
        return (module1.getModuleId() == module2.getModuleId());
    }

    public static void setVirtualRelationData(RelationContext relation, RelationRequestContext request) throws Exception {
        RelationMappingContext.Position position = RelationMappingContext.Position.valueOf(request.getPosition());
        String orderBy = Objects.equals(position, RelationMappingContext.Position.LEFT) ? "ASC" : "DESC";
        List<Long> virtualRelationIds = getVirtualRelationIds(relation.getId(), orderBy);

        String relationOrderBy = "FIELD(ID, " + StringUtils.join(virtualRelationIds, ',') + ")";
        List<RelationContext> relations = RelationUtil.getRelations(virtualRelationIds, true, relationOrderBy);

        List<VirtualRelationConfigContext> virtualRelationConfigList = getVirtualRelationConfigContexts(relations, position);
        request.setVirtualRelationConfig(virtualRelationConfigList);
        request.setVirtualRelationIds(virtualRelationIds);
    }

    private static List<VirtualRelationConfigContext> getVirtualRelationConfigContexts(List<RelationContext> relations, RelationMappingContext.Position position) throws Exception {
        List<VirtualRelationConfigContext> virtualRelationConfigList = new ArrayList<>();
        for (RelationContext relationContext : relations) {
            VirtualRelationConfigContext virtualRelationConfigContext = new VirtualRelationConfigContext();
            RelationMappingContext mapping;
            if (Objects.equals(position, RelationMappingContext.Position.LEFT)) {
                mapping = relationContext.getMapping1();
            } else {
                mapping = relationContext.getMapping2();
            }
            virtualRelationConfigContext.setRelationId(relationContext.getId());
            virtualRelationConfigContext.setFromModule(mapping.getFromModule());
            virtualRelationConfigContext.setToModule(mapping.getToModule());
            virtualRelationConfigContext.setRelationList(Collections.singletonList(getRelation(relationContext.getId(), true)));
            virtualRelationConfigList.add(virtualRelationConfigContext);
        }
        return virtualRelationConfigList;
    }

    private static void fillSameModuleRelationRequest(RelationRequestContext request, RelationContext relation, RelationMappingContext forwardMapping, RelationMappingContext reverseMapping) throws Exception {
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
        request.setRelationCategory(relation.getRelationCategory());
        request.setIsVirtual(relation.isVirtual());
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
        request.setRelMappingId(mapping.getId());
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
        relationContext.setIsCustom(relationRequest.isCustom());
        relationContext.setIsVirtual(relationRequest.isVirtual());

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

    public static List<Long> getAllCustomRelationsForRecId(RelationMappingContext relationMapping, long recordId) throws Exception {
        RelationContext relationContext = RelationUtil.getRelation(relationMapping.getRelationId(), Boolean.FALSE);
        RelationMappingContext.Position reversePosition = relationMapping.getReversePosition();

        ModuleBean moduleBean = Constants.getModBean();
        FacilioModule module = moduleBean.getModule(relationContext.getRelationModuleId());

        V3Config v3Config = ChainUtil.getV3Config(module.getName());
        Class beanClass = ChainUtil.getBeanClass(v3Config, module);
        List<FacilioField> relationFields = moduleBean.getAllFields(module.getName());

        SelectRecordsBuilder<RelationDataContext> selectRecordBuilder = new SelectRecordsBuilder()
                .module(module)
                .beanClass(beanClass)
                .select(relationFields)
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(relationContext.getRelationModuleId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(reversePosition.getColumnName(), reversePosition.getFieldName(), String.valueOf(recordId), NumberOperators.EQUALS));

        List<RelationDataContext> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            List<Long> recordIds = new ArrayList<>();
            for (RelationDataContext record : props) {
                ModuleBaseWithCustomFields moduleBaseWithCustomFields = reversePosition.equals(RelationMappingContext.Position.LEFT) ? record.getRight() : record.getLeft();
                recordIds.add(moduleBaseWithCustomFields.getId());
            }
            return recordIds;
        }
        return new ArrayList<>();
    }

    public static void addRelationCategoryCriteriaToBuilder(RelationContext.RelationCategory relationCategory, GenericSelectRecordBuilder builder, FacilioField relationCategoryField) {
        if (relationCategory != null) {
            Criteria relationCategoryCriteria = new Criteria();
            if (relationCategory.equals(RelationContext.RelationCategory.NORMAL)) {
                relationCategoryCriteria.addAndCondition(CriteriaAPI.getCondition(relationCategoryField,
                        String.valueOf(RelationContext.RelationCategory.NORMAL.getIndex()), NumberOperators.EQUALS));
                relationCategoryCriteria
                        .addOrCondition(CriteriaAPI.getCondition(relationCategoryField, CommonOperators.IS_EMPTY));
                builder.andCriteria(relationCategoryCriteria);
            } else {
                relationCategoryCriteria.addAndCondition(CriteriaAPI.getCondition(relationCategoryField,
                        String.valueOf(relationCategory.getIndex()), NumberOperators.EQUALS));
                builder.andCriteria(relationCategoryCriteria);
            }
        }
    }

    public static Map<Long, RelationRequestContext> getAllRelationsForRelMappingIds(FacilioModule module, List<Long> relationMappingIds) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(relationMappingIds, ModuleFactory.getRelationMappingModule()));
        List<RelationRequestContext> relationRequests = getAllRelations(module, criteria);

        Map<Long, RelationRequestContext> relationRequestContextMap = new HashMap<>();
        for (RelationRequestContext relationRequest : relationRequests) {
            relationRequestContextMap.put(relationRequest.getRelMappingId(), relationRequest);
        }

        return relationRequestContextMap;
    }

    public static List<RelationMappingContext> getAllRelationMappings(FacilioModule module) throws Exception{
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationMappingModule().getTableName())
                .select(FieldFactory.getRelationMappingFields())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<RelationMappingContext> mappingList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationMappingContext.class);
        return mappingList;
    }
    public static List<ImportRelationshipRequestContext> getAllRelationshipRequestForImport(FacilioModule module) throws Exception{
        List<ImportRelationshipRequestContext> requestContextList = new ArrayList<>();
        List<RelationMappingContext> relMappings = getAllRelationMappings(module);

        if(CollectionUtils.isEmpty(relMappings)){
            return null;
        }
        Map<Long,List<RelationMappingContext>> relationIdVsRelMap = relMappings.stream().collect(Collectors.groupingBy(RelationMappingContext::getRelationId));

        List<RelationContext> relations = getRelations(relationIdVsRelMap.keySet(),false);

        for(RelationContext relationContext : relations){
            ImportRelationshipRequestContext importRelationRequest = new ImportRelationshipRequestContext();
            importRelationRequest.setId(relationContext.getId());
            importRelationRequest.setName(relationContext.getName());

            List<RelationMappingContext> importRequestMappings = relationIdVsRelMap.get(relationContext.getId());
            importRelationRequest.setMappings(importRequestMappings);
            requestContextList.add(importRelationRequest);

        }
        return requestContextList;
    }


    public static List<RelationContext> getRelations(Collection<Long> ids ,boolean fetchRelations) throws Exception {
        return getRelations(ids, fetchRelations, null);
    }

    public static List<RelationContext> getRelations(Collection<Long> ids , boolean fetchRelations, String orderBy) throws Exception {
        FacilioModule module = ModuleFactory.getRelationModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getIdCondition(ids,module));

        if (StringUtils.isNotEmpty(orderBy)) {
            builder.orderBy(orderBy);
        }

        List<RelationContext> relations = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        if (relations != null) {
            if (fetchRelations) {
                fillRelation(relations);
            }
        }
        return relations;
    }
    
    public static List<RelationRequestContext> getAllRelationships(Long fromModuleId, Long toModuleId, int relatioinType, int relationCategory, String searchString) throws Exception {

        Map<String, FacilioField> relationFields = FieldFactory.getAsMap(FieldFactory.getRelationFields());
        Map<String, FacilioField> mappingFields = FieldFactory.getAsMap(FieldFactory.getRelationMappingFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getRelationModule().getTableName())
                .innerJoin(ModuleFactory.getRelationMappingModule().getTableName())
                .on(relationFields.get("id").getCompleteColumnName() + " = " + mappingFields.get("relationId").getCompleteColumnName())
                .select(FieldFactory.getRelationFields())
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("fromModuleId"), String.valueOf(fromModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("toModuleId"), String.valueOf(toModuleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(mappingFields.get("relationType"), String.valueOf(relatioinType), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(relationFields.get("relationCategory"), String.valueOf(relationCategory), NumberOperators.EQUALS));

        if (StringUtils.isNotEmpty(searchString)) {
            builder.andCondition(CriteriaAPI.getCondition(relationFields.get("name"), searchString, StringOperators.CONTAINS));
        }

        List<RelationContext> relationList = FieldUtil.getAsBeanListFromMapList(builder.get(), RelationContext.class);
        RelationUtil.fillRelation(relationList);
        List<RelationRequestContext> relationRequests = RelationUtil.convertToRelationRequest(relationList, fromModuleId);
        return relationRequests;
    }

}
