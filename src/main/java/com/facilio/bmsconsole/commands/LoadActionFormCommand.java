package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ActionForm;
import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.util.GroupAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class LoadActionFormCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ActionForm actionForm = new ActionForm();
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		List<GroupContext> groups = GroupAPI.getGroupsOfOrg(orgId, true);
		Map<Long, String> groupList = new HashMap<>();
		if(groups != null && groups.size() > 0) {
			
			for(GroupContext group : groups) {
				groupList.put(group.getGroupId(), group.getName());
			}
		}
		
		actionForm.setGroupList(groupList);
		context.put(FacilioConstants.ContextNames.ACTION_FORM, actionForm);
		
		return false;
	}

}
