package com.facilio.beans;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.NamespaceAPI;
import com.facilio.ns.context.*;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.util.RelationUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.facilio.db.criteria.CriteriaAPI.addCriteria;
import static com.facilio.ns.NamespaceAPI.constructNamespaceAndFields;
import static com.facilio.readingrule.util.NewReadingRuleAPI.setModuleNameForCriteria;

@Log4j
public class NamespaceBeanImpl implements NamespaceBean {

    @Override
    public NameSpaceCacheContext getNamespace(Long nsId) throws Exception {
        LOGGER.debug("Namespace Direct Fetch, nsId: " + nsId);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .leftJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldRelatedModule().getTableName()).on("Namespace_Fields.ID=Namespace_Field_Related.NAMESPACE_FIELD_ID")
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

        for (NameSpaceField fld : fields) {

            long resourceId = (fld.getResourceId() != null) ? fld.getResourceId() : -1;
            if (resourceId == -1) {
                if (fld.getNsFieldType() == NsFieldType.RELATED_READING) {
                    prepareNSField(fld, nsId, resourceId, false);
                    Long nsFieldId = addNsField(FieldUtil.getAsProperties(fld));
                    fld.setId(nsFieldId);
                    if (fld.getRelatedInfo() != null) {
                        addNsFieldRelatedInfo(FieldUtil.getAsProperties(prepareNsRelatedField(fld)));
                    }
                } else {
                    prepareNSField(fld, nsId, resourceId, true);
                    addNsField(FieldUtil.getAsProperties(fld));
                }
            } else {
                prepareNSField(fld, nsId, resourceId, false);
                addNsField(FieldUtil.getAsProperties(fld));
            }
        }

    }

    @Override
    public void updateNsCacheWithCategory(Long categoryId, List<NSType> nsType) throws Exception {
    }

    private static Long addNsField(Map<String, Object>  nsField) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFieldFields());

        return insertBuilder.insert(nsField);
    }

    private void addNsFieldRelatedInfo(Map<String, Object> relatedInfo) throws Exception {
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceFieldRelatedModule().getTableName())
                .fields(NamespaceModuleAndFieldFactory.getNamespaceFieldRelatedFields());
        insertBuilder.insert(relatedInfo);

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

    private static NamespaceFieldRelated prepareNsRelatedField(NameSpaceField nsField) throws Exception {
        NamespaceFieldRelated relatedInfo = nsField.getRelatedInfo();
        getRelationMappingFields(relatedInfo);
        relatedInfo.setNameSpaceFieldId(nsField.getId());
        if (nsField.getRelatedInfo().getCriteria() != null) {
            setModuleNameForCriteria(relatedInfo.getCriteria(), FacilioConstants.ContextNames.ASSET);
            Long criteriaId = addCriteria(relatedInfo.getCriteria());
            relatedInfo.setCriteriaId(criteriaId);
        }
        return relatedInfo;
    }

    private static void getRelationMappingFields(NamespaceFieldRelated relatedInfo) throws Exception {
        RelationMappingContext mapping = RelationUtil.getRelationMapping(relatedInfo.getRelMapContext().getMappingLinkName());
        relatedInfo.setRelMapId(mapping.getId());
        relatedInfo.setRelMapContext(mapping);
    }


    private void updateNamespaceInclusions(NameSpaceContext ns) throws Exception {
        List<Long> includedAssetIds = ns.getIncludedAssetIds();
        if (CollectionUtils.isEmpty(includedAssetIds)) {
            NamespaceAPI.deleteExistingInclusionRecords(ns);
        }
        NamespaceAPI.addInclusions(ns);
    }

}
