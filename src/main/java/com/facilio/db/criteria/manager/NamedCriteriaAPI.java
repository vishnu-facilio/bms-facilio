package com.facilio.db.criteria.manager;

import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NamedCriteriaAPI {

    public static long addOrUpdateNamedCriteria(NamedCriteria namedCriteria, String moduleName) throws Exception {
        if (namedCriteria == null) {
            return -1;
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        namedCriteria.validate();

        if (namedCriteria.getId() < 0) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                    .fields(FieldFactory.getNamedCriteriaFields());
            Map<String, Object> props = FieldUtil.getAsProperties(namedCriteria);
            builder.addRecord(props);
            builder.save();
            long criteriaId = (long) props.get("id");
            namedCriteria.setId(criteriaId);
        }
        else {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                    .fields(FieldFactory.getNamedCriteriaFields())
                    .andCondition(CriteriaAPI.getIdCondition(namedCriteria.getId(), ModuleFactory.getNamedCriteriaModule()));
            builder.update(FieldUtil.getAsProperties(namedCriteria));

            GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getNamedConditionModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_ID", "namedCriteriaId", String.valueOf(namedCriteria.getId()), NumberOperators.EQUALS));
            deleteBuilder.delete();
        }

        GenericInsertRecordBuilder conditionBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNamedConditionModule().getTableName())
                .fields(FieldFactory.getNamedConditionFields());
        for (String key : namedCriteria.getConditions().keySet()) {
            NamedCondition namedCondition = namedCriteria.getConditions().get(key);
            if (namedCondition.getType() == NamedCondition.Type.CRITERIA.getIndex()) {
                Criteria criteria = namedCondition.getCriteria();
                CriteriaAPI.updateConditionField(moduleName,criteria);
            }
            namedCondition.validateAndAddChildren();
            namedCondition.setNamedCriteriaId(namedCriteria.getId());
            namedCondition.setSequence(Integer.valueOf(key));
            conditionBuilder.addRecord(FieldUtil.getAsProperties(namedCondition));
        }
        conditionBuilder.save();

        return namedCriteria.getId();
    }

    public static NamedCriteria getNamedCriteria(long id) throws Exception {
        return getNamedCriteria(id, true);
    }

    public static NamedCriteria getNamedCriteria(long id, boolean populateChildren) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getNamedCriteriaModule()));
        NamedCriteria namedCriteria = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), NamedCriteria.class);
        if (populateChildren && namedCriteria != null) {
            populateChildren(Collections.singletonList(namedCriteria));
        }
        return namedCriteria;
    }

    public static Map<Long, NamedCriteria> getCriteriaAsMap(List<Long> ids) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getNamedCriteriaModule()));
        List<NamedCriteria> namedCriteriaList = FieldUtil.getAsBeanListFromMapList(builder.get(), NamedCriteria.class);
        populateChildren(namedCriteriaList);
        return namedCriteriaList.stream().collect(Collectors.toMap(NamedCriteria::getId, Function.identity()));
    }

    private static void populateChildren(List<NamedCriteria> namedCriteriaList) throws Exception {
        if (CollectionUtils.isEmpty(namedCriteriaList)) {
            return;
        }

        Map<Long, NamedCriteria> namedCriteriaMap = namedCriteriaList.stream().collect(Collectors.toMap(NamedCriteria::getId, Function.identity()));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedConditionModule().getTableName())
                .select(FieldFactory.getNamedConditionFields())
                .andCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_ID", "namedCriteriaId", StringUtils.join(namedCriteriaMap.keySet(), ","), NumberOperators.EQUALS));
        List<NamedCondition> namedConditionList = FieldUtil.getAsBeanListFromMapList(builder.get(), NamedCondition.class);

        List<Long> criteriaIds = new ArrayList<>();
        for (NamedCondition namedCondition : namedConditionList) {
            namedCriteriaMap.get(namedCondition.getNamedCriteriaId()).addCondition(
                    String.valueOf(namedCondition.getSequence()), namedCondition);
            switch (namedCondition.getTypeEnum()) {
                case CRITERIA:
                    criteriaIds.add(namedCondition.getCriteriaId());
                    break;

                case WORKFLOW:
                    namedCondition.setWorkflowContext(WorkflowUtil.getWorkflowContext(namedCondition.getWorkflowId()));
                    break;
            }
        }

        if (CollectionUtils.isNotEmpty(criteriaIds)) {
            Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
            for (NamedCondition namedCondition : namedConditionList) {
                switch (namedCondition.getTypeEnum()) {
                    case CRITERIA:
                        namedCondition.setCriteria(criteriaMap.get(namedCondition.getCriteriaId()));
                        break;
                }
            }
        }
    }

    public static List<NamedCriteria> getAllNamedCriteria(FacilioModule module) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields())
                .andCondition(CriteriaAPI.getCondition("NAMED_CRITERIA_MODULEID", "namedCriteriaModuleid", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
        List<NamedCriteria> namedCriteriaList = FieldUtil.getAsBeanListFromMapList(builder.get(), NamedCriteria.class);
        populateChildren(namedCriteriaList);
        return namedCriteriaList;
    }

    public static void deleteCriteria(Long id) throws Exception {
        try {
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getNamedCriteriaModule()));
            builder.delete();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Cannot delete this condition");
        }
    }

    public static void bulkDeleteCriteria(Collection<Long> ids) throws Exception {
        try {
            GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                    .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getNamedCriteriaModule()));
            builder.delete();
        } catch (SQLException ex) {
            throw new IllegalArgumentException("Cannot delete this condition");
        }
    }

    public static Map<Long, NamedCriteria> getAllNamedCriteriaAsMap(boolean populateChildren) throws Exception {
        Map<Long, NamedCriteria> namedCriteriaMap = null;
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields());

        List<NamedCriteria> namedCriteriaList = FieldUtil.getAsBeanListFromMapList(builder.get(), NamedCriteria.class);
        if (populateChildren) {
            populateChildren(namedCriteriaList);
        }
        if (CollectionUtils.isNotEmpty(namedCriteriaList)) {
            namedCriteriaMap = namedCriteriaList.stream().collect(Collectors.toMap(NamedCriteria::getId, Function.identity()));
        }
        return namedCriteriaMap;
    }

//    public static NamedCriteria convertCriteriaToNamedCriteria(String name, long moduleId, Criteria criteria) throws Exception {
//        NamedCriteria namedCriteria = new NamedCriteria();
//        namedCriteria.setName(name);
//        namedCriteria.setNamedCriteriaModuleId(moduleId);
//        namedCriteria.setPattern("1");
//
//        NamedCondition namedCondition = new NamedCondition();
//        namedCondition.setName(name);
//        namedCondition.setType(NamedCondition.Type.CRITERIA);
//        namedCondition.setCriteria(criteria);
//        namedCriteria.addCondition("1", namedCondition);
//
//        addOrUpdateNamedCriteria(namedCriteria);
//        return namedCriteria;
//    }

//    public static Criteria convertNamedCriteriaToCriteria(NamedCriteria namedCriteria) throws Exception {
//        if (namedCriteria == null) {
//            return null;
//        }
//
//        Map<String, NamedCondition> conditions = namedCriteria.getConditions();
//        Collection<NamedCondition> namedConditions = conditions.values();
//        for (NamedCondition condition : namedConditions) {
//            if (condition.getTypeEnum() == NamedCondition.Type.CRITERIA) {
//                return condition.getCriteria();
//            }
//        }
//        return null;
//    }
}
