package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AddApplicationUsersCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long appId = (Long)context.get(FacilioConstants.ContextNames.APPLICATION_ID);
		User user = (User) context.get(FacilioConstants.ContextNames.USER);
		if(appId <= 0) {
			throw new IllegalArgumentException("Invalid app id");
		}
		ApplicationContext app = ApplicationApi.getApplicationForId(appId);

		List<AppDomain> appDomains = IAMAppUtil.getAppDomainForType(app.getDomainType(), AccountUtil.getCurrentOrg().getOrgId());
		if(CollectionUtils.isEmpty(appDomains)) {
			throw new IllegalArgumentException("Invalid app domain");
		}
		
		if (user.getRoleId() <= 0 && app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP)) {
			long roleId = getAgentRole();
			user.setRoleId(roleId);
		}
		
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setAppDomain(appDomains.get(0));
		user.setApplicationId(appId);
	
		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomains.get(0).getIdentifier(), true, false);
		context.put(FacilioConstants.ContextNames.ORG_USER_ID, user.getOuid());
		
		return false;
	}
	
	private long getAgentRole() throws Exception {
		String roleName = "Agent Admin";
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		RoleBean roleBean = AccountUtil.getRoleBean();
		Role role = roleBean.getRole(orgId, roleName);
		if (role != null) {
			return role.getId();
		}
		
		role = new Role();
		role.setName(roleName);
		long roleId = roleBean.createRole(orgId, role);
		return roleId;
	}

}
