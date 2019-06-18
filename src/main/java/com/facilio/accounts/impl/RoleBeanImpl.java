package com.facilio.accounts.impl;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

import java.util.*;
import java.util.logging.Logger;

public class RoleBeanImpl implements RoleBean {
	
	private static final Logger LOGGER = Logger.getLogger(RoleBeanImpl.class.getName());

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
	
	private void populateEmailAndPhone(Role role, List<User> users) throws Exception {
		if (users != null && !users.isEmpty()) {
			StringJoiner emails = new StringJoiner(",");
			StringJoiner phones = new StringJoiner(",");
			StringJoiner ids = new StringJoiner(",");
			for (User user : users) {
				if (user.getEmail() != null && !user.getEmail().isEmpty()) {
					emails.add(user.getEmail());
				}
				
				if (user.getMobile() != null && !user.getMobile().isEmpty()) {
					phones.add(user.getMobile());
				}
				else if (user.getPhone() != null && !user.getPhone().isEmpty()) {
					phones.add(user.getPhone());
				}
				
				ids.add(String.valueOf(user.getOuid()));
			}
			if (ids.length() > 0) {
				role.setRoleMembersIds(ids.toString());
				
				if (emails.length() > 0) {
					role.setRoleMembersEmail(emails.toString());
				}
				
				if (phones.length() > 0) {
					role.setRoleMembersPhone(phones.toString());
				}
			}
		}
	}

	private List<Role> getRolesFromProps (List<Map<String, Object>> props, boolean fetchMembers) throws Exception {
		if (props != null && !props.isEmpty()) {
			List<Role> roles = new ArrayList<>();
			List<Long> roleIds = fetchMembers ? new ArrayList<>() : null;
			for(Map<String, Object> prop : props) {
				Role roleObj = FieldUtil.getAsBeanFromMap(prop, Role.class);
				List<Permissions> permissions = getPermissions(roleObj.getId());
				roleObj.setPermissions(permissions);
				roles.add(roleObj);
				
				if (fetchMembers) {
					roleIds.add(roleObj.getId());
				}
			}
			
			if (fetchMembers) {
				Map<Long, List<User>> roleUsers = AccountUtil.getUserBean().getUsersWithRoleAsMap(roleIds);
				if (roleUsers != null && !roleUsers.isEmpty()) {
					for (Role role : roles) {
						populateEmailAndPhone(role, roleUsers.get(role.getId()));
					}
				}
			}
			return roles;
		}
		return null;
	}
	
	@Override
	public Role getRole(long roleId) throws Exception {
		return getRole(roleId, false);
	}

	@Override
	public Role getRole(long roleId, boolean fetchMembers) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		List<Role> roles = getRolesFromProps(selectBuilder.get(), fetchMembers);
		if (roles != null && !roles.isEmpty()) {
			return roles.get(0);
		}
		return null;
	}
	
	@Override
	public Role getRole(long orgId, String roleName) throws Exception {
		return getRole(orgId, roleName, false);
	}

	@Override
	public Role getRole(long orgId, String roleName, boolean fetchMembers) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ORGID = ? AND NAME = ?", orgId, roleName);
		
		List<Role> roles = getRolesFromProps(selectBuilder.get(), fetchMembers);
		if (roles != null && !roles.isEmpty()) {
			return roles.get(0);
		}
		return null;
	}

	@Override
	public List<Role> getRoles(long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(AccountConstants.getRoleModule().getTableName())
				.andCustomWhere("ORGID = ?", orgId);
		
		return getRolesFromProps(selectBuilder.get(), false);
	}
	
	@Override
	public List<Role> getRoles(Criteria criteria) throws Exception {
		FacilioModule module = AccountConstants.getRoleModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRoleFields())
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCriteria(criteria);
		
		return getRolesFromProps(selectBuilder.get(), false);
	}
	
	@Override
	public List<Role> getRoles(Collection<Long> ids) throws Exception {
		FacilioModule module = AccountConstants.getRoleModule();
		List<FacilioField> fields = AccountConstants.getRoleFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("roleId"), ids, NumberOperators.EQUALS))
				;
		
		return getRolesFromProps(selectBuilder.get(), false);
	}
	
	public Map<String, Role> getRoleMap() throws Exception {
		
		List<FacilioField> fields = AccountConstants.getRoleFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getRoleModule().getTableName());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getRoleModule()));
				
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			Map<String, Role> map = new HashMap<>();  
			for (Map<String, Object> prop : props) {
				Role roleContext = FieldUtil.getAsBeanFromMap(prop, Role.class);
				map.put(roleContext.getName(), roleContext);
			}
			return map;
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
