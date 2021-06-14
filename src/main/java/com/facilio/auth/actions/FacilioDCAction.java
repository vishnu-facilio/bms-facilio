package com.facilio.auth.actions;

import java.util.Map;

import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class FacilioDCAction extends V3Action {
	
	private String userName;
	
	private GroupType groupType;
	public void setGroupType(int groupTypeInt) {
		this.groupType = GroupType.valueOf(groupTypeInt);
	}
	
	public String dclookup() throws Exception {
		int dc = IAMUserUtil.findDCForUser(userName, groupType);
		setData("dc", dc);
		
		return SUCCESS;
	}
	
	private Map<String, Object> user;
	public String addUser() throws Exception {
		IAMUserUtil.addDCLookup(user);
		setData("result", "success");
		
		return SUCCESS;
	}

}
