package com.facilio.oldsandbox.command;

import java.io.File;
import java.util.List;
import java.util.Locale;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bundle.bean.BundleBean;
import com.facilio.bundle.context.BundleContext;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.bundle.utils.BundleUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.util.DBConf;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.oldsandbox.context.SandboxContext;
import com.facilio.v3.context.Constants;

import lombok.extern.log4j.Log4j;

@Log4j
public class AddNewOrgAndInstallForSandboxCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SandboxContext sandbox = (SandboxContext) context.get(BundleConstants.Sandbox.SANDBOX);
		
		BundleContext productionSandboxBundle = (BundleContext) context.get(BundleConstants.BUNDLE_CONTEXT);
		
		File bundleZipfile = BundleUtil.getBundleZipFile(productionSandboxBundle);
		
		createNewSandboxOrg(bundleZipfile,sandbox);
		
		return false;
	}

	private void createNewSandboxOrg(File bundleZipfile, SandboxContext sandbox) throws Exception {

		Organization productionOrg = AccountUtil.getCurrentOrg();
		
		User user = AccountUtil.getCurrentAccount().getUser();

		List<User> users = AccountUtil.getOrgBean().getActiveOrgUsers(productionOrg.getOrgId());

		JSONObject signupInfo = new JSONObject();
		signupInfo.put("name", productionOrg.getName()+ " Sandbox");
		signupInfo.put("email", user.getEmail().replace("@", "+"+sandbox.getDomain()+"@"));
		signupInfo.put("cognitoId", "facilio");
		signupInfo.put("phone", productionOrg.getPhone());
		signupInfo.put("domainname", productionOrg.getDomain()+sandbox.getDomain());
		signupInfo.put("isFacilioAuth", true);
		signupInfo.put("timezone", productionOrg.getTimezone());
		signupInfo.put("language", productionOrg.getLanguage());
//		signupInfo.put("password", user.getPassword());
		
		Locale locale = DBConf.getInstance().getCurrentLocale();
		IAMAccount iamAccount = null;
		try {
			
			iamAccount = IAMOrgUtil.signUpOrg(signupInfo, locale);
			Account account = new Account(iamAccount.getOrg(), new User(iamAccount.getUser()));
			
			if (account != null && account.getOrg().getOrgId() > 0) {
				
				long sandboxOrgId = account.getOrg().getOrgId();
				
				BundleBean bundleBean = Constants.getBundleBean(sandboxOrgId);
				
				bundleBean.populateSignupData(account,sandboxOrgId, signupInfo);

				bundleBean.createUsers(users,sandboxOrgId,sandbox.getDomain());
				
				bundleBean.installBundle(bundleZipfile);
				
				bundleBean.createSandboxUnmanagedBundle(sandbox.getName(), sandbox.getDomain());
				
				sandbox.setSandboxOrgId(sandboxOrgId);

			}
		}
		catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			
			if(iamAccount != null && iamAccount.getOrg() != null && iamAccount.getOrg().getOrgId() > 0) {
				IAMOrgUtil.rollBackSignedUpOrg(iamAccount.getOrg().getOrgId(), iamAccount.getUser().getUid());
			}
		}
	}

}
