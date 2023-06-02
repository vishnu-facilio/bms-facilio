package com.facilio.componentpackage.command;

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

@Log4j
public class DeployPackageCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);

		PackageFileContext bundleXmlFileContext = rootFolder.getFile(PackageConstants.PACKAGE_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);

		XMLBuilder packageConfigXML = bundleXmlFileContext.getXmlContent();

		String packageUniqueName = packageConfigXML.getElement(PackageConstants.NAME).getText();
		String packageDisplayName = packageConfigXML.getElement(PackageConstants.DISPLAY_NAME).getText();
		Double version = Double.parseDouble(packageConfigXML.getElement(PackageConstants.VERSION).getText());
		Integer packageType = Integer.parseInt(packageConfigXML.getElement(PackageConstants.TYPE).getText());
		PackageContext.PackageType type = PackageContext.PackageType.valueOf(packageType);

		PackageContext packageContext = PackageUtil.getPackageByName(packageUniqueName, type);
		long currentUserId = (long) context.getOrDefault(PackageConstants.CREATED_USER_ID, -1l);
		if (currentUserId <= 0) {
			currentUserId = AccountUtil.getCurrentUser().getId();
		}
		double existingVersion;

		if(packageContext != null) {
			packageContext.setSysModifiedBy(currentUserId);
			packageContext.setSysModifiedTime(System.currentTimeMillis());
			existingVersion = packageContext.getVersion();

			PackageUtil.updatePackageContext(packageContext);
		} else {
			existingVersion = 0.0D;
			packageContext = PackageUtil.createPackage(packageUniqueName, packageDisplayName, type, version, currentUserId);
		}
		context.put(PackageConstants.PACKAGE_CONTEXT, packageContext);
		context.put(PackageConstants.PREVIOUS_VERSION, existingVersion);
		context.put(PackageConstants.PACKAGE_CONFIG_XML, packageConfigXML);

		return false;
	}

}
