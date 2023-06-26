package com.facilio.ns;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.*;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingkpi.context.ReadingKPIContext;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.V3Context;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.ns.factory.NamespaceModuleAndFieldFactory.getNamespaceInclusionModule;

@Log4j
public class NamespaceAPI {

    public static List<Long> getNamespacesByFieldId(Long fieldId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(FieldFactory.getField("id", "DISTINCT Namespace.ID", FieldType.NUMBER)))
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition("Namespace.STATUS", "status", String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition("Namespace_Fields.FIELD_ID", "fieldId", String.valueOf(fieldId), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        LOGGER.debug("fetching namespaces : " + selectBuilder);
        LOGGER.debug("result map : " + maps);
        if (CollectionUtils.isEmpty(maps)) {
            return new ArrayList<>();
        }

        List<Long> nsIds = new ArrayList<>();
        for (Map<String, Object> m : maps) {
            Long id = (Long) m.get("id");
            nsIds.add(id);
        }
        return nsIds;
    }

    public static List<NameSpaceContext> constructNamespaceAndFields(List<Map<String, Object>> maps) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<Long, NameSpaceContext> nsMap = new HashMap<>();

        for (Map<String, Object> m : maps) {

            Long nsId = (Long) m.get("id");
            NameSpaceContext ns = nsMap.get(nsId);
            if (ns == null) {
                ns = FieldUtil.getAsBeanFromMap(m, NameSpaceContext.class);
                nsMap.put(nsId, ns);
                updateWorkflow(ns);
                ns.setIncludedAssetIds(fetchResourceIdsFromNamespaceInclusions(nsId));
            }

            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);
            field.setId((Long) m.get("nsFieldId"));
            if (field.getPrimary() != null && field.getPrimary()) {
                field.setResourceId(null);
            }
            if (field.getNsFieldType() == NsFieldType.RELATED_READING) {
                NamespaceFieldRelated relatedInfo = FieldUtil.getAsBeanFromMap(m, NamespaceFieldRelated.class);
                RelationMappingContext mapping = RelationUtil.getRelationMapping(relatedInfo.getRelMapId());
                relatedInfo.setRelMapContext(mapping);
                if (relatedInfo.getCriteriaId() != null) {
                    relatedInfo.setCriteria(CriteriaAPI.getCriteria(relatedInfo.getCriteriaId()));
                }
                field.setRelatedInfo(relatedInfo);
            }
            ns.addField(field);
            FacilioField readingField = modBean.getField(field.getFieldId());
            field.setVarDataType(getScriptDataType(readingField));
            field.setField(readingField);
        }
        return new ArrayList<>(nsMap.values());
    }

    private static String getScriptDataType(FacilioField readingField) {
        String typeAsString = readingField.getDataTypeEnum().getTypeAsString();
        List<String> scriptDataType = Arrays.asList("Number", "String", "Map", "List", "Boolean");
        return scriptDataType.contains(typeAsString) ? typeAsString : "Number";
    }

    public static NameSpaceContext getNameSpaceByRuleId(Long ruleId, NSType type) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(Collections.singletonList(FieldFactory.getIdField(NamespaceModuleAndFieldFactory.getNamespaceModule())))
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName() + ".PARENT_RULE_ID", "parentRuleId", ruleId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(type.getIndex()), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            for (Map<String, Object> map : maps) {
                Long nsId = (Long) map.get("id");
                NamespaceBean nsBean = Constants.getNsBean();
                return new NameSpaceContext(nsBean.getNamespace(nsId));
            }
        }

        throw new Exception("Invalid ruleId, No Namespace Found!");
    }

    private static void updateWorkflow(NameSpaceContext namespaceContext) throws Exception {
        if (namespaceContext.getWorkflowId() != null) { //TODO: should be remove this check
            WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(namespaceContext.getWorkflowId());
            namespaceContext.setWorkflowContext(workflowContext);
        }
    }

    public static List getNsIndexFromList(List<NSType> nsList) {
        List<Long> list = new ArrayList<>();
        for (NSType nsType : nsList) {
            list.add(Long.valueOf(nsType.getIndex()));
        }
        return list;
    }

    public static List<Long> getMatchedResources(NameSpaceContext ns, V3Context fddContext) throws Exception {
        Set<Long> resourceIds = new HashSet<>();

        List<Long> inclusions = CollectionUtils.isEmpty(ns.getIncludedAssetIds()) ? fetchResourceIdsFromNamespaceInclusions(ns.getId()) : ns.getIncludedAssetIds();
        if (CollectionUtils.isNotEmpty(inclusions)) {
            return inclusions;
        }
        if (fddContext != null) {
            Long assetCategoryId;
            if (fddContext instanceof ReadingKPIContext) {
                assetCategoryId = ((ReadingKPIContext) fddContext).getAssetCategory().getId();
            } else {
                assetCategoryId = ((NewReadingRuleContext) fddContext).getAssetCategory().getId();
            }
            List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(assetCategoryId, null, fddContext.getSiteId());
            resourceIds.addAll(assets.stream().map((assetContext) -> assetContext.getId()).collect(Collectors.toList()));

        }
        return new ArrayList<>(resourceIds);
    }

    public static List<Long> getMatchedResources(NameSpaceContext ns, Long categoryId) throws Exception {
        Set<Long> resourceIds = new HashSet<>();

        List<Long> inclusions = fetchResourceIdsFromNamespaceInclusions(ns.getId());
        if (CollectionUtils.isNotEmpty(inclusions)) {
            return inclusions;
        }

        List<AssetContext> assets = AssetsAPI.getAssetListOfCategory(categoryId);
        if (CollectionUtils.isNotEmpty(assets)) {
            resourceIds.addAll(assets.stream().map((assetContext) -> assetContext.getId()).collect(Collectors.toList()));
            return new ArrayList<>(resourceIds);
        }

        return new ArrayList<>();
    }

    public static List<Long> fetchMatchedResourceIds(Long nsId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName() + ".ID", "id", nsId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isEmpty(maps)) {
            return new ArrayList<>();
        }

        List<Long> resourceIds = new ArrayList<>();
        for (Map<String, Object> m : maps) {
            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);
            if (field.getResourceId() != null) {
                resourceIds.add(field.getResourceId());
            }
        }

        return resourceIds;
    }

    public static void deleteExistingInclusionRecords(NameSpaceContext ns) throws Exception {
        if (ns.getId() != null) {
            GenericDeleteRecordBuilder del = new GenericDeleteRecordBuilder()
                    .table(NamespaceModuleAndFieldFactory.getNamespaceInclusionModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("NAMESPACE_ID", "namespaceId", String.valueOf(ns.getId()), NumberOperators.EQUALS));
            int records = del.delete();
            LOGGER.info("Successfully deleted " + records + " records.");
        }
    }

    private static List<Map<String, Object>> getInclusionList(long nsId, List<Long> resources) {
        long orgId = AccountUtil.getCurrentOrg().getId();
        List<Map<String, Object>> props = new ArrayList<>();
        for (Long resourceId : resources) {
            Map<String, Object> prop = new HashMap<>();
            prop.put("orgId", orgId);
            prop.put("namespaceId", nsId);
            prop.put("resourceId", resourceId);

            props.add(prop);
        }
        return props;
    }

    public static void addInclusions(NameSpaceContext ns) throws Exception {
        List<Long> assetIds = ns.getIncludedAssetIds();
        if (CollectionUtils.isNotEmpty(assetIds)) {
            List<AssetContext> assetInfo = AssetsAPI.getAssetInfo(assetIds);

            throwIfAssetNotFound(assetIds, assetInfo);
            List<Long> newAssetIds = assetInfo.stream().map(info -> info.getId()).collect(Collectors.toList());

            deleteExistingInclusionRecords(ns);

            List<Map<String, Object>> inclusionList = getInclusionList(ns.getId(), newAssetIds);

            new GenericInsertRecordBuilder()
                    .fields(NamespaceModuleAndFieldFactory.getNamespaceInclusionFields())
                    .table(NamespaceModuleAndFieldFactory.getNamespaceInclusionModule().getTableName())
                    .addRecords(inclusionList)
                    .save();
        }
    }


    public static List<Long> fetchResourceIdsFromNamespaceInclusions(Long nameSpaceId) throws Exception {
        List<FacilioField> fields = NamespaceModuleAndFieldFactory.getNamespaceInclusionFields();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(getNamespaceInclusionModule().getTableName())
                .select(Arrays.asList(fieldMap.get("resourceId")))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("namespaceId"), nameSpaceId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        List<Long> resourceIds = new ArrayList<>();
        for (Map<String, Object> m : maps) {
            resourceIds.add((Long) m.get("resourceId"));
        }

        return resourceIds;
    }


    private static void throwIfAssetNotFound(List<Long> assetIds, List<AssetContext> assetInfo) {
        if (assetIds.size() != assetInfo.size()) {
            for (Long asset : assetIds) {
                boolean present = assetInfo.stream().filter(assetCtx -> assetCtx.getId() == asset).findAny().isPresent();
                if (!present) {
                    throw new RuntimeException("Asset (" + asset + ") is not found");
                }
            }
        }
    }

    public static List<Long> getFieldIdsForNamespace(Long nsId) throws Exception {
        return getFieldIdsForNamespace(Collections.singletonList(nsId));
    }

    public static List<Long> getFieldIdsForNamespace(List<Long> nsIds) throws Exception {
        List<FacilioField> fields = NamespaceModuleAndFieldFactory.getNamespaceFieldFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .select(Collections.singleton(fieldsMap.get("fieldId")))
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("nsId"), nsIds, NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();
        List<Long> fieldIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                fieldIds.add((Long) prop.get("fieldId"));
            }
        }
        return fieldIds;
    }

    public static List<Long> getNsIdForRuleId(Long parentRuleId, List<NSType> nsTypeList) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(Collections.singleton(FieldFactory.getIdField("id", "ID", NamespaceModuleAndFieldFactory.getNamespaceModule())))
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", parentRuleId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getConditionFromList("TYPE", "type", getNsIndexFromList(nsTypeList), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        List<Long> nsIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                nsIds.add((Long) prop.get("id"));
            }
        }
        return nsIds;
    }

    public static List<Long> getNsFieldIdsForRuleId(Long parentRuleId, List<NSType> nsTypeList) throws Exception {
        List<Long> nsIds = getNsIdForRuleId(parentRuleId, nsTypeList);
        return CollectionUtils.isNotEmpty(nsIds) ? NamespaceAPI.getFieldIdsForNamespace(nsIds) : new ArrayList<>();
    }
}
