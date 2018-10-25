package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fw.BeanFactory;

public class SharingContext extends ArrayList<SingleSharingContext> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SharingContext() {
		super();
	}
	
	public SharingContext(Collection<SingleSharingContext> c) {
		super(c);
	}
	
	public boolean isAllowed () throws Exception {
		return isAllowed(AccountUtil.getCurrentUser());
	}
	
	public boolean isAllowed (User user) throws Exception {
		if (isEmpty()) {
			return true;
		}
		
		for (SingleSharingContext permission : this) {
			if (isMatching(permission, user)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isMatching (SingleSharingContext permission, User user) throws Exception {
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
		}
		return false;
	}
	
	public List<SingleSharingContext> getMatching() throws Exception {
		return getMatching(AccountUtil.getCurrentUser());
	}
	
	public List<SingleSharingContext> getMatching (User user) throws Exception {
		if (isEmpty()) {
			return null;
		}
		
		List<SingleSharingContext> matchingPermissions = new ArrayList<>();
		for (SingleSharingContext permission : this) {
			if (isMatching(permission, user)) {
				matchingPermissions.add(permission);
			}
		}
		return matchingPermissions;
	}
}
