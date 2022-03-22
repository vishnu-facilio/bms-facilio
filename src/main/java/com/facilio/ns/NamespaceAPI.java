package com.facilio.ns;

import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldUtil;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.ns.context.NameSpaceField;
import com.facilio.ns.factory.NamespaceModuleAndFieldFactory;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NamespaceAPI {

    public static Map<Long, NameSpaceContext> getReadingRuleNamespaces(Long fieldId) throws Exception {
        return getReadingRuleNamespaces(null, fieldId);
    }

    public static Map<Long, NameSpaceContext> getReadingRuleNamespaces(Long resourceId, Long fieldId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID");
        StringBuilder sb = new StringBuilder();
        sb.append("Namespace.ID IN (SELECT NAMESPACE_ID FROM Namespace_Fields WHERE FIELD_ID=? )");

        selectBuilder.andCustomWhere(sb.toString(), fieldId);

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
        if(resObj != null) {
            NameSpaceContext ns = FieldUtil.getAsBeanFromMap(resObj, NameSpaceContext.class);
            return ns;
        }
        return null;
    }

    public static NameSpaceContext getNameSpaceById(Long nsId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
        selectBuilder.select(NamespaceModuleAndFieldFactory.getNSAndFields())
                .table(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName())
                .innerJoin(NamespaceModuleAndFieldFactory.getNamespaceFieldsModule().getTableName()).on("Namespace.ID = Namespace_Fields.NAMESPACE_ID")
                .andCondition(CriteriaAPI.getCondition(NamespaceModuleAndFieldFactory.getNamespaceModule().getTableName()+".ID", "id", nsId.toString(), NumberOperators.EQUALS));

        List<Map<String, Object>> maps = selectBuilder.get();
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }

        NameSpaceContext nsCtx = null;

        for (Map<String, Object> m : maps) {

            if (nsCtx == null) {
                nsCtx = FieldUtil.getAsBeanFromMap(m, NameSpaceContext.class);
            }

            NameSpaceField field = FieldUtil.getAsBeanFromMap(m, NameSpaceField.class);
            nsCtx.addField(field);
        }

        return nsCtx;
    }

}
