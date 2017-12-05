package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;

public class AddGroupMembersCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Long groupId = (Long) context.get(FacilioConstants.ContextNames.GROUP_ID);
		Long[] memberIds = (Long[]) context.get(FacilioConstants.ContextNames.GROUP_MEMBER_IDS);
		
		if (groupId != null && memberIds != null) {
			
			List<Long> members = new ArrayList<>();
			for (long memberId : memberIds) {
				members.add(memberId);
			}
			
			GroupBean groupBean = AccountUtil.getGroupBean();
			
			groupBean.removeGroupMember(groupId, members);
			
			groupBean.addGroupMember(groupId, members, AccountConstants.GroupMemberRole.MEMBER);
		}
		else {
			throw new IllegalArgumentException("Group Object cannot be null");
		}
		return false;
	}

}
