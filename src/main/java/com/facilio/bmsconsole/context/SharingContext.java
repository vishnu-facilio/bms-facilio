package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.util.VendorsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import org.apache.commons.collections4.CollectionUtils;

public class SharingContext<E extends SingleSharingContext> extends ArrayList<E> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public SharingContext() {
		super();
	}

	public SharingContext(Collection<E> c) {
		super(c);
	}

	public boolean isAllowed () throws Exception {
		return isAllowed(AccountUtil.getCurrentUser());
	}

	public boolean isAllowed (Object object) throws Exception {
		return isAllowed(AccountUtil.getCurrentUser(), object);
	}

	public boolean isAllowed (User user, Object object) throws Exception {
		if (isEmpty()) {
			return true;
		}

		List<SingleSharingContext> appPermissions = new ArrayList<>();
		List<SingleSharingContext> otherPermissions = new ArrayList<>();
		stream().forEach(perm -> {
			if (perm.getTypeEnum() == SharingType.APP) {
				appPermissions.add(perm);
			}
			else {
				otherPermissions.add(perm);
			}
		});
		
		boolean isAppSharingMatched = false;
		if (!appPermissions.isEmpty()) {
			for (SingleSharingContext permission : appPermissions) {
				isAppSharingMatched = isMatching(permission, user, object);
				if (isAppSharingMatched) {
					break;
				}
			}
		}
		else {
			isAppSharingMatched = true;
		}
		
		boolean havePermission = false;
		if (!otherPermissions.isEmpty()) {
			for (SingleSharingContext permission : otherPermissions) {
				havePermission = isMatching(permission, user, object);
				if (havePermission) {
					break;
				}
			}
		}
		else {
			havePermission = true;
		}
		
		return isAppSharingMatched && havePermission;
	}

	public boolean isMatching (SingleSharingContext permission, User user, Object object) throws Exception {
		switch (permission.getTypeEnum()) {
			case USER:
				if (permission.getUserId() == user.getOuid()) {
					return true;
				}
				break;
			case ROLE:
				if (permission.getRoleId() == user.getRoleId()) {
					return true;
				}
			case GROUP:
				if (permission.getGroupMembers() == null) {
					GroupBean groupBean = (GroupBean) BeanFactory.lookup("GroupBean");
					List<GroupMember> members = groupBean.getGroupMembers(permission.getGroupId());
					permission.setGroupMembers(members);
				}

				if (permission.getGroupMembers() != null && !permission.getGroupMembers().isEmpty()) {
					for (GroupMember member : permission.getGroupMembers()) {
						if (member.getOuid() == user.getOuid()) {
							return true;
						}
					}
				}
				break;
			case FIELD:
				if (permission.getFieldId() > 0) {
					if (object != null) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioField field = modBean.getField(permission.getFieldId());
						if (field instanceof BaseLookupField) {
							FacilioModule lookupModule = ((BaseLookupField) field).getLookupModule();
							Map<String, Object> moduleDataMap = FieldUtil.getAsProperties(object);
							if (moduleDataMap.containsKey(field.getName())) {
								List<Long> objIds = getUserIdsForField(moduleDataMap, field);
								if (CollectionUtils.isNotEmpty(objIds)) {
									if ("users".equals(lookupModule.getName())) {
										if (objIds.contains(user.getOuid())) {
											return true;
										}
									} else {
										FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
										if (lookupModule.getExtendedModuleIds().contains(peopleModule.getModuleId())) {
											if (objIds.contains(user.getPeopleId())) {
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
				break;
			case TENANT: {
				boolean matching = isMatchingPeopleField(permission, object, user, "tenant");
				if (matching) {
					return true;
				}
				break;
			}
			case VENDOR: {
				boolean matching = isMatchingPeopleField(permission, object, user, "vendors");
				if (matching) {
					return true;
				}
				break;
			}
			case APP:
				if (permission.getAppType() > 0 && permission.getAppType() == AccountUtil.getCurrentUser().getAppDomain().getAppDomainType()) {
					return true;
				}
		}
		return false;
	}

	private List<Long> getUserIdsForField(Map<String, Object> moduleDataMap, FacilioField field) {
		List<Long> objIds = new ArrayList<>();
		if (field instanceof MultiLookupField) {
			List<Map<String, Object>> list = (List<Map<String, Object>>) moduleDataMap.get(field.getName());
			if (CollectionUtils.isNotEmpty(list)) {
				for (Map<String, Object> map : list) {
					objIds.add((Long) map.get("id"));
				}
			}
		}
		else if (field instanceof LookupField) {
			Map<String, Object> obj = (Map<String, Object>) moduleDataMap.get(field.getName());
			Long objId = (Long) obj.get("id");
			objIds.add(objId);
		}
		return objIds;
	}

	private boolean isMatchingPeopleField(SingleSharingContext permission, Object object, User user, String moduleName) throws Exception {
		if (permission.getFieldId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field = modBean.getField(permission.getFieldId());
			if (field instanceof LookupField && ((LookupField) field).getLookupModule().getName().equals(moduleName)) {
				ModuleBaseWithCustomFields peopleContext = null;
				Object obj = FieldUtil.getAsProperties(object).get(field.getName());
				if (obj instanceof ModuleBaseWithCustomFields) {
					peopleContext = (ModuleBaseWithCustomFields) obj;
				}
				else if (obj instanceof Map) {
					peopleContext = FieldUtil.getAsBeanFromMap((Map<String, Object>) obj, ModuleBaseWithCustomFields.class);
				}

				if (peopleContext != null) {
					ModuleBaseWithCustomFields peopleObjectForUser = null;
					if ("tenant".equals(moduleName)) {
						peopleObjectForUser = PeopleAPI.getTenantForUser(user.getOuid());
					}
					if ("vendors".equals(moduleName)) {
						peopleObjectForUser = PeopleAPI.getVendorForUser(user.getOuid());
					}
					if (peopleObjectForUser != null) {
						if (peopleObjectForUser.getId() == peopleContext.getId()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public List<SingleSharingContext> getMatching(Object object) throws Exception {
		return getMatching(AccountUtil.getCurrentUser(), object);
	}

	public List<SingleSharingContext> getMatching (User user, Object object) throws Exception {
		if (isEmpty()) {
			return null;
		}

		List<SingleSharingContext> matchingPermissions = new ArrayList<>();
		for (SingleSharingContext permission : this) {
			if (isMatching(permission, user, object)) {
				matchingPermissions.add(permission);
			}
		}
		return matchingPermissions;
	}

	public List<Map<String, Object>> getSharingDetails(Object object) throws Exception {
		List<Map<String, Object>> permissionList = new ArrayList<>();
		List<Long> ouIds = new ArrayList<>();
		List<Long> roleIds = new ArrayList<>();
		List<Long> groupIds = new ArrayList<>();
		List<Long> tenantIds = new ArrayList<>();
		List<Long> vendorIds = new ArrayList<>();
		List<Integer> appTypes = new ArrayList<>();
		for (SingleSharingContext sharingContext : this) {
			Map<String, Object> map = new HashMap<>();
			permissionList.add(map);
			SingleSharingContext.SharingType sharingType = sharingContext.getTypeEnum();
			map.put("type", sharingType);
			map.put("approverGroup", sharingContext.getId());
			switch (sharingType) {
				case USER:
					map.put("permissionId", sharingContext.getUserId());
					ouIds.add(sharingContext.getUserId());
					break;

				case GROUP:
					map.put("permissionId", sharingContext.getGroupId());
					groupIds.add(sharingContext.getGroupId());
					break;

				case ROLE:
					map.put("permissionId", sharingContext.getRoleId());
					roleIds.add(sharingContext.getRoleId());
					break;

				case FIELD: {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = modBean.getField(sharingContext.getFieldId());
					map.put("field", field);
					Map<String, Object> moduleDataMap = FieldUtil.getAsProperties(object);
					if (moduleDataMap.containsKey(field.getName())) {
						FacilioModule lookupModule = ((BaseLookupField) field).getLookupModule();
						List<Long> objIds = getUserIdsForField(moduleDataMap, field);
						if (!"users".equals(lookupModule.getName())) { // if not user, then it is people
							FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
							if (lookupModule.getExtendedModuleIds().contains(peopleModule.getModuleId())) {
								List<Long> peopleUserId = new ArrayList<>();
								for (Long userId : objIds) {
									List<Long> userIdForPeople = PeopleAPI.getUserIdForPeople(userId);
									if (CollectionUtils.isNotEmpty(userIdForPeople)) {
										peopleUserId.add(userIdForPeople.get(0));
									}
								}
								objIds = peopleUserId; // change people ids to user ids
							}
						}
						ouIds.addAll(objIds);

						if (field instanceof LookupField) {
							map.put("permissionId", objIds.get(0));
							ouIds.add(objIds.get(0));
						} else if (field instanceof MultiLookupField) {
							map.put("permissionIds", objIds);
							objIds.addAll(objIds);
						}
					}
					break;
				}

				case TENANT: {
					getSharingDetailsForPeople(sharingContext, object, map, tenantIds, "tenant");
					break;
				}

				case VENDOR: {
					getSharingDetailsForPeople(sharingContext, object, map, vendorIds, "vendors");
					break;
				}

				case APP:
					map.put("permissionId", sharingContext.getAppType());
					appTypes.add(sharingContext.getAppType());
					break;
			}
		}

		List<User> users = ouIds.size() == 0 ? new ArrayList<>() : AccountUtil.getUserBean().getUsers(null, true, false, ouIds);
		Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getOuid, Function.identity()));
		List<Group> groups = groupIds.size() == 0 ? new ArrayList<>() : AccountUtil.getGroupBean().getGroups(groupIds);
		Map<Long, Group> groupMap = groups.stream().collect(Collectors.toMap(Group::getId, Function.identity()));
		List<Role> roles = roleIds.size() == 0 ? new ArrayList<>() : AccountUtil.getRoleBean().getRoles(roleIds);
		Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));
		List<TenantContext> tenantList = tenantIds.size() == 0 ? new ArrayList<>() : TenantsAPI.getTenants(tenantIds);
		Map<Long, TenantContext> tenantMap = tenantList.stream().collect(Collectors.toMap(TenantContext::getId, Function.identity()));
		List<VendorContext> vendorList = vendorIds.size() == 0 ? new ArrayList<>() : VendorsAPI.getVendors(vendorIds);
		Map<Long, VendorContext> vendorMap = vendorList.stream().collect(Collectors.toMap(VendorContext::getId, Function.identity()));

		for (Map<String, Object> map : permissionList) {
			SingleSharingContext.SharingType sharingType = (SingleSharingContext.SharingType) map.get("type");
			Long permissionId = (Long) map.get("permissionId");
			switch (sharingType) {
				case USER:
					map.put("value", userMap.get(permissionId));
					break;
				case FIELD:
					if (map.containsKey("field")) {
						FacilioField field = (FacilioField) map.get("field");
						if (field instanceof LookupField) {
							map.put("value", userMap.get(permissionId));
						} else if (field instanceof MultiLookupField) {
							List<Long> permissionIds = (List<Long>) map.get("permissionIds");
							if (CollectionUtils.isNotEmpty(permissionIds)) {
								List<User> userList = new ArrayList<>();
								for (Long perId : permissionIds) {
									if (userMap.containsKey(perId)) {
										userList.add(userMap.get(perId));
									}
								}
								map.put("value", userList);
							}
						}
					}
					break;
				case GROUP:
					map.put("value", groupMap.get(permissionId));
					break;
				case ROLE:
					map.put("value", roleMap.get(permissionId));
					break;
				case TENANT:
					map.put("value", tenantMap.get(permissionId));
					break;
				case VENDOR:
					map.put("value", vendorMap.get(permissionId));
					break;
				case APP:
					map.put("value", AppDomain.AppDomainType.valueOf(permissionId.intValue()));
					break;
			}
		}

		return permissionList;
	}

	private static void getSharingDetailsForPeople(SingleSharingContext sharingContext,
												   Object object, Map<String, Object> map, List<Long> idList, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(sharingContext.getFieldId());
		if (field instanceof LookupField && ((LookupField) field).getLookupModule().getName().equals(moduleName)) {
			ModuleBaseWithCustomFields peopleContext = null;
			Object obj = FieldUtil.getAsProperties(object).get(field.getName());
			if (obj instanceof ModuleBaseWithCustomFields) {
				peopleContext = (ModuleBaseWithCustomFields) obj;
			} else if (obj instanceof Map) {
				peopleContext = FieldUtil.getAsBeanFromMap((Map<String, Object>) obj, ModuleBaseWithCustomFields.class);
			}

			if (peopleContext != null) {
				map.put("permissionId", peopleContext.getId());
				idList.add(peopleContext.getId());
			}
		}
	}
}
