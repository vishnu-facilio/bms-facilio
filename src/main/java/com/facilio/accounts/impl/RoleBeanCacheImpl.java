package com.facilio.accounts.impl;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.NewPermission;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.cache.CacheUtil;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.cache.FacilioCache;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Objects;

public class RoleBeanCacheImpl extends RoleBeanImpl implements RoleBean {
    private static final Logger LOGGER = LogManager.getLogger(RoleBeanCacheImpl.class.getName());

    @Override
    public Role getRole ( long roleId,boolean fetchMembers ) throws Exception {
        FacilioCache<String, Object> roleIdCache = LRUCache.getRoleIdCachePs();
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while fetching Role in Cache");
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        String key = CacheUtil.ROLE_ID_KEY(orgId,roleId);
        Role role = (Role)roleIdCache.get(key);
        if(role == null) {
            role = super.getRole(roleId,fetchMembers);
            roleIdCache.put(key,role);
        }
//        else {
//            LOGGER.info("RoleBean roleId cache is hit");
//        }
        return role;
    }

    @Override
    public Role getRole ( long orgId,String roleName,boolean fetchMembers ) throws Exception {
        FacilioCache<String, Object> roleNameCache = LRUCache.getRoleNameCachePs();
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while fetching Role in Cache");
        String key = CacheUtil.ROLE_NAME_KEY(orgId,roleName);
        Role role = (Role)roleNameCache.get(key);
        if(role == null) {
            role = super.getRole(orgId,roleName,fetchMembers);
            roleNameCache.put(key,role);
        }
//        else {
//           LOGGER.info("RoleBean roleName cache is hit");
//        }
        return role;
    }

    @Override
    public boolean updateRole ( long roleId,Role role, Boolean isWebTabPermission ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while updating User Role");
        removeCache(roleId);
        return super.updateRole(roleId, role, isWebTabPermission);
    }

    @Override
    public boolean deleteRole ( long roleId ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while deleting User Role");
        removeCache(roleId);
        return super.deleteRole(roleId);
    }

    @Override
    public boolean addPermission ( long roleId,Permissions permissions ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while adding User Permission");
        removeCache(roleId);
        return super.addPermission(roleId,permissions);
    }

    @Override
    public boolean addNewPermission (long roleId, NewPermission permissions ) throws Exception {
        Objects.requireNonNull(AccountUtil.getCurrentOrg(),"Current Org cannot be null in AccountUtil while adding User Permission");
        removeCache(roleId);
        return super.addNewPermission(roleId,permissions);
    }

    private void removeCache(long roleId) throws Exception {
        Role userRole = getRole(roleId);
        if(userRole != null) {
        	LRUCache.getRoleIdCachePs().remove(CacheUtil.ROLE_ID_KEY(AccountUtil.getCurrentOrg().getOrgId(),roleId));
            LRUCache.getRoleNameCachePs().remove(CacheUtil.ROLE_NAME_KEY(roleId,userRole.getName()));
        }
    }
}
