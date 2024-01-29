package com.facilio.beans;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.BasePermissionContext;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.context.TypeItem.PermissionSetGroupingItemContext;

import java.util.List;
import java.util.Map;

public interface PermissionSetBean {
    List<PermissionSetGroupingItemContext> getGroupListItemsForLinkName(String linkName) throws Exception;
    List<BasePermissionContext> getPermissionSetItems(long permissionSetId, Long groupId, String groupType) throws Exception;
    List<BasePermissionContext> readPermissionValuesForPermissionSetType(PermissionSetType.Type type, long permissionSetId, List<BasePermissionContext> permissionLists) throws Exception;
    List<Map<String,Object>> getPermissionValues(PermissionSetType.Type type, Map<String,Long> queryProp, PermissionFieldEnum permissionFieldEnum, Long permissionSetId) throws Exception;
    void deletePermissionsForPermissionSet(Map<String,Long> queryProp, Map<String,FacilioField> fieldsMap,FacilioModule module,Long permissionSetId,String permissionType) throws Exception;
    void addPermissionsForPermissionSet(Map<String,Object> prop) throws Exception;
    void updateUserPermissionSets(long peopleId,List<Long> permissionSetIds) throws Exception;
    List<PermissionSetContext> getUserPermissionSetMapping(long peopleId,boolean fetchOnlyActive) throws Exception;
    void deletePermissionSet(long id) throws Exception;
    PermissionSetContext getPermissionSet(long id) throws Exception;
    List<PermissionSetContext> getUserPermissionSetIds(long peopleId) throws Exception;
    List<PermissionSetContext> getPermissionSetsList(int page, int perPage, String searchQuery, boolean fetchDeleted) throws Exception;
    public long getPermissionSetsCount(String searchQuery) throws Exception;
    long addPermissionSet(PermissionSetContext permissionSetContext) throws Exception;
    void updatePermissionSet(PermissionSetContext permissionSetContext) throws Exception;
    boolean permissionSetHasPeopleAssociation(long permissionSetId) throws Exception;
    List<PermissionSetContext> getPermissionSet(List<String> linkNames) throws Exception;
    PermissionSetContext getPermissionSet(String linkName) throws Exception;

}