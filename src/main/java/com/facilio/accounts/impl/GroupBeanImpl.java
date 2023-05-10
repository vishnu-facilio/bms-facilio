package com.facilio.accounts.impl;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.*;

public class GroupBeanImpl implements GroupBean {

	@Override
	public long createGroup(Group group, FacilioModule module) throws Exception {

		group.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		group.setCreatedTime(System.currentTimeMillis());
		group.setModuleId(module.getModuleId());
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
		FacilioModule module = AccountConstants.getGroupModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(AccountConstants.getGroupFields())
				.andCustomWhere("ID = ?", groupId);

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
				.andCustomWhere("ID = ?", groupId);

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

		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);

		for (long ouid : ouidList) {
			GroupMember member = new GroupMember();
			member.setGroupId(groupId);
			member.setOuid(ouid);
			member.setMemberRole(memberRole);
			Map<String, Object> props = FieldUtil.getAsProperties(member);
			props.put(FacilioConstants.ContextNames.PEOPLE,PeopleAPI.getPeopleForId(PeopleAPI.getPeopleIdForUser(ouid)).getId());
			props.put("moduleId",module.getModuleId());
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
		fields.addAll(AccountConstants.getAppOrgUserFields());
		fields.addAll(AccountConstants.getGroupMemberFields());
		fields.add(AccountConstants.getRoleIdField());
		long applicationId = -1;
		if( AccountUtil.getCurrentApp() != null ){
			applicationId = AccountUtil.getCurrentApp().getId();
		} else if(SignupUtil.maintenanceAppSignup()){
			applicationId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		} else {
			applicationId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		}
		
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("FacilioGroupMembers")
				.on("FacilioGroupMembers.ORG_USERID = ORG_Users.ORG_USERID")
				.innerJoin("ORG_User_Apps")
				.on("ORG_User_Apps.ORG_USERID = ORG_Users.ORG_USERID")
				.andCustomWhere("FacilioGroupMembers.GROUPID = ?", groupId)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(AccountConstants.getGroupMemberModule()),CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getOrgId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", String.valueOf(applicationId), NumberOperators.EQUALS))
				
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<GroupMember> members = new ArrayList<>();
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
			for(Map<String, Object> prop : props) {
				long peopleId = prop.containsKey("people")? (long) prop.get("people") : -1;
				if (peopleId > 0){
					prop.put("people",PeopleAPI.getPeopleForId(peopleId));
				}
				prop.put("id", prop.get("ouid"));
				members.add(FieldUtil.getAsBeanFromMap(prop, GroupMember.class));
			}
			return members;
		}
		return null;
	}

	@Override
	public Group getGroup(long groupId) throws Exception {
		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
		SelectRecordsBuilder<Group> selectBuilder = new SelectRecordsBuilder<Group>()
				.select(Constants.getModBean().getAllFields(module.getName()))
				.module(module)
				.beanClass(Group.class)
				.andCondition(CriteriaAPI.getIdCondition(groupId,module));
		
		List<Group> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Group group =props.get(0);
			populateGroupEmailAndPhone(group);
			return group;
		}
		return null;
	}
	
	public Group getGroup(String groupName) throws Exception {
		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
		SelectRecordsBuilder<Group> selectBuilder = new SelectRecordsBuilder<Group>()
				.select(Constants.getModBean().getAllFields(module.getName()))
				.module(module)
				.beanClass(Group.class)
				.andCustomWhere("GROUP_NAME = ?", groupName);
		
		List<Group> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Group group = props.get(0);
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
		List<FacilioField> fields = AccountConstants.getGroupFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME","sysDeletedTime",null, CommonOperators.IS_EMPTY))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED_TIME","sysDeletedTime",null, CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(fields).get("id"), ids, NumberOperators.EQUALS))
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
		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
		SelectRecordsBuilder<Group> selectBuilder = new SelectRecordsBuilder<Group>()
				.select(Constants.getModBean().getAllFields(module.getName()))
				.module(module)
				.beanClass(Group.class)
				.andCustomWhere("ORGID = ?" + siteCondition, orgId);
		
		List<Group> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for(Group group : props) {

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
		return getOrgGroups(orgId, status, true);
	}

	@Override
	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers) throws Exception {
		return getOrgGroups(orgId,status,fetchMembers,-1,-1,null,null);
	}

	@Override
	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers,int offset, int perPage,String searchQuery,List<Long> filteredSiteId) throws Exception {
		return getOrgGroups(orgId,status,fetchMembers,offset,perPage,searchQuery,filteredSiteId, null, null);
	}

	@Override
	public List<Group> getOrgGroups(long orgId, boolean status, boolean fetchMembers,int offset, int perPage, String searchQuery, List<Long> filteredSiteId, String orderBy, String orderType) throws Exception {

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

		FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);
		SelectRecordsBuilder<Group> selectBuilder = new SelectRecordsBuilder<Group>()
				.select(Constants.getModBean().getAllFields(module.getName()))
				.module(module)
				.beanClass(Group.class)
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE","isActive", true+"", BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition("ORGID","orgId",orgId+"",NumberOperators.EQUALS));


		Criteria siteCriteria = null;

		if (CollectionUtils.isNotEmpty(filteredSiteId)) {
			filteredSiteId.retainAll(siteIds);
			if (CollectionUtils.isNotEmpty(filteredSiteId)) {
				siteCriteria = new Criteria();
				siteCriteria.addOrCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", "NULL", CommonOperators.IS_EMPTY));
				siteCriteria.addOrCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", StringUtils.join(filteredSiteId, ","), NumberOperators.EQUALS));
			}
		} else if (CollectionUtils.isNotEmpty(siteIds)) {
			siteCriteria = new Criteria();
			siteCriteria.addOrCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", "NULL", CommonOperators.IS_EMPTY));
			siteCriteria.addOrCondition(CriteriaAPI.getCondition("SITE_ID", "siteId", StringUtils.join(siteIds, ","), NumberOperators.EQUALS));
		}

		if (siteCriteria != null) {
			selectBuilder.andCriteria(siteCriteria);
		}

		if(perPage > 0 && offset >= 0) {
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		if(!StringUtils.isEmpty(searchQuery))
		{
			Criteria criteria = new Criteria();
			Condition condition_name = new Condition();
			condition_name.setColumnName("FacilioGroups.GROUP_NAME");
			condition_name.setFieldName("groupName");
			condition_name.setOperator(StringOperators.CONTAINS);
			condition_name.setValue(searchQuery);
			criteria.addOrCondition(condition_name);
			selectBuilder.andCriteria(criteria);
		}
		if (StringUtils.isNotEmpty(orderBy)) {
			selectBuilder.orderBy(orderBy + " " + orderType);
		}
		List<Group> props = selectBuilder.get();
		List<Long> ouids = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Group group : props) {

				if (fetchMembers) {
					populateGroupEmailAndPhone(group);
					if (group.getMembers() != null) {
						for (GroupMember member : group.getMembers()) {
							long ouid = member.getOuid();
							ouids.add(ouid);
						}
					}
				}
				groups.add(group);
			}

			if (fetchMembers) {
				Map<Long, List<Long>> ouidVsAccSpace = null;
				if(CollectionUtils.isNotEmpty(ouids) && CollectionUtils.size(ouids) > 0){
					 ouidVsAccSpace = UserBeanImpl.getAccessibleSpaceList(ouids);
				}
				for (Group group : groups) {
					if (group.getMembers() != null) {
						for (GroupMember member : group.getMembers()) {
							member.setAccessibleSpace(ouidVsAccSpace.get(member.getOuid()));
						}
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
		FacilioModule module = AccountConstants.getGroupModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getGroupFields())
				.table(module.getTableName())
				.innerJoin(AccountConstants.getGroupMemberModule().getTableName())
				.on("FacilioGroups.ID = FacilioGroupMembers.GROUPID")
				.andCustomWhere("FacilioGroupMembers.ORG_USERID = ? and FacilioGroups.IS_ACTIVE = true", ouid)
				.andCustomWhere("FacilioGroupMembers.SYS_DELETED = 0 or FacilioGroupMembers.SYS_DELETED IS NULL");

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
		FacilioModule module =AccountConstants.getGroupModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName()).fields(AccountConstants.getGroupFields())
				.andCustomWhere("ID = ?", group.getId());
		Map<String, Object> props = FieldUtil.getAsProperties(group);
		updateBuilder.update(props);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
}