package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.services.procon.consumer.FacilioConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAppSuperAdminCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long orgId = (long) context.get("orgId");
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN, false);
		User user = AccountUtil.getCurrentUser();
		
		user.setRoleId(superAdminRole.getRoleId());
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setInvitedTime(System.currentTimeMillis());
		if(FacilioProperties.isDevelopment()) {
			user.setUserVerified(true);
		}
		user.setUserType(UserType.USER.getValue());
		
		//adding default domains during super admin signup
		ApplicationApi.addDefaultAppDomains(orgId);
		ApplicationApi.addDefaultApps(orgId);
		long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		user.setApplicationId(appId);
		user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));
		AccountUtil.getUserBean().createUserEntry(orgId, user, true, false);

		//adding super admin to agent app
		long agentAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
		if(agentAppId > 0) {
			User clonedUser = FieldUtil.cloneBean(user, User.class);
			clonedUser.setApplicationId(agentAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		context.put(FacilioConstants.ContextNames.USER, user);
		return false;
	}


}
