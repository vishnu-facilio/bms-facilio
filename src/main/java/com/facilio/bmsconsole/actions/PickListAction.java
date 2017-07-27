package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionSupport;

public class PickListAction extends ActionSupport {
	
	@Override
	public String execute() throws Exception {
		// TODO Auto-generated method stub
		
		if(moduleName.equals(FacilioConstants.ContextNames.USERS)) {
			setPickList(UserAPI.getOrgUsers(OrgInfo.getCurrentOrgInfo().getOrgid()));
		}
		else if(moduleName.equals(FacilioConstants.ContextNames.GROUPS)) {
			List<GroupContext> groups = GroupAPI.getGroupsOfOrg(OrgInfo.getCurrentOrgInfo().getOrgid(), false);
			Map<Long, String> groupList = new HashMap<>();
			for(GroupContext group : groups) {
				groupList.put(group.getGroupId(), group.getName());
			}
			setPickList(groupList);
		}
		else {
			Context context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
			
			Chain pickListChain = FacilioChainFactory.getPickListChain();
			pickListChain.execute(context);
			
			setPickList((Map<Long, String>) context.get(FacilioConstants.ContextNames.PICKLIST));
		}
		
		return SUCCESS;
	}
	
	private Map<Long, String> pickList;
	public Map<Long, String> getPickList() {
		return pickList;
	}
	public void setPickList(Map<Long, String> pickList) {
		this.pickList = pickList;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
