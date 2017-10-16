package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.RoleContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.UserType;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class GroupAction extends ActionSupport {
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<GroupContext> groups = null;
	public List<GroupContext> getGroups() {
		return groups;
	}
	
	public void setGroups(List<GroupContext> groups) {
		this.groups = groups;
	}
	
	public String groupList() throws Exception 
	{
		setSetup(SetupLayout.getGroupsListLayout());
		setGroups(GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid()));
		
		List<RoleContext> roles = UserAPI.getRolesOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
		ActionContext.getContext().getValueStack().set("roles", roles);
		ActionContext.getContext().getValueStack().set("groups", getGroups());

		
		System.out.println(getGroups());

		System.out.println(roles);
		return SUCCESS;
	}
	
	private Map<Long, String> userList;
	public Map<Long, String> getUserList() {
		return userList;
	}
	public void setUserList(Map<Long, String> userList) {
		this.userList = userList;
	}
	
	public String newGroup() throws Exception {
		
		setSetup(SetupLayout.getNewGroupLayout());
		userList = UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.USER.getValue());
		
		return SUCCESS;
	}
	
	private Long members[];
	public Long[] getMembers() {
		return members;
	}
	public void setMembers(Long members[]) {
		this.members = members;
	}
	
	public String addGroup() throws Exception {
		
		// setting necessary fields
		group.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GROUP, getGroup());
		context.put(FacilioConstants.ContextNames.GROUP_MEMBER_IDS, getMembers());
		
		Command addGroup = FacilioChainFactory.getAddGroupCommand();
		addGroup.execute(context);
		setGroupId(group.getGroupId());
		
		return SUCCESS;
	}
	
	private GroupContext group;
	public GroupContext getGroup() {
		return group;
	}
	public void setGroup(GroupContext group) {
		this.group = group;
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	public String editGroup() throws Exception {
				
		setSetup(SetupLayout.getEditGroupLayout());
		setGroup(GroupAPI.getGroup(getGroupId()));
		
		Map<Long, String> membersMap = GroupAPI.getGroupMembersMap(getGroupId());
		List<Long> membersList = new ArrayList<>();
		Iterator<Long> itr = membersMap.keySet().iterator();
		while (itr.hasNext()) {
			membersList.add(itr.next());
		}
		members = membersList.toArray(new Long[membersList.size()]);
		
		return SUCCESS;
	}
	
	public String updateGroup() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GROUP, getGroup());
		context.put(FacilioConstants.ContextNames.GROUP_MEMBER_IDS, getMembers());

		Command updateGroup = FacilioChainFactory.getUpdateGroupCommand();
		updateGroup.execute(context);
		
		return SUCCESS;
	}
	
	public String deleteGroup() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GROUP_ID, getGroupId());

		Command deleteGroup = FacilioChainFactory.getDeleteGroupCommand();
		deleteGroup.execute(context);
		
		return SUCCESS;
	}
}