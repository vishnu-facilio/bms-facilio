package com.facilio.ns;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class NamespaceAPI {

    public static List<NameSpaceContext> getNamespacesByFieldId(Long fieldId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID");
        selectBuilder.andCondition(CriteriaAPI.getCondition("Namespace.STATUS", "status", String.valueOf(true), BooleanOperators.IS));

        selectBuilder.andCustomWhere("Namespace.ID IN (SELECT NAMESPACE_ID FROM Namespace_Fields WHERE FIELD_ID=? )", fieldId);

        List<Map<String, Object>> maps = selectBuilder.get();
        LOGGER.debug("fetching namespaces : " + selectBuilder);
        LOGGER.debug("result map : " + maps);
        if (CollectionUtils.isEmpty(maps)) {
            return new ArrayList<>();
        }

        return constructNamespaceAndFields(maps);
    }

    private static List<NameSpaceContext> constructNamespaceAndFields(List<Map<String, Object>> maps) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<Long, NameSpaceContext> nsMap = new HashMap<>();

        for (Map<String, Object> m : maps) {

            Long nsId = (Long) m.get("id");
            NameSpaceContext ns = nsMap.get(nsId);
            if (ns == null) {
                ns = FieldUtil.getAsBeanFromMap(m, NameSpaceContext.class);
                nsMap.put(nsId, ns);
                updateWorkflow(ns);
            }

            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);
            if (field.getPrimary() != null && field.getPrimary()) {
                field.setResourceId(null);
                if (alreadyExistsInList(ns.getFields(), field)) {
                    continue;
                }
            }
            if(field.getRelMapId()!=null){
                RelationMappingContext mapping = RelationUtil.getRelationMapping(field.getRelMapId());
                field.setRelMapContext(mapping);
            }
            ns.addField(field);
            field.setField(modBean.getField(field.getFieldId()));
        }
        return nsMap.values().stream().collect(Collectors.toList());
    }

    public static NameSpaceContext getNameSpaceByRuleId(Long ruleId, NSType type) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName() + ".PARENT_RULE_ID", "parentRuleId", ruleId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(type.getIndex()), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
            List<NameSpaceContext> nameSpaceContexts = constructNamespaceAndFields(maps);
            if (nameSpaceContexts.size() > 0) {
                return nameSpaceContexts.get(0);
            }
        }

        return null;
    }

    private static void updateWorkflow(NameSpaceContext namespaceContext) throws Exception {
        if(namespaceContext.getWorkflowId() != null) { //TODO: should be remove this check
            WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(namespaceContext.getWorkflowId());
            namespaceContext.setWorkflowContext(workflowContext);
        }
    }

    public static int updateNsStatus(Long ruleId,boolean status, NSType type) throws Exception{
        NameSpaceContext namespaceContext=getNameSpaceByRuleId(ruleId, type);
        namespaceContext.setStatus(status);
        Map<String,Object> namespaceMap =FieldUtil.getAsProperties(namespaceContext);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields())
                .andCondition(CriteriaAPI.getIdCondition(namespaceContext.getId(),NamespaceModuleAndFieldFactory.getNamespaceModule()))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(type.getIndex()), NumberOperators.EQUALS));
        int cnt = updateBuilder.update(namespaceMap);
        return cnt;
    }

    private static boolean alreadyExistsInList(List<NameSpaceField> fields, NameSpaceField field) {
        for (NameSpaceField fld : fields) {
            if (fld.getPrimary() && fld.getFieldId().equals(field.getFieldId()) &&
                    fld.getAggregationType() == field.getAggregationType()) {
                return true;
            }
        }
        return false;
    }

    public static List<Long> getMatchedResources(NameSpaceContext ns) {
        List<NameSpaceField> fields = ns.getFields();
        List<Long> resources = new ArrayList<>();
        if (CollectionUtils.isEmpty(fields)) {
            return new ArrayList<>();
        }
        for (NameSpaceField fld : fields) {
            if (fld.getResourceId() != null && fld.getResourceId() > 0 && (fld.getPrimary() != null && fld.getPrimary())) {
                resources.add(fld.getResourceId());
            }
        }
        return resources;
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
            if (field.getPrimary() != null && field.getPrimary() && field.getResourceId() != null) {
                resourceIds.add(field.getResourceId());
            }
        }
        return resourceIds;
    }

    public static void deleteNameSpacesFromRuleId(Long parentId, NSType typ) throws Exception {
        GenericDeleteRecordBuilder del = new GenericDeleteRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", parentId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(typ.getIndex()), NumberOperators.EQUALS));
        del.delete();
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

    public static Long addNamespace(NameSpaceContext ns) throws Exception {

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields());
        long id = insertBuilder.insert(FieldUtil.getAsProperties(ns));
        ns.setId(id);

        addInclusions(ns);

        return id;
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
}
