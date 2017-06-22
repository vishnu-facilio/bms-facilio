package com.facilio.bmsconsole.group.actions;

import java.util.List;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class GroupListAction extends ActionSupport {
	
	private List<GroupContext> groups = null;
	public List<GroupContext> getGroups() {
		return groups;
	}
	public void setGroups(List<GroupContext> groups) {
		this.groups = groups;
	}
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		groups = GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid());
		return SUCCESS;
	}
}