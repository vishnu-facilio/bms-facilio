package com.facilio.accounts.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class RoleBeanImpl implements RoleBean {

	@Override
	public long createSuperdminRoles(long orgId) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getRoleModule().getTableName())
				.fields(AccountConstants.getRoleFields());
		
		Role role = new Role();
		role.setOrgId(orgId);
		role.setName(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN);
		role.setDescription(AccountConstants.DefaultSuperAdmin.SUPER_ADMIN);
		role.setCreatedTime(System.currentTimeMillis());
		
		Map<String, Object> props = FieldUtil.getAsProperties(role);
		insertBuilder.addRecord(props);
		
//		Map<String, Role> defaultRoles = AccountConstants.DefaultRole.DEFAULT_ROLES;
//		Iterator<String> keys = defaultRoles.keySet().iterator();
//		List<Role> roles = new ArrayList<>();
//		while (keys.hasNext()) {
//			String key = keys.next();
//			
//			Role role = defaultRoles.get(key);
//			role.setOrgId(orgId);
//			
//			Map<String, Object> props = FieldUtil.getAsProperties(role);
//			insertBuilder.addRecord(props);
//			roles.add(role);
//		}
		insertBuilder.save();
//		List<Map<String, Object>> roleProps = insertBuilder.getRecords();
		
//		for(int i=0; i<roleProps.size(); i++) {
//			long id = (long) roleProps.get(i).get("id");
//			roles.get(i).setRoleId(id);
//		}
		
//		createPermission(roles);
		long roleId = (Long) props.get("id");
		return roleId;
		
	}
	

	@Override
	public long createRole(long orgId, Role role) throws Exception {
		
		role.setOrgId(orgId);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getRoleModule().getTableName())
				.fields(AccountConstants.getRoleFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(role);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long roleId = (Long) props.get("id");
		return roleId;
	}
	

	@Override
	public boolean updateRole(long roleId, Role role) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getRoleModule().getTableName())
				.fields(AccountConstants.getRoleFields())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		deletePermission(roleId);
		Map<String, Object> props = FieldUtil.getAsProperties(role);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteRole(long roleId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		int deletedRows = builder.delete();
		if (deletedRows == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean deleteRolePermission(long roleId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		int deletedRows = builder.delete();
		if (deletedRows == 1) {
			return true;
		}
		return false;
	}


	@Override
	public Role getRole(long roleId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Role roleObj = FieldUtil.getAsBeanFromMap(props.get(0), Role.class);
			List<Permissions> permissions = getPermissions(roleObj.getId());
			roleObj.setPermissions(permissions);
			return roleObj;
//			return FieldUtil.getAsBeanFromMap(props.get(0), Role.class);
		}
		return null;
	}

	@Override
	public Role getRole(long orgId, String roleName) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ORGID = ? AND NAME = ?", orgId, roleName);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Role roleObj = FieldUtil.getAsBeanFromMap(props.get(0), Role.class);
			List<Permissions> permissions = getPermissions(roleObj.getId());
			roleObj.setPermissions(permissions);
			return roleObj;
//			return FieldUtil.getAsBeanFromMap(props.get(0), Role.class);
		}
		return null;
	}

	@Override
	public List<Role> getRoles(long orgId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ORGID = ?", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Role> roles = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				Role roleObj = FieldUtil.getAsBeanFromMap(prop, Role.class);
				List<Permissions> permissions = getPermissions(roleObj.getId());
				roleObj.setPermissions(permissions);
				roles.add(roleObj);
//				roles.add(FieldUtil.getAsBeanFromMap(prop, Role.class));
			}
			return roles;
		}
		return null;
	}
	
	@Override
	public List<Permissions> getPermissions (long roleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getPermissionFields())
				.table(AccountConstants.getPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<Permissions> permissions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				permissions.add(FieldUtil.getAsBeanFromMap(prop, Permissions.class));
			}
			return permissions;
		}
		return null;
	}
	
	private void createPermission(List<Role> roles) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getPermissionModule().getTableName())
				.fields(AccountConstants.getPermissionFields());
		
		for(Role role : roles) {
			for(Permissions permission : role.getPermissions()) {
				permission.setRoleId(role.getId());
				insertBuilder.addRecord(FieldUtil.getAsProperties(permission));
			}
		}
		insertBuilder.save();
		
	}


	@Override
	public boolean addPermission(long roleId, Permissions permissions) throws Exception {
		
		permissions.setRoleId(roleId);
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getPermissionModule().getTableName())
				.fields(AccountConstants.getPermissionFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(permissions);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		return true;
	}
	
	private void deletePermission(long roleId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		builder.delete();
	}
	
}
