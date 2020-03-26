package com.facilio.bmsconsole.context;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.*;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;

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

		for (SingleSharingContext permission : this) {
			if (isMatching(permission, user, object)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatching (SingleSharingContext permission, User user, Object object) throws Exception {
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
						// todo check here
						Map<String,Object> userObj = (Map<String, Object>) FieldUtil.getAsProperties(object).get(field.getName());
						if (userObj != null) {
							Long ouid = (Long) userObj.get("id");
							if (ouid != null && ouid == user.getOuid()) {
								return true;
							}
						}
					}
				}
				break;
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

	public static List<Map<String, Object>> getSharingDetails(List<SingleSharingContext> list, Object object) throws Exception {
		List<Map<String, Object>> permissionList = new ArrayList<>();
		List<Long> ouIds = new ArrayList<>();
		List<Long> roleIds = new ArrayList<>();
		List<Long> groupIds = new ArrayList<>();
		for (SingleSharingContext sharingContext : list) {
			Map<String, Object> map = new HashMap<>();
			permissionList.add(map);
			SingleSharingContext.SharingType sharingType = sharingContext.getTypeEnum();
			map.put("type", sharingType);
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

				case FIELD:
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = modBean.getField(sharingContext.getFieldId());
					// todo check here
					Map<String,Object> userObj = (Map<String, Object>) FieldUtil.getAsProperties(object).get(field.getName());
					if (userObj != null) {
						Long ouid = (Long) userObj.get("id");
						map.put("permisionId", ouid);
						ouIds.add(ouid);
					}
					break;
			}
		}

		List<User> users = ouIds.size() == 0 ? new ArrayList<>() : AccountUtil.getUserBean().getUsers(null, true, false, ouIds);
		Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getOuid, Function.identity()));
		List<Group> groups = groupIds.size() == 0 ? new ArrayList<>() : AccountUtil.getGroupBean().getGroups(groupIds);
		Map<Long, Group> groupMap = groups.stream().collect(Collectors.toMap(Group::getId, Function.identity()));
		List<Role> roles = roleIds.size() == 0 ? new ArrayList<>() : AccountUtil.getRoleBean().getRoles(roleIds);
		Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));

		for (Map<String, Object> map : permissionList) {
			SingleSharingContext.SharingType sharingType = (SingleSharingContext.SharingType) map.get("type");
			Long permissionId = (Long) map.get("permissionId");
			switch (sharingType) {
				case USER:
				case FIELD:
					map.put("value", userMap.get(permissionId));
					break;
				case GROUP:
					map.put("value", groupMap.get(permissionId));
					break;
				case ROLE:
					map.put("value", roleMap.get(permissionId));
					break;
			}
		}

		return permissionList;
	}
}
