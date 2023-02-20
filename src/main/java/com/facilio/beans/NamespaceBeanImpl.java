package com.facilio.beans;

import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceCacheContext;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.ns.NamespaceAPI.constructNamespaceAndFields;

@Log4j
public class NamespaceBeanImpl implements NamespaceBean {

    @Override
    public NameSpaceCacheContext getNamespace(Long nsId) throws Exception {
        LOGGER.debug("Namespace Direct Fetch, nsId: " + nsId);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getIdCondition(nsId, NamespaceModuleAndFieldFactory.getNamespaceModule()));

        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<NameSpaceContext> nameSpaceContexts = constructNamespaceAndFields(props);
            return new NameSpaceCacheContext(nameSpaceContexts.get(0));
        }
        throw new Exception("Invalid namespace Id");

    }

    @Override
    public Long addNamespace(NameSpaceContext ns) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields());
        long id = insertBuilder.insert(FieldUtil.getAsProperties(ns));
        ns.setId(id);

        NamespaceAPI.addInclusions(ns);

        return id;
    }

    @Override
    public void deleteNameSpacesFromRuleId(Long parentId, List<NSType> nsTypeList) throws Exception {
        GenericDeleteRecordBuilder del = new GenericDeleteRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", parentId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getConditionFromList("TYPE", "type", NamespaceAPI.getNsIndexFromList(nsTypeList), NumberOperators.EQUALS));
        del.delete();
    }

    @Override
    public List<Long> getNamespaceIdsForFieldId(Long fieldId) throws Exception {
        LOGGER.debug("Namespace Ids with Field Id Direct Fetch for fieldId: " + fieldId);
        return NamespaceAPI.getNamespacesByFieldId(fieldId);
    }


    @Override
    public void updateNsStatus(Long ruleId, boolean status, List<NSType> nsList) throws Exception {
        NameSpaceContext namespaceContext = new NameSpaceContext();
        namespaceContext.setStatus(status);
        Map<String, Object> namespaceMap = FieldUtil.getAsProperties(namespaceContext);
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields())
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", String.valueOf(ruleId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getConditionFromList("TYPE", "type", NamespaceAPI.getNsIndexFromList(nsList), NumberOperators.EQUALS));
        updateBuilder.update(namespaceMap);
    }

    @Override
    public void updateNamespace(NameSpaceContext ns) throws Exception {
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(ns.getId()), NumberOperators.EQUALS));
        updateBuilder.update(FieldUtil.getAsProperties(ns));

        addNamespaceFields(ns.getId(), ns.getFields());
        updateNamespaceInclusions(ns);
    }

    public void addNamespaceFields(Long nsId, List<NameSpaceField> fields) throws Exception {
        deleteFieldsIfAlreadyExists(nsId);
        List<Map<String, Object>> assetList = new ArrayList<>();

        for (NameSpaceField fld : fields) {
            Long resourceID = (fld.getResourceId() != null && fld.getResourceId() != -1) ? fld.getResourceId() : -1;
            if (resourceID == -1) {
                if (fld.getRelMapContext() != null && fld.getRelMapContext().getMappingLinkName() != null) {
                    getRelationMappingFields(fld);
                    prepareNSField(fld, nsId, resourceID, false);
                    assetList.add(FieldUtil.getAsProperties(fld));
                } else {
                    prepareNSField(fld, nsId, resourceID, true);
                    assetList.add(FieldUtil.getAsProperties(fld));
                }

            } else {
                prepareNSField(fld, nsId, resourceID, false);
                assetList.add(FieldUtil.getAsProperties(fld));
            }
        }

        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFieldFields())
                .addRecords(assetList);

        insertBuilder.save();
    }

    private void getRelationMappingFields(NameSpaceField nsField) throws Exception {
        RelationMappingContext mapping = RelationUtil.getRelationMapping(nsField.getRelMapContext().getMappingLinkName());
        nsField.setRelMapId(mapping.getId());
        nsField.setRelMapContext(mapping);
    }

    private void deleteFieldsIfAlreadyExists(Long nsId) throws Exception {
        GenericDeleteRecordBuilder delBuilder = new GenericDeleteRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("NAMESPACE_ID", "nsId", String.valueOf(nsId), NumberOperators.EQUALS));
        delBuilder.delete();

    }

    private void prepareNSField(NameSpaceField fld, long nsId, long resourceId, boolean isPrimary) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField field = modBean.getField(fld.getFieldId());
        FacilioModule module = field.getModule();

        fld.setField(field);
        fld.setModule(module);
        fld.setNsId(nsId);
        fld.setResourceId(resourceId);
        fld.setPrimary(isPrimary);

    }

    private void updateNamespaceInclusions(NameSpaceContext ns) throws Exception {
        List<Long> includedAssetIds = ns.getIncludedAssetIds();
        if (CollectionUtils.isEmpty(includedAssetIds)) {
            NamespaceAPI.deleteExistingInclusionRecords(ns);
        }
        NamespaceAPI.addInclusions(ns);
    }
}
