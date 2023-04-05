package com.facilio.beans;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.FWLRUCaches;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.PermissionFieldEnum;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.permission.context.PermissionSetType;
import com.facilio.permission.util.PermissionSetUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class PermissionSetBeanCacheImpl extends PermissionSetBeanImpl implements PermissionSetBean {
    public List<Long> getUserPermissionSetIds(long peopleId) throws Exception {
        FacilioCache<String, List<Long>> userPermissionSets = LRUCache.getPeoplePermissionSets();
        String key = CacheUtil.PERMISSION_SET_KEY(AccountUtil.getCurrentOrg().getId(), peopleId);
        List<Long> permissionSetIds = FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(userPermissionSets, key, () -> {
            return super.getUserPermissionSetIds(peopleId);
        });
        if (permissionSetIds != null) {
            return Collections.unmodifiableList(permissionSetIds);
        }
        return null;
    }

    public void updateUserPermissionSets(long peopleId, List<Long> permissionSetIds) throws Exception {
        FacilioCache<String, List<Long>> userPermissionSets = LRUCache.getPeoplePermissionSets();
        String key = CacheUtil.PERMISSION_SET_KEY(AccountUtil.getCurrentOrg().getId(), peopleId);
        super.updateUserPermissionSets(peopleId, permissionSetIds);
        userPermissionSets.remove(key);
    }
    public long addPermissionSet(PermissionSetContext permissionSet) throws Exception {
        FacilioCache<String, List<Long>> userPermissionSets = LRUCache.getPeoplePermissionSets();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        long id = super.addPermissionSet(permissionSet);
        userPermissionSets.removeStartsWith(key);
        return id;
    }

    public void updatePermissionSet(PermissionSetContext permissionSet) throws Exception {
        FacilioCache<String, List<Long>> userPermissionSets = LRUCache.getPeoplePermissionSets();
        String key = CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId());
        super.updatePermissionSet(permissionSet);
        userPermissionSets.removeStartsWith(key);
    }
    public List<Map<String,Object>> getPermissionValues(PermissionSetType.Type type, Map<String,Long> queryProp, PermissionFieldEnum permissionFieldEnum, Long permissionSetId) throws Exception {
        String propKey = PermissionSetUtil.getPropKey(queryProp,type.getQueryFields());
        FacilioCache<String, List<Map<String,Object>>> cache = LRUCache.getPermissionSetsTypePermissionCache();
        if(permissionSetId != null) {
            String key = CacheUtil.PERMISSION_SET_PERMISSION_KEY(AccountUtil.getCurrentOrg().getId(),propKey,permissionSetId,permissionFieldEnum.name());
            return FWLRUCaches.Util.genericGetFromCacheAndHandleMissLogic(cache, key, () -> {
                return super.getPermissionValues(type,queryProp,permissionFieldEnum,permissionSetId);
            });
        }
        return null;
    }

    public void deletePermissionsForPermissionSet(Map<String,Long> queryProp, Map<String, FacilioField> fieldsMap, FacilioModule module, Long permissionSetId, String permissionType) throws Exception {
        FacilioCache<String, List<Map<String,Object>>> cache = LRUCache.getPermissionSetsTypePermissionCache();
        super.deletePermissionsForPermissionSet(queryProp,fieldsMap,module,permissionSetId,permissionType);
        cache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }
    public void addPermissionsForPermissionSet(Map<String,Object> prop) throws Exception {
        FacilioCache<String, List<Map<String,Object>>> cache = LRUCache.getPermissionSetsTypePermissionCache();
        super.addPermissionsForPermissionSet(prop);
        cache.removeStartsWith(CacheUtil.ORG_KEY(AccountUtil.getCurrentOrg().getId()));
    }
}