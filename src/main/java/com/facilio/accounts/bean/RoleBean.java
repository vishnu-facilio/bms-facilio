package com.facilio.accounts.bean;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.db.criteria.Criteria;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RoleBean {

	public long createSuperdminRoles(long orgId) throws Exception;
	
	public long createRole(long orgId, Role role) throws Exception;

	public boolean updateRole(long roleId, Role role) throws Exception;

	public boolean deleteRole(long roleId) throws Exception;
	
	public boolean deleteRolePermission(long roleId) throws Exception;

	public Role getRole(long roleId) throws Exception;
	
	public Role getRole(long roleId, boolean fetchMembers) throws Exception;
	
	public Role getRole(long orgId, String roleName) throws Exception;
	
	public Role getRole(long orgId, String roleName, boolean fetchMembers) throws Exception;

	public List<Role> getRoles(Criteria criteria) throws Exception;
	
	public List<Role> getRoles(Collection<Long> ids) throws Exception;
	
	public List<Role> getRoles(long orgId) throws Exception;
	
	public Map<String, Role> getRoleMap() throws Exception;
	
	public List<Permissions> getPermissions (long roleId) throws Exception;
	
	public boolean addPermission(long roleId, Permissions permissions) throws Exception;

}
