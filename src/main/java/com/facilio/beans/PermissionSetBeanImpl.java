package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import com.facilio.bmsconsoleV3.context.scoping.ValueGeneratorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.config.PermissionSetConstants;
import com.facilio.permission.context.*;
import com.facilio.permission.context.TypeItem.GroupingType;
import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;
import com.facilio.permission.factory.PermissionSetFieldFactory;
import com.facilio.permission.factory.PermissionSetModuleFactory;
import com.facilio.permission.handlers.group.PermissionSetGroupHandler;
import com.facilio.permission.handlers.item.GroupItemHandler;
import com.facilio.permission.util.PermissionSetUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PermissionSetBeanImpl implements PermissionSetBean {
    public List<PermissionSetGroupingItemContext> getGroupListItemsForLinkName(String linkName) throws Exception {
        GroupingType groupingType = GroupingType.valueOf(linkName.toUpperCase());
        if(groupingType != null) {
            GroupItemHandler handler = groupingType.getHandler();
            if(handler != null) {
                return handler.getGroupItems();
            }
        }
        return null;
    }

    public List<BasePermissionContext> getPermissionSetItems(long permissionSetId, Long groupId, String groupType) throws Exception {
        PermissionSetType.Type type = PermissionSetType.Type.valueOf(groupType.toUpperCase());
        if(type != null) {
            return readPermissionValuesForPermissionSetType(type,permissionSetId,groupId);
        }
        return null;
    }


    public List<BasePermissionContext> readPermissionValuesForPermissionSetType(PermissionSetType.Type type, long permissionSetId, Long groupId) throws Exception {
        PermissionSetGroupHandler handler = type.getHandler();
        FacilioModule module = type.getModule();
        List<FacilioField> fields = PermissionSetUtil.getAllExtendModuleFields(module);
        Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        List<String> queryConditionFields = new ArrayList<>(type.getQueryFields());
        queryConditionFields.add("permissionType");
        List<BasePermissionContext> permissionLists = handler.getPermissions(groupId);
        List<Map<String,Object>> constructedPermissionProps = FieldUtil.getAsMapList(permissionLists,type.getSetClass());
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("permissionSetId"),String.valueOf(permissionSetId), NumberOperators.EQUALS));
        PermissionSetUtil.constructTableJoin(module, selectRecordBuilder);
        if(CollectionUtils.isNotEmpty(permissionLists)) {
            List<Map<String,Object>> propsList = selectRecordBuilder.get();
            Map<String,Map<String,Object>> dbPermissionPropMap = new HashMap<>();
            if(CollectionUtils.isNotEmpty(propsList)) {
                for(Map<String,Object> prop : propsList) {
                    dbPermissionPropMap.put(PermissionSetUtil.getPropUniqueKey(prop,queryConditionFields),prop);
                }
            }
            return constructAllPermissionFromDb(dbPermissionPropMap,constructedPermissionProps,queryConditionFields, type, permissionSetId);
        }
        return null;
    }

    public List<Map<String,Object>> getPermissionValues(PermissionSetType.Type type, Map<String,Long> queryProp, PermissionFieldEnum permissionFieldEnum, Long permissionSet) throws Exception {
        FacilioModule module = type.getModule();

        if(permissionSet != null) {
            List<FacilioField> fields = PermissionSetUtil.getAllExtendModuleFields(module);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .select(Collections.singletonList(fieldsMap.get("permission")))
                    .table(module.getTableName())
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("permissionSetId"), String.valueOf(permissionSet), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition(fieldsMap.get("permissionType"), permissionFieldEnum.name(), StringOperators.IS));
            PermissionSetUtil.constructTableJoin(module, selectRecordBuilder);

            if (MapUtils.isNotEmpty(queryProp)) {
                Criteria criteria = new Criteria();
                for (Map.Entry<String, Long> entrySet : queryProp.entrySet()) {
                    if(!type.getQueryFields().contains(entrySet.getKey())) {
                        return null;
                    }
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(entrySet.getKey()), String.valueOf(entrySet.getValue()), NumberOperators.EQUALS));
                }
                selectRecordBuilder.andCriteria(criteria);
            }
            List<Map<String, Object>> propsList = selectRecordBuilder.get();
            if (CollectionUtils.isNotEmpty(propsList)) {
                return propsList;
            }
        }
        return null;
    }

    private List<BasePermissionContext> constructAllPermissionFromDb(Map<String,Map<String,Object>> dbPermissionPropMap,List<Map<String,Object>> constructedPermissionProps,List<String> queryConditionFields, PermissionSetType.Type type,long permissionSetId) {
        if(CollectionUtils.isNotEmpty(constructedPermissionProps)) {
            List<PermissionFieldEnum> permissionFieldEnums = type.getPermissionFieldEnumList();
            List<Map<String,Object>> computedPropsResult = new ArrayList<>();
            for(PermissionFieldEnum permissionFieldEnum : permissionFieldEnums) {
                for (Map<String, Object> prop : constructedPermissionProps) {
                    Map<String, Object> computeProp = new HashMap<>(prop);
                    computeProp.put("permissionType",permissionFieldEnum.name());
                    String key = PermissionSetUtil.getPropUniqueKey(computeProp, queryConditionFields);
                    Map<String, Object> dbProp = new HashMap<>();
                    if (dbPermissionPropMap.containsKey(key)) {
                        dbProp = dbPermissionPropMap.get(key);
                    }
                    computeProp.put("permission", PermissionSetUtil.getPermissionValueForKey(dbProp, "permission"));
                    computeProp.put("id", dbProp.get("id"));
                    computedPropsResult.add(computeProp);
                }
            }
            List<BasePermissionContext> computedPermissionList = FieldUtil.getAsBeanListFromMapList(computedPropsResult,type.getSetClass());
            if(CollectionUtils.isNotEmpty(computedPermissionList)) {
                for(BasePermissionContext perm : computedPermissionList) {
                    perm.setType(type);
                    perm.setPermissionSetId(permissionSetId);
                }
            }
            return computedPermissionList;
        }
        return null;
    }



    public void deletePermissionsForPermissionSet(Map<String,Long> queryProp, Map<String,FacilioField> fieldsMap,FacilioModule module,Long permissionSetId,String permissionType) throws Exception {

        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldsMap.get("permissionSetId"), String.valueOf(permissionSetId),NumberOperators.EQUALS));
        PermissionSetUtil.constructTableJoin(module, deleteRecordBuilder);

        if(MapUtils.isNotEmpty(queryProp)) {
            Criteria criteria = new Criteria();
            for(Map.Entry<String,Long> entrySet : queryProp.entrySet()) {
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(entrySet.getKey()),String.valueOf(entrySet.getValue()),NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("permissionType"),String.valueOf(permissionType),StringOperators.IS));
            deleteRecordBuilder.andCriteria(criteria);
        }
        deleteRecordBuilder.delete();
    }
    public void addPermissionsForPermissionSet(Map<String,Object> prop) throws Exception {
        BasePermissionContext permission = FieldUtil.getAsBeanFromMap(prop, BasePermissionContext.class);
        if(permission != null) {
            PermissionSetType.Type groupType = permission.getType();
            if(groupType != null) {
                FacilioModule module = groupType.getModule();

                List<FacilioField> fields = PermissionSetUtil.getAllExtendModuleFields(module);
                Map<String,FacilioField> fieldsMap = FieldFactory.getAsMap(fields);

                Map<String,Long> queryProp = new HashMap<>();
                for(String queryField : groupType.getQueryFields()) {
                    queryProp.put(queryField, (Long) prop.get(queryField));
                }
                deletePermissionsForPermissionSet(queryProp,fieldsMap,module,permission.getPermissionSetId(), (String) prop.get("permissionType"));
                insertIntoExtendTables(prop,module);
            }
        }
    }

    private void insertIntoExtendTables(Map<String,Object> prop, FacilioModule module) throws Exception {
        Map<String,FacilioModule> extendModuleMap = PermissionSetConstants.EXTENDED_MODULE_REL;
        FacilioModule extendModule = module;
        List<FacilioModule> joinModules = new ArrayList<>();
        while(extendModule != null) {
            joinModules.add(0,extendModule);
            extendModule = extendModuleMap.get(extendModule.getName());
        }
        Long id = null;
        for(FacilioModule mod : joinModules) {
            if(id != null) {
                prop.put("id",id);
            }
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .table(mod.getTableName())
                    .fields(PermissionSetFieldFactory.MODULE_VS_FIELDS.get(mod.getName()));
            id = insertRecordBuilder.insert(prop);
        }
    }

    public void updateUserPermissionSets(long peopleId,List<Long> permissionSetIds) throws Exception {
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PEOPLE_ID","peopleId",String.valueOf(peopleId),NumberOperators.EQUALS));
        deleteRecordBuilder.delete();

        List<Map<String,Object>> propsList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(permissionSetIds)) {
            if(permissionSetIds.size() > 5) {
                throw new IllegalArgumentException("Maximum allowed permission sets for a user exceeded");
            }
            for(Long permissionSetId : permissionSetIds) {
                Map<String,Object> prop = new HashMap<>();
                prop.put("peopleId",peopleId);
                prop.put("permissionSetId",permissionSetId);
                propsList.add(prop);
            }
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                    .fields(PermissionSetFieldFactory.getUserPermissionSetsFields());
            insertBuilder.addRecords(propsList);
            insertBuilder.save();
        }
    }

    public List<PermissionSetContext> getUserPermissionSetIds(long peopleId) throws Exception {
        List<PermissionSetContext> permissionSets = getUserPermissionSetMapping(peopleId,true);
        return permissionSets;
    }
    public List<PermissionSetContext> getUserPermissionSetMapping(long peopleId,boolean fetchOnlyActive) throws Exception {
        List<FacilioField> fieldsList = new ArrayList<>();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getUserPermissionSetsFields())
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                .innerJoin(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .on("PermissionSet.ID = PeoplePermissionSets.PERMISSION_SET_ID")
                .andCondition(CriteriaAPI.getCondition("PEOPLE_ID","peopleId",String.valueOf(peopleId),NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIsDeletedField(PermissionSetModuleFactory.getPermissionSetModule()),String.valueOf(false),BooleanOperators.IS));
        if(fetchOnlyActive) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS));
        }
        List<Map<String,Object>> propsList = selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(propsList)) {
            List<Long> permissionSetIds = new ArrayList<>();
            for(Map<String,Object> prop : propsList) {
                permissionSetIds.add((Long)prop.get("permissionSetId"));
            }
            GenericSelectRecordBuilder permissionSetBuilder = new GenericSelectRecordBuilder()
                    .select(PermissionSetFieldFactory.getPermissionSetFields())
                    .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("ID","id",StringUtils.join(permissionSetIds,","),NumberOperators.EQUALS));
            List<Map<String,Object>> permissionSetPropsList = permissionSetBuilder.get();
            if(CollectionUtils.isNotEmpty(permissionSetPropsList)) {
                List<PermissionSetContext> permissionSetList = FieldUtil.getAsBeanListFromMapList(permissionSetPropsList,PermissionSetContext.class);
                return permissionSetList;
            }
        }
        return null;
    }

    @Override
    public void deletePermissionSet(long id) throws Exception {
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(PermissionSetFieldFactory.getPermissionSetFields());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .fields(Arrays.asList(fieldsMap.get("deleted"),fieldsMap.get("sysDeletedBy"),fieldsMap.get("sysDeletedTime")))
                .andCondition(CriteriaAPI.getIdCondition(id,PermissionSetModuleFactory.getPermissionSetModule()));
        Map<String,Object> prop = new HashMap<>();
        prop.put("deleted",true);
        prop.put("sysDeletedBy",AccountUtil.getCurrentUser().getId());
        prop.put("sysDeletedTime",System.currentTimeMillis());
        updateRecordBuilder.update(prop);
    }

    @Override
    public PermissionSetContext getPermissionSet(long id) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getPermissionSetFields())
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getIdCondition(id,PermissionSetModuleFactory.getPermissionSetModule()));
        Map<String,Object> prop = selectRecordBuilder.fetchFirst();
        if(MapUtils.isEmpty(prop)) {
            throw new IllegalArgumentException("Permission set not found");
        }
        PermissionSetContext permissionSet = FieldUtil.getAsBeanFromMap(prop,PermissionSetContext.class);
        return permissionSet;
    }

    public List<PermissionSetContext> getPermissionSetsList(int page, int perPage, String searchQuery, boolean fetchDeleted) throws Exception {
        List<FacilioField> fieldList = PermissionSetFieldFactory.getPermissionSetFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .select(PermissionSetFieldFactory.getPermissionSetFields());
        if (!fetchDeleted) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        }
        if (page > -1 && perPage > -1) {
            int offset = ((page - 1) * perPage);
            if (offset < 0) {
                offset = 0;
            }
            selectRecordBuilder.offset(offset);
            selectRecordBuilder.limit(perPage);
        }
        if (StringUtils.isNotEmpty(searchQuery)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchQuery, StringOperators.CONTAINS));
        }
        List<Map<String, Object>> props = selectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            List<PermissionSetContext> permissionSetList = FieldUtil.getAsBeanListFromMapList(props, PermissionSetContext.class);
            return permissionSetList;
        }
        return new ArrayList<>();
    }

    @Override
    public long getPermissionSetsCount(String searchQuery) throws Exception {
        List<FacilioField> fieldList = PermissionSetFieldFactory.getPermissionSetFields();
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fieldList);
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .select(FieldFactory.getCountField());
        selectRecordBuilder.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", String.valueOf(Boolean.FALSE), BooleanOperators.IS));
        if (StringUtils.isNotEmpty(searchQuery)) {
            selectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchQuery, StringOperators.CONTAINS));
        }

        Map<String, Object> props = selectRecordBuilder.fetchFirst();
        long count = MapUtils.isNotEmpty(props) ? (long) props.get("count") : 0;
        return count;
    }

    @Override
    public long addPermissionSet(PermissionSetContext permissionSet) throws Exception {
        permissionSet.setLinkName(PermissionSetUtil.constructLinkName(permissionSet.getLinkName(),permissionSet.getDisplayName()));
        GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .fields(PermissionSetFieldFactory.getPermissionSetFields());
        return insertRecordBuilder.insert(FieldUtil.getAsProperties(permissionSet));
    }

    @Override
    public void updatePermissionSet(PermissionSetContext permissionSet) throws Exception {
        permissionSet.setSysModifiedTime(System.currentTimeMillis());
        permissionSet.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
        List<FacilioField> fields = PermissionSetFieldFactory.getPermissionSetFields().stream().filter(f -> !f.getName().equals("linkName")).collect(Collectors.toList());
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(PermissionSetModuleFactory.getPermissionSetModule()),String.valueOf(permissionSet.getId()), NumberOperators.EQUALS));
        updateRecordBuilder.update(FieldUtil.getAsProperties(permissionSet));
    }

    @Override
    public boolean permissionSetHasPeopleAssociation(long permissionSetId) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getUserPermissionSetsFields())
                .table(PermissionSetModuleFactory.getPeoplePermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("PERMISSION_SET_ID","permissionSetId",String.valueOf(permissionSetId),NumberOperators.EQUALS));
        Map<String,Object> prop = selectRecordBuilder.fetchFirst();
        if(MapUtils.isNotEmpty(prop)) {
            return true;
        }
        return false;
    }

    @Override
    public List<PermissionSetContext> getPermissionSet(List<String> linkNames) throws Exception {
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .select(PermissionSetFieldFactory.getPermissionSetFields())
                .table(PermissionSetModuleFactory.getPermissionSetModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("LINK_NAME","linkName",StringUtils.join(linkNames,","),StringOperators.IS));
        List<Map<String,Object>> props = selectRecordBuilder.get();
        if(CollectionUtils.isNotEmpty(props)) {
            List<PermissionSetContext> permissionSets = FieldUtil.getAsBeanListFromMapList(props,PermissionSetContext.class);
            return permissionSets;
        }
        return null;
    }
}