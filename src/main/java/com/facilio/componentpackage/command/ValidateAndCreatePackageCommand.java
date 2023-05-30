package com.facilio.componentpackage.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.utils.PackageUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class ValidateAndCreatePackageCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		String packageName = (String) context.get(PackageConstants.UNIQUE_NAME);
		String packageDisplayName = (String) context.get(PackageConstants.DISPLAY_NAME);
		PackageContext.PackageType packageType = (PackageContext.PackageType) context.get(PackageConstants.PACKAGE_TYPE);

		PackageContext packageContext = PackageUtil.getPackageByName(packageName, packageType);
		if(packageContext != null) {
			throw new IllegalArgumentException("Package with same name already exists");
		}
		long createdUserId = (long) context.getOrDefault(PackageConstants.CREATED_USER_ID, -1l);
		if(createdUserId <= 0) {
			createdUserId = AccountUtil.getCurrentUser().getId();
		}
		packageContext = PackageUtil.createPackage(packageName, packageDisplayName, packageType, 1.0D, createdUserId);

		context.put(PackageConstants.PACKAGE_CONTEXT, packageContext);
		return false;
	}

}
