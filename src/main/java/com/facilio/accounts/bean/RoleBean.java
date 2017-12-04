package com.facilio.accounts.bean;

import java.util.List;

import com.facilio.accounts.dto.Role;

public interface RoleBean {

	public boolean createDefaultRoles(long orgId) throws Exception;
	
	public long createRole(long orgId, Role role) throws Exception;

	public boolean updateRole(long roleId, Role role) throws Exception;

	public boolean deleteRole(long roleId) throws Exception;

	public Role getRole(long roleId) throws Exception;
	
	public Role getRole(long orgId, String roleName) throws Exception;

	public List<Role> getRoles(long orgId) throws Exception;
}
