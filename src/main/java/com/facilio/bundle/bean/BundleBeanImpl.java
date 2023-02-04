package com.facilio.bundle.bean;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.RoleFactory;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bundle.command.BundleTransactionChainFactory;
import com.facilio.bundle.context.BundleChangeSetContext;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.context.BundleContext.BundleTypeEnum;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class BundleBeanImpl implements BundleBean {

	@Override
	public void installBundle(File bundleZipFile) throws Exception {

		FacilioChain installBundle = BundleTransactionChainFactory.getInstallBundleChain();
		
		FacilioContext newContext = installBundle.getContext();
		
		newContext.put(BundleConstants.BUNDLE_ZIP_FILE, bundleZipFile);
		newContext.put(BundleConstants.BUNDLE_ZIP_FILE_NAME, "sandboxV1");
		
		installBundle.execute();
	}

	@Override
	public void createSandboxUnmanagedBundle(String sandboxName,String sandboxDomainName) throws Exception {
		// TODO Auto-generated method stub
		
		BundleContext sandboxBundle = new BundleContext();
		sandboxBundle.setBundleName(sandboxName+"_Sandbox");
		sandboxBundle.setBundleGlobalName(sandboxDomainName+"_Sandbox");
		sandboxBundle.setTypeEnum(BundleContext.BundleTypeEnum.SANDBOX_UN_MANAGED);
		
		FacilioChain addBundle = BundleTransactionChainFactory.addBundleChain();
		
		FacilioContext newcontext = addBundle.getContext();
		
		newcontext.put(BundleConstants.BUNDLE_CONTEXT, sandboxBundle);
		
		addBundle.execute();
	}

	@Override
	public void createUsers(List<User> users, long sandboxOrgId, String domain) throws Exception{

		Map<String, Role> roleMap = AccountUtil.getRoleBean(sandboxOrgId).getRoleMap();

		for(User user : users){

			if(user.getRole().getName().equalsIgnoreCase(RoleFactory.Role.SUPER_ADMIN.getName())){
				continue;
			}

			user.setId(-1L);
			user.setIamOrgUserId(-1L);
			user.setInviteAcceptStatus(false);
			user.setPassword(null);
			Role role = roleMap.get(user.getRole().getName());
			user.setRole(role);
			user.setRoleId(role.getRoleId());
			user.setUserVerified(false);
			user.setIamOrgUserId(-1L);
			user.setInvitedTime(-1L);
			user.setOuid(-1L);
			user.setPeopleId(-1L);
			user.setUserType(AccountConstants.UserType.USER.getValue());
			user.setEmail(user.getEmail().replace("@", "+" + domain + "@"));
			user.setUserName(user.getUserName().replace("@", "+sandbox@"));

			AccountUtil.getUserBean().createUser(sandboxOrgId,user, user.getIdentifier(), true,false);
		}
	}

	@Override
	public void populateSignupData(Account account, long orgId, JSONObject signupInfo) throws Exception {
		// TODO Auto-generated method stub
		
		AccountUtil.setCurrentAccount(account);
		FacilioChain c = TransactionChainFactory.getOrgSignupChain();
		
		FacilioContext signupContext = c.getContext();
		signupContext.put("orgId", orgId);
		signupContext.put(FacilioConstants.ContextNames.SIGNUP_INFO, signupInfo);
		
		c.execute();
	}

	@Override
	public List<BundleChangeSetContext> getChangeSet() throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, Object> bundleMap = BundleUtil.fetchBundleRelated(ModuleFactory.getBundleModule(), FieldFactory.getBundleFields(), null, CriteriaAPI.getCondition("TYPE", "type", BundleTypeEnum.SANDBOX_UN_MANAGED.getValue()+"", NumberOperators.EQUALS)).get(0);
		
		BundleContext bundle = FieldUtil.getAsBeanFromMap(bundleMap, BundleContext.class);
		
		FacilioChain addBundle = BundleTransactionChainFactory.getBundleChangeSetChain();
		
		FacilioContext context = addBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, bundle);
		
		addBundle.execute();
		
		return (List<BundleChangeSetContext>) context.get(BundleConstants.BUNDLE_CHANGE_SET_LIST);
	}

	@Override
	public File packSandboxChanges() throws Exception {
		// TODO Auto-generated method stub
		
		Map<String, Object> bundleMap = BundleUtil.fetchBundleRelated(ModuleFactory.getBundleModule(), FieldFactory.getBundleFields(), null, CriteriaAPI.getCondition("TYPE", "type", BundleTypeEnum.SANDBOX_UN_MANAGED.getValue()+"", NumberOperators.EQUALS)).get(0);
		
		BundleContext bundle = FieldUtil.getAsBeanFromMap(bundleMap, BundleContext.class);
		
		FacilioChain addBundle = BundleTransactionChainFactory.getCreateVersionChain();
		
		FacilioContext context = addBundle.getContext();
		
		context.put(BundleConstants.BUNDLE_CONTEXT, bundle);
		
		addBundle.execute();
		
		BundleContext bundleNewVersion = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		return BundleUtil.getBundleZipFile(bundleNewVersion);
	}

}
