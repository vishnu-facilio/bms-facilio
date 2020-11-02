package com.facilio.db.criteria.manager;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NamedCriteriaAPI {

    public static long addNamedCriteria(NamedCriteria namedCriteria) throws Exception {
        if (namedCriteria == null) {
            return -1;
        }

        NamedCriteria.Type type = namedCriteria.getTypeEnum();
        switch (type) {
            case CRITERIA:
                if (namedCriteria.getCriteria() == null || namedCriteria.getCriteria().isEmpty()) {
                    throw new IllegalArgumentException("Criteria cannot be empty");
                }
                namedCriteria.setCriteriaId(CriteriaAPI.addCriteria(namedCriteria.getCriteria()));
                break;

            case WORKFLOW:
                if (namedCriteria.getWorkflowContext() == null) {
                    throw new IllegalArgumentException("Workflow cannot be empty");
                }
                namedCriteria.setWorkflowId(WorkflowUtil.addWorkflow(namedCriteria.getWorkflowContext()));
                break;

            default:
                throw new IllegalArgumentException("Not a valid named criteria type");
        }

        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .fields(FieldFactory.getNamedCriteriaFields());
        Map<String, Object> props = FieldUtil.getAsProperties(namedCriteria);
        builder.addRecord(props);
        builder.save();
        long id = (long) props.get("id");
        namedCriteria.setId(id);
        return id;
    }

    public static NamedCriteria getNamedCriteria(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields())
                .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getNamedCriteriaModule()));
        NamedCriteria namedCriteria = FieldUtil.getAsBeanFromMap(builder.fetchFirst(), NamedCriteria.class);
        populateChildren(Collections.singletonList(namedCriteria));
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

        List<Long> criteriaIds = new ArrayList<>();
        for (NamedCriteria namedCriteria : namedCriteriaList) {
            switch (namedCriteria.getTypeEnum()) {
                case CRITERIA:
                    criteriaIds.add(namedCriteria.getCriteriaId());
                    break;

                case WORKFLOW:
                    namedCriteria.setWorkflowContext(WorkflowUtil.getWorkflowContext(namedCriteria.getWorkflowId()));
                    break;
            }
        }

        Map<Long, Criteria> criteriaMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
        for (NamedCriteria namedCriteria : namedCriteriaList) {
            switch (namedCriteria.getTypeEnum()) {
                case CRITERIA:
                    namedCriteria.setCriteria(criteriaMap.get(namedCriteria.getCriteriaId()));
                    break;
            }
        }
    }
}
