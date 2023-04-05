package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ScopingConfigCacheContext;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsoleV3.util.ScopingUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

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

    public void deleteScopingConfigs(List<Long> scopingConfigIds) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(scopingConfigIds,","),
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

    public void addUserScoping(ScopingContext userScoping) throws Exception {
        userScoping.setCreatedBy(AccountUtil.getCurrentUser().getOuid());
        userScoping.setCreatedTime(System.currentTimeMillis());
        GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .fields(FieldFactory.getScopingFields());
        long id = builder.insert(FieldUtil.getAsProperties(userScoping));
        userScoping.setId(id);
    }

    public void updateUserScoping(ScopingContext userScoping) throws Exception {
        userScoping.setModifiedBy(AccountUtil.getCurrentUser().getOuid());
        userScoping.setModifiedTime(System.currentTimeMillis());
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .fields(FieldFactory.getScopingFields())
                .andCondition(CriteriaAPI.getIdCondition(userScoping.getId(), ModuleFactory.getScopingModule()));
        builder.update(FieldUtil.getAsProperties(userScoping));
    }

    public Long getUserScopingCount(String searchQuery) throws Exception {
        List<FacilioField> fieldsList = FieldFactory.getScopingFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldsList);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getCountField());
        if (StringUtils.isNotEmpty(searchQuery)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopeName"), searchQuery, StringOperators.CONTAINS));
        }
        Map<String, Object> props = builder.fetchFirst();
        Long count = MapUtils.isNotEmpty(props) ? (Long) props.get("count") : 0;
        return count;
    }

    public List<ScopingContext> getUserScopingList(String searchQuery, int page, int perPage) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScopingFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .select(FieldFactory.getScopingFields());
        if (StringUtils.isNotEmpty(searchQuery)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("scopeName"), searchQuery, StringOperators.CONTAINS));
        }
        if (page > -1 && perPage > -1) {
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            builder.offset(offset);
            builder.limit(perPage);
        }
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ScopingContext> userScopingList = FieldUtil.getAsBeanListFromMapList(props, ScopingContext.class);
            return userScopingList;
        }
        return new ArrayList<>();
    }

    public void deleteUserScoping(Long userScopingId) throws Exception {
        if (userScopingId == null) {
            throw new IllegalArgumentException("Scoping id can't be null");
        }
        ScopingUtil.deleteCriteriaAssociatedWithUserScoping(userScopingId);
        deleteScopingConfig(userScopingId);
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(userScopingId, ModuleFactory.getScopingModule()));
        builder.delete();
    }

    public void updateUserScopingStatus(Long scopingId, Boolean status) throws Exception {
        if (scopingId == null) {
            throw new IllegalArgumentException("Scoping id can't be null");
        }
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getScopingFields());
        Map<String,Object> props = new HashMap<>();
        props.put("status", status);
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getScopingModule().getTableName())
                .fields(Collections.singletonList(fieldMap.get("status")))
                .andCondition(CriteriaAPI.getIdCondition(scopingId, ModuleFactory.getScopingModule()));
        builder.update(props);

    }

    public List<ScopingConfigContext> getUserScopingConfig(Long userScopingId) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getScopingConfigFields());
        if (userScopingId == null) {
            throw new IllegalArgumentException("scopingId cannot be null");
        }
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .select(FieldFactory.getScopingConfigFields())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("scopingId"), Collections.singleton(userScopingId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<ScopingConfigContext> userScopingConfigList = FieldUtil.getAsBeanListFromMapList(props, ScopingConfigContext.class);
            for (ScopingConfigContext userScopingConfig : userScopingConfigList) {
                Long criteriaId = userScopingConfig.getCriteriaId();
                Criteria criteria = CriteriaAPI.getCriteria(criteriaId);
                userScopingConfig.setCriteria(criteria);

            }
            return userScopingConfigList;

        }
        return new ArrayList<>();
    }

    private static List<ScopingConfigContext> getScopingConfigForScopingIdAndModule(long scopingId,long moduleId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(FieldFactory.getScopingConfigFields())
                .table(ModuleFactory.getScopingConfigModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("MODULE_ID","moduleId",String.valueOf(moduleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SCOPING_ID","scopingId",String.valueOf(scopingId),NumberOperators.EQUALS));
        List<Map<String,Object>> props = selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(props)) {
            List<ScopingConfigContext> configs = FieldUtil.getAsBeanListFromMapList(props,ScopingConfigContext.class);
            return configs;
        }
        return null;
    }

    public void updateScopingConfigForUserScoping(List<ScopingConfigContext> userScopingConfigList, Long userScopingId) throws Exception {
        if (userScopingId != null && userScopingId > 0) {
            if(CollectionUtils.isNotEmpty(userScopingConfigList)) {
                for(ScopingConfigContext config : userScopingConfigList) {
                    List<ScopingConfigContext> existingConfigs = getScopingConfigForScopingIdAndModule(userScopingId,config.getModuleId());
                    if(CollectionUtils.isNotEmpty(existingConfigs)) {
                        for(ScopingConfigContext existingConfig : existingConfigs) {
                            CriteriaAPI.deleteCriteria(existingConfig.getCriteriaId());
                            deleteScopingConfigForId(existingConfig.getId());
                        }
                    }
                }
            }
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<ScopingConfigContext> scopingConfigContextList = new ArrayList<>();
            for (ScopingConfigContext userScopingConfig : userScopingConfigList) {
                long moduleId = userScopingConfig.getModuleId();
                if (moduleId <= 0) {
                    throw new IllegalArgumentException("moduleId can't be zero or negative number");
                }
                FacilioModule module = modBean.getModule(moduleId);
                Criteria moduleCriteria = userScopingConfig.getCriteria();
                if(moduleCriteria != null) {
                    for (String key : moduleCriteria.getConditions().keySet()) {
                        Condition condition = moduleCriteria.getConditions().get(key);
                        FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
                        condition.setField(field);
                    }
                    userScopingConfig.setScopingId(userScopingId);
                    scopingConfigContextList.add(userScopingConfig);
                }
            }
            addScopingConfigForApp(scopingConfigContextList);
        } else {
            throw new IllegalArgumentException("Scoping Id is mandatory");
        }
    }

    @Override
    public Long getPeopleScoping(Long peopleId) throws Exception {
        if(peopleId != null && peopleId > -1) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getPeopleUserScopingFields())
                    .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
            Map<String, Object> props = selectRecordBuilder.fetchFirst();
            if (MapUtils.isNotEmpty(props)) {
                if (props.containsKey("userScopingId")) {
                    return (Long) props.get("userScopingId");
                }
            }
        }
        return null;
    }

    @Override
    public void updatePeopleScoping(Long peopleId,Long scopingId) throws Exception {

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PEOPLE_ID","peopleId",String.valueOf(peopleId),NumberOperators.EQUALS));
        deleteRecordBuilder.delete();

        if(scopingId != null && scopingId > -1) {
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                    .fields(FieldFactory.getPeopleUserScopingFields());
            Map<String, Object> prop = new HashMap<>();
            prop.put("peopleId", peopleId);
            prop.put("userScopingId", scopingId);
            insertRecordBuilder.insert(prop);
        }
    }

    @Override
    public boolean scopingHasPeople(Long scopingId) throws Exception {
        if(scopingId != null && scopingId > -1) {
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getPeopleUserScopingFields())
                    .table(ModuleFactory.getPeopleUserScopingModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("USER_SCOPING_ID", "userScopingId", String.valueOf(scopingId), NumberOperators.EQUALS));
            Map<String, Object> props = selectRecordBuilder.fetchFirst();
            if (MapUtils.isNotEmpty(props)) {
                return true;
            }
        }
        return false;
    }

}