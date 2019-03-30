package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.license.LicenseApi;
import com.facilio.license.LicenseContext;
import com.facilio.license.LicenseContext.FacilioLicense;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class ValidateLicenseCommand implements Command{

public boolean execute(Context context) throws Exception {
		
	User user = (User) context.get(FacilioConstants.ContextNames.USER);
	
	LicenseContext license = LicenseApi.getLicensecontext(user.getLicenseEnum());
	if(license.getTotalLicense() == license.getUsedLicense())
	{
		throw new IllegalArgumentException("User License Exceeded");
	}
	if (license.getLicenseEnum() != FacilioLicense.ADMINSTRATOR) {
		Map<String, Role> roles = AccountUtil.getRoleBean().getRoleMap();
		Role admin = roles.get("Administrator");
		Role superAdmin = roles.get("Super Administrator");
		if (user.getRoleId() == admin.getRoleId() || user.getRoleId() == superAdmin.getRoleId()) {
			throw new IllegalArgumentException("Role Mismatch ");
		}
	}
		return false;
	}
}
