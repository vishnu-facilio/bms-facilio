package com.facilio.ns;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.assetcatergoryfeature.util.AssetCategoryFeatureStatusUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.NamespaceBean;
import com.facilio.connected.CommonConnectedUtil;
import com.facilio.connected.FacilioDataProcessing;
import com.facilio.connected.IConnectedRule;
import com.facilio.connected.ResourceType;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
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

import java.util.*;

import static com.facilio.ns.factory.NamespaceModuleAndFieldFactory.getNamespaceInclusionModule;

@Log4j
public class NamespaceAPI {

    public static List<Long> getNamespacesByFieldId(Long fieldId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(Collections.singleton(FieldFactory.getField("id", "DISTINCT Namespace.ID", FieldType.NUMBER)))
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
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
            setNsStatusNdResourceType(ns);
            FacilioField readingField = modBean.getField(field.getFieldId());
            field.setVarDataType(getScriptDataType(readingField));
            field.setField(readingField);
        }
        return new ArrayList<>(nsMap.values());
    }

    @FacilioDataProcessing
    private static void setNsStatusNdResourceType(NameSpaceContext ns) throws Exception {

        Boolean categoryLevelStatus = AssetCategoryFeatureStatusUtil.validateFldWithNs(ns.getCategoryId(), ns.getTypeEnum());
        ns.setStatus(ns.getStatus() && categoryLevelStatus);
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

    public static List<Long> getMatchedResources(NameSpaceContext ns, V3Context ruleCtx) throws Exception {
        List<Long> resourceIds = new ArrayList<>();

        List<Long> inclusions = CollectionUtils.isEmpty(ns.getIncludedAssetIds()) ? fetchResourceIdsFromNamespaceInclusions(ns.getId()) : ns.getIncludedAssetIds();
        if (CollectionUtils.isNotEmpty(inclusions)) {
            return inclusions;
        }
        if (ruleCtx != null) {
            Long categoryId;
            if (ruleCtx instanceof ReadingKPIContext) {
                categoryId = ((ReadingKPIContext) ruleCtx).getCategory().fetchId();
            } else {
                categoryId = ((NewReadingRuleContext) ruleCtx).getCategory().fetchId();
            }
            resourceIds = CommonConnectedUtil.getResourceIdsBasedOnCategory(((IConnectedRule) ruleCtx).getCategory().getResType(), categoryId);
            //TODO:remove this method after testing
        }
        return resourceIds;
    }

    public static List<Long> getMatchedResources(NameSpaceContext ns) throws Exception {

        if (CollectionUtils.isNotEmpty(ns.getIncludedAssetIds())) {
            return ns.getIncludedAssetIds();
        }
        List<Long> inclusions = fetchResourceIdsFromNamespaceInclusions(ns.getId());
        if (CollectionUtils.isNotEmpty(inclusions)) {
            return inclusions;
        }

        return CommonConnectedUtil.getResourceIdsBasedOnCategory(ResourceType.valueOf(ns.getResourceType()), ns.getCategoryId());
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
            deleteExistingInclusionRecords(ns);

            List<Map<String, Object>> inclusionList = getInclusionList(ns.getId(), assetIds);

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

    public static Optional<Long> getNsIdForRuleId(Long parentRuleId, NSType nsType) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(Collections.singleton(FieldFactory.getIdField("id", "ID", NamespaceModuleAndFieldFactory.getNamespaceModule())))
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", parentRuleId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getConditionFromList("TYPE", "type", Collections.singletonList(Long.valueOf(nsType.getIndex())), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();

        return props.stream().map(prop -> (Long) prop.get("id")).findFirst();
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

    public static List<Long> getNsFieldIdsForRuleId(List<Long> nsIds, List<NSType> nsTypeList) throws Exception {
        return CollectionUtils.isNotEmpty(nsIds) ? NamespaceAPI.getFieldIdsForNamespace(nsIds) : new ArrayList<>();
    }

    public static List<Long> getNsIdForCategoryId(Long parentRuleId, List<NSType> nsTypeList) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(Collections.singleton(FieldFactory.getIdField("id", "ID", NamespaceModuleAndFieldFactory.getNamespaceModule())))
                .andCondition(CriteriaAPI.getCondition("CATEGORY_ID", "categoryId", parentRuleId.toString(), NumberOperators.EQUALS))
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
}
