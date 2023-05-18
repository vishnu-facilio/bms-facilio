package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.delegate.context.DelegationType;
import com.facilio.delegate.util.DelegationUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

public class SharingAPI {
	public static void addSharing (SharingContext<? extends SingleSharingContext> sharing, long parentId, FacilioModule module) throws Exception {
		List<FacilioField> sharingFieldsList = FieldFactory.getSharingFields(module);
		addSharing(sharing,sharingFieldsList,parentId,module);
	}

	public static void addSharing (SharingContext<? extends SingleSharingContext> sharing,List<FacilioField> facilioFields, long parentId, FacilioModule module) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
														.table(module.getTableName())
														.fields(facilioFields)
														;
		
		for (SingleSharingContext share : sharing) {
			share.setOrgId(AccountUtil.getCurrentOrg().getId());
			share.setParentId(parentId);
			insertBuilder.addRecord(FieldUtil.getAsProperties(share));
		}
		insertBuilder.save();
		
		for (int i = 0; i < insertBuilder.getRecords().size(); i++) {
			SingleSharingContext share = sharing.get(i);
			share.setId((long) insertBuilder.getRecords().get(i).get("id"));
		}
	}
	
	
	public static <E extends SingleSharingContext> SharingContext<E> getSharing (long parentId, FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> sharingFields = FieldFactory.getSharingFields(module);
		Map<Long, SharingContext<E>> sharingContextMap = getSharing(Collections.singletonList(parentId), module, classObj, sharingFields);
		return sharingContextMap.get(parentId);
	}


	public static int deleteSharingForParent (List<Long> parentIds, FacilioModule module) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", StringUtils.join(parentIds, ","), NumberOperators.EQUALS))
				;

		return deleteBuilder.delete();
	}
	public static int deleteSharing (Collection<Long> ids, FacilioModule module) throws Exception {
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getIdCondition(ids, module))
														;
		
		return deleteBuilder.delete();
	}
	public static <E extends SingleSharingContext> SharingContext<E> getSharingList(FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		List<Map<String, Object>> props = select.get();
		SharingContext<E> sharingList = new SharingContext<E>();
		if(props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				sharingList.add(FieldUtil.getAsBeanFromMap(prop, classObj));
			}
		}
		return sharingList;	
		
	}
	
	public static <E extends SingleSharingContext> Map<Long, SharingContext<E>> getSharingMap(FacilioModule module, Class<E> classObj) throws Exception {
		List<FacilioField> fields = FieldFactory.getSharingFields(module);
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		List<Map<String, Object>> props = select.get();
		Map<Long, SharingContext<E>> map = new HashMap<>();
		if(props != null && !props.isEmpty()) {
			List<E> sharingList = FieldUtil.getAsBeanListFromMapList(props, classObj);
			for (E sharing: sharingList) {
				long parentId = sharing.getParentId();
				SharingContext<E> recordSharing = map.get(parentId);
				if (recordSharing == null) {
					recordSharing = new SharingContext<E>();
					map.put(parentId, recordSharing);
				}
				recordSharing.add(sharing);
			}
		}
		return map;	
		
	}

	public static SharingContext<SingleSharingContext> getDefaultAppTypeSharing(FacilioView defaultView){
		SharingContext<SingleSharingContext> appSharing = new SharingContext<>();
		if(CollectionUtils.isNotEmpty(defaultView.getViewSharing())) {
			for(SingleSharingContext defaultSharing : defaultView.getViewSharing()){
				if(defaultSharing.getTypeEnum() == SingleSharingContext.SharingType.APP){
					appSharing.add(defaultSharing);
				}
			}
		}
		return appSharing;

	}
	public static SingleSharingContext getCurrentAppTypeSharingForCustomViews() {
		if(AccountUtil.getCurrentUser() != null && AccountUtil.getCurrentUser().getAppDomain() != null) {
			SingleSharingContext appSharing = new SingleSharingContext();
			appSharing.setType(SingleSharingContext.SharingType.APP);
			appSharing.setAppType(AccountUtil.getCurrentUser().getAppDomain().getAppDomainType());
			return appSharing;
		}
		return null;
	}

	public static <E extends SingleSharingContext> SharingContext<E> getSharing(long parentId, FacilioModule module, Class<E> classObj, List<FacilioField> fields) throws Exception {
		Map<Long, SharingContext<E>> sharingContextMap = getSharing(Collections.singletonList(parentId), module, classObj, fields);
		return sharingContextMap.get(parentId);
	}

	public static <E extends SingleSharingContext> Map<Long, SharingContext<E>> getSharing(List<Long> parentIds, FacilioModule module, Class<E> classObj, List<FacilioField> fields) throws Exception {

		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentId");

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(parentIdField, StringUtils.join(parentIds, ","), NumberOperators.EQUALS))
				;

		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, SharingContext<E>> map = new HashMap<>();
		if(CollectionUtils.isNotEmpty(props)) {
			List<E> sharingList = FieldUtil.getAsBeanListFromMapList(props, classObj);
			for (E sharing: sharingList) {
				long parentId = sharing.getParentId();
				SharingContext<E> recordSharing = map.get(parentId);
				if (recordSharing == null) {
					recordSharing = new SharingContext<E>();
					map.put(parentId, recordSharing);
				}
				recordSharing.add(sharing);
			}
		}
		return map;
	}

	public static List<Long> getAllowedParentIds (Map<Long, SharingContext<SingleSharingContext>> parentIdVsSharingContextMap, DelegationType delegationType) throws Exception {
		User currentUser = AccountUtil.getCurrentUser();
		// Delegation Users
		List<User> delegationUsers = DelegationUtil.getUsers(currentUser, System.currentTimeMillis(), delegationType);
		// Groups
		Set<Long> groupIds = new HashSet<>();
		parentIdVsSharingContextMap.forEach((key, value) -> value.stream().map(SingleSharingContext::getGroupId).filter(groupId -> groupId != -1).forEach(groupIds::add));

		List<Group> groups = CollectionUtils.isNotEmpty(groupIds) ? AccountUtil.getGroupBean().getGroups(groupIds) : null;
		Map<Long, List<GroupMember>> groupsMap = new HashMap<>();
		if (CollectionUtils.isNotEmpty(groups)) {
			for (Group group : groups) {
				if (group.getMembers() != null) {
					groupsMap.put(group.getGroupId(), group.getMembers());
				}
			}
		}

		List<Long> allowedParentIds = new ArrayList<>();

		for (Map.Entry<Long, SharingContext<SingleSharingContext>> sharingContextEntry : parentIdVsSharingContextMap.entrySet()) {
			boolean havePermission = false;
			for (SingleSharingContext singleSharingContext : sharingContextEntry.getValue()) {
				havePermission = isMatching(singleSharingContext, currentUser, delegationUsers, groupsMap);
				if (havePermission) {
					allowedParentIds.add(sharingContextEntry.getKey());
					break;
				}
			}
		}
		return allowedParentIds;
	}

	public static boolean isMatching (SingleSharingContext sharingContext, User currentUser, List<User> delegationUsers, Map<Long, List<GroupMember>> groupsMap) {
		switch (sharingContext.getTypeEnum()) {
			case USER:
				if (sharingContext.getUserId() == currentUser.getOuid()) {
					return true;
				}
				if (CollectionUtils.isNotEmpty(delegationUsers)) {
					for (User delegationUser : delegationUsers) {
						if (sharingContext.getUserId() == delegationUser.getId()) {
							return true;
						}
					}
				}
				break;

			case ROLE:
				if (sharingContext.getRoleId() == currentUser.getRoleId()) {
					return true;
				}
				if (CollectionUtils.isNotEmpty(delegationUsers)) {
					for (User delegationUser : delegationUsers) {
						if (sharingContext.getRoleId() == delegationUser.getRoleId()) {
							return true;
						}
					}
				}
				break;

			case GROUP:
				if (sharingContext.getGroupMembers() == null) {
					if (groupsMap.containsKey(sharingContext.getGroupId())) {
						List<GroupMember> members = groupsMap.get(sharingContext.getGroupId());
						sharingContext.setGroupMembers(members);
					}
				}

				if (CollectionUtils.isNotEmpty(sharingContext.getGroupMembers())) {
					for (GroupMember member : sharingContext.getGroupMembers()) {
						if (member.getOuid() == currentUser.getOuid()) {
							return true;
						}
						if (CollectionUtils.isNotEmpty(delegationUsers)) {
							for (User delegationUser : delegationUsers) {
								if (member.getOuid() == delegationUser.getOuid()) {
									return true;
								}
							}
						}
					}
				}
				break;
		}
		return false;
	}
}
