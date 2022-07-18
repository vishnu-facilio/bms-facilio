package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.ScopeOperator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateAppSuperAdminCommand extends FacilioCommand {

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
		ApplicationApi.updateCreatedByForDefaultScoping(user);
		User clonedUser = FieldUtil.cloneBean(user, User.class);
		//adding super admin to agent app
		long agentAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
		if(agentAppId > 0) {
			clonedUser.setApplicationId(agentAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		//adding super admin to maintenance app
		long cafmAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		if(cafmAppId > 0) {
			Role cafmAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN);
			clonedUser.setRole(cafmAdminRole);
			clonedUser.setRoleId(cafmAdminRole.getRoleId());
			//admin scoping
			ScopingContext scoping = new ScopingContext();
			scoping.setScopeName("Default scoping for app - " + cafmAppId + " admin");
			scoping.setDescription("Default scoping for app - " + cafmAppId + " admin");
			scoping.setApplicationId(cafmAppId);
			scoping.setIsDefault(false);
			long scopingId = ApplicationApi.addScoping(scoping);
			context.put(FacilioConstants.ContextNames.Maintenance.MAINTENANCE_ADMIN_SCOPING_ID,scopingId);
			ScopingContext adminScoping = ApplicationApi.getScoping(scopingId);
			clonedUser.setScoping(adminScoping);
			clonedUser.setApplicationId(cafmAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		long dataLoaderAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);
		if(dataLoaderAppId > 0) {
			Role dataLoaderAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(), FacilioConstants.DefaultRoleNames.DATA_LOADER_ADMIN);
			clonedUser.setRole(dataLoaderAdmin);
			clonedUser.setRoleId(dataLoaderAdmin.getRoleId());
			//admin scoping
			ScopingContext scoping = new ScopingContext();
			scoping.setScopeName("Default scoping for app - " + dataLoaderAppId + " admin");
			scoping.setDescription("Default scoping for app - " + dataLoaderAppId + " admin");
			scoping.setApplicationId(dataLoaderAppId);
			scoping.setIsDefault(false);
			long scopingId = ApplicationApi.addScoping(scoping);
			ScopingContext adminScoping = ApplicationApi.getScoping(scopingId);
			clonedUser.setScoping(adminScoping);
			clonedUser.setApplicationId(dataLoaderAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		context.put(FacilioConstants.ContextNames.USER, user);
		return false;
	}
}
