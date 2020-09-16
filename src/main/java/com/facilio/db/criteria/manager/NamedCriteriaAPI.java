package com.facilio.db.criteria.manager;

import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NamedCriteriaAPI {

    public static long addNamedCriteria(NamedCriteria namedCriteria) throws Exception {
        if (namedCriteria == null) {
            return -1;
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
        return FieldUtil.getAsBeanFromMap(builder.fetchFirst(), NamedCriteria.class);
    }

    public static Map<Long, NamedCriteria> getCriteriaAsMap(List<Long> ids) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNamedCriteriaModule().getTableName())
                .select(FieldFactory.getNamedCriteriaFields())
                .andCondition(CriteriaAPI.getIdCondition(ids, ModuleFactory.getNamedCriteriaModule()));
        List<NamedCriteria> namedCriteriaList = FieldUtil.getAsBeanListFromMapList(builder.get(), NamedCriteria.class);
        return namedCriteriaList.stream().collect(Collectors.toMap(NamedCriteria::getId, Function.identity()));
    }
}
