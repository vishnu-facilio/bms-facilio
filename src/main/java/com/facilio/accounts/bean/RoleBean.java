package com.facilio.accounts.bean;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.*;
import com.facilio.db.criteria.Criteria;

public interface RoleBean {

	public long createSuperdminRoles(long orgId) throws Exception;
	
	public long createRole(long orgId, Role role) throws Exception;

	public boolean updateRole(long roleId, Role role,  Boolean isWebTabPermission) throws Exception;

	public boolean deleteRole(long roleId) throws Exception;
	
	public boolean deleteRolePermission(long roleId) throws Exception;

	public Role getRole(long roleId) throws Exception;
	
	public Role getRole(long roleId, boolean fetchMembers) throws Exception;
	
	public Role getRole(long orgId, String roleName) throws Exception;
	
	public Role getRole(long orgId, String roleName, boolean fetchMembers) throws Exception;

	public List<Role> getRoles(Criteria criteria) throws Exception;
	
	public List<Role> getRoles(Collection<Long> ids) throws Exception;
	
	public List<Role> getRolesForApps(Collection<Long> appIds) throws Exception;

	public List<Role> getRolesForApps(Collection<Long> appIds, int perPage, int offset, String searchQuery, String orderBy, String orderType) throws Exception;

	public Map<String, Role> getRoleMap() throws Exception;
	
	public List<Permissions> getPermissions (long roleId) throws Exception;
	
	public boolean addPermission(long roleId, Permissions permissions) throws Exception;
	
	public boolean addNewPermission(long roleId, NewPermission permissions) throws Exception;

	public void addNewPermission(NewPermission permissions) throws Exception;

	public void deleteSingleNewPermission(long roleId) throws Exception;

	public long addSingleNewPermission(long roleId, NewPermission permissions) throws Exception;

	public List<NewPermission> getNewPermission(long roleId) throws Exception;

	public void addRolesAppsMapping(List<RoleApp> rolesApps) throws Exception;

	public Map<Long, List<RoleApp>> getRolesAppsMapping(List<Long> roleIds) throws Exception;

	public List<OrgUserApp> getRolesAppsMappingForUser(Long ouId) throws Exception;

}
