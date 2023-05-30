package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class DeployPackageCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);

		PackageFileContext bundleXmlFileContext = rootFolder.getFile(PackageConstants.PACKAGE_CONF_FILE_NAME + PackageConstants.FILE_EXTENSION_SEPARATOR + PackageConstants.XML_FILE_EXTN);

		XMLBuilder packageConfigXML = bundleXmlFileContext.getXmlContent();

		String packageUniqueName = packageConfigXML.getElement(PackageConstants.NAME).getText();
		Double version = Double.parseDouble(packageConfigXML.getElement(PackageConstants.VERSION).getText());

		return false;
	}

}
