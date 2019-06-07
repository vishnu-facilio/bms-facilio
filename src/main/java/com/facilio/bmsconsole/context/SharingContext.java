package com.facilio.bmsconsole.context;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
				else if (permission.getUserId() == -1 && permission.getFieldId() > 0) {
					if (object != null) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioField field = modBean.getField(permission.getFieldId());
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
}
