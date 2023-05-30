package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

@Log4j
public class DeployPackageComponentCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		PackageFolderContext packageFolderContext = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);


		return false;
	}

}
