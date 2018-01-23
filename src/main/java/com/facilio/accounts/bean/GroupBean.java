package com.facilio.accounts.bean;

import java.util.List;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.bmsconsole.criteria.Criteria;

public interface GroupBean {
	
	public long createGroup(long orgId, Group group) throws Exception;

	public boolean updateGroup(long groupId, Group group) throws Exception;

	public boolean deleteGroup(long groupId) throws Exception;

	public boolean addGroupMember(long groupId, List<Long> ouidList, GroupMemberRole memberRole) throws Exception;
	
	public boolean addGroupMember(long groupId, List<GroupMember> members) throws Exception;
	
	public boolean removeGroupMember(long groupId, List<Long> ouidList) throws Exception;
	
	public boolean updateGroupMemberRole(long groupId, long ouid, GroupMemberRole memberRole) throws Exception;
	
	public boolean changeGroupStatus(long groupId, Group group) throws Exception;
	
	public List<GroupMember> getGroupMembers(long groupId) throws Exception;

	public Group getGroup(long groupId) throws Exception;
	
	public List<Group> getGroups(Criteria criteria) throws Exception;

	public List<Group> getAllOrgGroups(long orgId) throws Exception;
	
	public List<Group> getOrgGroups(long orgId, boolean status) throws Exception;
	
	public List<Group> getMyGroups(long ouid) throws Exception;
}
