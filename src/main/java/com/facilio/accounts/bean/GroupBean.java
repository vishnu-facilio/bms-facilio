package com.facilio.accounts.bean;

import java.util.Collection;
import java.util.List;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;

public interface GroupBean {
	
	public long createGroup(Group group, FacilioModule module) throws Exception;

	public boolean updateGroup(long groupId, Group group) throws Exception;

	public boolean deleteGroup(long conext) throws Exception;

	public boolean addGroupMember(long groupId, List<Long> ouidList, GroupMemberRole memberRole) throws Exception;
	
	public boolean addGroupMember(long groupId, List<GroupMember> members) throws Exception;
	
	public boolean removeGroupMember(long groupId, List<Long> ouidList) throws Exception;
	
	public boolean removeAllGroupMembers(long groupId) throws Exception;
	
	public boolean updateGroupMemberRole(long groupId, long ouid, GroupMemberRole memberRole) throws Exception;
	
	public boolean changeGroupStatus(long groupId, Group group) throws Exception;
	
	public List<GroupMember> getGroupMembers(long groupId) throws Exception;

	public Group getGroup(long groupId) throws Exception;
	
	public Group getGroup(String groupName) throws Exception;
	
	public List<Group> getGroups(Collection<Long> ids) throws Exception;
	
	public List<Group> getGroups(Criteria criteria) throws Exception;

	public List<Group> getAllOrgGroups(long orgId) throws Exception;

	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers) throws Exception;
	
	public List<Group> getOrgGroups(long orgId, boolean status) throws Exception;
	
	public List<Group> getMyGroups(long ouid) throws Exception;

	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers,int offset, int perPage,String searchQuery,List<Long> filteredSiteId) throws Exception;

	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers, int offset, int perPage, String searchQuery, List<Long> filteredSiteId, String orderBy, String orderType) throws Exception;
}
