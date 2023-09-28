package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsole.context.ScopingContext;
import com.facilio.bmsconsoleV3.signup.util.SignupUtil;
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
		if(SignupUtil.maintenanceAppSignup()) {
			maintenanceAppSignup(context);
		} else {
			facilioNewappSignup(context);
		}
		return false;
	}
	private void maintenanceAppSignup(Context context) throws Exception {
		long orgId = (long) context.get("orgId");
//		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN,
//				false);
		User user = AccountUtil.getCurrentUser();

		//user.setRoleId(superAdminRole.getRoleId());
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setInvitedTime(System.currentTimeMillis());
		if (FacilioProperties.isDevelopment()) {
			user.setUserVerified(true);
		}
		user.setUserType(UserType.USER.getValue());

		// adding default domains during super admin signup
		ApplicationApi.addDefaultAppDomains(orgId);
		ApplicationApi.addDefaultApps(orgId);
		long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		Role cafmAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
				FacilioConstants.DefaultRoleNames.MAINTENANCE_SUPER_ADMIN);
		user.setRole(cafmAdminRole);
		user.setRoleId(cafmAdminRole.getRoleId());
		user.setApplicationId(appId);
		user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));


		AccountUtil.getUserBean().createUserEntry(orgId, user, true, false);
		ApplicationApi.updateCreatedByForDefaultScoping(user);
		User clonedUser = FieldUtil.cloneBean(user, User.class);
		// adding super admin to agent app

		long agentAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
		Role agentAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
				FacilioConstants.DefaultRoleNames.AGENT_ADMIN);
		clonedUser.setApplicationId(agentAppId);
		clonedUser.setRole(agentAdmin);
		clonedUser.setRoleId(agentAdmin.getRoleId());
		clonedUser.setApplicationId(agentAppId);

		if (agentAppId > 0) {
			clonedUser.setApplicationId(agentAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		// adding super admin to maintenance app
		long cafmAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		if (cafmAppId > 0) {
//			Role cafmAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
//					FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN);
//			clonedUser.setRole(cafmAdminRole);
//			clonedUser.setRoleId(cafmAdminRole.getRoleId());
			// admin scoping
			//AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		long dataLoaderAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);
		if (dataLoaderAppId > 0) {
			Role dataLoaderAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.DATA_LOADER_ADMIN);
			clonedUser.setRole(dataLoaderAdmin);
			clonedUser.setRoleId(dataLoaderAdmin.getRoleId());
			clonedUser.setApplicationId(dataLoaderAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}
		// adding super admin to kiosk app
		long kioskAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.KIOSK_APP);
		if (kioskAppId > 0) {
			Role kioskAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.KIOSK_ADMIN);
			clonedUser.setApplicationId(kioskAppId);
			clonedUser.setRole(kioskAdmin);
			clonedUser.setRoleId(kioskAdmin.getRoleId());
			clonedUser.setApplicationId(kioskAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		long iwmsAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.IWMS_APP);
		if (iwmsAppId > 0) {
			Role iwmsAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.IWMS_ADMIN);
			clonedUser.setApplicationId(iwmsAppId);
			clonedUser.setRole(iwmsAdmin);
			clonedUser.setRoleId(iwmsAdmin.getRoleId());
			clonedUser.setApplicationId(iwmsAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}
//		long energyAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.ENERGY_APP);
//		if(energyAppId > 0){
//			Role superAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
//					FacilioConstants.DefaultRoleNames.SUPER_ADMIN);
//			clonedUser.setApplicationId(energyAppId);
//			clonedUser.setRole(superAdmin);
//			clonedUser.setRoleId(superAdmin.getRoleId());
//			clonedUser.setApplicationId(energyAppId);
//			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
//		}

		context.put(FacilioConstants.ContextNames.USER, user);
	}

	private void facilioNewappSignup(Context context) throws Exception {
		long orgId = (long) context.get("orgId");
		Role superAdminRole = AccountUtil.getRoleBean(orgId).getRole(orgId, AccountConstants.DefaultRole.SUPER_ADMIN,
				false);
		superAdminRole.setIsSuperAdmin(true);
		User user = AccountUtil.getCurrentUser();

		user.setRoleId(superAdminRole.getRoleId());
		user.setInviteAcceptStatus(true);
		user.setDefaultOrg(true);
		user.setInvitedTime(System.currentTimeMillis());
		if (FacilioProperties.isDevelopment()) {
			user.setUserVerified(true);
		}
		user.setUserType(UserType.USER.getValue());

		// adding default domains during super admin signup
		ApplicationApi.addDefaultAppDomains(orgId);
		ApplicationApi.addDefaultApps(orgId);
		long appId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
		user.setApplicationId(appId);
		user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));
		AccountUtil.getUserBean().createUserEntry(orgId, user, true, false);
		ApplicationApi.updateCreatedByForDefaultScoping(user);
		User clonedUser = FieldUtil.cloneBean(user, User.class);
		// adding super admin to agent app
		long agentAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_AGENT_APP);
		if (agentAppId > 0) {
			clonedUser.setApplicationId(agentAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		// adding super admin to maintenance app
		long cafmAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
		if (cafmAppId > 0) {
			Role cafmAdminRole = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.MAINTENANCE_SUPER_ADMIN);
			clonedUser.setRole(cafmAdminRole);
			clonedUser.setRoleId(cafmAdminRole.getRoleId());
			clonedUser.setApplicationId(cafmAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		long dataLoaderAppId = ApplicationApi
				.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.DATA_LOADER_APP);
		if (dataLoaderAppId > 0) {
			Role dataLoaderAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.DATA_LOADER_ADMIN);
			clonedUser.setRole(dataLoaderAdmin);
			clonedUser.setRoleId(dataLoaderAdmin.getRoleId());
			clonedUser.setApplicationId(dataLoaderAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}
		// adding super admin to kiosk app
		long kioskAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.KIOSK_APP);
		if (kioskAppId > 0) {
			Role kioskAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.KIOSK_ADMIN);
			clonedUser.setApplicationId(kioskAppId);
			clonedUser.setRole(kioskAdmin);
			clonedUser.setRoleId(kioskAdmin.getRoleId());
			clonedUser.setApplicationId(kioskAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		long iwmsAppId = ApplicationApi.getApplicationIdForLinkName(FacilioConstants.ApplicationLinkNames.IWMS_APP);
		if (iwmsAppId > 0) {
			Role iwmsAdmin = AccountUtil.getRoleBean().getRole(AccountUtil.getCurrentOrg().getOrgId(),
					FacilioConstants.DefaultRoleNames.IWMS_ADMIN);
			clonedUser.setApplicationId(iwmsAppId);
			clonedUser.setRole(iwmsAdmin);
			clonedUser.setRoleId(iwmsAdmin.getRoleId());
			clonedUser.setApplicationId(iwmsAppId);
			AccountUtil.getUserBean().addToORGUsersApps(clonedUser, false);
		}

		context.put(FacilioConstants.ContextNames.USER, user);
	}
}
