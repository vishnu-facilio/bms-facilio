package com.facilio.accounts.impl;

import java.util.*;
import java.util.logging.Logger;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.*;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.util.V3PermissionUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
		role.setIsPrevileged(true);
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
		if (role.getCreatedTime() <= 0) {
			role.setCreatedTime(System.currentTimeMillis());
		}
		
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
	public boolean updateRole(long roleId, Role role, Boolean isWebTabPermission) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getRoleModule().getTableName())
				.fields(AccountConstants.getRoleFields())
				.andCustomWhere("ROLE_ID = ?", roleId);
		if(isWebTabPermission == null || Boolean.FALSE.equals(isWebTabPermission)){
			deletePermission(roleId);
		}
		deleteNewPermsisions(roleId);
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
				List<NewPermission> newPermissions = getNewPermission(roleObj.getId());
				roleObj.setNewPermissions(newPermissions);
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
	public List<Role> getRolesForApps(Collection<Long> appIds) throws Exception {
		return getRolesForApps(appIds, -1, -1, null,null, null);
	}

	@Override
	public List<Role> getRolesForApps(Collection<Long> appIds, int perPage, int offset, String searchQuery, String orderBy, String orderType) throws Exception {

		List<Long> roleIds = getRolesForApp(appIds);
		if(CollectionUtils.isNotEmpty(roleIds)) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(AccountConstants.getRoleFields())
					.table(AccountConstants.getRoleModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleID", StringUtils.join(roleIds, ","), NumberOperators.EQUALS));

			if(perPage > 0 && offset >= 0) {
				selectBuilder.offset(offset);
				selectBuilder.limit(perPage);
			}
			if(StringUtils.isNotEmpty(searchQuery)) {
				selectBuilder.andCondition(CriteriaAPI.getCondition("NAME", "name", searchQuery, StringOperators.CONTAINS));
			}
			if (StringUtils.isNotEmpty(orderBy)) {
				selectBuilder.orderBy(orderBy + " " + orderType);
			}

			return getRolesFromProps(selectBuilder.get(), false);
		}
		return Collections.EMPTY_LIST;
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

	private void deleteNewPermsisions(long roleId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		builder.delete();
		deleteV3Permissions(roleId);
	}

	private void deleteV3Permissions(long roleId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder();

		builder.table(ModuleFactory.getNewTabPermissionModule().getTableName());
		builder.andCondition(CriteriaAPI.getCondition("ROLE_ID","roleId",String.valueOf(roleId),NumberOperators.EQUALS));
		builder.delete();
	}

	@Override
	public boolean addNewPermission(long roleId, NewPermission permissions) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.fields(FieldFactory.getNewPermissionFields());
		builder.insert(FieldUtil.getAsProperties(permissions));
		V3PermissionUtil.addPermissionV3(roleId,permissions);
		return false;
	}

	public void addNewPermission(NewPermission permissions) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.fields(FieldFactory.getNewPermissionFields());
		builder.insert(FieldUtil.getAsProperties(permissions));
	}

	@Override
	public void deleteSingleNewPermission(long roleId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		builder.delete();
		deleteV3Permissions(roleId);
	}

	@Override
	public long addSingleNewPermission(long roleId, NewPermission permissions) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.fields(FieldFactory.getNewPermissionFields());
		long permissionId = builder.insert(FieldUtil.getAsProperties(permissions));
		V3PermissionUtil.addPermissionV3(roleId,permissions);
		return permissionId;
	}

	@Override
	public List<NewPermission> getNewPermission(long roleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getNewPermissionFields())
				.table(ModuleFactory.getNewPermissionModule().getTableName())
				.andCustomWhere("ROLE_ID = ?", roleId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<NewPermission> permissions = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				permissions.add(FieldUtil.getAsBeanFromMap(prop, NewPermission.class));
			}
			return permissions;
		}
		return null;
	}

	@Override
	public void addRolesAppsMapping(List<RoleApp> rolesApps) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getRolesAppsModule().getTableName())
				.fields(AccountConstants.getRolesAppsFields());

		for (RoleApp ra : rolesApps) {
			Map<String, Object> props = new HashMap<>();
			props.put("roleId", ra.getRoleId());
			props.put("applicationId", ra.getApplicationId());
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();

	}

	@Override
	public Map<Long, List<RoleApp>> getRolesAppsMapping(List<Long> roleIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRolesAppsFields())
				.table(AccountConstants.getRolesAppsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(roleIds, ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<Long, List<RoleApp>> roleAppsMapping = new HashMap<>();
			for(Map<String, Object> prop : props) {
				List<RoleApp> roleApps;
				if(roleAppsMapping.containsKey(prop.get("roleId"))) {
					roleApps = roleAppsMapping.get(prop.get("roleId"));
				}
				else {
					roleApps = new ArrayList<>();
					roleApps.add(FieldUtil.getAsBeanFromMap(prop, RoleApp.class));
				}
				roleAppsMapping.put((Long) prop.get("roleId"), roleApps);
			}
			return roleAppsMapping;
		}
		return null;
	}

	private List<Long> getRolesForApp(Collection<Long> appIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getRolesAppsFields())
				.table(AccountConstants.getRolesAppsModule().getTableName());

		if(CollectionUtils.isNotEmpty(appIds)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", StringUtils.join(appIds, ","), NumberOperators.EQUALS));
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			List<Long> roleIds = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				roleIds.add((Long)prop.get("roleId"));
			}
			return roleIds;
		}
		return null;
	}

	@Override
	public List<OrgUserApp> getRolesAppsMappingForUser(Long ouId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgUserAppsFields())
				.table(AccountConstants.getOrgUserAppsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", String.valueOf(ouId), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, OrgUserApp.class);
		}
		return null;
	}


}
