package com.facilio.componentpackage.command;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

@Log4j
public class DeployPackageCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		long packageId = (long) context.getOrDefault(PackageConstants.PACKAGE_ID, -1L);
		long currentUserId = (long) context.getOrDefault(PackageConstants.CREATED_USER_ID, -1l);
		boolean patchInstall = (boolean) context.getOrDefault(PackageConstants.PATCH_INSTALL, false);
		PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);
		PackageFileContext bundleXmlFileContext = rootFolder.getFile(PackageConstants.PACKAGE_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);

		XMLBuilder packageConfigXML = bundleXmlFileContext.getXmlContent();

		String packageUniqueName = packageConfigXML.getElement(PackageConstants.NAME).getText();
		String packageDisplayName = packageConfigXML.getElement(PackageConstants.DISPLAY_NAME).getText();
		Double version = Double.parseDouble(packageConfigXML.getElement(PackageConstants.VERSION).getText());
		Integer packageType = Integer.parseInt(packageConfigXML.getElement(PackageConstants.TYPE).getText());
		PackageContext.PackageType type = PackageContext.PackageType.valueOf(packageType);

		if (currentUserId <= 0) {
			User currentUser = AccountUtil.getCurrentUser();
			currentUserId = currentUser != null ? currentUser.getOuid() :
					AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId()).getOuid();
		}

		double existingVersion = 0.0D;
		PackageContext packageContext = null;

		if (packageId > 0) {
			packageContext = PackageUtil.getPackageById(packageId);
		} else if (StringUtils.isNotEmpty(packageUniqueName)) {
			packageContext = PackageUtil.getPackageByName(packageUniqueName, type);
			existingVersion = packageContext != null ? packageContext.getVersion() : existingVersion;
		}

		if(packageContext != null) {
			packageContext.setSysModifiedBy(currentUserId);
			packageContext.setSysModifiedTime(System.currentTimeMillis());

			PackageUtil.updatePackageContext(packageContext);
			// Used while reinstall certain components, so use existingVersion = 0
			existingVersion = patchInstall ? existingVersion : packageContext.getVersion();
		} else {
			packageContext = PackageUtil.createPackage(packageUniqueName, packageDisplayName, type, version, currentUserId);
		}
		context.put(PackageConstants.PACKAGE_CONTEXT, packageContext);
		context.put(PackageConstants.PREVIOUS_VERSION, existingVersion);
		context.put(PackageConstants.PACKAGE_CONFIG_XML, packageConfigXML);

		return false;
	}

}
