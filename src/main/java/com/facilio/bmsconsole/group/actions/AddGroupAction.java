package com.facilio.bmsconsole.group.actions;

import org.apache.commons.chain.Command;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class AddGroupAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		
		String actionName = ActionContext.getContext().getName();
		
		GroupContext context = new GroupContext();
		
		String curUser = (String)ActionContext.getContext().getSession().get("USERNAME");
		long curUserId = UserAPI.getUser(curUser).getUserId();
		
		Command cmd = null;
		if ("save".equalsIgnoreCase(actionName)) {
			cmd = FacilioChainFactory.getAddGroupCommand();
			
			context.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
			context.setName(getName());
			context.setEmail(getEmail());
			context.setDescription(getDescription());
			context.setActive(true);
			context.setCreatedBy(curUserId);
			context.setCreatedTime(System.currentTimeMillis());
		}
		else if ("update".equalsIgnoreCase(actionName)) {
			cmd = FacilioChainFactory.getUpdateGroupCommand();
			
			context = GroupAPI.getGroup(getGroupId());
			context.setName(getName());
			context.setEmail(getEmail());
			context.setDescription(getDescription());
			context.setActive(true);
		}
		else if ("delete".equalsIgnoreCase(actionName)) {
			GroupAPI.deleteGroup(getGroupId());
			return SUCCESS;
		}
		
		try {
			cmd.execute(context);
			System.out.println("Group ID : "+context.getGroupId());
			
			GroupAPI.deleteGroupMembers(getGroupId());
			GroupAPI.addGroupMember(context.getGroupId(), getMembers(), 1);
			
			setGroupId(context.getGroupId());
			return SUCCESS;
		}
		catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		}
	}
	
	private long groupId;
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String email;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long members[];
	public long[] getMembers() {
		return members;
	}
	public void setMembers(long members[]) {
		this.members = members;
	}
}
