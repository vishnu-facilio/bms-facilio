package com.facilio.accounts.impl;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class GroupBeanImpl implements GroupBean {

	@Override
	public long createGroup(long orgId, Group group) throws Exception {
		
		group.setOrgId(orgId);
		group.setCreatedTime(System.currentTimeMillis());
		group.setIsActive(true);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getGroupModule().getTableName())
				.fields(AccountConstants.getGroupFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(group);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		long groupId = (Long) props.get("id");
		return groupId;
	}

	@Override
	public boolean updateGroup(long groupId, Group group) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getGroupModule().getTableName())
				.fields(AccountConstants.getGroupFields())
				.andCustomWhere("GROUPID = ?", groupId);

		Map<String, Object> props = FieldUtil.getAsProperties(group);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean deleteGroup(long groupId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getGroupModule().getTableName())
				.andCustomWhere("GROUPID = ?", groupId);
		
		int deletedRows = builder.delete();
		if (deletedRows == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean addGroupMember(long groupId, List<Long> ouidList, GroupMemberRole memberRole) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.fields(AccountConstants.getGroupMemberFields());
		
		for (long ouid : ouidList) {
			GroupMember member = new GroupMember();
			member.setGroupId(groupId);
			member.setOuid(ouid);
			member.setMemberRole(memberRole);
			
			member.setGroupId(groupId);
			Map<String, Object> props = FieldUtil.getAsProperties(member);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		
		return true;
	}
	
	@Override
	public boolean addGroupMember(long groupId, List<GroupMember> members) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.fields(AccountConstants.getGroupMemberFields());
		
		for (GroupMember member : members) {
			member.setGroupId(groupId);
			
			Map<String, Object> props = FieldUtil.getAsProperties(member);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
		
		return true;
	}

	@Override
	public boolean removeGroupMember(long groupId, List<Long> ouidList) throws Exception {
		
		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(AccountConstants.getGroupMemberModule());
		
		String ids = StringUtils.join(ouidList, ",");
		Condition idCondition = new Condition();
		idCondition.setField(ouid);
		idCondition.setOperator(NumberOperators.EQUALS);
		idCondition.setValue(ids);
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.andCondition(idCondition)
				.andCustomWhere("GROUPID = ?", groupId);
		
		int deletedRows = builder.delete();
		if (deletedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAllGroupMembers(long groupId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.andCustomWhere("GROUPID = ?", groupId);
		
		int deletedRows = builder.delete();
		if (deletedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean updateGroupMemberRole(long groupId, long ouid, GroupMemberRole memberRole) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getGroupMemberModule().getTableName())
				.fields(AccountConstants.getGroupMemberFields())
				.andCustomWhere("GROUPID = ? AND ORG_USERID = ?", groupId, ouid);
		
		Map<String, Object> props = new HashMap<>();
		props.put("memberRole", memberRole.getMemberRole());
		
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<GroupMember> getGroupMembers(long groupId) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(AccountConstants.getUserFields());
		fields.addAll(AccountConstants.getOrgUserFields());
		fields.addAll(AccountConstants.getGroupMemberFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_Users.USERID")
				.innerJoin("GroupMembers")
				.on("GroupMembers.ORG_USERID = ORG_Users.ORG_USERID")
				.andCustomWhere("GroupMembers.GROUPID = ? AND ORG_Users.DELETED_TIME = -1", groupId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<GroupMember> members = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				members.add(FieldUtil.getAsBeanFromMap(prop, GroupMember.class));
			}
			return members;
		}
		return null;
	}

	@Override
	public Group getGroup(long groupId) throws Exception {
		FacilioModule module = AccountConstants.getGroupModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("GROUPID = ?", groupId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Group group = FieldUtil.getAsBeanFromMap(props.get(0), Group.class);
			populateGroupEmailAndPhone(group);
			return group;
		}
		return null;
	}
	
	public Group getGroup(String groupName) throws Exception {
		FacilioModule module = AccountConstants.getGroupModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("GROUP_NAME = ?", groupName);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Group group = FieldUtil.getAsBeanFromMap(props.get(0), Group.class);
			populateGroupEmailAndPhone(group);
			return group;
		}
		return null;
	}
	
	private void populateGroupEmailAndPhone(Group group) throws Exception {
		group.setMembers(AccountUtil.getGroupBean().getGroupMembers(group.getId()));
		if (group.getMembers() != null && !group.getMembers().isEmpty()) {
			StringJoiner emails = new StringJoiner(",");
			StringJoiner phones = new StringJoiner(",");
			StringJoiner ids = new StringJoiner(",");
			for (GroupMember member : group.getMembers()) {
				if (member.getEmail() != null && !member.getEmail().isEmpty()) {
					emails.add(member.getEmail());
				}
				
				if (member.getMobile() != null && !member.getMobile().isEmpty()) {
					phones.add(member.getMobile());
				}
				else if (member.getPhone() != null && !member.getPhone().isEmpty()) {
					phones.add(member.getPhone());
				}
				
				ids.add(String.valueOf(member.getOuid()));
			}
			
			if (ids.length() > 0) {
				group.setGroupMembersIds(ids.toString());
				
				if (emails.length() > 0) {
					group.setGroupMembersEmail(emails.toString());
				}
				
				if (phones.length() > 0) {
					group.setGroupMembersPhone(phones.toString());
				}
			}
		}
	}
	
	@Override
	public List<Group> getGroups(Criteria criteria) throws Exception {
		FacilioModule module = AccountConstants.getGroupModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;

		if (criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Group> groups = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				Group group = FieldUtil.getAsBeanFromMap(prop, Group.class);
				populateGroupEmailAndPhone(group);
				groups.add(group);
			}
			return groups;
		}
		return null;
	}
	
	@Override
	public List<Group> getGroups(Collection<Long> ids) throws Exception {
		FacilioModule module = AccountConstants.getGroupModule();
		List<FacilioField> fields = AccountConstants.getGroupFields(); 
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("groupId"), ids, NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Group> groups = new ArrayList<>();
			for(Map<String, Object> prop : props) {
				Group group = FieldUtil.getAsBeanFromMap(prop, Group.class);
				populateGroupEmailAndPhone(group);
				groups.add(group);
			}
			return groups;
		}
		return null;
	}

	@Override
	public List<Group> getAllOrgGroups(long orgId) throws Exception {
		
		List<Group> groups = new ArrayList<>();
		
		List<Long> siteIds = new ArrayList<>();
		long siteId = AccountUtil.getCurrentSiteId();
		if (siteId > 0) {
			siteIds.add(siteId);
		} else {
			List<BaseSpaceContext> sites = CommonCommandUtil.getMySites();
			if (sites != null && !sites.isEmpty()) {
				for (BaseSpaceContext b: sites) {
					siteIds.add(b.getSiteId());
				}
			}
		}
		
		String siteCondition = "";
		
		if (!siteIds.isEmpty()) {
			siteCondition = " AND (SITE_ID IS NULL OR SITE_ID IN (" + Strings.join(siteIds, ',') + "))";
		}
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(AccountConstants.getGroupModule().getTableName())
				.andCustomWhere("ORGID = ?" + siteCondition, orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				Group group = FieldUtil.getAsBeanFromMap(prop, Group.class);
				group.setMembers(AccountUtil.getGroupBean().getGroupMembers(group.getId()));
				//populateGroupEmailAndPhone(group);
				groups.add(group);
			}
			return groups;
		}
		return groups;
	}
	
	@Override
	public List<Group> getOrgGroups(long orgId, boolean status) throws Exception {
		
		List<Group> groups = new ArrayList<>();
		
		List<Long> siteIds = new ArrayList<>();
		long siteId = AccountUtil.getCurrentSiteId();
		if (siteId > 0) {
			siteIds.add(siteId);
		} else {
			List<BaseSpaceContext> sites = CommonCommandUtil.getMySites();
			if (sites != null && !sites.isEmpty()) {
				for (BaseSpaceContext b: sites) {
					siteIds.add(b.getSiteId());
				}
			}
		}
		
		String siteCondition = "";
		
		if (!siteIds.isEmpty()) {
			siteCondition = " AND (SITE_ID IS NULL OR SITE_ID IN (" + Strings.join(siteIds, ',') + "))";
		}
		
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(AccountConstants.getGroupModule().getTableName())
				.andCustomWhere("ORGID = ? AND IS_ACTIVE = true"+siteCondition, orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<Long> ouids = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				Group group = FieldUtil.getAsBeanFromMap(prop, Group.class);
				populateGroupEmailAndPhone(group);
				if (group.getMembers() != null) {
					for (GroupMember member: group.getMembers()) {
						long ouid = member.getOuid();
						ouids.add(ouid);
					}
				}
				groups.add(group);
			}
			Map<Long, List<Long>> ouidVsAccSpace = UserBeanImpl.getAccessibleSpaceList(ouids);
			for (Group group: groups) {
				if (group.getMembers() != null) {
					for (GroupMember member: group.getMembers()) {
						member.setAccessibleSpace(ouidVsAccSpace.get(member.getOuid()));
					}
				}
			}
			return groups;
		}
		return groups;
	}

	@Override
	public List<Group> getMyGroups(long ouid) throws Exception {
		
		List<Group> groups = new ArrayList<>();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table("Groups")
				.innerJoin("GroupMembers")
				.on("Groups.GROUPID = GroupMembers.GROUPID")
				.andCustomWhere("GroupMembers.ORG_USERID = ? and Groups.IS_ACTIVE = true", ouid);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				Group group = FieldUtil.getAsBeanFromMap(prop, Group.class);
				populateGroupEmailAndPhone(group);
				groups.add(group);
			}
			return groups;
		}
		return groups;
	}

	@Override
	public boolean changeGroupStatus(long groupId, Group group) throws Exception {
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(AccountConstants.getGroupModule().getTableName()).fields(AccountConstants.getGroupFields())
				.andCustomWhere("GROUPID = ?", group.getGroupId());
		Map<String, Object> props = FieldUtil.getAsProperties(group);
		updateBuilder.update(props);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
}