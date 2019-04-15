package com.facilio.bmsconsole.actions;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Command;

import java.util.List;
import java.util.Map;

public class GroupAction extends ActionSupport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	private List<Group> groups = null;
	public List<Group> getGroups() {
		return groups;
	}
	
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	
	public String groupList() throws Exception 
	{
		setSetup(SetupLayout.getGroupsListLayout());
		setGroups(AccountUtil.getGroupBean().getAllOrgGroups(AccountUtil.getCurrentOrg().getOrgId()));
		
		ActionContext.getContext().getValueStack().set("groups", getGroups());
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
//		userList = UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid(), UserType.USER.getValue());
		
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
		group.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GROUP, getGroup());
		context.put(FacilioConstants.ContextNames.GROUP_MEMBER_IDS, getMembers());
		
		Command addGroup = FacilioChainFactory.getAddGroupCommand();
		addGroup.execute(context);
		setGroupId(group.getGroupId());
		
		return SUCCESS;
	}
	
	private Group group;
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
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
				
//		setSetup(SetupLayout.getEditGroupLayout());
//		setGroup(GroupAPI.getGroup(getGroupId()));
//		
//		Map<Long, String> membersMap = GroupAPI.getGroupMembersMap(getGroupId());
//		List<Long> membersList = new ArrayList<>();
//		Iterator<Long> itr = membersMap.keySet().iterator();
//		while (itr.hasNext()) {
//			membersList.add(itr.next());
//		}
//		members = membersList.toArray(new Long[membersList.size()]);
		
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
	
	
	public String changeTeamStatus() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.GROUP, getGroup());
		Map params = ActionContext.getContext().getParameters();
		
		System.out.println("Group object is "+params+"\n"+ getGroup());
		Command addGroup = FacilioChainFactory.getChangeTeamStatusCommand();
		addGroup.execute(context);
		
		return SUCCESS;
	}
}