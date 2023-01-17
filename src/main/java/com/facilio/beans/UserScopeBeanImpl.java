package com.facilio.beans;

import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserScopeBeanImpl implements UserScopeBean {
    public List<ScopingConfigCacheContext> getScopingConfig(long scopingId) throws Exception {
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScopingConfigFields())
                .table("Scoping_Config")
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID", "scopingId", String.valueOf(scopingId),
                        NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectBuilder.get();
        List<ScopingConfigContext> list = FieldUtil.getAsBeanListFromMapList(props, ScopingConfigContext.class);
        List<ScopingConfigCacheContext> resultList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (ScopingConfigContext scopingConfig : list) {
                if (scopingConfig.getCriteriaId() > 0) {
                    scopingConfig.setCriteria(CriteriaAPI.getCriteria(scopingConfig.getCriteriaId()));
                    resultList.add(new ScopingConfigCacheContext(scopingConfig));
                }
            }
            return resultList;
        }
        return null;
    }


    public void deleteScopingConfigForId(long scopingConfigId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(scopingConfigId),
                        NumberOperators.EQUALS));
        builder.delete();
    }


    public void addScopingConfigForApp(List<ScopingConfigContext> scoping) throws Exception {

        List<FacilioField> fields = FieldFactory.getScopingConfigFields();
        for (ScopingConfigContext scopingConfig : scoping) {
            if (scopingConfig.getCriteria() == null) {
                throw new IllegalArgumentException("Criteria Object cannot be null");
            }
            long criteriaId = CriteriaAPI.addCriteria(scopingConfig.getCriteria());
            scopingConfig.setCriteriaId(criteriaId);
        }
        GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .fields(fields);

        List<Map<String, Object>> props = FieldUtil.getAsMapList(scoping, ScopingConfigContext.class);

        insertBuilder.addRecords(props);
        insertBuilder.save();

    }

    public void deleteScopingConfig(long scopingId) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID", "scopingId", String.valueOf(scopingId),
                        NumberOperators.EQUALS));
        builder.delete();
    }
}