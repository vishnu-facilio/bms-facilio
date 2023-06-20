package com.facilio.workflowv2.modulefunctions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.scriptengine.annotation.ScriptModule;
import com.facilio.scriptengine.context.ScriptContext;

@ScriptModule(moduleName = FacilioConstants.ContextNames.GROUPS)
public class FacilioTeamModuleFunctions extends FacilioModuleFunctionImpl {

	
	public Object getTeamUsers(Map<String,Object> globalParams, List<Object> objects, ScriptContext scriptContext) throws Exception{

		if (objects.size() > 1) {
			String teamName = objects.get(1).toString();
			Group group = AccountUtil.getGroupBean().getGroup(teamName);
			if(group.getMembers() != null && !group.getMembers().isEmpty()) {
				
				List<Long> ouids = group.getMembers().stream().map(GroupMember::getOuid).collect(Collectors.toList());
				List<User> users = AccountUtil.getUserBean().getUsersAsMap(null, ouids).values().stream().collect(Collectors.toList());
				
				return FieldUtil.getAsMapList(users, User.class);
			}
			
		}
		
		return Collections.EMPTY_LIST;
	}
}
