package com.facilio.ns;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.ns.context.NSType;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import com.facilio.readingrule.context.NewReadingRuleContext;
import com.facilio.readingrule.faultimpact.FaultImpactAPI;
import com.facilio.readingrule.util.NewReadingRuleAPI;
import com.facilio.rule.AbstractRuleInterface;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class NamespaceAPI {

    public static Map<Long, NameSpaceContext> getReadingRuleNamespaces(Long fieldId) throws Exception {
        return getReadingRuleNamespaces(null, fieldId);
    }

    public static Map<Long, NameSpaceContext> getReadingRuleNamespaces(Long resourceId, Long fieldId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .innerJoin(ModuleFactory.getNewReadingRuleModule().getTableName()).on("New_Reading_Rule.ID = Namespace.PARENT_RULE_ID")
                .andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(NSType.READING_RULE.getIndex()), NumberOperators.EQUALS));

        selectBuilder.andCustomWhere("Namespace.ID IN (SELECT NAMESPACE_ID FROM Namespace_Fields WHERE FIELD_ID=? )", fieldId);

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isEmpty(maps)) {
            return new HashMap<>();
        }

        Map<Long, NameSpaceContext> nsMap = new HashMap<>();

        for (Map<String, Object> m : maps) {

            Long parentRuleId = (Long) m.get("parentRuleId");
            NameSpaceContext ns = nsMap.get(parentRuleId);
            if (ns == null) {
                ns = FieldUtil.getAsBeanFromMap(m, NameSpaceContext.class);
                nsMap.put(parentRuleId, ns);
            }

            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);

            if (field.getResourceId() == null || field.getResourceId() == -1L) {
                field.setResourceId(resourceId);
            }

            if (field.getResourceId() == resourceId) {
                ns.addField(field);
            }

        }

        return nsMap;
    }

    public static NameSpaceContext getNameSpaceById1(Long id) throws Exception {
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder();
        select.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", id.toString(), NumberOperators.EQUALS));
        Map<String, Object> resObj = select.fetchFirst();
        if (resObj != null) {
            NameSpaceContext ns = FieldUtil.getAsBeanFromMap(resObj, NameSpaceContext.class);
            return ns;
        }
        return null;
    }

    public static NameSpaceContext getNameSpaceByRuleId(Long ruleId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName() + ".PARENT_RULE_ID", "ruleId", ruleId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(NSType.READING_RULE.getIndex()), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }
        return constructNamespaceAndFields(maps);
    }

    public static NameSpaceContext getNameSpaceById(Long nsId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName() + ".ID", "id", nsId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }

        return constructNamespaceAndFields(maps);
    }

    private static NameSpaceContext constructNamespaceAndFields(List<Map<String, Object>> maps) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        NameSpaceContext nsCtx = null;
        List<NameSpaceField> fields = new ArrayList<>();
        for (Map<String, Object> m : maps) {

            if (nsCtx == null) {
                nsCtx = FieldUtil.getAsBeanFromMap(m, NameSpaceContext.class);
            }

            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);
            if (field.getPrimary() != null && field.getPrimary()) {
                if (alreadyExistsInList(fields, field)) {
                    continue;
                }
                field.setResourceId(null);
            }
            FacilioField facilioField = modBean.getField(field.getFieldId());
            field.setField(modBean.getField(field.getFieldId()));
            field.setModule(modBean.getModule(facilioField.getModuleId()));
            fields.add(field);
        }
        nsCtx.setFields(fields);

        return nsCtx;
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

    public static AbstractRuleInterface getParentRule(NameSpaceContext ns) throws Exception {
        switch (ns.getTypeEnum()) {
            case READING_RULE:
                return NewReadingRuleAPI.getRule(ns.getParentRuleId());
            case FAULT_IMPACT_RULE:
                //TODO:SPK Need to convert single query
                NewReadingRuleContext rule = NewReadingRuleAPI.getRule(ns.getParentRuleId());
                return FaultImpactAPI.getFaultImpactContext(rule.getImpactId());
            case KPI_RULE:
                throw new RuntimeException("Not implemented");
        }
        throw new Exception("Rule type is not matched!!");
    }

    public static void deleteNameSpacesFromRuleId(Long parentId, NSType typ) throws Exception {
        GenericDeleteRecordBuilder del = new GenericDeleteRecordBuilder()
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PARENT_RULE_ID", "parentRuleId", parentId.toString(), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("TYPE", "type", String.valueOf(typ.getIndex()), NumberOperators.EQUALS));
        del.delete();
    }
}
